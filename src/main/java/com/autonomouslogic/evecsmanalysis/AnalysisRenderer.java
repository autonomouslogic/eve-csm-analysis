package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.csv.CsmCandidatesCsv;
import com.autonomouslogic.evecsmanalysis.csv.CsmVotesCsv;
import com.autonomouslogic.evecsmanalysis.models.CsmAnalysis;
import com.autonomouslogic.evecsmanalysis.models.CsmConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

	@SneakyThrows
	public void renderAll() {
		var allAnalysis = new ArrayList<CsmAnalysis>();
		for (var csmConfig : csmConfigs) {
			var analysis = objectMapper.readValue(csmConfig.getAnalysisJson(), CsmAnalysis.class);
			allAnalysis.add(analysis);
			renderCsm(csmConfig, analysis);
		}
		renderIndex(allAnalysis);
	}

	@SneakyThrows
	public void renderCsm(CsmConfig csmConfig, CsmAnalysis analysis) {
		log.info("Rendering analysis for {} to {}", csmConfig.getCsmNumber(), csmConfig.getMarkdownFile());
		var engine = createEngine();
		try (var writer = new FileWriter(csmConfig.getMarkdownFile(), StandardCharsets.UTF_8)) {
			engine.process("analysis.md", createCsmContext(analysis), writer);
		}
	}

	@SneakyThrows
	private void renderIndex(List<CsmAnalysis> allAnalysis) {
		new CsmVotesCsv(allAnalysis, new File("docs/data/csm-votes.csv")).write();
		new CsmCandidatesCsv(allAnalysis, new File("docs/data/csm-candidates.csv")).write();

		var indexFile = new File("docs/index2.md");
		log.info("Rendering index to {}", indexFile);
		var engine = createEngine();
		try (var writer = new FileWriter(indexFile, StandardCharsets.UTF_8)) {
			engine.process("index.md", createIndexContext(), writer);
		}
	}

	@SneakyThrows
	private IContext createCsmContext(CsmAnalysis analysis) {
		var context = new Context(Locale.ENGLISH);
		context.setVariable("data", analysis);
		return context;
	}

	@SneakyThrows
	private IContext createIndexContext() {
		var context = new Context(Locale.ENGLISH);
//		context.setVariable("data", analysis);
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
