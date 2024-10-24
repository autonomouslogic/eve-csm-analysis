package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import lombok.SneakyThrows;

public class Main {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@SneakyThrows
	public static void main(String[] args) {
		var ballotFile = parseBallotFile(new File("csm18/votes.blt"));
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("csm18/votes.json"), ballotFile);
		var data = new AnalysisRunner(ballotFile).run();
		new AnalysisRenderer(18, data).render(new File("csm18/Readme.md"));
	}

	private static BallotFile parseBallotFile(File file) {
		return new BallotParser(file).parse();
	}
}
