package com.autonomouslogic.evecsmanalysis.models;

import java.util.List;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class AnalysisData {
	int csmNumber;

	int candidateCount;

	int totalVotes;

	@Singular
	List<CandidateRound> winners;

	@Singular
	List<CandidateRound> eliminations;
}
