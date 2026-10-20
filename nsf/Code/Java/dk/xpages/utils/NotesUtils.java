package dk.xpages.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import dk.xpages.log.LogManager;
import dk.xpages.log.Logger;

public class NotesUtils {

	private static Logger log = LogManager.getLogger();

	public static List<Object> evaluate(String formula, Document doc) {
		Session session = XSPUtils.getCurrentSession();

		if (NotesStrings.isBlank(formula)) {
			return new ArrayList<Object>();
		}

		try {
			Vector<?> vector = session.evaluate(formula, doc);
			return new ArrayList<Object>(vector);
		} catch (NotesException e) {
			log.error(e);
			List<Object> result = new ArrayList<Object>();
			result.add(e.getMessage());
			return result;
		}
	}

	public static List<Object> evaluate(String formula) {
		return evaluate(formula, null);
	}

	public static String evaluateString(String formula, Document doc) {
		List<Object> result = evaluate(formula, doc);
		return NotesStrings.join(";", result);
	}

	public static String evaluateString(String formula) {
		return evaluateString(formula, null);
	}
}
