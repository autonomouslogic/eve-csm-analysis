package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.CsmAnalysis;
import com.autonomouslogic.evecsmanalysis.models.CsmConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
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
	private final List<CsmConfig> csmConfigs;

	@NonNull
	private final ObjectMapper objectMapper;

	public void renderAll() {
		for (var csmConfig : csmConfigs) {
			render(csmConfig);
		}
	}

	@SneakyThrows
	public void render(CsmConfig csmConfig) {
		log.info("Rendering analysis for {} to {}", csmConfig.getCsmNumber(), csmConfig.getMarkdownFile());
		var engine = createEngine();
		try (var writer = new FileWriter(csmConfig.getMarkdownFile(), StandardCharsets.UTF_8)) {
			engine.process("analysis.md", createContext(csmConfig), writer);
		}
	}

	@SneakyThrows
	private IContext createContext(CsmConfig csmConfig) {
		var analysis = objectMapper.readValue(csmConfig.getAnalysisJson(), CsmAnalysis.class);
		var context = new Context(Locale.ENGLISH);
		context.setVariable("data", analysis);
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
