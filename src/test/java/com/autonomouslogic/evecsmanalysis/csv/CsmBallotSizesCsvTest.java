package com.autonomouslogic.evecsmanalysis.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.autonomouslogic.evecsmanalysis.models.CsmAnalysis;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class CsmBallotSizesCsvTest {
	@TempDir
	Path tempDir;

	@Test
	public void shouldOmitZeroRankRowAndStartAtOneRank() throws Exception {
		var file = tempDir.resolve("ballot-sizes.csv").toFile();
		new CsmBallotSizesCsv(
						CsmAnalysis.builder().ballotSizes(List.of(0, 1, 1, 2)).build(), file)
				.write();
		assertEquals("Ranks,Ballots\n1,1\n2,1\n3,2\n", read(file));
	}

	private static String read(File file) throws Exception {
		return Files.readString(file.toPath(), StandardCharsets.UTF_8);
	}
}
