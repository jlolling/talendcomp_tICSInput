package de.jlo.talendcomp.ics;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.Date;
import java.util.Iterator;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.util.CompatibilityHints;

public class ICSReader {
	
	private String icsFilePath = null;
	private String icsContent = null;
	
	private Calendar calendar = null;
	private Iterator<Component> calIterator = null;
	private Component oneEntry = null;
	private boolean parseRelaxed = true;
	private int totalCountEntries = 0;
	private int currentEntryIndex = 0;
	private int countSkippedEntires = 0;
	private boolean skipNoneEventEntries = true;
	
	@SuppressWarnings("unchecked")
	public void setup() throws Exception {
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, parseRelaxed);
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, parseRelaxed);
		if (icsFilePath != null) {
			File f = new File(icsFilePath);
			if (f.exists() == false) {
				throw new Exception("The given ics-file path: " + f.getAbsolutePath() + " does not exist!");
			}
			CalendarBuilder builder = new CalendarBuilder();
			try (FileInputStream in = new FileInputStream(f)) {
				calendar = builder.build(in);
			} catch (Throwable t) {
				throw new Exception("Fail to parse calendar input from file: " + f.getAbsolutePath(), t);
			}
		} else if (icsContent != null) {
			if (icsContent.trim().isEmpty()) {
				throw new Exception("The given ics content is empty!");
			}
			CalendarBuilder builder = new CalendarBuilder();
			try (StringReader in = new StringReader(icsContent)) {
				calendar = builder.build(in);
			} catch (Throwable t) {
				throw new Exception("Fail to parse calendar input from string content:\n" + icsContent, t);
			}
		} else {
			throw new Exception("Neither ics file path nor string content is given. Please provide one of them!");
		}
		ComponentList list = calendar.getComponents();
		totalCountEntries = list.size();
		calIterator = list.iterator();
	}
		
	public boolean next() {
		if (calIterator.hasNext()) {
			oneEntry = null;
			while (oneEntry == null) {
		    	oneEntry = calIterator.next();
		    	if (skipNoneEventEntries) {
			    	Property uid = oneEntry.getProperty("UID");
			    	if (uid == null) {
			    		oneEntry = null;
			    		countSkippedEntires++;
			    		if (calIterator.hasNext() == false) {
			    			break;
			    		}
			    	}
		    	}
			}
	    	currentEntryIndex++;
			return oneEntry != null;
		}
		return false;
	}
	
	public String getAllCurrentPropertiesAsString() {
		StringBuilder sb = new StringBuilder();
		for (@SuppressWarnings("unchecked")	Iterator<Property> j = oneEntry.getProperties().iterator(); j.hasNext();) {
			Property property = (Property) j.next();
			sb.append(property.getName());
			sb.append("=");
			sb.append(property.getValue());
			sb.append("\n");
	    }
		return sb.toString();
	}
	
	public String getName() throws Exception {
		String name = getPropertyAsString("SUMMARY", true);
		if (name == null || name.trim().isEmpty()) {
			name = getPropertyAsString("TITLE", true);
		}
		if (name == null || name.trim().isEmpty()) {
			name = getPropertyAsString("NAME", true);
		}
		return name;
	}
	
	public String getPropertyAsString(String key, boolean ignoreMissing) throws Exception {
		String value = null;
		Property property = null;
		for (@SuppressWarnings("unchecked")	Iterator<Property> j = oneEntry.getProperties().iterator(); j.hasNext();) {
	        property = (Property) j.next();
	        if (key.equalsIgnoreCase(property.getName())) {
	        	value = property.getValue();
	        	break;
	        } else {
	        	property = null;
	        }
	    }
		if (ignoreMissing == false && property == null) {
			throw new Exception("Mandatory property with name: " + key + "  NOT found!");
		}
		return value;
	}

	public Date getPropertyAsDate(String key, boolean ignoreMissing) throws Exception {
		String s = getPropertyAsString(key, ignoreMissing);
		Date value = GenericDateUtil.parseDate(s, "yyyyMMdd'T'HHmmss");
		return value;
	}

	public Integer getPropertyAsInt(String key, boolean ignoreMissing) throws Exception {
		String s = getPropertyAsString(key, ignoreMissing);
		if (s != null && s.trim().isEmpty() == false) {
			return Integer.parseInt(s);
		}
		return null;
	}

	public Long getPropertyAsLong(String key, boolean ignoreMissing) throws Exception {
		String s = getPropertyAsString(key, ignoreMissing);
		if (s != null && s.trim().isEmpty() == false) {
			return Long.parseLong(s);
		}
		return null;
	}

	public Boolean getPropertyAsBoolean(String key, boolean ignoreMissing) throws Exception {
		String s = getPropertyAsString(key, ignoreMissing);
		if (s != null && s.trim().isEmpty() == false) {
			return TypeUtil.convertToBoolean(s);
		}
		return null;
	}

	public Double getPropertyAsDouble(String key, boolean ignoreMissing) throws Exception {
		String s = getPropertyAsString(key, ignoreMissing);
		if (s != null && s.trim().isEmpty() == false) {
			return Double.parseDouble(s);
		}
		return null;
	}

	public String getIcsFilePath() {
		return icsFilePath;
	}

	public void setIcsFilePath(String icsFilePath) {
		this.icsFilePath = icsFilePath;
	}

	public String getIcsContent() {
		return icsContent;
	}

	public void setIcsContent(String icsContent) {
		this.icsContent = icsContent;
	}

	public void setParseRelaxed(boolean parseRelaxed) {
		this.parseRelaxed = parseRelaxed;
	}

	public int getTotalCountEntries() {
		return totalCountEntries;
	}

	public int getCurrentEntryIndex() {
		return currentEntryIndex;
	}

	public int getCountSkippedEntires() {
		return countSkippedEntires;
	}
	
}
