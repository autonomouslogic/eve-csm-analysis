package com.autonomouslogic.evecsmanalysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.autonomouslogic.evecsmanalysis.models.CandidateRound;
import com.autonomouslogic.evecsmanalysis.models.CsmAnalysis;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.Test;

public class FirstPastThePostTest {
	@Test
	public void shouldElectAllCandidatesAndSumVotes() {
		var votes = new LinkedHashMap<String, Integer>();
		votes.put("Alice", 30);
		votes.put("Bob", 20);
		var analysis = CsmAnalyser.firstPastThePost(4, votes);
		assertEquals(4, analysis.getCsmNumber());
		assertTrue(analysis.isFirstPastThePost());
		assertEquals(50, analysis.getTotalVotes());
		assertEquals(
				List.of(
						CandidateRound.builder()
								.candidate("Alice")
								.round(0)
								.votes(30.0)
								.build(),
						CandidateRound.builder()
								.candidate("Bob")
								.round(0)
								.votes(20.0)
								.build()),
				analysis.getWinners());
	}

	@Test
	public void shouldOverrideTotalVotesFromCsmData() {
		var analysis = CsmAnalysis.builder().csmNumber(4).totalVotes(50).build();
		assertEquals(100, AnalysisRenderer.applyVotesOverride(analysis, 100).getTotalVotes());
	}

	@Test
	public void shouldKeepTotalVotesWithoutOverride() {
		var analysis = CsmAnalysis.builder().csmNumber(4).totalVotes(50).build();
		assertEquals(50, AnalysisRenderer.applyVotesOverride(analysis, null).getTotalVotes());
	}
}
