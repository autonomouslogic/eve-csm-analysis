package com.autonomouslogic.evecsmanalysis.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * A group of multiple similar ballots.
 */
@Value
@Builder(toBuilder = true)
@Jacksonized
public class Votes {
	int count;
	Ballot ballot;

	public String toString() {
		return "%d %s".formatted(count, ballot);
	}
}
