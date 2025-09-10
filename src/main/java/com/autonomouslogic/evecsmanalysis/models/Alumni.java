package com.autonomouslogic.evecsmanalysis.models;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class Alumni {
	private Map<String, Alumnus> alumni = new HashMap<>();
}
