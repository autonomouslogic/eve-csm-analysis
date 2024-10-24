package com.autonomouslogic.evecsmanalysis.models;

import lombok.Builder;
import lombok.Value;

/**
 * A group of multiple similar ballots.
 */
@Value
@Builder(toBuilder = true)
public class Votes {
	int count;
	Ballot ballot;

	public String toString() {
		return "%d %s".formatted(count, ballot);
	}
}
