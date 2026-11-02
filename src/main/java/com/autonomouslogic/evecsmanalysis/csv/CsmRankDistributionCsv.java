package com.autonomouslogic.evecsmanalysis.csv;

import com.autonomouslogic.evecsmanalysis.models.CsmAnalysis;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Long-format rank distribution for one election: one row per candidate per rank.
 */
@RequiredArgsConstructor
public class CsmRankDistributionCsv {
	private final CsmAnalysis analysis;
	private final File file;

	@SneakyThrows
	public void write() {
		try (var writer = new FileWriter(file, StandardCharsets.UTF_8)) {
			writer.write("Candidate,Order,Rank,Votes\n");
			var rows = analysis.getRankDistribution();
			for (int i = 0; i < rows.size(); i++) {
				var row = rows.get(i);
				var label = row.getCandidate();
				var quoted = "\"" + label.replace("\"", "\"\"") + "\"";
				for (int rank = 0; rank < row.getRankVotes().size(); rank++) {
					writer.write("%s,%d,%d,%d\n"
							.formatted(
									quoted, i + 1, rank + 1, row.getRankVotes().get(rank)));
				}
			}
		}
	}
}
