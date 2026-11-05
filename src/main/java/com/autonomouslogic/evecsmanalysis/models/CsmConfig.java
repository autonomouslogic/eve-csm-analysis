package com.autonomouslogic.evecsmanalysis.models;

import java.io.File;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CsmConfig {
	int csmNumber;

	File csmDir;

	File csmData;

	File talleyScriptFile;

	File votesBlt;

	File auditLogTxt;

	/**
	 * Results of a first-past-the-post election. Only present for those elections, instead of the ballot files.
	 */
	File resultsCsv;

	File votesJson;

	File auditLogJson;

	File markdownFile;

	File analysisJson;
}
