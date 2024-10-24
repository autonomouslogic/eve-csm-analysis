package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class BallotParser {
	private static final Pattern CANDIDATES = Pattern.compile("(\\d+) (\\d+)");

	private final List<String> lines;
	private int lineNumber;
	private BallotFile.BallotFileBuilder ballotFile;

	public BallotParser(File file) {
		this(readFile(file));
	}

	public BallotParser(String contents) {
		this(readLines(contents));
	}

	@SneakyThrows
	public BallotFile parse() {
		lineNumber = 0;
		ballotFile = BallotFile.builder();
		parseCandidates();
		return ballotFile.build();
	}

	@SneakyThrows
	private void parseCandidates() {
		var line = lines.get(lineNumber);
		var matcher = CANDIDATES.matcher(line);
		if (!matcher.matches()) {
			throw new IOException("Invalid candidates on line " + lineNumber + ": " + line);
		}
		ballotFile.candidates(Integer.parseInt(matcher.group(1)));
		ballotFile.seats(Integer.parseInt(matcher.group(2)));
		lineNumber++;
	}

	@SneakyThrows
	private static String readFile(File file) {
		try (var in = new FileInputStream(file)) {
			return IOUtils.toString(in, StandardCharsets.UTF_8);
		}
	}

	@SneakyThrows
	private static List<String> readLines(String contents) {
		try (var reader = new BufferedReader(new StringReader(contents))) {
			return reader.lines().toList();
		}
	}
}
