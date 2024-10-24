package com.autonomouslogic.evecsmanalysis.parser;

import com.autonomouslogic.evecsmanalysis.models.AuditLog;
import java.io.File;
import java.util.List;
import lombok.NonNull;

public class AuditLogParser extends AbstractParser {
	public AuditLogParser(List<String> lines) {
		super(lines);
	}

	public AuditLogParser(@NonNull File file) {
		super(file);
	}

	public AuditLogParser(@NonNull String contents) {
		super(contents);
	}

	public AuditLog parse() {}
}
