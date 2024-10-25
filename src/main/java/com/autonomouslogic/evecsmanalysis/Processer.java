package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.AuditLog;
import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import com.autonomouslogic.evecsmanalysis.parser.AuditLogParser;
import com.autonomouslogic.evecsmanalysis.parser.BallotParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

/**
 * Does the actual analysis and stores results a JSON file in the build directory.
 */
@RequiredArgsConstructor
@Log4j2
public class Processer {
	private final ObjectMapper objectMapper;

	public void run() {
		log.info("Starting analysis");
		var dirs = Main.getAllCsmDirs().toList();
		log.info("Found {} CSM dirs: {}", dirs.size(), dirs);
		for (var csmDir : dirs) {
			processCsmDir(csmDir);
		}
	}

	@SneakyThrows
	private void processCsmDir(File csmDir) {
		log.info("Processing CSM dir: {}", csmDir);
		var csmNumber = Integer.parseInt(csmDir.getName().substring(3));
		if (csmNumber < 1) {
			throw new IllegalArgumentException("Invalid CSM number: " + csmNumber + " on dir: " + csmDir);
		}
		var votesBlt = Main.requiredFile(csmDir, "votes.blt");
		var votesJson = new File(csmDir, "votes.json");
		var auditLogTxt = Main.requiredFile(csmDir, "auditLog.txt");
		var auditLogJson = new File(csmDir, "auditLog.json");
		var readme = new File(csmDir, "Readme.md");

		var ballotFile = parseBallotFile(votesBlt);
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(votesJson, ballotFile);

		var auditLog = parseAuditLog(auditLogTxt);
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(auditLogJson, auditLog);

		var data = new AnalysisRunner(csmDir, csmNumber, ballotFile, auditLog).run();
		new AnalysisRenderer(data).render(readme);
	}

	private BallotFile parseBallotFile(File file) {
		log.info("Parsing ballot file: {}", file);
		return new BallotParser(file).parse();
	}

	private AuditLog parseAuditLog(File file) {
		log.info("Parsing audit log: {}", file);
		return new AuditLogParser(file).parse();
	}
}
