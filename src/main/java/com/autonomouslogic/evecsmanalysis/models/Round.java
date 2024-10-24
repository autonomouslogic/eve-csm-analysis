package com.autonomouslogic.evecsmanalysis.models;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Round {
	int roundNumber;

	int votesCount;

	int candidatesLeft;

	double quota;

	@Singular
	Map<String, Double> initialTallies;

	@Singular
	List<String> electedCandidates;

	@Singular
	List<VotesTransfer> votesTransfers;

	@Singular
	Map<String, Double> preEliminationTallies;

	Elimination elimination;
}
