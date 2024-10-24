package com.autonomouslogic.evecsmanalysis.models;

import java.util.List;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class AuditLog {
	@Singular
	List<Round> rounds;

	@Singular
	List<String> finalResults;
}
