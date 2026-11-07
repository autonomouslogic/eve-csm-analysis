package com.autonomouslogic.evecsmanalysis.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.autonomouslogic.evecsmanalysis.models.Alumnus;
import com.autonomouslogic.evecsmanalysis.models.CandidateRankDistribution;
import com.autonomouslogic.evecsmanalysis.models.CsmAnalysis;
import com.autonomouslogic.evecsmanalysis.models.CsmData;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AlumnusRankDistributionCsvTest {
	@TempDir
	Path tempDir;

	@Test
	public void shouldWriteOnlyTheAlumnusRowsForElectionsRunIn() throws Exception {
		var file = tempDir.resolve("a.csv").toFile();
		var alumnus = alumnus("Ali Aras", 20, 21);
		var written = new AlumnusRankDistributionCsv(
						alumnus, List.of(csm(19, 100, "Ali Aras", 1, 2), csm(20, 100, "Ali Aras", 10, 5)), file)
				.write();
		assertEquals(true, written);
		assertEquals(
				"Election,Number,Rank,Votes,Share\nCSM 20,20,1,10,0.100000\nCSM 20,20,2,5,0.050000\n", read(file));
	}

	@Test
	public void shouldNotWriteFileWithoutDistribution() {
		var file = tempDir.resolve("a.csv").toFile();
		var csm = CsmData.builder()
				.csmNumber(5)
				.analysis(CsmAnalysis.builder().csmNumber(5).build())
				.build();
		assertFalse(new AlumnusRankDistributionCsv(alumnus("Ali Aras", 5), List.of(csm), file).write());
		assertFalse(file.exists());
	}

	private static Alumnus alumnus(String name, int... runs) {
		var alumnus = new Alumnus().setName(name);
		for (int run : runs) {
			alumnus.getRuns().put(run, "elected");
		}
		return alumnus;
	}

	private static CsmData csm(int number, int total, String candidate, Integer... ranks) {
		return CsmData.builder()
				.csmNumber(number)
				.analysis(CsmAnalysis.builder()
						.csmNumber(number)
						.totalVotes(total)
						.rankDistribution(List.of(
								CandidateRankDistribution.builder()
										.candidate(candidate)
										.rankVotes(List.of(ranks))
										.build(),
								CandidateRankDistribution.builder()
										.candidate("Other")
										.rankVotes(List.of(1))
										.build()))
						.build())
				.build();
	}

	private static String read(File file) throws Exception {
		return Files.readString(file.toPath(), StandardCharsets.UTF_8);
	}
}
