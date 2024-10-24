package com.autonomouslogic.evecsmanalysis;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BallotParserTest {
	@Test
	@SneakyThrows
	public void shouldParseBallotFile() {
		var file = new File("csm18/votes.blt");
		assertTrue(file.exists());
		var parsed = new BallotParser(file).parse();
		assertNotNull(parsed);
		String ballotContents;
		try (var in = new FileInputStream(file)) {
			ballotContents = new String(in.readAllBytes());
		}
		assertEquals(ballotContents, parsed.toString());
	}
}
