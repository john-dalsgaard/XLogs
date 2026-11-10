package dk.xpages.utils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.servlet.http.HttpServletRequest;

import com.ibm.xsp.designer.context.XSPContext;
import com.ibm.xsp.designer.context.XSPUrl;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;

import lotus.domino.ACL;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.View;
import lotus.domino.NotesException;
import lotus.domino.Session;

/**
 * 
 * Note: More handy utils in com.ibm.xsp.extlib.util.ExtLibUtil
 *
 */
public class XSPUtils implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DEBUG_KEY = "xlogs.debug.state";
	private static final String DEBUG_VIEW = "(XLogsDebugSettings)";

	public static XSPUrl getUrl() {
		try {
			return XSPContext.getXSPContext(getContext()).getUrl();
		} catch (Exception e) {
			return null;
		}
	}

	public static String getUrlParameter(String key) {
		try {
			return getUrl().getParameter(key);
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public static Object getVariableValue(String varName) {
		FacesContext context = getContext();
		return getContext().getApplication().getVariableResolver().resolveVariable(context, varName);
	}

	public static Session getCurrentSession() {
		return (Session) getVariableValue("session");
	}

	public static Database getCurrentDatabase() {
		return (Database) getVariableValue("database");
	}

	public static DominoDocument getCurrentDocument() {
		return (DominoDocument) getVariableValue("currentDocument");
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getSessionScope() {
		return (Map<String, Object>) getVariableValue("sessionScope");
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getApplicationScope() {
		return (Map<String, Object>) getVariableValue("applicationScope");
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getViewScope() {
		return (Map<String, Object>) getVariableValue("viewScope");
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRequestScope() {
		// JSF 1.x exposes a raw Map; its request attribute keys are Strings.
		return (Map<String, Object>) getExternalContext().getRequestMap();
	}

	public static InputStream getResourceAsStream(String filename) {
		return getExternalContext().getResourceAsStream(filename);
	}

	public static String getFullyQualifiedDatabaseURL() {
		HttpServletRequest request = getRequest();

		String scheme = request.getScheme();
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
		String contextPath = request.getContextPath();

		return scheme + "://" + serverName + ((serverPort == 80) ? "" : ":" + serverPort) + contextPath;
	}

	public static FacesContext getContext() {
		return FacesContext.getCurrentInstance();
	}

	public static ExternalContext getExternalContext() {
		return getContext().getExternalContext();
	}

	public static HttpServletRequest getRequest() {
		return (HttpServletRequest) getContext().getExternalContext().getRequest();

	}

	public static Object getBean(String expr) {
		Application app = getContext().getApplication();
		ValueBinding binding = app.createValueBinding("#{" + expr + "}");
		Object value = binding.getValue(getContext());
		return value;
	}

	public static void addWarning(String message) {
		getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, message, ""));
	}

	public static void addInfo(String message) {
		getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, ""));
	}

	public static void addError(String message) {
		getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, ""));
	}

	public static void addFatal(String message) {
		getContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, message, ""));
	}

	public static String getReadableErrorMessage(Throwable t) {
		StackTraceElement e = t.getStackTrace()[0];
		String msg = NotesStrings.messageFormat("{0} in {1}.{2}, line: {3}", t.getMessage(), e.getClassName(), e.getMethodName(), e.getLineNumber());
		return msg;
	}

	public static void addFatal(Throwable t) {
		addFatal(getReadableErrorMessage(t));
	}


	// Cache only document IDs across requests; always read the current document state.
	public static String getDebugServer() throws NotesException {
		String server = getCurrentSession().getServerName();
		if (server == null || server.length() == 0) throw new IllegalStateException("Debug settings require a Domino server.");
		return server;
	}

	public static boolean canManageDebug() {
		try {
			Session session = getCurrentSession();
			String user = session.getEffectiveUserName();
			if (user == null || "Anonymous".equalsIgnoreCase(user)) return false;
			Database db = getCurrentDatabase();
			// Internet access may be capped at Editor even for database Managers.
			return db.getCurrentAccessLevel() >= ACL.LEVEL_EDITOR
					&& db.queryAccessRoles(user).contains("[Admin]");
		} catch (Exception e) {
			return false;
		}
	}

	/** Compatibility for About controls deployed with the earlier access diagnostic. */
	@Deprecated
	public static String getDebugAccessStatus() {
		return canManageDebug()
				? "Debug administration is available."
				: "Debug administration requires the [Admin] role and at least Editor web access.";
	}

	private static String debugDocumentKey(String server) {
		return "xlogs.debug.unid." + server;
	}

	private static Document findDebugSettings(Database db, String server) throws NotesException {
		String key = debugDocumentKey(server);
		Object unid = getApplicationScope().get(key);
		if (unid instanceof String) {
			try {
				Document doc = db.getDocumentByUNID((String) unid);
				if (doc != null && !doc.isDeleted()) return doc;
				if (doc != null) doc.recycle();
			} catch (NotesException e) {
				// Recover from a deleted/replaced configuration via the keyed view.
			}
			getApplicationScope().remove(key);
		}
		View view = db.getView(DEBUG_VIEW);
		if (view == null) return null;
		try {
			Document doc = view.getDocumentByKey(server, true);
			if (doc != null) getApplicationScope().put(key, doc.getUniversalID());
			return doc;
		} finally {
			view.recycle();
		}
	}

	private static void requireDebugView(Database db) throws NotesException {
		View view = db.getView(DEBUG_VIEW);
		if (view == null) throw new IllegalStateException("Required design view is missing: " + DEBUG_VIEW);
		view.recycle();
	}

	private static long[] debugState() {
		if (getContext() == null) return new long[] {0, 0, 180};
		// Debug can be called while the XPages variable resolver is initializing beans.
		// Access the JSF request map directly and mark the lookup before touching Domino.
		Map<String, Object> request;
		try {
			request = getRequestScope();
		} catch (Exception e) {
			return new long[] {0, 0, 180};
		}
		Object cached = request.get(DEBUG_KEY);
		if (cached instanceof long[]) return (long[]) cached;
		long[] state = new long[] {0, 0, 180};
		request.put(DEBUG_KEY, state);
		Document doc = null;
		try {
			doc = findDebugSettings(getCurrentDatabase(), getDebugServer());
			if (doc != null) {
				String duration = doc.getItemValueString("DebugDurationMinutes");
				if (duration.length() > 0) state[2] = Long.parseLong(duration);
			}
			if (doc != null && "1".equals(doc.getItemValueString("DebugEnabled"))) {
				String expiry = doc.getItemValueString("DebugExpiresAt");
				// Legacy settings without an explicit expiry are disabled until saved again.
				if (expiry.length() > 0) {
					state[1] = Long.parseLong(expiry);
					state[0] = state[1] >= 0 ? 1 : 0;
				}
			}
		} catch (Exception e) {
			// Fail closed without calling the logger recursively.
		} finally {
			try { if (doc != null) doc.recycle(); } catch (NotesException e) { }
		}
		request.put(DEBUG_KEY, state);
		return state;
	}

	public static boolean isDebugEnabled() {
		long[] state = debugState();
		return state[0] == 1 && (state[1] == 0 || System.currentTimeMillis() < state[1]);
	}

	/** Format application timestamps in the server JVM's time zone, independent of browser locale. */
	public static String formatServerDate(java.util.Date date) {
		if (date == null) return "";
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", java.util.Locale.ENGLISH);
		format.setTimeZone(java.util.TimeZone.getDefault());
		return format.format(date);
	}

	public static String getDebugExpiry() {
		long[] state = debugState();
		if (!isDebugEnabled()) return "Disabled";
		return state[1] == 0 ? "No automatic expiry" : formatServerDate(new java.util.Date(state[1]));
	}

	public static String getDebugDurationSelection() {
		long minutes = debugState()[2];
		return Long.toString(minutes == 0 || minutes == 15 || minutes == 30 || minutes == 60 || minutes == 180 ? minutes : 180);
	}

	public static void setDebugEnabled(boolean enabled) throws NotesException {
		setDebugEnabled(enabled, 180);
	}

	public static void setDebugEnabled(boolean enabled, int minutes) throws NotesException {
		if (!canManageDebug()) throw new SecurityException("The [Admin] role and at least Editor access are required to change debug logging.");
		if (minutes < 0 || minutes > 180) throw new IllegalArgumentException("Choose 0 (no expiry) or 1 to 180 minutes.");
		Database db = getCurrentDatabase();
		String server = getDebugServer();
		// Serialize document creation within this application to avoid duplicates.
		synchronized (getApplicationScope()) {
			requireDebugView(db);
			Document doc = null;
			try {
				doc = findDebugSettings(db, server);
				if (doc == null) doc = db.createDocument();
				long expiry = enabled && minutes > 0 ? System.currentTimeMillis() + minutes * 60000L : 0;
				doc.replaceItemValue("Form", "XLogsDebugSettings");
				doc.replaceItemValue("DebugServer", server);
				doc.replaceItemValue("DebugEnabled", enabled ? "1" : "0");
				doc.replaceItemValue("DebugExpiresAt", Long.toString(expiry));
				if (enabled) doc.replaceItemValue("DebugDurationMinutes", Integer.toString(minutes));
				doc.replaceItemValue("DebugChangedBy", getCurrentSession().getEffectiveUserName());
				if (!doc.save()) throw new IllegalStateException("Could not save debug settings.");
				getApplicationScope().put(debugDocumentKey(server), doc.getUniversalID());
				getRequestScope().put(DEBUG_KEY, new long[] {enabled ? 1 : 0, expiry, enabled ? minutes : debugState()[2]});
			} finally {
				try { if (doc != null) doc.recycle(); } catch (NotesException e) { }
			}
		}
	}

	public static void debug(String x) {
		if (isDebugEnabled()) {
			System.out.println(x);
		}
	}

}