package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.Ballot;
import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import com.autonomouslogic.evecsmanalysis.models.Votes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

@RequiredArgsConstructor
public class BallotParser {
	private static final Pattern CANDIDATES = Pattern.compile("(?<candidates>\\d+) (?<seats>\\d+)");
	private static final Pattern VOTES = Pattern.compile("(?<count>\\d+)((?: \\d+)+)");

	private final List<String> lines;
	private int lineIndex;
	private BallotFile.BallotFileBuilder ballotFile;

	public BallotParser(File file) {
		this(readFile(file));
	}

	public BallotParser(String contents) {
		this(readLines(contents));
	}

	@SneakyThrows
	public BallotFile parse() {
		lineIndex = 0;
		ballotFile = BallotFile.builder();
		parseCandidates();
		// parseWithdrawals();
		parseVotes();
		return ballotFile.build();
	}

	@SneakyThrows
	private void parseCandidates() {
		var matcher = parseLine(CANDIDATES, "Invalid candidates");
		ballotFile.candidates(Integer.parseInt(matcher.group("candidates")));
		ballotFile.seats(Integer.parseInt(matcher.group("seats")));
		lineIndex++;
	}

	private void parseWithdrawals() {
		// @todo
	}

	@SneakyThrows
	private void parseVotes() {
		nextVotes();
		while (true) {
			try {
				nextVotes();
			} catch (IOException e) {
				lineIndex--;
				break;
			}
		}
	}

	private void nextVotes() throws IOException {
		var matcher = parseLine(VOTES, "Invalid votes");
		var votes = Votes.builder();
		var ballot = Ballot.builder();
		votes.count(Integer.parseInt(matcher.group("count")));
		var rankings = Arrays.stream(matcher.group(2).split(" "))
				.filter(s -> !s.isBlank())
				.map(Integer::parseInt)
				.toList();
		ballot.rankings(rankings);
		votes.ballot(ballot.build());
		ballotFile.allVote(votes.build());
		lineIndex++;
	}

	@SneakyThrows
	private Matcher parseLine(Pattern candidates, String error) {
		var line = lines.get(lineIndex);
		var matcher = candidates.matcher(line);
		if (!matcher.matches()) {
			throw new IOException(error + " on line " + (lineIndex + 1) + ": " + line);
		}
		return matcher;
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
