package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.AnalysisData;
import com.autonomouslogic.evecsmanalysis.models.AuditLog;
import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import com.autonomouslogic.evecsmanalysis.models.Votes;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnalysisRunner {
	@NonNull
	private final BallotFile ballotFile;

	@NonNull
	private final AuditLog auditLog;

	public AnalysisData run() {
		return AnalysisData.builder()
				.candidateCount(ballotFile.getCandidateCount())
				.totalVotes(totalVotes())
				.build();
	}

	private int totalVotes() {
		return ballotFile.getAllVotes().stream().mapToInt(Votes::getCount).sum();
	}
}
