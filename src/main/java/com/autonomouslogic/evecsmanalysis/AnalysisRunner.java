package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnalysisRunner {
	@NonNull
	private final BallotFile ballotFile;

	public Map<String, Object> run() {
		return new LinkedHashMap<>();
	}
}
