package com.autonomouslogic.evecsmanalysis.models;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Ballot {
	@Singular
	List<Integer> rankings;

	public String toString() {
		return rankings.stream().map(Object::toString).collect(Collectors.joining(" "));
	}
}
