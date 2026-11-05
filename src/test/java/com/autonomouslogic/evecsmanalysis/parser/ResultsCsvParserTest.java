package com.autonomouslogic.evecsmanalysis.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ResultsCsvParserTest {
	@TempDir
	File tempDir;

	@Test
	public void shouldParseVotesInFileOrder() throws IOException {
		var expected = new LinkedHashMap<String, Integer>();
		expected.put("The Mittani", 10058);
		expected.put("T'Amber", 696);
		assertEquals(expected, parse("Character,Votes\nThe Mittani,10058\nT'Amber,696\n"));
	}

	@Test
	public void shouldRejectMissingHeader() throws IOException {
		assertThrows(IOException.class, () -> parse("The Mittani,10058\n"));
	}

	@Test
	public void shouldRejectDuplicateCandidate() throws IOException {
		assertThrows(IOException.class, () -> parse("Character,Votes\nAlice,1\nAlice,2\n"));
	}

	private Map<String, Integer> parse(String contents) throws IOException {
		var file = new File(tempDir, "results.csv");
		Files.writeString(file.toPath(), contents);
		return new ResultsCsvParser(file).parse();
	}
}
