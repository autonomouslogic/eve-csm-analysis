package com.autonomouslogic.evecsmanalysis;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnalysisRunner {
	private final File ballotFile;

	public Map<String, Object> run() {
		return new LinkedHashMap<>();
	}
}
