package com.autonomouslogic.evecsmanalysis.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.autonomouslogic.evecsmanalysis.Main;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AuditLogParserTest {
	@Test
	@SneakyThrows
	public void shouldParseAuditLogs() {
		var auditLog = new AuditLogParser(new File("csm18/auditLog.txt")).parse();
		System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(auditLog));

		var round1 = auditLog.getRounds().get(0);
		assertEquals(1, round1.getRoundNumber());
		assertEquals(49, round1.getCandidatesLeft());
		assertEquals(47155, round1.getVotesCount());
		assertEquals(4287, round1.getQuota());

		var round1InitialTalley = round1.getInitialTalley();
		assertEquals(6741, round1InitialTalley.get("Kazanir"));
		assertEquals(6501, round1InitialTalley.get("Alcoholic Satan"));
		assertEquals(49, round1InitialTalley.size());

		assertEquals(List.of("Kazanir", "Alcoholic Satan"), round1.getElectedCandidates());

		var round1Transfers = round1.getVotesTransfers();
		assertEquals(2, round1Transfers.size());
		assertEquals("Kazanir", round1Transfers.get(0).getFromCandidate());
		assertEquals(6741.000000, round1Transfers.get(0).getVotes());
		assertEquals(0.364041, round1Transfers.get(0).getFactor());
		assertEquals(2454.000000, round1Transfers.get(0).getExcess());
		assertEquals(2301.830886, round1Transfers.get(0).getToCandidates().get("Angry Mustache"));
		assertEquals(39.680463, round1Transfers.get(0).getToCandidates().get("Kontan Rekor"));
		assertEquals(21.842457, round1Transfers.get(0).getToCandidates().get("Exhausted"));
		assertEquals(0.364041, round1Transfers.get(0).getToCandidates().get("Dhuras"));
		assertEquals("Alcoholic Satan", round1Transfers.get(1).getFromCandidate());

		var preEliminationTallies = round1.getPreEliminationTalley();
		assertEquals(3529, preEliminationTallies.get("Luke Anninan"));
		assertEquals(3326, preEliminationTallies.get("Angry Mustache"));

		assertEquals(39, auditLog.getRounds().size());

		assertEquals(32, auditLog.getRounds().get(31).getRoundNumber());
		assertEquals(
				List.of("Alcoholic Satan", "Kazanir", "Luke Anninan", "Angry Mustache"),
				auditLog.getRounds().get(31).getElectedCandidates());

		assertEquals(
				List.of(
						"The Oz ",
						"Mark Resurrectus",
						"Storm Delay",
						"Mike Azariah",
						"Alcoholic Satan",
						"Kazanir",
						"Angry Mustache",
						"Amelia Duskspace",
						"Dark Shines",
						"Luke Anninan"),
				auditLog.getFinalResults());
	}

	@ParameterizedTest
	@MethodSource("csmDirs")
	public void shouldParseAllAuditLogsWithoutErroring(File dir) {
		new AuditLogParser(new File(dir, "auditLog.txt")).parse();
	}

	public static Stream<Arguments> csmDirs() {
		return Main.getAllCsmDirs()
				.filter(d -> new File(d, "votes.blt").exists())
				.map(d -> Arguments.of(d));
	}
}
