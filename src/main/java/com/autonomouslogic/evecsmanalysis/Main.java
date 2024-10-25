package com.autonomouslogic.evecsmanalysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
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
		var command = args[0];
		switch (command) {
			case "process":
				new Processer(objectMapper).run();
				break;
			case "thymeleaft":
				new Processer(objectMapper).run();
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

	public static Stream<File> getAllCsmDirs() {
		return Arrays.stream(new File(".").listFiles())
				.filter(f -> f.isDirectory())
				.filter(d -> d.getName().startsWith("csm"))
				.sorted();
	}
}
