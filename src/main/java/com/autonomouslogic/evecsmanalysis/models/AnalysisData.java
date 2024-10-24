package com.autonomouslogic.evecsmanalysis.models;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class AnalysisData {
	int candidateCount;

	int totalVotes;

	@Singular
	List<CandidateRound> winners;

	@Singular
	List<CandidateRound> eliminations;
}
