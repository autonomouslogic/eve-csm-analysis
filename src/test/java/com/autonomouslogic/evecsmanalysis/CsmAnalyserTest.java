package com.autonomouslogic.evecsmanalysis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.autonomouslogic.evecsmanalysis.models.AuditLog;
import com.autonomouslogic.evecsmanalysis.models.CandidateRankDistribution;
import com.autonomouslogic.evecsmanalysis.models.CandidateStatistics;
import com.autonomouslogic.evecsmanalysis.models.CsmAnalysis;
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
		var data = CsmAnalysis.builder();
		new CsmAnalyser(csmConfig, ballotFile, auditLog).winnersAndEliminations(data);
		var winners = data.build().getWinners();
		assertEquals(40.0, winners.get(1).getVotes());
		assertEquals(40.0, data.build().getEliminations().get(0).getVotes());
	}

	@Test
	public void shouldCalculateVotesShortOfQuotaAtElimination() {
		var auditLog = new AuditLogParser("Round 1 beginning - 3 candidates remain\n"
						+ "100 votes, 40 quota\n"
						+ "Initial talley:\n"
						+ "  45 \"Alice\"\n"
						+ "  30 \"Bob\"\n"
						+ "  25 \"Carol\"\n"
						+ "Actions:\n"
						+ "  Elected: \"Alice\"\n"
						+ "  Transfer from \"Alice\":\n"
						+ "    Votes: 45.000000, Factor: 0.111111, Excess: 5.000000\n"
						+ "    0.500000 votes to \"Carol\"\n"
						+ "    4.500000 votes to Exhausted\n"
						+ "  Pre-elimination tally:\n"
						+ "  30 \"Bob\"\n"
						+ "  25 \"Carol\"\n"
						+ "  Elimination: \"Carol\" with 25.500000 votes\n"
						+ "\n"
						+ "Result:\n"
						+ "\"Alice\"\n"
						+ "\"Bob\"\n")
				.parse();
		var ballotFile = new BallotParser(
						"3 2\n" + "1 1 2\n" + "0\n" + "\"Alice\"\n" + "\"Bob\"\n" + "\"Carol\"\n" + "CSM test\n")
				.parse();
		var csmConfig = CsmConfig.builder().csmNumber(1).build();
		var data = CsmAnalysis.builder();
		new CsmAnalyser(csmConfig, ballotFile, auditLog).winnersAndEliminations(data);
		assertEquals(14.5, data.build().getEliminations().get(0).getVotesShortOfQuota());
	}

	@Test
	public void shouldCountBallotsRankingNoWinner() {
		var ballotFile = new BallotParser("4 1\n"
						+ "-4\n"
						+ "3 1\n"
						+ "1 2 1\n"
						+ "2 2 3\n"
						+ "5 3\n"
						+ "7 4\n"
						+ "0\n"
						+ "\"Alice\"\n"
						+ "\"Bob\"\n"
						+ "\"Carol\"\n"
						+ "\"Dave\"\n"
						+ "CSM test\n")
				.parse();
		assertEquals(14, CsmAnalyser.ballotsWithoutWinner(ballotFile, List.of("Alice")));
	}

	@Test
	public void shouldCalculateBallotsFirstChoiceMeanAndMedianRank() {
		var distribution = List.of(
				row("Alice", false, List.of(2, 1, 1)),
				row("Bob", false, List.of(0, 0, 0)),
				row("Carol", false, List.of(0, 3, 0)));
		var stats = CsmAnalyser.candidateStatistics(distribution);
		assertEquals(
				List.of(
						CandidateStatistics.builder()
								.candidate("Alice")
								.ballots(4)
								.firstChoiceBallots(2)
								.meanRank(1.75)
								.medianRank(1.5)
								.build(),
						CandidateStatistics.builder()
								.candidate("Bob")
								.ballots(0)
								.firstChoiceBallots(0)
								.build(),
						CandidateStatistics.builder()
								.candidate("Carol")
								.ballots(3)
								.firstChoiceBallots(0)
								.meanRank(2.0)
								.medianRank(2.0)
								.build()),
				stats);
	}

	@Test
	public void shouldUseOddCountMiddleValueForMedian() {
		assertEquals(2.0, CsmAnalyser.median(List.of(1, 1, 1), 3));
		assertEquals(2.5, CsmAnalyser.median(List.of(1, 1, 1, 1), 4));
		assertEquals(3.0, CsmAnalyser.median(List.of(0, 0, 5), 5));
	}

	@Test
	public void shouldCountBallotsPerNumberOfRanks() {
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
		assertEquals(List.of(0, 1, 1, 2), CsmAnalyser.ballotSizes(ballotFile));
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
