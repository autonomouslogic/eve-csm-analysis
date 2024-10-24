package com.autonomouslogic.evecsmanalysis.parser;

import com.autonomouslogic.evecsmanalysis.models.Ballot;
import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import com.autonomouslogic.evecsmanalysis.models.Votes;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import lombok.NonNull;
import lombok.SneakyThrows;

public class BallotParser extends AbstractParser {
	private static final Pattern CANDIDATES = Pattern.compile("(?<candidates>\\d+) (?<seats>\\d+)");

	// The standard defined "Each such list must end with a zero", but the EVE implementation does not do this.
	private static final Pattern VOTES = Pattern.compile("(?<count>\\d+)(?<rankings>( \\d+)+)");

	private BallotFile.BallotFileBuilder ballotFile;

	public BallotParser(List<String> lines) {
		super(lines);
	}

	public BallotParser(@NonNull File file) {
		super(file);
	}

	public BallotParser(@NonNull String contents) {
		super(contents);
	}

	@SneakyThrows
	public BallotFile parse() {
		initParser();
		ballotFile = BallotFile.builder();
		parseHeader();
		parseWithdrawals();
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
		// @todo support multiple withdrawals
		var line = lines.get(lineIndex);
		if (!line.startsWith("-")) {
			return;
		}
		var withdrawal = Integer.parseInt(line.substring(1));
		ballotFile.withdrawnCandidate(withdrawal);
		lineIndex++;
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
}
