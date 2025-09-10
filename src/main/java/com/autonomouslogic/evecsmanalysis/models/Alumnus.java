package com.autonomouslogic.evecsmanalysis.models;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Alumnus {
	private String name;

	@Singular
	private Map<Integer, String> elections = new HashMap<>();
}
