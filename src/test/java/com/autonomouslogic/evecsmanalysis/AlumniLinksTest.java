package com.autonomouslogic.evecsmanalysis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.autonomouslogic.evecsmanalysis.models.Alumni;
import com.autonomouslogic.evecsmanalysis.models.Alumnus;
import org.junit.jupiter.api.Test;

public class AlumniLinksTest {
	@Test
	public void shouldLinkAlumnus() {
		var alumni = new Alumni();
		alumni.getAlumni().put("Ali Aras", new Alumnus().setName("Ali Aras"));
		assertEquals("<a href=\"/alumni/ali-aras/\">Ali Aras</a>", new AlumniLinks(alumni).html("Ali Aras"));
	}

	@Test
	public void shouldLeaveNonAlumnusUnlinked() {
		assertEquals("Nobody", new AlumniLinks(new Alumni()).html("Nobody"));
	}
}
