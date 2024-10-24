package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.AuditLog;
import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import com.autonomouslogic.evecsmanalysis.parser.AuditLogParser;
import com.autonomouslogic.evecsmanalysis.parser.BallotParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.SneakyThrows;

public class Main {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@SneakyThrows
	public static void main(String[] args) {
		for (var csmDir : getAllCsmDirs().toList()) {
			processCsmDir(csmDir);
		}
	}

	@SneakyThrows
	private static void processCsmDir(File csmDir) {
		var csmNumber = Integer.parseInt(csmDir.getName().substring(3));
		if (csmNumber < 1) {
			throw new IllegalArgumentException("Invalid CSM number: " + csmNumber + " on dir: " + csmDir);
		}
		var votesBlt = requiredFile(csmDir, "votes.blt");
		var votesJson = new File(csmDir, "votes.json");
		var auditLogTxt = requiredFile(csmDir, "auditLog.txt");
		var auditLogJson = new File(csmDir, "auditLog.json");
		var readme = new File(csmDir, "Readme.md");

		var ballotFile = parseBallotFile(votesBlt);
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(votesJson, ballotFile);

		var auditLog = parseAuditLog(auditLogTxt);
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(auditLogJson, auditLog);

		var data = new AnalysisRunner(csmNumber, ballotFile, auditLog).run();
		new AnalysisRenderer(data).render(readme);
	}

	private static BallotFile parseBallotFile(File file) {
		return new BallotParser(file).parse();
	}

	private static AuditLog parseAuditLog(File file) {
		return new AuditLogParser(file).parse();
	}

	private static File requiredFile(File csmDir, String child) throws FileNotFoundException {
		var file = new File(csmDir, child);
		if (!file.exists()) {
			throw new FileNotFoundException(file.toString());
		}
		return file;
	}

	public static Stream<File> getAllCsmDirs() {
		return Arrays.stream(new File(".").listFiles())
				.filter(f -> f.isDirectory())
				.filter(d -> d.getName().startsWith("csm"))
				.sorted();
	}
}
