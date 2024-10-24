package com.autonomouslogic.evecsmanalysis.parser;

import com.autonomouslogic.evecsmanalysis.models.AuditLog;
import com.autonomouslogic.evecsmanalysis.models.Round;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.NonNull;
import lombok.SneakyThrows;

public class AuditLogParser extends AbstractParser {
	private static final Pattern ROUND_HEADER =
			Pattern.compile("Round (?<round>\\d+) beginning - (?<candidates>\\d+) candidates remain");
	private static final Pattern ROUND_VOTES =
			Pattern.compile("(?<votes>\\d+(\\.\\d+)?) votes, (?<quota>\\d+(\\.\\d+)?) quota");
	private static final Pattern TALLEY =
			Pattern.compile("  (?<votes>\\d+(\\.\\d+)?) \"(?<candidate>[a-zA-Z0-9 _-]+)\"");

	private AuditLog.AuditLogBuilder auditLog;

	public AuditLogParser(List<String> lines) {
		super(lines);
	}

	public AuditLogParser(@NonNull File file) {
		super(file);
	}

	public AuditLogParser(@NonNull String contents) {
		super(contents);
	}

	public AuditLog parse() {
		initParser();
		auditLog = AuditLog.builder();
		parseRounds();
		return auditLog.build();
	}

	private void parseRounds() {
		parseRound();
	}

	private void parseRound() {
		var round = Round.builder();
		parseRoundCandidates(round);
		parseRoundVotes(round);
		parseInitialTalley(round);
		auditLog.round(round.build());
	}

	private void parseRoundCandidates(Round.RoundBuilder round) {
		var matcher = parseLine(ROUND_HEADER, "Invalid round header");
		round.roundNumber(Integer.parseInt(matcher.group("round")));
		round.candidatesLeft(Integer.parseInt(matcher.group("candidates")));
		lineIndex++;
	}

	private void parseRoundVotes(Round.RoundBuilder round) {
		var matcher = parseLine(ROUND_VOTES, "Invalid round votes");
		round.votesCount(Integer.parseInt(matcher.group("votes")));
		round.quota(Double.parseDouble(matcher.group("quota")));
		lineIndex++;
	}

	@SneakyThrows
	private void parseInitialTalley(Round.RoundBuilder round) {
		if (!lines.get(lineIndex).equals("Initial talley:")) {
			throw new IllegalArgumentException(
					"Invalid initial talley on line" + (lineIndex + 1) + ": " + lines.get(lineIndex));
		}
		lineIndex++;
		round.initialTalley(parseTalley());
	}

	private Map<String, Double> parseTalley() {
		var map = new LinkedHashMap<String, Double>();
		String line;
		while ((line = lines.get(lineIndex)).startsWith("  ")) {
			var matcher = parseLine(line, TALLEY, "Invalid talley");
			map.put(matcher.group("candidate"), Double.parseDouble(matcher.group("votes")));
			lineIndex++;
		}
		return map;
	}
}
