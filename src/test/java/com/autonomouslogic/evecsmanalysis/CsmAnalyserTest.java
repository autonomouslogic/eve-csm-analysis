package com.autonomouslogic.evecsmanalysis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.autonomouslogic.evecsmanalysis.models.CandidateRankDistribution;
import com.autonomouslogic.evecsmanalysis.parser.BallotParser;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CsmAnalyserTest {
	@Test
	public void shouldCountVotesPerRank() {
		var ballotFile = new BallotParser("3 1\n"
						+ "1 1 2\n"
						+ "2 2 1 3\n"
						+ "1 3\n"
						+ "0\n"
						+ "\"Alice\"\n"
						+ "\"Bob\"\n"
						+ "\"Carol\"\n"
						+ "CSM test\n")
				.parse();
		var distribution = CsmAnalyser.rankDistribution(ballotFile);
		assertEquals(
				List.of(
						row("Bob", false, List.of(2, 1, 0)),
						row("Alice", false, List.of(1, 2, 0)),
						row("Carol", false, List.of(1, 0, 2))),
				distribution);
	}

	@Test
	public void shouldIncludeWithdrawnCandidateAndFlagIt() {
		var ballotFile = new BallotParser("3 1\n"
						+ "-3\n"
						+ "1 1 2\n"
						+ "2 2 1 3\n"
						+ "1 3\n"
						+ "0\n"
						+ "\"Alice\"\n"
						+ "\"Bob\"\n"
						+ "\"Carol\"\n"
						+ "CSM test\n")
				.parse();
		assertEquals(List.of("Carol"), CsmAnalyser.withdrawnCandidates(ballotFile));
		var distribution = CsmAnalyser.rankDistribution(ballotFile);
		assertEquals(
				List.of(
						row("Bob", false, List.of(2, 1, 0)),
						row("Alice", false, List.of(1, 2, 0)),
						row("Carol", true, List.of(1, 0, 2))),
				distribution);
	}

	@Test
	public void shouldOrderByVotesPerRankThenName() {
		var ballotFile = new BallotParser("5 1\n"
						+ "3 2\n"
						+ "4 3 1\n"
						+ "0\n"
						+ "\"Alice\"\n"
						+ "\"Bob\"\n"
						+ "\"Carol\"\n"
						+ "\"Dave\"\n"
						+ "\"Erin\"\n"
						+ "CSM test\n")
				.parse();
		var distribution = CsmAnalyser.rankDistribution(ballotFile);
		assertEquals(
				List.of(
						row("Carol", false, List.of(4, 0)),
						row("Bob", false, List.of(3, 0)),
						row("Alice", false, List.of(0, 4)),
						row("Dave", false, List.of(0, 0)),
						row("Erin", false, List.of(0, 0))),
				distribution);
	}

	private static CandidateRankDistribution row(String candidate, boolean withdrawn, List<Integer> rankVotes) {
		return CandidateRankDistribution.builder()
				.candidate(candidate)
				.rankVotes(rankVotes)
				.build();
	}
}
