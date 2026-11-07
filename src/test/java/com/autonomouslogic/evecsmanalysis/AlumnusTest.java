package com.autonomouslogic.evecsmanalysis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.autonomouslogic.evecsmanalysis.models.Alumnus;
import org.junit.jupiter.api.Test;

public class AlumnusTest {
	@Test
	public void shouldSlugifyNames() {
		assertEquals("tamber", new Alumnus().setName("T'Amber").slug());
		assertEquals("jinx-decaire", new Alumnus().setName("Jinx De'Caire").slug());
		assertEquals(
				"rhiload-feron-drake",
				new Alumnus().setName("rhiload Feron-drake").slug());
	}
}
