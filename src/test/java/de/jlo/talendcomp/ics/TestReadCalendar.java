package de.jlo.talendcomp.ics;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestReadCalendar {

	@Test
	public void testRead() throws Exception {
		String icsFilePath = "/Users/jan/development/testdata/ics/calendar_bankholiday.ics";
		ICSReader r = new ICSReader();
		r.setIcsFilePath(icsFilePath);
		r.setup();
		System.out.println("total: " + r.getTotalCountEntries());
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
//		assertEquals(2, r.getCountSkippedEntires());
//		assertEquals(236, r.getTotalCountEntries());
	}

}
