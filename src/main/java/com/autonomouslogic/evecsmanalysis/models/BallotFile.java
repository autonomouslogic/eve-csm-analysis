package com.autonomouslogic.evecsmanalysis.models;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/**
 * Root model for parsing <code>.blt</code> files.
 */
@Value
@Builder(toBuilder = true)
public class BallotFile {
	int candidates;
	int seats;

	@Singular
	List<Votes> allVotes;

	public String toString() {
		return Stream.of(serCandidates(), serWithdrawals(), serVotes())
				.flatMap(Function.identity())
				.collect(Collectors.joining("\n"));
	}

	private Stream<String> serCandidates() {
		return Stream.of("%d %d".formatted(candidates, seats));
	}

	private Stream<String> serWithdrawals() {
		return Stream.of(); // @todo
	}

	private Stream<String> serVotes() {
		return allVotes.stream().map(Votes::toString);
	}
}
