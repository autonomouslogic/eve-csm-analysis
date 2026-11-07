package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.csv.CsmBallotSizesCsv;
import com.autonomouslogic.evecsmanalysis.csv.CsmCandidatesCsv;
import com.autonomouslogic.evecsmanalysis.csv.CsmJaccardCsv;
import com.autonomouslogic.evecsmanalysis.csv.AlumnusRankDistributionCsv;
import com.autonomouslogic.evecsmanalysis.csv.CsmRankDistributionCsv;
import com.autonomouslogic.evecsmanalysis.csv.CsmVotesCsv;
import com.autonomouslogic.evecsmanalysis.models.Alumni;
import com.autonomouslogic.evecsmanalysis.models.Alumnus;
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
import org.apache.commons.io.FileUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@RequiredArgsConstructor
@Log4j2
public class AnalysisRenderer {
	private static final int FREQUENT_ELECTED_MINIMUM = 4;

	@NonNull
	private final List<CsmConfig> csmConfigs;

	@NonNull
	private final ObjectMapper objectMapper;

	public void renderAll() {
		// Every election is loaded first, because the alumni set spans all of them and CSM pages link into it.
		var all = new ArrayList<CsmData>();
		for (var csmConfig : csmConfigs) {
			all.add(loadCsm(csmConfig));
		}
		var alumni = new AlumniFactory(all).create();
		var links = new AlumniLinks(alumni);
		for (int i = 0; i < csmConfigs.size(); i++) {
			renderCsm(csmConfigs.get(i), all.get(i), links);
		}
		renderAlumni(alumni, all);
		renderAlumnusPages(alumni, all);
		renderIndex(all);
	}

	@SneakyThrows
	private CsmData loadCsm(CsmConfig csmConfig) {
		var data = objectMapper.readValue(csmConfig.getCsmData(), CsmData.class).toBuilder()
				.csmNumber(csmConfig.getCsmNumber())
				.build();
		var analysis = applyVotesOverride(
				objectMapper.readValue(csmConfig.getAnalysisJson(), CsmAnalysis.class).toBuilder()
						.csmNumber(csmConfig.getCsmNumber())
						.build(),
				data.getVotes());
		return data.toBuilder().analysis(analysis).build();
	}

	/**
	 * Replaces the derived total votes with the official total from csm.json, when one is given.
	 */
	static CsmAnalysis applyVotesOverride(CsmAnalysis analysis, Integer votes) {
		if (votes == null) {
			return analysis;
		}
		if (votes != analysis.getTotalVotes()) {
			log.info(
					"Overriding total votes for CSM {}: {} derived, {} from csm.json",
					analysis.getCsmNumber(),
					analysis.getTotalVotes(),
					votes);
		}
		return analysis.toBuilder().totalVotes(votes).build();
	}

	@SneakyThrows
	public void renderCsm(CsmConfig csmConfig, CsmData data, AlumniLinks links) {
		log.info("Rendering analysis for {} to {}", csmConfig.getCsmNumber(), csmConfig.getMarkdownFile());
		var engine = createEngine();
		if (data.getAnalysis().isFirstPastThePost()) {
			// First-past-the-post elections have no ballots, so there is no ballot data to chart.
			try (var writer = new FileWriter(csmConfig.getMarkdownFile(), StandardCharsets.UTF_8)) {
				engine.process("csm-fptp.md", createCsmContext(data, data.getAnalysis(), links), writer);
			}
			return;
		}
		var rankDistributionFile = new File("docs/data/csm%d-rank-distribution.csv".formatted(data.getCsmNumber()));
		new CsmRankDistributionCsv(data.getAnalysis(), rankDistributionFile).write();
		new CsmBallotSizesCsv(
						data.getAnalysis(), new File("docs/data/csm%d-ballot-sizes.csv".formatted(data.getCsmNumber())))
				.write();
		new CsmJaccardCsv(data.getAnalysis(), new File("docs/data/csm%d-jaccard.csv".formatted(data.getCsmNumber())))
				.write();
		try (var writer = new FileWriter(csmConfig.getMarkdownFile(), StandardCharsets.UTF_8)) {
			engine.process("csm.md", createCsmContext(data, data.getAnalysis(), links), writer);
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
	private void renderAlumni(Alumni alumni, List<CsmData> csms) {
		var file = new File("docs/alumni.md");
		log.info("Rendering alumni to {}", file);
		var csmNumbers = csms.stream()
				.map(CsmData::getCsmNumber)
				.sorted(Ordering.natural().reversed())
				.toList();
		var engine = createEngine();
		try (var writer = new FileWriter(file, StandardCharsets.UTF_8)) {
			engine.process("alumni.md", createAlukniContext(alumni, csmNumbers, csms), writer);
		}
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("docs/data/alumni.json"), alumni);
	}

	/**
	 * Rewrites docs/alumni/ from scratch, so pages for renamed or removed alumni do not linger.
	 */
	@SneakyThrows
	private void renderAlumnusPages(Alumni alumni, List<CsmData> csms) {
		var dir = new File("docs/alumni");
		var dataDir = new File("docs/data/alumni");
		FileUtils.deleteDirectory(dir);
		FileUtils.deleteDirectory(dataDir);
		FileUtils.forceMkdir(dir);
		FileUtils.forceMkdir(dataDir);
		var engine = createEngine();
		for (var alumnus : alumni.getAlumni().values()) {
			var file = new File(dir, alumnus.slug() + ".md");
			var hasDistribution = new AlumnusRankDistributionCsv(
							alumnus, csms, new File(dataDir, alumnus.slug() + "-rank-distribution.csv"))
					.write();
			try (var writer = new FileWriter(file, StandardCharsets.UTF_8)) {
				engine.process("alumnus.md", createAlumnusContext(alumnus, hasDistribution), writer);
			}
		}
		log.info("Rendered {} alumni pages to {}", alumni.getAlumni().size(), dir);
	}

	@SneakyThrows
	private IContext createCsmContext(CsmData data, CsmAnalysis analysis, AlumniLinks links) {
		var context = new Context(Locale.ENGLISH);
		context.setVariable("data", data);
		context.setVariable("analysis", analysis);
		context.setVariable("links", links);
		return context;
	}

	@SneakyThrows
	private IContext createAlumnusContext(Alumnus alumnus, boolean hasDistribution) {
		var context = new Context(Locale.ENGLISH);
		context.setVariable("alumnus", alumnus);
		context.setVariable("hasDistribution", hasDistribution);
		return context;
	}

	@SneakyThrows
	private IContext createIndexContext() {
		var context = new Context(Locale.ENGLISH);
		return context;
	}

	@SneakyThrows
	private IContext createAlukniContext(Alumni alumni, List<Integer> csmNumbers, List<CsmData> csms) {
		var context = new Context(Locale.ENGLISH);
		context.setVariable("alumni", alumni);
		context.setVariable("links", new AlumniLinks(alumni));
		context.setVariable("csmNumbers", csmNumbers);
		context.setVariable("ranCount", AlumniFactory.countRan(csms));
		context.setVariable("electedCount", AlumniFactory.countElected(alumni));
		context.setVariable("frequent", AlumniFactory.frequentlyElected(alumni, FREQUENT_ELECTED_MINIMUM));
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
