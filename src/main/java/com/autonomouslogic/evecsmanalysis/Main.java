package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.BallotFile;
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
		var votesBlt = new File(csmDir, "votes.blt");
		if (!votesBlt.exists()) {
			throw new FileNotFoundException(votesBlt.toString());
		}
		var votesJson = new File(csmDir, "votes.json");
		var readme = new File(csmDir, "Readme.md");

		var ballotFile = parseBallotFile(votesBlt);
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(votesJson, ballotFile);

		var data = new AnalysisRunner(ballotFile).run();
		new AnalysisRenderer(csmNumber, data).render(readme);
	}

	private static BallotFile parseBallotFile(File file) {
		return new BallotParser(file).parse();
	}

	public static Stream<File> getAllCsmDirs() {
		return Arrays.stream(new File(".").listFiles())
				.filter(f -> f.isDirectory())
				.filter(d -> d.getName().startsWith("csm"))
				.sorted();
	}
}
