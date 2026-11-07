package com.autonomouslogic.evecsmanalysis.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.autonomouslogic.evecsmanalysis.models.Ballot;
import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import com.autonomouslogic.evecsmanalysis.models.Votes;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class CsmBallotsCsvTest {
	@TempDir
	Path tempDir;

	@Test
	public void shouldReplaceNumbersWithNamesAndPadShortBallots() throws Exception {
		var ballotFile = BallotFile.builder()
				.candidateCount(3)
				.seatCount(1)
				.candidateName(1, "Alice")
				.candidateName(2, "Bob, Jr")
				.candidateName(3, "Carol")
				.allVote(votes(5, 2, 1, 3))
				.allVote(votes(2, 3))
				.build();
		var file = tempDir.resolve("ballots.csv").toFile();
		new CsmBallotsCsv(ballotFile, file).write();
		assertEquals(
				"Ballots,Rank 1,Rank 2,Rank 3\n5,\"Bob, Jr\",Alice,Carol\n2,Carol,,\n", read(file));
	}

	private static Votes votes(int count, Integer... rankings) {
		return Votes.builder()
				.count(count)
				.ballot(Ballot.builder().rankings(List.of(rankings)).build())
				.build();
	}

	private static String read(File file) throws Exception {
		return Files.readString(file.toPath(), StandardCharsets.UTF_8);
	}
}
