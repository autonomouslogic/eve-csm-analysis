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
		sortAlumni(alumni);
		return alumni;
	}

	private void populateElections(Alumni alumni) {
		for (CsmData csm : csms) {
			Optional.ofNullable(csm.getCcpPicks())
					.ifPresent(p -> p.forEach(c -> addCandidate(c, alumni, csm.getCsmNumber(), "picked")));
			Optional.ofNullable(csm.getAnalysis().getWinners())
					.ifPresent(r -> r.forEach(
							c -> addCandidate(c.getCandidate().trim(), alumni, csm.getCsmNumber(), "elected")));
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
		Ordering<Alumnus> latestWin = Ordering.natural().reverse().onResultOf(a -> a.getElections().entrySet().stream()
				.filter(e -> !e.getValue().isEmpty())
				.map(e -> e.getKey())
				.max(Ordering.natural())
				.orElse(-1));
		Ordering<Alumnus> name = Ordering.natural().onResultOf(a -> a.getName());
		return latestWin.compound(name);
	}
}
