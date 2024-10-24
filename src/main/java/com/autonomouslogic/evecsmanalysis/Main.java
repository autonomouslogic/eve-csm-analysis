package com.autonomouslogic.evecsmanalysis;

import java.io.File;

public class Main {
	public static void main(String[] args) {
		new AnalysisRenderer(18, new File("csm18/votes.blt")).render(new File("csm18/index.md"));
	}
}
