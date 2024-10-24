package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Arrays;
import java.util.stream.Stream;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

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

	public static Stream<File> getAllCsmDirs() {
		return Arrays.stream(new File(".").listFiles())
			.filter(f -> f.isDirectory())
			.filter(d -> d.getName().startsWith("csm"))
			.sorted();
	}
}
