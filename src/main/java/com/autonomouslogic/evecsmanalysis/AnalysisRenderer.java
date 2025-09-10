package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.csv.CsmCandidatesCsv;
import com.autonomouslogic.evecsmanalysis.csv.CsmVotesCsv;
import com.autonomouslogic.evecsmanalysis.models.Alumni;
import com.autonomouslogic.evecsmanalysis.models.CsmAnalysis;
import com.autonomouslogic.evecsmanalysis.models.CsmConfig;
import com.autonomouslogic.evecsmanalysis.models.CsmData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Ordering;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
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

	@SneakyThrows
	public void renderAll() {
		var all = new ArrayList<CsmData>();
		for (var csmConfig : csmConfigs) {
			var analysis = objectMapper.readValue(csmConfig.getAnalysisJson(), CsmAnalysis.class).toBuilder()
					.csmNumber(csmConfig.getCsmNumber())
					.build();
			var data = objectMapper.readValue(csmConfig.getCsmData(), CsmData.class).toBuilder()
					.csmNumber(csmConfig.getCsmNumber())
					.analysis(analysis)
					.build();
			all.add(data);
			renderCsm(csmConfig, data);
		}
		renderAlumni(all);
		renderIndex(all);
	}

	@SneakyThrows
	public void renderCsm(CsmConfig csmConfig, CsmData data) {
		log.info("Rendering analysis for {} to {}", csmConfig.getCsmNumber(), csmConfig.getMarkdownFile());
		var engine = createEngine();
		try (var writer = new FileWriter(csmConfig.getMarkdownFile(), StandardCharsets.UTF_8)) {
			engine.process("csm.md", createCsmContext(data, data.getAnalysis()), writer);
		}
	}

	@SneakyThrows
	private void renderIndex(List<CsmData> csms) {
		var allAnalysis = new ArrayList<CsmAnalysis>();
		for (CsmData csm : csms) {
			allAnalysis.add(csm.getAnalysis());
		}
		new CsmVotesCsv(allAnalysis, new File("docs/data/csm-votes.csv")).write();
		new CsmCandidatesCsv(allAnalysis, new File("docs/data/csm-candidates.csv")).write();

		var indexFile = new File("docs/index.md");
		log.info("Rendering index to {}", indexFile);
		var engine = createEngine();
		try (var writer = new FileWriter(indexFile, StandardCharsets.UTF_8)) {
			engine.process("index.md", createIndexContext(), writer);
		}
	}

	@SneakyThrows
	private void renderAlumni(List<CsmData> csms) {
		var file = new File("docs/alumni.md");
		log.info("Rendering alumni to {}", file);
		var alumni = new AlumniFactory(csms).create();
		var csmNumbers = csms.stream()
				.map(CsmData::getCsmNumber)
				.sorted(Ordering.natural().reversed())
				.toList();
		var engine = createEngine();
		try (var writer = new FileWriter(file, StandardCharsets.UTF_8)) {
			engine.process("alumni.md", createAlukniContext(alumni, csmNumbers), writer);
		}
	}

	@SneakyThrows
	private IContext createCsmContext(CsmData data, CsmAnalysis analysis) {
		var context = new Context(Locale.ENGLISH);
		context.setVariable("data", data);
		context.setVariable("analysis", analysis);
		return context;
	}

	@SneakyThrows
	private IContext createIndexContext() {
		var context = new Context(Locale.ENGLISH);
		return context;
	}

	@SneakyThrows
	private IContext createAlukniContext(Alumni alumni, List<Integer> csmNumbers) {
		var context = new Context(Locale.ENGLISH);
		context.setVariable("alumni", alumni);
		context.setVariable("csmNumbers", csmNumbers);
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
