package com.autonomouslogic.evecsmanalysis.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

@RequiredArgsConstructor
public abstract class AbstractParser {
	protected final List<String> lines;
	protected int lineIndex;

	public AbstractParser(@NonNull File file) {
		this(readFile(file));
	}

	public AbstractParser(@NonNull String contents) {
		this(readLines(contents));
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

	protected void initParser() {
		lineIndex = 0;
	}

	@SneakyThrows
	protected Matcher parseLine(Pattern pattern, String error) {
		var line = lines.get(lineIndex);
		return parseLine(line, pattern, error);
	}

	@SneakyThrows
	protected Matcher parseLine(String line, Pattern pattern, String error) {
		var matcher = pattern.matcher(line);
		if (!matcher.matches()) {
			throw new IOException(error + " on line " + (lineIndex + 1) + ": " + line);
		}
		return matcher;
	}
}
