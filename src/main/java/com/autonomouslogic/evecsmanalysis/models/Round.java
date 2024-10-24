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

	Map<String, Double> initialTalley;

	@Singular
	List<String> electedCandidates;

	@Singular
	List<VotesTransfer> votesTransfers;

	Map<String, Double> preEliminationTalley;

	Elimination elimination;
}
