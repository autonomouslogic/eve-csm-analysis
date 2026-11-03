package com.autonomouslogic.evecsmanalysis.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Ballot statistics for one candidate. Mean and median rank are across the ballots the candidate appears on, and are
 * null if they appear on none.
 */
@Value
@Builder(toBuilder = true)
@Jacksonized
public class CandidateStatistics {
	String candidate;

	/**
	 * Ballots ranking the candidate at any position.
	 */
	int ballots;

	/**
	 * Ballots ranking the candidate first.
	 */
	int firstChoiceBallots;

	Double meanRank;

	Double medianRank;
}
