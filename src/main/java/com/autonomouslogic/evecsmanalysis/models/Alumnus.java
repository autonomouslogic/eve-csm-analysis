package com.autonomouslogic.evecsmanalysis.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Alumnus {
	@JsonProperty
	private String name;

	@Singular
	@JsonProperty
	private Map<Integer, String> elections = new HashMap<>();

	/**
	 * Number of CSMs in which this alumnus won a seat, including seats later resigned.
	 * Replacements and CCP picks are not counted.
	 */
	public long electedCount() {
		return elections.values().stream()
				.filter(t -> t.equals("elected") || t.equals("resigned"))
				.count();
	}
}
