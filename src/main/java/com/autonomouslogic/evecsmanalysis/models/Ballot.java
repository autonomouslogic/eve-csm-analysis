package com.autonomouslogic.evecsmanalysis.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ballot {
	@Singular
	List<Integer> rankings;

	public String toString() {
		return rankings.stream().map(Object::toString).collect(Collectors.joining(" "));
	}
}
