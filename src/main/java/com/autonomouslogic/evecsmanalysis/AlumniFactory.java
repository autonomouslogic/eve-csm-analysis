package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.Alumni;
import com.autonomouslogic.evecsmanalysis.models.Alumnus;
import com.autonomouslogic.evecsmanalysis.models.CsmData;
import com.google.common.collect.Ordering;
import java.util.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Builds the alumni table from CSM data.
 */
@RequiredArgsConstructor
public class AlumniFactory {
	@NonNull
	private final List<CsmData> csms;

	public Alumni create() {
		var alumni = new Alumni();
		var csmNumbers = csms.stream().map(CsmData::getCsmNumber).toList();
		populateElections(alumni);
		populateRuns(alumni);
		checkUniqueSlugs(alumni);
		sortAlumni(alumni);
		return alumni;
	}

	/**
	 * Returns alumni elected at least {@code minimum} times, most elections first, then by name.
	 */
	public static List<Alumnus> frequentlyElected(Alumni alumni, int minimum) {
		return alumni.getAlumni().values().stream()
				.filter(a -> a.electedCount() >= minimum)
				.sorted(Comparator.<Alumnus>comparingLong(Alumnus::electedCount)
						.reversed()
						.thenComparing(Alumnus::getName))
				.toList();
	}

	private void populateElections(Alumni alumni) {
		for (CsmData csm : csms) {
			Optional.ofNullable(csm.getCcpPicks())
					.ifPresent(p -> p.forEach(c -> addCandidate(c, alumni, csm.getCsmNumber(), "picked")));
			Optional.ofNullable(csm.getAnalysis().getWinners())
					.ifPresent(r -> r.forEach(c -> addCandidate(
							c.getCandidate(),
							alumni,
							csm.getCsmNumber(),
							csm.getResigned().contains(c.getCandidate()) ? "resigned" : "elected")));
			csm.getReplacements().forEach(c -> addCandidate(c, alumni, csm.getCsmNumber(), "replacement"));
		}
	}

	/**
	 * Copies the seat statuses into each alumnus's runs, then adds the losing runs. A seat status is never overwritten.
	 */
	private void populateRuns(Alumni alumni) {
		alumni.getAlumni().values().forEach(a -> a.getRuns().putAll(a.getElections()));
		for (CsmData csm : csms) {
			Optional.ofNullable(csm.getAnalysis().getEliminations())
					.ifPresent(e -> e.forEach(r -> addRun(r.getCandidate(), alumni, csm.getCsmNumber(), "eliminated")));
			Optional.ofNullable(csm.getAnalysis().getWithdrawnCandidates())
					.ifPresent(w -> w.forEach(c -> addRun(c, alumni, csm.getCsmNumber(), "withdrawn")));
		}
	}

	private void addRun(String name, Alumni alumni, int csmNumber, String status) {
		var alumnus = alumni.getAlumni().get(name);
		if (alumnus != null) {
			alumnus.getRuns().putIfAbsent(csmNumber, status);
		}
	}

	private void checkUniqueSlugs(Alumni alumni) {
		var bySlug = new HashMap<String, String>();
		for (var alumnus : alumni.getAlumni().values()) {
			var previous = bySlug.put(alumnus.slug(), alumnus.getName());
			if (previous != null) {
				throw new IllegalStateException("\"%s\" and \"%s\" share the page slug \"%s\""
						.formatted(previous, alumnus.getName(), alumnus.slug()));
			}
		}
	}

	private void addCandidate(String name, Alumni alumni, int csmNumber, String type) {
		var alumnus = alumni.getAlumni().computeIfAbsent(name, ignore -> new Alumnus().setName(name));
		alumnus.getElections().put(csmNumber, type);
	}

	private void sortAlumni(Alumni alumni) {
		var sorted = new LinkedHashMap<String, Alumnus>();
		alumni.getAlumni().entrySet().stream()
				.map(Map.Entry::getValue)
				.sorted(sortAlumniOrdering())
				.forEach(e -> {
					sorted.put(e.getName(), e);
				});
		alumni.setAlumni(sorted);
	}

	private Ordering<Alumnus> sortAlumniOrdering() {
		Ordering<Alumnus> latestWin = Ordering.natural()
				.reverse()
				.onResultOf(a -> a.getElections().entrySet().stream()
						.filter(e -> !e.getValue().isEmpty())
						.map(e -> e.getKey())
						.max(Ordering.natural())
						.orElse(-1));
		Ordering<Alumnus> name = Ordering.natural().onResultOf(a -> a.getName());
		return latestWin.compound(name);
	}
}
