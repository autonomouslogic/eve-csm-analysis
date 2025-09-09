package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.CsmConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Ordering;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@SneakyThrows
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Exactly one arg expected");
			System.exit(1);
		}
		var csmConfigs = createCsmConfig();
		var command = args[0];
		switch (command) {
			case "process":
				new Processor(csmConfigs, objectMapper).run();
				break;
			case "thymeleaf":
				new AnalysisRenderer(csmConfigs, objectMapper).renderAll();
				break;
			default:
				System.err.println("Unknown command: " + command);
				System.exit(1);
		}
	}

	public static File requiredFile(File csmDir, String child) throws FileNotFoundException {
		var file = new File(csmDir, child);
		if (!file.exists()) {
			throw new FileNotFoundException(file.toString());
		}
		return file;
	}

	@SneakyThrows
	private static List<CsmConfig> createCsmConfig() {
		var buildDir = new File("build", "csm");
		if (!buildDir.exists()) {
			buildDir.mkdirs();
		}
		var docsDir = new File("docs");
		if (!docsDir.exists()) {
			throw new FileNotFoundException(docsDir.toString());
		}
		return getAllCsmDirs()
				.map(csmDir -> {
					return CsmConfig.builder()
							.csmNumber(Integer.parseInt(csmDir.getName().substring(3)))
							.csmDir(csmDir)
							.csmData(new File(csmDir, "csm.json"))
							.talleyScriptFile(new File(csmDir, "WrightTalley.py"))
							.votesBlt(new File(csmDir, "votes.blt"))
							.auditLogTxt(new File(csmDir, "auditLog.txt"))
							.votesJson(new File(buildDir, csmDir.getName() + "-votes.json"))
							.auditLogJson(new File(buildDir, csmDir.getName() + "-auditLog.json"))
							.analysisJson(new File(buildDir, csmDir.getName() + "-analysis.json"))
							.markdownFile(new File(docsDir, csmDir.getName() + ".md"))
							.build();
				})
				.sorted(Ordering.natural().onResultOf(CsmConfig::getCsmNumber))
				.toList();
	}

	public static Stream<File> getAllCsmDirs() {
		return Arrays.stream(new File(".").listFiles())
				.filter(f -> f.isDirectory())
				.filter(d -> d.getName().startsWith("csm"))
				.sorted();
	}
}
