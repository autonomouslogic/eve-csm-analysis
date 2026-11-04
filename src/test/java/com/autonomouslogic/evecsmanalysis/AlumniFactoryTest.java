package com.autonomouslogic.evecsmanalysis;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

	private static Map<String, Alumnus> create(CsmData... csms) {
		return new AlumniFactory(List.of(csms)).create().getAlumni();
	}

	private static CsmData.CsmDataBuilder csm(int csmNumber) {
		return CsmData.builder().csmNumber(csmNumber);
	}

	private static CsmAnalysis analysis(int csmNumber, String... winners) {
		var builder = CsmAnalysis.builder().csmNumber(csmNumber);
		for (String winner : winners) {
			builder.winner(CandidateRound.builder().candidate(winner).round(1).build());
		}
		return builder.build();
	}
}
