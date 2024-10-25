package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.CsmAnalysis;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@RequiredArgsConstructor
@Log4j2
public class AnalysisRenderer {
	@NonNull
	private final CsmAnalysis data;

	@SneakyThrows
	public void render(File outputFile) {
		log.info("Rendering analysis to: {}", outputFile);
		var engine = createEngine();
		try (var writer = new FileWriter(outputFile, StandardCharsets.UTF_8)) {
			engine.process("analysis.md", createContext(), writer);
		}
	}

	private IContext createContext() {
		var context = new Context(Locale.ENGLISH);
		context.setVariable("data", data);
		return context;
	}

	private TemplateEngine createEngine() {
		var engine = new TemplateEngine();
		engine.addTemplateResolver(createResolver(TemplateMode.TEXT, true, ""));
		return engine;
	}

	private static ClassLoaderTemplateResolver createResolver(
			TemplateMode mode, boolean checkExistence, String suffix) {
		var resolver = new ClassLoaderTemplateResolver();
		resolver.setCheckExistence(checkExistence);
		resolver.setPrefix("/templates/");
		if (suffix != null) {
			resolver.setSuffix(suffix);
		}
		resolver.setTemplateMode(mode);
		resolver.setCacheable(true);
		resolver.setCacheTTLMs(Duration.ofDays(1000).toMillis());
		return resolver;
	}
}
