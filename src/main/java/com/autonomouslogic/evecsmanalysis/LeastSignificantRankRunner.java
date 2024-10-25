package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.Ballot;
import com.autonomouslogic.evecsmanalysis.models.BallotFile;
import com.autonomouslogic.evecsmanalysis.parser.AuditLogParser;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

@RequiredArgsConstructor
@Log4j2
public class LeastSignificantRankRunner {
	private final File baseTalleyScript;
	private final BallotFile baseBallotFile;
	private final List<String> baseResult;

	public int findLeastSignificantRank() {
		var baseResultSet = new HashSet<>(baseResult);
		for (int r = 10; r >= 1; r--) {
			log.info("Simulating rank: {}", r);
			var result = simulate(r);
			if (!baseResultSet.equals(new HashSet<>(result))) {
				log.info("Least significant rank found: {}", r);
				return r;
			}
		}
		throw new IllegalStateException("Least significant rank not found");
	}

	@SneakyThrows
	private List<String> simulate(int rank) {
		var dir = Files.createTempDirectory("least-significant-rank").toFile();
		var talleyScript = new File(dir, baseTalleyScript.getName());
		var ballotFile = new File(dir, "votes.blt");
		var auditLogFile = new File(dir, "auditLog.txt");
		FileUtils.copyFile(baseTalleyScript, talleyScript);
		var truncatedBallot = truncateBallotFile(baseBallotFile, rank);
		FileUtils.write(ballotFile, truncatedBallot.toString(), StandardCharsets.UTF_8);
		runTalley(dir, talleyScript);
		var auditLog = new AuditLogParser(auditLogFile).parse();
		FileUtils.deleteDirectory(dir);
		return auditLog.getFinalResults();
	}

	private BallotFile truncateBallotFile(BallotFile ballot, int rank) {
		var votes = ballot.getAllVotes().stream()
				.map(v -> v.toBuilder()
						.ballot(truncateBallot(v.getBallot(), rank))
						.build())
				.toList();
		return ballot.toBuilder().clearAllVotes().allVotes(votes).build();
	}

	private Ballot truncateBallot(Ballot ballot, int rank) {
		var trunc = ballot.getRankings().stream().limit(rank - 1).toList();
		return ballot.toBuilder().clearRankings().rankings(trunc).build();
	}

	@SneakyThrows
	private void runTalley(File dir, File talleyScript) {
		var out = new File(dir, talleyScript.getName() + ".out");
		var err = new File(dir, talleyScript.getName() + ".err");
		var process = new ProcessBuilder(
						"docker",
						"run",
						"-v",
						".:/data",
						"python:2",
						"bash",
						"-c",
						"cd /data && python " + talleyScript.getName())
				.directory(dir)
				.redirectOutput(out)
				.redirectError(err)
				.start();
		var exitCode = process.waitFor();
		if (exitCode != 0) {
			throw new IOException("Talley script failed with exit code: {} - output in {}, error in {}"
					.formatted(exitCode, out, err));
		}
	}
}
