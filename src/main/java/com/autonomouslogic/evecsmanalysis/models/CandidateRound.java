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

	/**
	 * Votes held in {@link #round}: at election for winners, at elimination for eliminated candidates. Null for
	 * winners declared in the final result without an election in a round.
	 */
	Double votes;

	/**
	 * Quota in {@link #round} minus {@link #votes}. Only set for eliminated candidates.
	 */
	Double votesShortOfQuota;
}
