package com.autonomouslogic.evecsmanalysis.parser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.NonNull;
import lombok.SneakyThrows;

/**
 * Parses the <code>results.csv</code> files of first-past-the-post elections. The header is <code>Character,Votes</code>
 * and each row is a candidate with their vote count. Names may contain spaces and apostrophes, but not commas.
 */
public class ResultsCsvParser extends AbstractParser {
	private static final String HEADER = "Character,Votes";
	private static final Pattern ROW = Pattern.compile("(?<name>[^,]+),(?<votes>\\d+)");

	public ResultsCsvParser(@NonNull File file) {
		super(file);
	}

	/**
	 * Candidate name to votes, in file order.
	 */
	@SneakyThrows
	public Map<String, Integer> parse() {
		initParser();
		if (lines.isEmpty() || !HEADER.equals(lines.get(0).trim())) {
			throw new IOException("Expected header \"" + HEADER + "\" on line 1");
		}
		var votes = new LinkedHashMap<String, Integer>();
		for (lineIndex = 1; lineIndex < lines.size(); lineIndex++) {
			var line = lines.get(lineIndex);
			if (line.isBlank()) {
				continue;
			}
			var matcher = parseLine(line, ROW, "Invalid row");
			var name = normalizeName(matcher.group("name"));
			if (votes.put(name, Integer.parseInt(matcher.group("votes"))) != null) {
				throw new IOException("Duplicate candidate \"" + name + "\" on line " + (lineIndex + 1));
			}
		}
		return votes;
	}
}
