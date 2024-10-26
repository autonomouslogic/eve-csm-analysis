package com.autonomouslogic.evecsmanalysis.models;

import java.io.File;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CsmConfig {
	int csmNumber;

	File csmDir;

	File talleyScriptFile;

	File votesBlt;

	File auditLogTxt;

	File votesJson;

	File auditLogJson;

	File markdownFile;

	File analysisJson;
}
