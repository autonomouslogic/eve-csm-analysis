package com.autonomouslogic.evecsmanalysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.autonomouslogic.evecsmanalysis.models.Alumnus;
import com.autonomouslogic.evecsmanalysis.models.CandidateRound;
import com.autonomouslogic.evecsmanalysis.models.CsmAnalysis;
import com.autonomouslogic.evecsmanalysis.models.CsmData;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class AlumniFactoryTest {
	@Test
	public void shouldMarkResignedWinnerAsResigned() {
		var alumni = create(csm(9).resigned(List.of("Matias Otero"))
				.analysis(analysis(9, "Matias Otero", "Ali Aras"))
				.build());
		assertEquals(Map.of(9, "resigned"), alumni.get("Matias Otero").getElections());
	}

	@Test
	public void shouldMarkWinnerNotResignedAsElected() {
		var alumni = create(csm(9).resigned(List.of("Matias Otero"))
				.analysis(analysis(9, "Matias Otero", "Ali Aras"))
				.build());
		assertEquals(Map.of(9, "elected"), alumni.get("Ali Aras").getElections());
	}

	@Test
	public void shouldMarkReplacementAsReplacement() {
		var alumni = create(csm(9).replacements(List.of("Asayanami Dei"))
				.analysis(analysis(9, "Matias Otero"))
				.build());
		assertEquals(Map.of(9, "replacement"), alumni.get("Asayanami Dei").getElections());
	}

	@Test
	public void shouldKeepElectedInOtherCsmWhenReplacementInAnother() {
		var alumni = create(
				csm(9).replacements(List.of("Asayanami Dei"))
						.analysis(analysis(9, "Matias Otero"))
						.build(),
				csm(13).analysis(analysis(13, "Asayanami Dei")).build());
		assertEquals(
				Map.of(9, "replacement", 13, "elected"),
				alumni.get("Asayanami Dei").getElections());
	}

	@Test
	public void shouldListAlumniElectedFiveOrMoreTimesOrderedByCountDescending() {
		var alumni = new AlumniFactory(List.of(
						csm(9).analysis(analysis(9, "Four Times", "Five Times", "Six Times"))
								.build(),
						csm(10).analysis(analysis(10, "Five Times", "Six Times"))
								.build(),
						csm(11).analysis(analysis(11, "Five Times", "Six Times"))
								.build(),
						csm(12).analysis(analysis(12, "Five Times", "Six Times"))
								.build(),
						csm(13).analysis(analysis(13, "Five Times", "Six Times"))
								.build(),
						csm(14).analysis(analysis(14, "Six Times")).build(),
						csm(15).analysis(analysis(15, "Four Times")).build()))
				.create();
		var frequent = AlumniFactory.frequentlyElected(alumni, 5);
		assertEquals(
				List.of("Six Times", "Five Times"),
				frequent.stream().map(Alumnus::getName).toList());
	}

	@Test
	public void shouldCountResignedAsElectedButNotPicksOrReplacements() {
		var alumni = new AlumniFactory(List.of(
						csm(9).resigned(List.of("Resigner"))
								.analysis(analysis(9, "Resigner"))
								.ccpPicks(List.of("Picked"))
								.replacements(List.of("Replacement"))
								.build(),
						csm(10).analysis(analysis(10, "Resigner")).build(),
						csm(11).analysis(analysis(11, "Resigner")).build(),
						csm(12).analysis(analysis(12, "Resigner")).build(),
						csm(13).analysis(analysis(13, "Resigner")).build(),
						csm(14).analysis(analysis(14))
								.ccpPicks(List.of("Picked"))
								.build(),
						csm(15).analysis(analysis(15))
								.ccpPicks(List.of("Picked"))
								.build(),
						csm(16).analysis(analysis(16))
								.ccpPicks(List.of("Picked"))
								.build(),
						csm(17).analysis(analysis(17))
								.ccpPicks(List.of("Picked"))
								.build()))
				.create();
		var frequent = AlumniFactory.frequentlyElected(alumni, 5);
		assertEquals(
				List.of("Resigner"), frequent.stream().map(Alumnus::getName).toList());
	}

	@Test
	public void shouldRecordEliminatedRunOutsideElections() {
		var alumni = create(
				csm(9).analysis(analysis(9, "Ali Aras")).build(),
				csm(10).analysis(eliminated(10, "Ali Aras")).build());
		assertEquals(Map.of(9, "elected"), alumni.get("Ali Aras").getElections());
		assertEquals(
				Map.of(9, "elected", 10, "eliminated"), alumni.get("Ali Aras").getRuns());
	}

	@Test
	public void shouldKeepPickOverEliminationInSameElection() {
		var alumni = create(csm(9).ccpPicks(List.of("Picked"))
				.analysis(eliminated(9, "Picked"))
				.build());
		assertEquals(Map.of(9, "picked"), alumni.get("Picked").getRuns());
	}

	@Test
	public void shouldRecordWithdrawalAndListRunsNewestFirst() {
		var alumni = create(
				csm(9).analysis(analysis(9, "Ali Aras")).build(),
				csm(10).analysis(CsmAnalysis.builder()
								.csmNumber(10)
								.withdrawnCandidates(List.of("Ali Aras"))
								.build())
						.build());
		assertEquals(
				Map.of(9, "elected", 10, "withdrawn"), alumni.get("Ali Aras").getRuns());
		assertEquals(
				List.of(10, 9), List.copyOf(alumni.get("Ali Aras").getRuns().keySet()));
	}

	@Test
	public void shouldOmitCandidatesWhoNeverHeldASeat() {
		var alumni = create(
				csm(9).analysis(analysis(9, "Ali Aras")).build(),
				csm(10).analysis(eliminated(10, "Loser")).build());
		assertEquals(List.of("Ali Aras"), List.copyOf(alumni.keySet()));
	}

	@Test
	public void shouldNotOrderByLosingRuns() {
		var alumni = new AlumniFactory(List.of(
						csm(8).analysis(analysis(8, "Earlier Winner")).build(),
						csm(9).analysis(analysis(9, "Later Winner")).build(),
						csm(10).analysis(eliminated(10, "Earlier Winner")).build()))
				.create()
				.getAlumni();
		assertEquals(List.of("Later Winner", "Earlier Winner"), List.copyOf(alumni.keySet()));
	}

	@Test
	public void shouldHandleFirstPastThePostElections() {
		var alumni = create(csm(4).analysis(CsmAnalysis.builder()
						.csmNumber(4)
						.firstPastThePost(true)
						.winner(round("Ali Aras"))
						.build())
				.build());
		assertEquals(Map.of(4, "elected"), alumni.get("Ali Aras").getRuns());
	}

	@Test
	public void shouldRejectSlugCollisions() {
		assertThrows(
				IllegalStateException.class,
				() -> create(csm(9).analysis(analysis(9, "T'Amber", "TAmber")).build()));
	}

	@Test
	public void shouldCountEachPersonOnceAcrossElections() {
		var csms = List.of(
				csm(8).analysis(mixed(8, List.of("Ali Aras"), "Loser One")).build(),
				csm(9).analysis(mixed(9, List.of("Ali Aras", "Matias Otero"), "Loser One", "Loser Two"))
						.build());
		assertEquals(4, AlumniFactory.countRan(csms));
	}

	@Test
	public void shouldNotCountPickOrReplacementAsElected() {
		var alumni = new AlumniFactory(List.of(csm(9).ccpPicks(List.of("Pick"))
						.replacements(List.of("Replacement"))
						.analysis(analysis(9, "Ali Aras"))
						.build()))
				.create();
		assertEquals(1, AlumniFactory.countElected(alumni));
	}

	private static CsmAnalysis mixed(int csmNumber, List<String> winners, String... eliminated) {
		var builder = CsmAnalysis.builder().csmNumber(csmNumber);
		winners.forEach(w -> builder.winner(round(w)));
		for (String name : eliminated) {
			builder.elimination(round(name));
		}
		return builder.build();
	}

	private static Map<String, Alumnus> create(CsmData... csms) {
		return new AlumniFactory(List.of(csms)).create().getAlumni();
	}

	private static CsmData.CsmDataBuilder csm(int csmNumber) {
		return CsmData.builder().csmNumber(csmNumber);
	}

	private static CsmAnalysis analysis(int csmNumber, String... winners) {
		var builder = CsmAnalysis.builder().csmNumber(csmNumber);
		for (String winner : winners) {
			builder.winner(round(winner));
		}
		return builder.build();
	}

	private static CsmAnalysis eliminated(int csmNumber, String... names) {
		var builder = CsmAnalysis.builder().csmNumber(csmNumber);
		for (String name : names) {
			builder.elimination(round(name));
		}
		return builder.build();
	}

	private static CandidateRound round(String candidate) {
		return CandidateRound.builder().candidate(candidate).round(1).build();
	}
}
