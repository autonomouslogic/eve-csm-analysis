package com.autonomouslogic.evecsmanalysis.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class CandidateRound {
	String candidate;
	int round;
}
