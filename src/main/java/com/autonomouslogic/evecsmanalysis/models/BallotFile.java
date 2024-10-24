package com.autonomouslogic.evecsmanalysis.models;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Root model for parsing <code>.blt</code> files.
 */
@Value
@Builder(toBuilder = true)
@Jacksonized
public class BallotFile {
	String name;

	int candidateCount;

	int seatCount;

	@Singular
	Map<Integer, String> candidateNames;

	@Singular
	List<Votes> allVotes;

	public String toString() {
		return Stream.of(serHeader(), serWithdrawals(), serVotes(), Stream.of("0"), serCandidates(), serName())
						.flatMap(Function.identity())
						.collect(Collectors.joining("\n"))
				+ "\n";
	}

	private Stream<String> serHeader() {
		return Stream.of("%d %d".formatted(candidateCount, seatCount));
	}

	private Stream<String> serWithdrawals() {
		return Stream.of(); // @todo
	}

	private Stream<String> serVotes() {
		return allVotes.stream().map(Votes::toString);
	}

	private Stream<String> serCandidates() {
		return candidateNames.keySet().stream()
				.sorted()
				.map(k -> candidateNames.get(k))
				.map(c -> "\"" + c + "\"");
	}

	private Stream<String> serName() {
		return Stream.of(name);
	}
}
