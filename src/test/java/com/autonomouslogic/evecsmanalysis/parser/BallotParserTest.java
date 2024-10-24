package com.autonomouslogic.evecsmanalysis.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.autonomouslogic.evecsmanalysis.Main;
import java.io.File;
import java.io.FileInputStream;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class BallotParserTest {
	@ParameterizedTest
	@MethodSource("csmDirs")
	@SneakyThrows
	public void shouldParseBallotFile(File dir) {
		var file = new File(dir, "votes.blt");
		assertTrue(file.exists());
		var parsed = new BallotParser(file).parse();
		// System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(parsed));
		assertNotNull(parsed);
		String ballotContents;
		try (var in = new FileInputStream(file)) {
			ballotContents = new String(in.readAllBytes());
		}
		assertEquals(ballotContents.replace("\r\n", "\n"), parsed.toString());
	}

	public static Stream<Arguments> csmDirs() {
		return Main.getAllCsmDirs()
				.filter(d -> new File(d, "votes.blt").exists())
				.map(d -> Arguments.of(d));
	}
}
