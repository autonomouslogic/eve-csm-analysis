package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.AuditLog;
import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import com.autonomouslogic.evecsmanalysis.models.CandidateRound;
import com.autonomouslogic.evecsmanalysis.models.CsmAnalysis;
import com.autonomouslogic.evecsmanalysis.models.CsmConfig;
import com.autonomouslogic.evecsmanalysis.models.Votes;
import java.util.HashSet;
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
				.leastSignificantRank(findLeastSignificantRank());
		winnersAndEliminations(data);
		return data.build();
	}

	private int totalVotes() {
		log.info("Calculating total votes");
		return ballotFile.getAllVotes().stream().mapToInt(Votes::getCount).sum();
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
