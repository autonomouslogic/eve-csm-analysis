package com.autonomouslogic.evecsmanalysis.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
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
	 * Every election this alumnus ran in, newest first. Holds the seat statuses from {@link #elections}, plus
	 * {@code eliminated} and {@code withdrawn} for runs that did not win a seat. Seat statuses take precedence.
	 */
	@JsonIgnore
	private Map<Integer, String> runs = new TreeMap<>(Collections.reverseOrder());

	/**
	 * URL slug for the alumnus page, e.g. {@code T'Amber} becomes {@code tamber}.
	 */
	public String slug() {
		return name.toLowerCase(Locale.ROOT)
				.replace("'", "")
				.replaceAll("[^a-z0-9]+", "-")
				.replaceAll("^-+|-+$", "");
	}

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
