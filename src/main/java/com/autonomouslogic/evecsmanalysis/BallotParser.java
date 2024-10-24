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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

@RequiredArgsConstructor
public class BallotParser {
	private static final Pattern CANDIDATES = Pattern.compile("(?<candidates>\\d+) (?<seats>\\d+)");

	// The standard defined "Each such list must end with a zero", but the EVE implementation does not do this.
	private static final Pattern VOTES = Pattern.compile("(?<count>\\d+)(?<rankings>( \\d+)+)");

	private final List<String> lines;
	private int lineIndex;
	private BallotFile.BallotFileBuilder ballotFile;

	public BallotParser(@NonNull File file) {
		this(readFile(file));
	}

	public BallotParser(@NonNull String contents) {
		this(readLines(contents));
	}

	@SneakyThrows
	public BallotFile parse() {
		lineIndex = 0;
		ballotFile = BallotFile.builder();
		parseHeader();
		// parseWithdrawals();
		parseVotes();
		parseCandidates();
		parseName();
		return ballotFile.build();
	}

	@SneakyThrows
	private void parseHeader() {
		var matcher = parseLine(CANDIDATES, "Invalid candidates");
		ballotFile.candidateCount(Integer.parseInt(matcher.group("candidates")));
		ballotFile.seatCount(Integer.parseInt(matcher.group("seats")));
		lineIndex++;
	}

	private void parseWithdrawals() {
		// @todo
	}

	@SneakyThrows
	private void parseVotes() {
		do {
			nextVotes();
		} while (!lines.get(lineIndex).equals("0"));
		lineIndex++;
	}

	private void nextVotes() throws IOException {
		var matcher = parseLine(VOTES, "Invalid votes");
		var votes = Votes.builder();
		var ballot = Ballot.builder();
		votes.count(Integer.parseInt(matcher.group("count")));
		var rankings = Arrays.stream(matcher.group("rankings").split(" "))
				.filter(s -> !s.isBlank())
				.map(Integer::parseInt)
				.toList();
		ballot.rankings(rankings);
		votes.ballot(ballot.build());
		ballotFile.allVote(votes.build());
		lineIndex++;
	}

	@SneakyThrows
	private void parseCandidates() {
		var candidates = ballotFile.build().getCandidateCount();
		for (int i = 0; i < candidates; i++) {
			var line = lines.get(lineIndex);
			if (!line.startsWith("\"") || !line.endsWith("\"")) {
				throw new IOException("Invalid candidate on line " + (lineIndex + 1) + ": " + line);
			}
			ballotFile.candidateName(i + 1, line.substring(1, line.length() - 1));
			lineIndex++;
		}
	}

	@SneakyThrows
	private void parseName() {
		ballotFile.name(lines.get(lineIndex));
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
