package com.autonomouslogic.evecsmanalysis.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class Alumni {
	@JsonProperty
	private Map<String, Alumnus> alumni = new HashMap<>();
}
