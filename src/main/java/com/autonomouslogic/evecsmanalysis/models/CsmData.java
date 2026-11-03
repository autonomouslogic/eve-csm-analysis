package com.autonomouslogic.evecsmanalysis.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Extra data about a CSM election.
 */
@Value
@Builder(toBuilder = true)
@Jacksonized
public class CsmData {
	@JsonIgnore
	int csmNumber;

	/**
	 * Years the CSM served, as free text, e.g. {@code 2025-2026}. Optional.
	 */
	String years;

	@Builder.Default
	List<String> ccpPicks = List.of();

	@Builder.Default
	Map<String, String> urls = Map.of();

	@JsonIgnore
	CsmAnalysis analysis;
}
