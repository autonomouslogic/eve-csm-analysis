package com.autonomouslogic.evecsmanalysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class BallotParserTest {
	@Test
	@SneakyThrows
	public void shouldParseBallotFile() {
		var file = new File("csm18/votes.blt");
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
}
