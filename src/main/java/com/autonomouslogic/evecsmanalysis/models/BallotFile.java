package com.autonomouslogic.evecsmanalysis.models;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Root model for parsing <code>.blt</code> files.
 */
@Value
@Builder(toBuilder = true)
public class BallotFile {
	int candidates;
	int seats;

	public String toString() {
		return Stream.of(
			serCandidates()
		).flatMap(Function.identity())
			.collect(Collectors.joining("\n"));
	}

	private Stream<String> serCandidates() {
		return Stream.of(
			"%d %d".formatted(candidates, seats)
		);
	}
}
