package com.autonomouslogic.evecsmanalysis.models;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Number of ballots placing a candidate at each rank. Index 0 of <code>rankVotes</code> is rank 1.
 */
@Value
@Builder(toBuilder = true)
@Jacksonized
public class CandidateRankDistribution {
	String candidate;

	List<Integer> rankVotes;
}
