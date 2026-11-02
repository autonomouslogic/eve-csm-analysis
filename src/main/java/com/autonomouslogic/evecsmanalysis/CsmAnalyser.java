package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.AuditLog;
import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import com.autonomouslogic.evecsmanalysis.models.CandidateRankDistribution;
import com.autonomouslogic.evecsmanalysis.models.CandidateRound;
import com.autonomouslogic.evecsmanalysis.models.CsmAnalysis;
import com.autonomouslogic.evecsmanalysis.models.CsmConfig;
import com.autonomouslogic.evecsmanalysis.models.Votes;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
public class CsmAnalyser {
	@NonNull
	private final CsmConfig csmConfig;

	@NonNull
	private final BallotFile ballotFile;

	@NonNull
	private final AuditLog auditLog;

	public CsmAnalysis run() {
		log.info("Running analysis for CSM: {}", csmConfig.getCsmNumber());
		var data = CsmAnalysis.builder()
				.csmNumber(csmConfig.getCsmNumber())
				.candidateCount(ballotFile.getCandidateCount())
				.totalVotes(totalVotes())
				.leastSignificantRank(findLeastSignificantRank())
				.withdrawnCandidates(withdrawnCandidates(ballotFile))
				.rankDistribution(rankDistribution(ballotFile));
		winnersAndEliminations(data);
		return data.build();
	}

	private int totalVotes() {
		log.info("Calculating total votes");
		return ballotFile.getAllVotes().stream().mapToInt(Votes::getCount).sum();
	}

	static int rankCount(BallotFile ballotFile) {
		return ballotFile.getAllVotes().stream()
				.mapToInt(votes -> votes.getBallot().getRankings().size())
				.max()
				.orElse(0);
	}

	static List<String> withdrawnCandidates(BallotFile ballotFile) {
		return ballotFile.getWithdrawnCandidates().stream()
				.map(id -> candidateName(ballotFile, id))
				.toList();
	}

	/**
	 * Counts, per candidate, how many ballots place them at each rank. Withdrawn candidates are included.
	 * Sorted by votes at rank 1, then rank 2, and so on, then by name.
	 */
	static List<CandidateRankDistribution> rankDistribution(BallotFile ballotFile) {
		log.info("Calculating rank distribution");
		var rankCount = rankCount(ballotFile);
		var counts = new HashMap<Integer, int[]>();
		for (var id : ballotFile.getCandidateNames().keySet()) {
			counts.put(id, new int[rankCount]);
		}
		for (var votes : ballotFile.getAllVotes()) {
			var rankings = votes.getBallot().getRankings();
			for (int i = 0; i < rankings.size(); i++) {
				var ranks = counts.get(rankings.get(i));
				if (ranks == null) {
					throw new IllegalArgumentException("Ballot references unknown candidate: " + rankings.get(i));
				}
				ranks[i] += votes.getCount();
			}
		}
		return counts.entrySet().stream()
				.map(entry -> CandidateRankDistribution.builder()
						.candidate(candidateName(ballotFile, entry.getKey()))
						.rankVotes(Arrays.stream(entry.getValue()).boxed().toList())
						.build())
				.sorted(rankDistributionOrdering())
				.toList();
	}

	private static Comparator<CandidateRankDistribution> rankDistributionOrdering() {
		return (a, b) -> {
			for (int i = 0; i < a.getRankVotes().size(); i++) {
				int c = Integer.compare(b.getRankVotes().get(i), a.getRankVotes().get(i));
				if (c != 0) {
					return c;
				}
			}
			return a.getCandidate().compareTo(b.getCandidate());
		};
	}

	private static String candidateName(BallotFile ballotFile, int id) {
		var name = ballotFile.getCandidateNames().get(id);
		if (name == null) {
			throw new IllegalArgumentException("Unknown candidate id: " + id);
		}
		return name;
	}

	private void winnersAndEliminations(CsmAnalysis.CsmAnalysisBuilder data) {
		log.info("Calculating winners and eliminations");
		var finalResults = auditLog.getFinalResults();
		var seenWinners = new HashSet<String>();
		var seenEliminations = new HashSet<String>();
		int r = -1;
		for (var round : auditLog.getRounds()) {
			r = round.getRoundNumber();
			for (var elected : round.getElectedCandidates()) {
				if (!finalResults.contains(elected)) {
					throw new IllegalArgumentException();
				}
				if (!seenWinners.contains(elected)) {
					data.winner(
							CandidateRound.builder().candidate(elected).round(r).build());
					seenWinners.add(elected);
				}
			}
			var eliminated = round.getElimination().getCandidate();
			if (finalResults.contains(eliminated)) {
				throw new IllegalArgumentException();
			}
			if (!seenEliminations.contains(eliminated)) {
				data.elimination(
						CandidateRound.builder().candidate(eliminated).round(r).build());
				seenEliminations.add(eliminated);
			}
		}
		for (var winner : auditLog.getFinalResults()) {
			if (!seenWinners.contains(winner)) {
				data.winner(CandidateRound.builder().candidate(winner).round(r).build());
			}
		}
	}

	private int findLeastSignificantRank() {
		log.info("Finding least significant rank");
		var rank = new LeastSignificantRankRunner(
						csmConfig.getTalleyScriptFile(), ballotFile, auditLog.getFinalResults())
				.findLeastSignificantRank();
		return rank;
	}
}
