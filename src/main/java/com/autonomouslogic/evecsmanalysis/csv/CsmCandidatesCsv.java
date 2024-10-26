package com.autonomouslogic.evecsmanalysis.csv;

import com.autonomouslogic.evecsmanalysis.models.CsmAnalysis;
import com.google.common.collect.Ordering;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
public class CsmCandidatesCsv {
	private final List<CsmAnalysis> allAnalysis;
	private final File file;

	@SneakyThrows
	public void write() {
		try (var writer = new FileWriter(file, StandardCharsets.UTF_8)) {
			writer.write("CSM,Candidates\n");
			var csms = allAnalysis.stream().sorted(Ordering.natural().onResultOf(CsmAnalysis::getCsmNumber)).toList();
			for (var csm : csms) {
				writer.write("%s,%s\n".formatted(csm.getCsmNumber(), csm.getCandidateCount()));
			}
		}
	}
}
