package com.autonomouslogic.evecsmanalysis.parser;

import com.autonomouslogic.evecsmanalysis.models.AuditLog;
import com.autonomouslogic.evecsmanalysis.models.Elimination;
import com.autonomouslogic.evecsmanalysis.models.Round;
import com.autonomouslogic.evecsmanalysis.models.VotesTransfer;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.NonNull;
import lombok.SneakyThrows;

public class AuditLogParser extends AbstractParser {
	private static final String name = "[a-zA-Z0-9 '_-]+";
	private static final String decimal = "\\d+(\\.\\d+)?";

	private static final Pattern ROUND_HEADER =
			Pattern.compile("Round (?<round>\\d+) beginning - (?<candidates>\\d+) candidates remain");
	private static final Pattern ROUND_VOTES =
			Pattern.compile("(?<votes>" + decimal + ") votes, (?<quota>" + decimal + ") quota");
	private static final Pattern TALLEY = Pattern.compile("  (?<votes>" + decimal + ") \"(?<candidate>" + name + ")\"");
	private static final Pattern ELECTED = Pattern.compile("  Elected: \"(?<candidate>" + name + ")\"");
	private static final Pattern TRANSFER_FROM = Pattern.compile("  Transfer from \"(?<candidate>" + name + ")\":");
	private static final Pattern TRANSFER_VOTES = Pattern.compile("    Votes: (?<votes>" + decimal
			+ "), Factor: (?<factor>" + decimal + "), Excess: (?<excess>" + decimal + ")");
	private static final Pattern TRANSFER_TO =
			Pattern.compile("    (?<votes>" + decimal + ") votes to \"?(?<candidate>" + name + ")\"?");
	private static final Pattern ELIMINATION =
			Pattern.compile("  Elimination: \"(?<candidate>" + name + ")\" with (?<votes>" + decimal + ") votes");

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
		parseFinalResult();
		return auditLog.build();
	}

	private void parseRounds() {
		String line;
		while ((line = lines.get(lineIndex)).startsWith("Round ")) {
			parseRound();
			if (lines.get(lineIndex).equals("")) {
				lineIndex++;
			}
		}
	}

	private void parseRound() {
		var round = Round.builder();
		parseRoundCandidates(round);
		parseRoundVotes(round);
		parseInitialTalley(round);
		parseActions(round);
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
			var matcher = TALLEY.matcher(line);
			if (!matcher.matches()) {
				break;
			}
			map.put(matcher.group("candidate"), Double.parseDouble(matcher.group("votes")));
			lineIndex++;
		}
		return map;
	}

	@SneakyThrows
	private void parseActions(Round.RoundBuilder round) {
		if (!lines.get(lineIndex).equals("Actions:")) {
			throw new IllegalArgumentException(
					"Invalid actions on line" + (lineIndex + 1) + ": " + lines.get(lineIndex));
		}
		lineIndex++;
		parseElectedCandidates(round);
		parseTransfers(round);
		parsePreEliminationTalley(round);
		parseElimination(round);
	}

	private void parseElectedCandidates(Round.RoundBuilder round) {
		String line;
		while ((line = lines.get(lineIndex)).startsWith("  Elected: ")) {
			var matcher = parseLine(line, ELECTED, "Invalid elected candidate");
			round.electedCandidate(matcher.group("candidate"));
			lineIndex++;
		}
	}

	private void parseTransfers(Round.RoundBuilder round) {
		String line;
		while ((line = lines.get(lineIndex)).startsWith("  Transfer from ")) {
			var transfer = VotesTransfer.builder();
			parseTransferFrom(transfer);
			parseTransferVotes(transfer);
			parseTransferLines(transfer);
			round.votesTransfer(transfer.build());
			parseElectedCandidates(round);
		}
	}

	private void parseTransferFrom(VotesTransfer.VotesTransferBuilder transfer) {
		var matcher = parseLine(TRANSFER_FROM, "Invalid transfer from");
		transfer.fromCandidate(matcher.group("candidate"));
		lineIndex++;
	}

	private void parseTransferVotes(VotesTransfer.VotesTransferBuilder transfer) {
		var matcher = parseLine(TRANSFER_VOTES, "Invalid transfer votes");
		transfer.votes(Double.parseDouble(matcher.group("votes")));
		transfer.factor(Double.parseDouble(matcher.group("factor")));
		transfer.excess(Double.parseDouble(matcher.group("excess")));
		lineIndex++;
	}

	private void parseTransferLines(VotesTransfer.VotesTransferBuilder transfer) {
		while (true) {
			var line = lines.get(lineIndex);
			var matcher = TRANSFER_TO.matcher(line);
			if (!matcher.matches()) {
				break;
			}
			transfer.toCandidate(matcher.group("candidate"), Double.parseDouble(matcher.group("votes")));
			lineIndex++;
		}
	}

	@SneakyThrows
	private void parsePreEliminationTalley(Round.RoundBuilder round) {
		if (!lines.get(lineIndex).equals("  Pre-elimination tally:")) {
			throw new IllegalArgumentException(
					"Invalid pre-elimination talley on line " + (lineIndex + 1) + ": " + lines.get(lineIndex));
		}
		lineIndex++;
		round.preEliminationTalley(parseTalley());
	}

	private void parseElimination(Round.RoundBuilder round) {
		var matcher = parseLine(ELIMINATION, "Invalid elimination");
		round.elimination(Elimination.builder()
				.candidate(matcher.group("candidate"))
				.votes(Double.parseDouble(matcher.group("votes")))
				.build());
		lineIndex++;
	}

	@SneakyThrows
	private void parseFinalResult() {
		if (!lines.get(lineIndex).equals("Result:")) {
			throw new IOException();
		}
		lineIndex++;
		while (lineIndex < lines.size()) {
			var line = lines.get(lineIndex);
			if (!line.startsWith("\"") || !line.endsWith("\"")) {
				break;
			}
			auditLog.finalResult(line.substring(1, line.length() - 1));
			lineIndex++;
		}
	}
}
