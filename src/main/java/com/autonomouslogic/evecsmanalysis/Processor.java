package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.AuditLog;
import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import com.autonomouslogic.evecsmanalysis.models.CsmConfig;
import com.autonomouslogic.evecsmanalysis.parser.AuditLogParser;
import com.autonomouslogic.evecsmanalysis.parser.BallotParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

/**
 * Does the actual analysis and stores results a JSON file in the build directory.
 */
@RequiredArgsConstructor
@Log4j2
public class Processor {
	@NonNull
	private final List<CsmConfig> csmConfigs;

	@NonNull
	private final ObjectMapper objectMapper;

	public void run() {
		log.info("Processing {} CSM election files", csmConfigs.size());
		for (var csmConfig : csmConfigs) {
			processCsmDir(csmConfig);
		}
	}

	@SneakyThrows
	private void processCsmDir(CsmConfig csmConfig) {
		log.info("Processing CSM {}", csmConfig.getCsmNumber());
		if (csmConfig.getCsmNumber() < 1) {
			throw new IllegalArgumentException(
					"Invalid CSM number: " + csmConfig.getCsmNumber() + " on dir: " + csmConfig);
		}

		var ballotFile = parseBallotFile(csmConfig.getVotesBlt());
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(csmConfig.getVotesJson(), ballotFile);

		var auditLog = parseAuditLog(csmConfig.getAuditLogTxt());
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(csmConfig.getAuditLogJson(), auditLog);

		var csmAnalysis = new CsmAnalyser(csmConfig, ballotFile, auditLog).run();
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(csmConfig.getAnalysisJson(), csmAnalysis);
		//		new AnalysisRenderer(data).render(readme);
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
