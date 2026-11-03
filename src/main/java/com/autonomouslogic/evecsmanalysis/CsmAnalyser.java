package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.AuditLog;
import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import com.autonomouslogic.evecsmanalysis.models.CandidateRankDistribution;
import com.autonomouslogic.evecsmanalysis.models.CandidateRound;
import com.autonomouslogic.evecsmanalysis.models.CsmAnalysis;
import com.autonomouslogic.evecsmanalysis.models.CsmConfig;
import com.autonomouslogic.evecsmanalysis.models.Round;
import com.autonomouslogic.evecsmanalysis.models.Votes;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
public class CsmAnalyser {
	private static final ObjectMapper objectMapper = new ObjectMapper();

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
				.ballotsWithoutWinner(ballotsWithoutWinner(ballotFile, auditLog.getFinalResults()))
				//				.leastSignificantRank(1)
				.leastSignificantRank(findLeastSignificantRank())
				.withdrawnCandidates(withdrawnCandidates(ballotFile))
				.rankDistribution(rankDistribution(ballotFile, electionOrder(auditLog)));
		winnersAndEliminations(data);
		return data.build();
	}

	private int totalVotes() {
		log.info("Calculating total votes");
		return ballotFile.getAllVotes().stream().mapToInt(Votes::getCount).sum();
	}

	/**
	 * Counts ballots that rank none of the given winners, i.e. only eliminated or withdrawn candidates. This is not
	 * the STV notion of an exhausted ballot, and it does not include ballots that rank a winner but never counted
	 * towards one.
	 */
	static int ballotsWithoutWinner(BallotFile ballotFile, List<String> winners) {
		log.info("Calculating ballots without a winner");
		var winnerIds = new HashSet<Integer>();
		for (var entry : ballotFile.getCandidateNames().entrySet()) {
			if (winners.contains(entry.getValue())) {
				winnerIds.add(entry.getKey());
			}
		}
		if (winnerIds.size() != winners.size()) {
			throw new IllegalArgumentException("Winners not found in ballot file: " + winners);
		}
		return ballotFile.getAllVotes().stream()
				.filter(votes -> votes.getBallot().getRankings().stream().noneMatch(winnerIds::contains))
				.mapToInt(Votes::getCount)
				.sum();
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
	 * Candidates in display order: elected candidates in the order they were chosen, followed by eliminated
	 * candidates in reverse order of elimination.
	 */
	static List<String> electionOrder(AuditLog auditLog) {
		var elected = new LinkedHashSet<String>();
		var eliminated = new ArrayList<String>();
		for (var round : auditLog.getRounds()) {
			elected.addAll(round.getElectedCandidates());
			eliminated.add(round.getElimination().getCandidate());
		}
		elected.addAll(auditLog.getFinalResults());
		Collections.reverse(eliminated);
		var order = new ArrayList<>(elected);
		order.addAll(eliminated);
		return order;
	}

	/**
	 * Counts, per candidate, how many ballots place them at each rank. Withdrawn candidates are included.
	 * Sorted by the given order. Candidates not in the order (withdrawn) come last, sorted by name.
	 */
	static List<CandidateRankDistribution> rankDistribution(BallotFile ballotFile, List<String> order) {
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
				.sorted(rankDistributionOrdering(order))
				.toList();
	}

	private static Comparator<CandidateRankDistribution> rankDistributionOrdering(List<String> order) {
		var positions = new HashMap<String, Integer>();
		for (int i = 0; i < order.size(); i++) {
			positions.put(order.get(i), i);
		}
		return Comparator.comparing(
						(CandidateRankDistribution d) -> positions.getOrDefault(d.getCandidate(), Integer.MAX_VALUE))
				.thenComparing(CandidateRankDistribution::getCandidate);
	}

	private static String candidateName(BallotFile ballotFile, int id) {
		var name = ballotFile.getCandidateNames().get(id);
		if (name == null) {
			throw new IllegalArgumentException("Unknown candidate id: " + id);
		}
		return name;
	}

	void winnersAndEliminations(CsmAnalysis.CsmAnalysisBuilder data) {
		log.info("Calculating winners and eliminations");
		var finalResults = auditLog.getFinalResults();
		var seenWinners = new HashSet<String>();
		var seenEliminations = new HashSet<String>();
		var eliminations = new ArrayList<CandidateRound>();
		int r = -1;
		for (var round : auditLog.getRounds()) {
			r = round.getRoundNumber();
			for (var elected : round.getElectedCandidates()) {
				if (!finalResults.contains(elected)) {
					throw new IllegalArgumentException();
				}
				if (!seenWinners.contains(elected)) {
					data.winner(CandidateRound.builder()
							.candidate(elected)
							.round(r)
							.votes(votesAtElection(round, elected))
							.build());
					seenWinners.add(elected);
				}
			}
			var eliminated = round.getElimination().getCandidate();
			if (finalResults.contains(eliminated)) {
				throw new IllegalArgumentException();
			}
			if (!seenEliminations.contains(eliminated)) {
				eliminations.add(CandidateRound.builder()
						.candidate(eliminated)
						.round(r)
						.votes(round.getElimination().getVotes())
						.votesShortOfQuota(
								round.getQuota() - round.getElimination().getVotes())
						.build());
				seenEliminations.add(eliminated);
			}
		}
		for (var winner : auditLog.getFinalResults()) {
			if (!seenWinners.contains(winner)) {
				data.winner(CandidateRound.builder().candidate(winner).round(r).build());
			}
		}
		// Stored last eliminated first, matching the display order.
		Collections.reverse(eliminations);
		eliminations.forEach(data::elimination);
	}

	/**
	 * Votes a candidate held when elected: their initial talley plus the votes transferred to them by the transfers
	 * listed before their election in the round. Null if the candidate is not in the round's initial talley.
	 */
	static Double votesAtElection(Round round, String candidate) {
		var initial = round.getInitialTalley().get(candidate);
		if (initial == null) {
			return null;
		}
		var transfers = round.getVotesTransfers();
		var transfersBefore = round.getTransfersBeforeElection().getOrDefault(candidate, 0);
		var received = 0.0;
		for (var transfer : transfers.subList(0, transfersBefore)) {
			received += transfer.getToCandidates().getOrDefault(candidate, 0.0);
		}
		return initial + received;
	}

	private int findLeastSignificantRank() {
		var previous = previousLeastSignificantRank();
		if (previous.isPresent()) {
			log.info("Reusing least significant rank from previous analysis: {}", previous.get());
			return previous.get();
		}
		log.info("Finding least significant rank");
		var rank = new LeastSignificantRankRunner(
						csmConfig.getTalleyScriptFile(), ballotFile, auditLog.getFinalResults())
				.findLeastSignificantRank();
		return rank;
	}

	/**
	 * Reuses the rank from the previous analysis file if it is newer than every input file. Mtime based, so it can
	 * give false positives (e.g. a touched input that did not change), but never reuses a rank computed before an input
	 * was modified.
	 */
	private Optional<Integer> previousLeastSignificantRank() {
		var analysisFile = csmConfig.getAnalysisJson();
		if (analysisFile == null || !analysisFile.exists()) {
			return Optional.empty();
		}
		var newestInput = Stream.of(
						csmConfig.getVotesBlt(), csmConfig.getAuditLogTxt(), csmConfig.getTalleyScriptFile())
				.mapToLong(File::lastModified)
				.max()
				.orElseThrow();
		if (analysisFile.lastModified() <= newestInput) {
			log.debug("Analysis file older than inputs");
			return Optional.empty();
		}
		try {
			return Optional.of(
					objectMapper.readValue(analysisFile, CsmAnalysis.class).getLeastSignificantRank());
		} catch (IOException e) {
			log.warn("Unable to read previous analysis: {}", analysisFile, e);
			return Optional.empty();
		}
	}
}
