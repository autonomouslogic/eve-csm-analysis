package com.autonomouslogic.evecsmanalysis;

import java.io.File;

public class Main {
	public static void main(String[] args) {
		var data = new AnalysisRunner(new File("csm18/votes.blt")).run();
		new AnalysisRenderer(18, data).render(new File("csm18/index.md"));
	}
}
