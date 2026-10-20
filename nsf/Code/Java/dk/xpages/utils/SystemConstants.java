package dk.xpages.utils;

// 2022.11.23/Jda
// Dalsgaard Data A/S, John Dalsgaard
// Keep limitation values here. These are used to ensure that the application can run in various environments without timing out
public class SystemConstants {
	private static final int TEXTFILE_MAX_LINES = 5000;
	private static final int LOGFILE_MAX_DOCS = 1000;
	private static int maxTextLines = 0;
	private static int maxLogFileDocs = 0;
	private static boolean maxTextLinesSilenced = false;
	private static boolean maxLogFileDocsSilenced = false;

	private static boolean setLimitSilenced(XML itemXML) {
		return NotesStrings.equalsIgnoreCase(itemXML.string("silence"), "true");
	}

	private static int setLimitCount(XML itemXML) {
		if (NotesStrings.isNotEmpty(itemXML.string("max"))) {
			return Integer.parseInt(itemXML.string("max"));
		}
		return 0;
	}

	public static void loadLimits(XML limitsXML) {
		if (NotesStrings.equalsIgnoreCase(limitsXML.name(), "limits")) {
			//Root element
			for (XML itemXML : limitsXML.children("item")) {
				if (NotesStrings.equalsIgnoreCase(itemXML.string("id"), "MaxTextLines")) {
					maxTextLines = setLimitCount(itemXML);
					maxTextLinesSilenced = setLimitSilenced(itemXML);
				} else if (NotesStrings.equalsIgnoreCase(itemXML.string("id"), "MaxLogFileDocs")) {
					maxLogFileDocs = setLimitCount(itemXML);
					maxLogFileDocsSilenced = setLimitSilenced(itemXML);
				}
			}
		}
	}

	public static int getMaxTextLines() {
		return maxTextLines == 0 ? TEXTFILE_MAX_LINES : maxTextLines;
	}

	public static int getMaxLogFileDocs() {
		return maxLogFileDocs == 0 ? LOGFILE_MAX_DOCS : maxLogFileDocs;
	}

	public static boolean isMaxTextLinesSilenced() {
		return maxTextLinesSilenced;
	}

	public static boolean isMaxLogFileDocsSilenced() {
		return maxLogFileDocsSilenced;
	}

}
