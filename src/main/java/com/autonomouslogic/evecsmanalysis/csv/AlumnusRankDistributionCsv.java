package com.autonomouslogic.evecsmanalysis.csv;

import com.autonomouslogic.evecsmanalysis.models.Alumnus;
import com.autonomouslogic.evecsmanalysis.models.CsmData;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Long-format rank distribution for one alumnus: one row per election run per rank. Elections without ballot data
 * (first-past-the-post) and elections where the alumnus has no distribution (e.g. withdrawn) are left out.
 */
@RequiredArgsConstructor
public class AlumnusRankDistributionCsv {
	private final Alumnus alumnus;
	private final List<CsmData> csms;
	private final File file;

	/**
	 * Writes the file, unless there is nothing to write.
	 *
	 * @return whether the file was written
	 */
	@SneakyThrows
	public boolean write() {
		var rows = rows();
		if (rows.isEmpty()) {
			return false;
		}
		try (var writer = new FileWriter(file, StandardCharsets.UTF_8)) {
			writer.write("Election,Number,Rank,Votes,Share\n");
			for (var row : rows) {
				writer.write(row);
			}
		}
		return true;
	}

	private List<String> rows() {
		var rows = new ArrayList<String>();
		for (var csm : csms) {
			if (!alumnus.getRuns().containsKey(csm.getCsmNumber())) {
				continue;
			}
			var analysis = csm.getAnalysis();
			var distribution = analysis.getRankDistribution();
			if (distribution == null) {
				continue;
			}
			for (var candidate : distribution) {
				if (!candidate.getCandidate().equals(alumnus.getName())) {
					continue;
				}
				var ranks = candidate.getRankVotes();
				for (int rank = 0; rank < ranks.size(); rank++) {
					var votes = ranks.get(rank);
					var share = analysis.getTotalVotes() == 0 ? 0.0 : (double) votes / analysis.getTotalVotes();
					rows.add("CSM %d,%d,%d,%d,%.6f\n"
							.formatted(csm.getCsmNumber(), csm.getCsmNumber(), rank + 1, votes, share));
				}
			}
		}
		return rows;
	}
}
