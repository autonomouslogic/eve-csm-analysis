package com.autonomouslogic.evecsmanalysis;

import com.autonomouslogic.evecsmanalysis.models.Alumni;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Turns candidate names into links to their alumni pages. Used by the Thymeleaf templates.
 */
@RequiredArgsConstructor
public class AlumniLinks {
	@NonNull
	private final Alumni alumni;

	/**
	 * Returns a link to the alumnus page if the name is an alumnus, otherwise the name unchanged.
	 */
	public String html(String name) {
		var alumnus = alumni.getAlumni().get(name);
		if (alumnus == null) {
			return name;
		}
		return "<a href=\"/alumni/%s/\">%s</a>".formatted(alumnus.slug(), name);
	}
}
