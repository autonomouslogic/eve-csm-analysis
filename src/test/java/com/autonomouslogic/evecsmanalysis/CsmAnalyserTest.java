package com.autonomouslogic.evecsmanalysis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.autonomouslogic.evecsmanalysis.models.AuditLog;
import com.autonomouslogic.evecsmanalysis.models.CandidateRankDistribution;
import com.autonomouslogic.evecsmanalysis.models.CsmConfig;
import com.autonomouslogic.evecsmanalysis.models.Elimination;
import com.autonomouslogic.evecsmanalysis.models.Round;
import com.autonomouslogic.evecsmanalysis.parser.AuditLogParser;
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
		var distribution = CsmAnalyser.rankDistribution(ballotFile, List.of("Bob", "Alice", "Carol"));
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
		var distribution = CsmAnalyser.rankDistribution(ballotFile, List.of("Bob", "Alice"));
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
		var distribution = CsmAnalyser.rankDistribution(ballotFile, List.of("Carol", "Bob", "Alice", "Dave", "Erin"));
		assertEquals(
				List.of(
						row("Carol", false, List.of(4, 0)),
						row("Bob", false, List.of(3, 0)),
						row("Alice", false, List.of(0, 4)),
						row("Dave", false, List.of(0, 0)),
						row("Erin", false, List.of(0, 0))),
				distribution);
	}

	@Test
	public void shouldOrderElectedInChosenOrderThenEliminatedInReverseEliminationOrder() {
		var auditLog = AuditLog.builder()
				.round(Round.builder()
						.roundNumber(1)
						.elimination(elimination("Dave"))
						.build())
				.round(Round.builder()
						.roundNumber(2)
						.electedCandidate("Bob")
						.elimination(elimination("Erin"))
						.build())
				.round(Round.builder()
						.roundNumber(3)
						.electedCandidate("Carol")
						.electedCandidate("Alice")
						.elimination(elimination("Frank"))
						.build())
				.finalResult("Bob")
				.finalResult("Carol")
				.finalResult("Alice")
				.build();
		assertEquals(List.of("Bob", "Carol", "Alice", "Frank", "Erin", "Dave"), CsmAnalyser.electionOrder(auditLog));
	}

	@Test
	public void shouldSortRankDistributionByGivenOrderThenUnlistedByName() {
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
		var distribution = CsmAnalyser.rankDistribution(ballotFile, List.of("Alice", "Carol"));
		assertEquals(
				List.of(
						row("Alice", false, List.of(0, 4)),
						row("Carol", false, List.of(4, 0)),
						row("Bob", false, List.of(3, 0)),
						row("Dave", false, List.of(0, 0)),
						row("Erin", false, List.of(0, 0))),
				distribution);
	}

	@Test
	public void shouldIncludeTransfersReceivedBeforeElectionInVotesAtElection() {
		var ballotFile = new BallotParser(
						"3 2\n" + "1 1 2\n" + "0\n" + "\"Zintage\"\n" + "\"hyprviper1\"\n" + "\"Bob\"\n" + "CSM test\n")
				.parse();
		var auditLog = new AuditLogParser("Round 1 beginning - 3 candidates remain\n"
						+ "100 votes, 40 quota\n"
						+ "Initial talley:\n"
						+ "  50 \"Zintage\"\n"
						+ "  10 \"hyprviper1\"\n"
						+ "  40 \"Bob\"\n"
						+ "Actions:\n"
						+ "  Elected: \"Zintage\"\n"
						+ "  Transfer from \"Zintage\":\n"
						+ "    Votes: 50.000000, Factor: 0.200000, Excess: 10.000000\n"
						+ "    30.000000 votes to \"hyprviper1\"\n"
						+ "    20.000000 votes to Exhausted\n"
						+ "  Elected: \"hyprviper1\"\n"
						+ "  Pre-elimination tally:\n"
						+ "  40 \"Bob\"\n"
						+ "  Elimination: \"Bob\" with 40 votes\n"
						+ "\n"
						+ "Result:\n"
						+ "\"Zintage\"\n"
						+ "\"hyprviper1\"\n")
				.parse();
		var csmConfig = CsmConfig.builder().csmNumber(1).build();
		var winners = new CsmAnalyser(csmConfig, ballotFile, auditLog).run().getWinners();
		assertEquals(40.0, winners.get(1).getVotes());
	}

	private static Elimination elimination(String candidate) {
		return Elimination.builder().candidate(candidate).votes(0).build();
	}

	private static CandidateRankDistribution row(String candidate, boolean withdrawn, List<Integer> rankVotes) {
		return CandidateRankDistribution.builder()
				.candidate(candidate)
				.rankVotes(rankVotes)
				.build();
	}
}
