package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import com.autonomouslogic.evecsmanalysis.models.Votes;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnalysisRunner {
	@NonNull
	private final BallotFile ballotFile;

	public Map<String, Object> run() {
		return Map.of(
				"candidateCount", ballotFile.getCandidateCount(),
				"totalVotes", totalVotes());
	}

	private int totalVotes() {
		return ballotFile.getAllVotes().stream().mapToInt(Votes::getCount).sum();
	}
}
