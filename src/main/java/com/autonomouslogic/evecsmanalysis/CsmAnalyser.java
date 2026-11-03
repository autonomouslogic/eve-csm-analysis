package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.AuditLog;
import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import com.autonomouslogic.evecsmanalysis.models.CandidateRankDistribution;
import com.autonomouslogic.evecsmanalysis.models.CandidateRound;
import com.autonomouslogic.evecsmanalysis.models.CandidateStatistics;
import com.autonomouslogic.evecsmanalysis.models.CsmAnalysis;
import com.autonomouslogic.evecsmanalysis.models.CsmConfig;
import com.autonomouslogic.evecsmanalysis.models.PopularBallot;
import com.autonomouslogic.evecsmanalysis.models.Round;
import com.autonomouslogic.evecsmanalysis.models.Votes;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
public class CsmAnalyser {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Number of ballot compositions listed in {@link CsmAnalysis#getPopularBallots()}.
	 */
	static final int POPULAR_BALLOT_COUNT = 6;

	@NonNull
	private final CsmConfig csmConfig;

	@NonNull
	private final BallotFile ballotFile;

	@NonNull
	private final AuditLog auditLog;

	public CsmAnalysis run() {
		log.info("Running analysis for CSM: {}", csmConfig.getCsmNumber());
		var totalVotes = totalVotes();
		var rankDistribution = rankDistribution(ballotFile, electionOrder(auditLog));
		var candidateStatistics = candidateStatistics(rankDistribution, auditLog.getFinalResults());
		var data = CsmAnalysis.builder()
				.csmNumber(csmConfig.getCsmNumber())
				.candidateCount(ballotFile.getCandidateCount())
				.totalVotes(totalVotes)
				.ballotsWithoutWinner(ballotsWithoutWinner(ballotFile, auditLog.getFinalResults()))
				//				.leastSignificantRank(1)
				.leastSignificantRank(findLeastSignificantRank())
				.withdrawnCandidates(withdrawnCandidates(ballotFile))
				.rankDistribution(rankDistribution)
				.candidateStatistics(candidateStatistics)
				.electedCount((int) candidateStatistics.stream()
						.filter(CandidateStatistics::isElected)
						.count())
				.jaccardIndex(jaccardIndex(ballotFile, rankDistribution))
				.ballotSizes(ballotSizes(ballotFile))
				.popularBallots(popularBallots(ballotFile, POPULAR_BALLOT_COUNT));
		winnersAndEliminations(data);
		return data.build();
	}

	private int totalVotes() {
		log.info("Calculating total votes");
		return ballotFile.getAllVotes().stream().mapToInt(Votes::getCount).sum();
	}

	/**
	 * Counts ballots that rank none of the given winners, i.e. only eliminated or withdrawn candidates. This is not
	 * the STV notion of an exhausted ballot, and it does not include ballots that rank a winner but never counted
	 * towards one.
	 */
	static int ballotsWithoutWinner(BallotFile ballotFile, List<String> winners) {
		log.info("Calculating ballots without a winner");
		var winnerIds = new HashSet<Integer>();
		for (var entry : ballotFile.getCandidateNames().entrySet()) {
			if (winners.contains(entry.getValue())) {
				winnerIds.add(entry.getKey());
			}
		}
		if (winnerIds.size() != winners.size()) {
			throw new IllegalArgumentException("Winners not found in ballot file: " + winners);
		}
		return ballotFile.getAllVotes().stream()
				.filter(votes -> votes.getBallot().getRankings().stream().noneMatch(winnerIds::contains))
				.mapToInt(Votes::getCount)
				.sum();
	}

	static int rankCount(BallotFile ballotFile) {
		return ballotFile.getAllVotes().stream()
				.mapToInt(votes -> votes.getBallot().getRankings().size())
				.max()
				.orElse(0);
	}

	/**
	 * Ballots per ballot size, where size is the number of ranks a ballot defines. Index is the size, so index 0 is
	 * ballots defining no ranks. Weighted by {@link Votes#getCount()}.
	 */
	static List<Integer> ballotSizes(BallotFile ballotFile) {
		log.info("Calculating ballot sizes");
		var counts = new int[rankCount(ballotFile) + 1];
		for (var votes : ballotFile.getAllVotes()) {
			counts[votes.getBallot().getRankings().size()] += votes.getCount();
		}
		return Arrays.stream(counts).boxed().toList();
	}

	/**
	 * The most common ballot compositions, i.e. the exact candidate ranking, most votes first. Ties are ordered by
	 * candidate names. Ballots ranking no candidates are skipped.
	 */
	static List<PopularBallot> popularBallots(BallotFile ballotFile, int limit) {
		log.info("Calculating popular ballots");
		var counts = new HashMap<List<String>, Integer>();
		for (var votes : ballotFile.getAllVotes()) {
			var names = votes.getBallot().getRankings().stream()
					.map(id -> candidateName(ballotFile, id))
					.toList();
			if (!names.isEmpty()) {
				counts.merge(names, votes.getCount(), Integer::sum);
			}
		}
		return counts.entrySet().stream()
				.sorted((a, b) -> {
					int byCount = Integer.compare(b.getValue(), a.getValue());
					return byCount != 0
							? byCount
							: String.join("\u0000", a.getKey()).compareTo(String.join("\u0000", b.getKey()));
				})
				.limit(limit)
				.map(entry -> PopularBallot.builder()
						.candidates(entry.getKey())
						.ballots(entry.getValue())
						.build())
				.toList();
	}

	static List<String> withdrawnCandidates(BallotFile ballotFile) {
		return ballotFile.getWithdrawnCandidates().stream()
				.map(id -> candidateName(ballotFile, id))
				.toList();
	}

	/**
	 * Candidates in display order: elected candidates in the order they were chosen, followed by eliminated
	 * candidates in reverse order of elimination.
	 */
	static List<String> electionOrder(AuditLog auditLog) {
		var elected = new LinkedHashSet<String>();
		var eliminated = new ArrayList<String>();
		for (var round : auditLog.getRounds()) {
			elected.addAll(round.getElectedCandidates());
			eliminated.add(round.getElimination().getCandidate());
		}
		elected.addAll(auditLog.getFinalResults());
		Collections.reverse(eliminated);
		var order = new ArrayList<>(elected);
		order.addAll(eliminated);
		return order;
	}

	/**
	 * Counts, per candidate, how many ballots place them at each rank. Withdrawn candidates are included.
	 * Sorted by the given order. Candidates not in the order (withdrawn) come last, sorted by name.
	 */
	static List<CandidateRankDistribution> rankDistribution(BallotFile ballotFile, List<String> order) {
		log.info("Calculating rank distribution");
		var rankCount = rankCount(ballotFile);
		var counts = new HashMap<Integer, int[]>();
		for (var id : ballotFile.getCandidateNames().keySet()) {
			counts.put(id, new int[rankCount]);
		}
		for (var votes : ballotFile.getAllVotes()) {
			var rankings = votes.getBallot().getRankings();
			for (int i = 0; i < rankings.size(); i++) {
				var ranks = counts.get(rankings.get(i));
				if (ranks == null) {
					throw new IllegalArgumentException("Ballot references unknown candidate: " + rankings.get(i));
				}
				ranks[i] += votes.getCount();
			}
		}
		return counts.entrySet().stream()
				.map(entry -> CandidateRankDistribution.builder()
						.candidate(candidateName(ballotFile, entry.getKey()))
						.rankVotes(Arrays.stream(entry.getValue()).boxed().toList())
						.build())
				.sorted(rankDistributionOrdering(order))
				.toList();
	}

	/**
	 * Ballot statistics per candidate, in the same order as the given rank distribution. Ballot counts are weighted by
	 * {@link Votes#getCount()}, so the rank counts from {@link #rankDistribution} are used directly. Candidates in the
	 * elected list are flagged as elected.
	 */
	static List<CandidateStatistics> candidateStatistics(
			List<CandidateRankDistribution> distribution, List<String> elected) {
		return distribution.stream()
				.map(d -> candidateStatistic(d, elected.contains(d.getCandidate())))
				.toList();
	}

	/**
	 * Jaccard index between every pair of candidates. A candidate's set is the ballots ranking them at any position,
	 * weighted by {@link Votes#getCount()}. The index is the ballots ranking both candidates divided by the ballots
	 * ranking either. Rows and columns are in the same order as the given rank distribution. A pair with no ballots
	 * between them is null, which only happens when neither candidate appears on any ballot.
	 */
	static List<List<Double>> jaccardIndex(BallotFile ballotFile, List<CandidateRankDistribution> distribution) {
		log.info("Calculating Jaccard index");
		var idByName = new HashMap<String, Integer>();
		for (var entry : ballotFile.getCandidateNames().entrySet()) {
			idByName.put(entry.getValue(), entry.getKey());
		}
		var positionById = new HashMap<Integer, Integer>();
		for (int i = 0; i < distribution.size(); i++) {
			positionById.put(idByName.get(distribution.get(i).getCandidate()), i);
		}
		var size = distribution.size();
		var ballots = new long[size];
		var both = new long[size][size];
		for (var votes : ballotFile.getAllVotes()) {
			var present = new HashSet<Integer>();
			for (var id : votes.getBallot().getRankings()) {
				present.add(positionById.get(id));
			}
			for (int a : present) {
				ballots[a] += votes.getCount();
				for (int b : present) {
					both[a][b] += votes.getCount();
				}
			}
		}
		var index = new ArrayList<List<Double>>(size);
		for (int a = 0; a < size; a++) {
			var row = new ArrayList<Double>(size);
			for (int b = 0; b < size; b++) {
				var union = ballots[a] + ballots[b] - both[a][b];
				row.add(union == 0 ? null : (double) both[a][b] / union);
			}
			index.add(row);
		}
		return index;
	}

	private static CandidateStatistics candidateStatistic(CandidateRankDistribution distribution, boolean elected) {
		var rankVotes = distribution.getRankVotes();
		long ballots = 0;
		double rankSum = 0;
		for (int rank = 0; rank < rankVotes.size(); rank++) {
			ballots += rankVotes.get(rank);
			rankSum += (double) (rank + 1) * rankVotes.get(rank);
		}
		var builder = CandidateStatistics.builder()
				.candidate(distribution.getCandidate())
				.elected(elected)
				.ballots((int) ballots)
				.firstChoiceBallots(rankVotes.isEmpty() ? 0 : rankVotes.getFirst());
		if (ballots > 0) {
			builder.meanRank(rankSum / ballots).medianRank(median(rankVotes, ballots));
		}
		return builder.build();
	}

	/**
	 * Median of the rank values, where each rank {@code r} occurs {@code rankVotes[r - 1]} times. Averages the two middle
	 * values when the count is even.
	 */
	static double median(List<Integer> rankVotes, long count) {
		return (rankAt(rankVotes, (count + 1) / 2) + rankAt(rankVotes, count / 2 + 1)) / 2.0;
	}

	/**
	 * Rank at the given 1-based position when all ballots are sorted by rank.
	 */
	private static int rankAt(List<Integer> rankVotes, long position) {
		long seen = 0;
		for (int rank = 0; rank < rankVotes.size(); rank++) {
			seen += rankVotes.get(rank);
			if (seen >= position) {
				return rank + 1;
			}
		}
		throw new IllegalArgumentException("Position out of range: " + position);
	}

	private static Comparator<CandidateRankDistribution> rankDistributionOrdering(List<String> order) {
		var positions = new HashMap<String, Integer>();
		for (int i = 0; i < order.size(); i++) {
			positions.put(order.get(i), i);
		}
		return Comparator.comparing(
						(CandidateRankDistribution d) -> positions.getOrDefault(d.getCandidate(), Integer.MAX_VALUE))
				.thenComparing(CandidateRankDistribution::getCandidate);
	}

	private static String candidateName(BallotFile ballotFile, int id) {
		var name = ballotFile.getCandidateNames().get(id);
		if (name == null) {
			throw new IllegalArgumentException("Unknown candidate id: " + id);
		}
		return name;
	}

	void winnersAndEliminations(CsmAnalysis.CsmAnalysisBuilder data) {
		log.info("Calculating winners and eliminations");
		var finalResults = auditLog.getFinalResults();
		var seenWinners = new HashSet<String>();
		var seenEliminations = new HashSet<String>();
		var eliminations = new ArrayList<CandidateRound>();
		int r = -1;
		for (var round : auditLog.getRounds()) {
			r = round.getRoundNumber();
			for (var elected : round.getElectedCandidates()) {
				if (!finalResults.contains(elected)) {
					throw new IllegalArgumentException();
				}
				if (!seenWinners.contains(elected)) {
					data.winner(CandidateRound.builder()
							.candidate(elected)
							.round(r)
							.votes(votesAtElection(round, elected))
							.build());
					seenWinners.add(elected);
				}
			}
			var eliminated = round.getElimination().getCandidate();
			if (finalResults.contains(eliminated)) {
				throw new IllegalArgumentException();
			}
			if (!seenEliminations.contains(eliminated)) {
				eliminations.add(CandidateRound.builder()
						.candidate(eliminated)
						.round(r)
						.votes(round.getElimination().getVotes())
						.votesShortOfQuota(
								round.getQuota() - round.getElimination().getVotes())
						.build());
				seenEliminations.add(eliminated);
			}
		}
		for (var winner : auditLog.getFinalResults()) {
			if (!seenWinners.contains(winner)) {
				data.winner(CandidateRound.builder().candidate(winner).round(r).build());
			}
		}
		// Stored last eliminated first, matching the display order.
		Collections.reverse(eliminations);
		eliminations.forEach(data::elimination);
	}

	/**
	 * Votes a candidate held when elected: their initial talley plus the votes transferred to them by the transfers
	 * listed before their election in the round. Null if the candidate is not in the round's initial talley.
	 */
	static Double votesAtElection(Round round, String candidate) {
		var initial = round.getInitialTalley().get(candidate);
		if (initial == null) {
			return null;
		}
		var transfers = round.getVotesTransfers();
		var transfersBefore = round.getTransfersBeforeElection().getOrDefault(candidate, 0);
		var received = 0.0;
		for (var transfer : transfers.subList(0, transfersBefore)) {
			received += transfer.getToCandidates().getOrDefault(candidate, 0.0);
		}
		return initial + received;
	}

	private int findLeastSignificantRank() {
		var previous = previousLeastSignificantRank();
		if (previous.isPresent()) {
			log.info("Reusing least significant rank from previous analysis: {}", previous.get());
			return previous.get();
		}
		log.info("Finding least significant rank");
		var rank = new LeastSignificantRankRunner(
						csmConfig.getTalleyScriptFile(), ballotFile, auditLog.getFinalResults())
				.findLeastSignificantRank();
		return rank;
	}

	/**
	 * Reuses the rank from the previous analysis file if it is newer than every input file. Mtime based, so it can
	 * give false positives (e.g. a touched input that did not change), but never reuses a rank computed before an input
	 * was modified.
	 */
	private Optional<Integer> previousLeastSignificantRank() {
		var analysisFile = csmConfig.getAnalysisJson();
		if (analysisFile == null || !analysisFile.exists()) {
			return Optional.empty();
		}
		var newestInput = Stream.of(
						csmConfig.getVotesBlt(), csmConfig.getAuditLogTxt(), csmConfig.getTalleyScriptFile())
				.mapToLong(File::lastModified)
				.max()
				.orElseThrow();
		if (analysisFile.lastModified() <= newestInput) {
			log.debug("Analysis file older than inputs");
			return Optional.empty();
		}
		try {
			return Optional.of(
					objectMapper.readValue(analysisFile, CsmAnalysis.class).getLeastSignificantRank());
		} catch (IOException e) {
			log.warn("Unable to read previous analysis: {}", analysisFile, e);
			return Optional.empty();
		}
	}
}
