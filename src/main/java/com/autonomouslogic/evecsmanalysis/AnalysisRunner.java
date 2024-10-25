package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.AnalysisData;
import com.autonomouslogic.evecsmanalysis.models.AuditLog;
import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import com.autonomouslogic.evecsmanalysis.models.CandidateRound;
import com.autonomouslogic.evecsmanalysis.models.Votes;
import java.util.HashSet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnalysisRunner {
	private final int csmNumber;

	@NonNull
	private final BallotFile ballotFile;

	@NonNull
	private final AuditLog auditLog;

	public AnalysisData run() {
		var data = AnalysisData.builder()
				.csmNumber(csmNumber)
				.candidateCount(ballotFile.getCandidateCount())
				.totalVotes(totalVotes());
		winnersAndEliminations(data);
		return data.build();
	}

	private int totalVotes() {
		return ballotFile.getAllVotes().stream().mapToInt(Votes::getCount).sum();
	}

	private void winnersAndEliminations(AnalysisData.AnalysisDataBuilder data) {
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
}
