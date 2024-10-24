package com.autonomouslogic.evecsmanalysis.models;

import java.util.Map;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class VotesTransfer {
	String fromCandidate;
	double votes;
	double factor;
	double excess;

	@Singular
	Map<String, Double> toCandidates;
}
