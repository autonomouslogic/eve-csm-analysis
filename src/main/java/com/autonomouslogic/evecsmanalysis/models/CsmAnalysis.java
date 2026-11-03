package com.autonomouslogic.evecsmanalysis.models;

import java.util.List;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class CsmAnalysis {
	int csmNumber;

	int candidateCount;

	int totalVotes;

	/**
	 * Ballots ranking none of the elected candidates. CCP picks are not counted as elected.
	 */
	int ballotsWithoutWinner;

	@Singular
	List<CandidateRound> winners;

	@Singular
	List<CandidateRound> eliminations;

	int leastSignificantRank;

	List<String> withdrawnCandidates;

	List<CandidateRankDistribution> rankDistribution;

	/**
	 * Same order as {@link #rankDistribution}.
	 */
	List<CandidateStatistics> candidateStatistics;
}
