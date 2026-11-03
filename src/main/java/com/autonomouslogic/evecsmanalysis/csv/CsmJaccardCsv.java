package com.autonomouslogic.evecsmanalysis.csv;

import com.autonomouslogic.evecsmanalysis.models.CsmAnalysis;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Long-format Jaccard index for one election: one row per pair of candidates, including each candidate with itself.
 * Pairs with no index value are left out.
 */
@RequiredArgsConstructor
public class CsmJaccardCsv {
	private final CsmAnalysis analysis;
	private final File file;

	@SneakyThrows
	public void write() {
		try (var writer = new FileWriter(file, StandardCharsets.UTF_8)) {
			writer.write("Candidate,Order,Other,OtherOrder,Jaccard\n");
			var names = analysis.getRankDistribution();
			var index = analysis.getJaccardIndex();
			for (int a = 0; a < names.size(); a++) {
				for (int b = 0; b < names.size(); b++) {
					var value = index.get(a).get(b);
					if (value == null) {
						continue;
					}
					writer.write("%s,%d,%s,%d,%s\n"
							.formatted(
									quote(names.get(a).getCandidate()),
									a + 1,
									quote(names.get(b).getCandidate()),
									b + 1,
									value));
				}
			}
		}
	}

	private static String quote(String label) {
		return "\"" + label.replace("\"", "\"\"") + "\"";
	}
}
