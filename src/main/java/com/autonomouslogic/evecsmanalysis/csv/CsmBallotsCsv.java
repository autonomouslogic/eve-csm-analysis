package com.autonomouslogic.evecsmanalysis.csv;

import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import com.autonomouslogic.evecsmanalysis.models.Votes;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * The ballots of one election as in the <code>.blt</code> file, with candidate numbers replaced by names: one row per
 * distinct ballot, with its ballot count and the ranked candidates. Rows are padded to the longest ballot.
 */
@RequiredArgsConstructor
public class CsmBallotsCsv {
	private final BallotFile ballotFile;
	private final File file;

	@SneakyThrows
	public void write() {
		var votes = ballotFile.getAllVotes();
		var maxRanks = votes.stream()
				.map(v -> v.getBallot().getRankings().size())
				.max(Comparator.naturalOrder())
				.orElse(0);
		try (var writer = new FileWriter(file, StandardCharsets.UTF_8)) {
			var header = new StringBuilder("Ballots");
			for (int rank = 1; rank <= maxRanks; rank++) {
				header.append(",Rank ").append(rank);
			}
			writer.write(header + "\n");
			for (Votes v : votes) {
				var line = new StringBuilder().append(v.getCount());
				var rankings = v.getBallot().getRankings();
				for (int i = 0; i < maxRanks; i++) {
					line.append(',');
					if (i < rankings.size()) {
						line.append(escape(name(rankings.get(i))));
					}
				}
				writer.write(line + "\n");
			}
		}
	}

	private String name(int candidate) {
		var name = ballotFile.getCandidateNames().get(candidate);
		return name != null ? name : "#" + candidate;
	}

	private static String escape(String value) {
		if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
			return "\"" + value.replace("\"", "\"\"") + "\"";
		}
		return value;
	}
}
