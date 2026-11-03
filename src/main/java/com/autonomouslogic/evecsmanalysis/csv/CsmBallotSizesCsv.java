package com.autonomouslogic.evecsmanalysis.csv;

import com.autonomouslogic.evecsmanalysis.models.CsmAnalysis;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Ballots per ballot size for one election: one row per number of ranks defined.
 */
@RequiredArgsConstructor
public class CsmBallotSizesCsv {
	private final CsmAnalysis analysis;
	private final File file;

	@SneakyThrows
	public void write() {
		try (var writer = new FileWriter(file, StandardCharsets.UTF_8)) {
			writer.write("Ranks,Ballots\n");
			var sizes = analysis.getBallotSizes();
			// Index 0 is ballots defining no ranks, which are not shown.
			for (int ranks = 1; ranks < sizes.size(); ranks++) {
				writer.write("%d,%d\n".formatted(ranks, sizes.get(ranks)));
			}
		}
	}
}
