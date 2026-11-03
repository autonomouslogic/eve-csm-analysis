package com.autonomouslogic.evecsmanalysis.models;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * A ballot composition, i.e. the candidates ranked in order, and how many votes used exactly that ranking.
 */
@Value
@Builder(toBuilder = true)
@Jacksonized
public class PopularBallot {
	/**
	 * Candidate names in rank order, first choice first.
	 */
	List<String> candidates;

	/**
	 * Votes with this exact ranking. Weighted by {@link Votes#getCount()}.
	 */
	int ballots;
}
