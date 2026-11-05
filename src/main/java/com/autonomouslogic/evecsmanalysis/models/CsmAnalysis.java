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

	/**
	 * Whether the election used first-past-the-post voting. Only {@link #totalVotes}, {@link #winners} and the elected
	 * count are set for these elections.
	 */
	boolean firstPastThePost;

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

	/**
	 * Number of candidates flagged as elected in {@link #candidateStatistics}. They come first in
	 * {@link #rankDistribution} order.
	 */
	int electedCount;

	/**
	 * Jaccard index for each pair of candidates, indexed as [row][column] in the same order as {@link #rankDistribution}.
	 * Null where the pair has no ballots at all.
	 */
	List<List<Double>> jaccardIndex;

	/**
	 * Most common ballot compositions, most votes first.
	 */
	List<PopularBallot> popularBallots;

	/**
	 * Ballots per ballot size. Index is the number of ranks a ballot defines.
	 */
	List<Integer> ballotSizes;
}
