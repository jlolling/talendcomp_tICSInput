package de.jlo.talendcomp.ics;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestReadCalendar {

	@Test
	public void testRead() throws Exception {
		String icsFilePath = "src/test/resources/calendar_bankholiday.ics";
		ICSReader r = new ICSReader();
		r.setIcsFilePath(icsFilePath);
		r.setup();
		System.out.println("total: " + r.getTotalCountEntries());
		System.out.println("skipped: " + r.getCountSkippedEntires());
		while (r.next()) {
			System.out.println("################################## " + r.getCurrentEntryIndex());
			System.out.println(r.getAllCurrentPropertiesAsString());
			System.out.println("Name:        " + r.getName());
			System.out.println("Created:     " + r.getPropertyAsDate("CREATED", true));
			System.out.println("Start:       " + r.getPropertyAsDate("DTSTART", true));
			System.out.println("End:         " + r.getPropertyAsDate("DTEND", true));
			System.out.println("last-modified:         " + r.getPropertyAsDate("LAST-MODIFIED", true));
//			System.out.println("Description: " + r.getPropertyAsString("DESCRIPTION", true));
		}
		assertEquals(0, r.getCountSkippedEntires());
		assertEquals(116, r.getTotalCountEntries());
	}

}
