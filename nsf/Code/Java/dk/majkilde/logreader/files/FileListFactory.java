package dk.majkilde.logreader.files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;
import java.io.File;
import java.io.IOException;

import dk.xpages.log.LogManager;
import dk.xpages.log.Logger;
import dk.xpages.utils.NotesObjects;
import dk.xpages.utils.SystemConstants;
import dk.xpages.utils.XML;
import dk.xpages.utils.XMLException;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.View;

public class FileListFactory {

	private static Logger log = LogManager.getLogger();

	static public FileList create(XML config) throws Exception {
		dk.xpages.utils.XSPUtils.debug("FileListFactory: create...");

		String filetype = config.string("filetype");

		if (filetype.equalsIgnoreCase("text")) {
			return getTextFiles(config);
		}

		if (filetype.equalsIgnoreCase("xml")) {
			return getXMLFiles(config);
		}

		if (filetype.equalsIgnoreCase("log")) {
			return getLogFiles(config);
		}

		throw new XMLException("config.xml: required filetype attribute is missing");
	}

	private static Filters getFilters(XML config) {
		return new Filters(config.child("filters"));
	}

	private static FileList getTextFiles(XML config) throws XMLException {
		if (!config.hasChild("filename")) {
			throw new XMLException("config.xml: Required <filename> is missing");
		}

		Filters filters = getFilters(config);
		List<IFile> files = new ArrayList<IFile>();
		Set<String> seen = new LinkedHashSet<String>();
		List<String> patterns = new ArrayList<String>();
		List<XML> filenameConfigs = config.children("filename");
		String pagingKey = filenameConfigs.get(0).content();
		for (XML filenameConfig : filenameConfigs) {
			String pattern = filenameConfig.content();
			patterns.add(Directory.getCleanPattern(pattern));
			String encoding = filenameConfig.string("encoding");
			for (String filename : Directory.getFileNames(pattern)) {
				File candidate = new File(filename);
				String key;
				try {
					key = candidate.getCanonicalPath();
				} catch (IOException e) {
					key = candidate.getAbsolutePath();
				}
				if (seen.add(key)) {
					files.add(new TextFile(filename, filters).setEncoding(encoding).setPagingKey(pagingKey));
				}
			}
		}
		Collections.sort(files);
		return new FileList(files, dk.xpages.utils.NotesStrings.join(" | ", patterns));
	}

	private static FileList getXMLFiles(XML config) throws XMLException {
		if (!config.hasChild("filename")) {
			throw new XMLException("config.xml: Required <filename> is missing");
		}

		String pattern = config.child("filename").content();
		String xlsFilename = null;

		XML transform = config.child("transform");
		if (transform != null) {
			xlsFilename = transform.content();
		}

		List<String> filenames = Directory.getFileNames(pattern);

		String encoding = config.child("filename").string("encoding");

		List<IFile> files = new ArrayList<IFile>();
		for (String filename : filenames) {
			files.add(new XMLFile(filename, xlsFilename).setEncoding(encoding));
		}

		Collections.sort(files);

		return new FileList(files, pattern);
	}

	private static FileList getLogFiles(XML config) throws XMLException {
		if (!config.hasChild("filename")) {
			throw new XMLException("config.xml: Required <filename> is missing");
		}

		String filename = config.child("filename").content();
		String viewname = "($SearchEventsView)";
		dk.xpages.utils.XSPUtils.debug("getLogFiles: " + filename);
		Filters filters = getFilters(config);

		Database db = null;
		View view = null;
		Document doc = null;
		Document nextdoc = null;

		List<IFile> files = new ArrayList<IFile>();
		int i = 0;
		try {
			db = NotesObjects.getDatabaseAsSigner(filename);
			if (db == null || !db.isOpen()) {
				throw new IllegalStateException("Cannot open log database '" + filename + "' as signer");
			}
			if (db.isOpen()) {
				view = NotesObjects.getView(db, viewname);
				if (view == null) {
					throw new IllegalStateException("View '" + viewname + "' is unavailable in '" + filename + "'");
				}
				view.setAutoUpdate(false);

				//doc = view.getFirstDocument();
				doc = view.getLastDocument(); // Get the newest (if we have to restrict count)
				while (doc != null) {
					i++;
					if (i % 10 == 0) dk.xpages.utils.XSPUtils.debug("getLogFiles: read document (" + i + ")...");

					IFile nsffile = new LognsfDoc(filename, doc, filters);
					files.add(nsffile);

					//nextdoc = view.getNextDocument(doc);
					nextdoc = view.getPrevDocument(doc); // From the newest
					NotesObjects.incinerate(doc);
					doc = nextdoc;
					// Stop breaking by limiting number of read docs.......
					if (i >= SystemConstants.getMaxLogFileDocs()) {
						//dk.xpages.utils.XSPUtils.debug("getLogFiles: Stop after loading " + i + " documents");
						if (!SystemConstants.isMaxLogFileDocsSilenced()) System.out.println("XLogs: Limit to last " + i + " documents from " + filename);
						NotesObjects.incinerate(doc);
						doc = null;
					}
				}
			}
		} catch (Exception e) {
			log.error(e);
		} finally {
			NotesObjects.incinerate(doc, view, db);
		}

		Collections.sort(files);

		return new FileList(files, filename);
	}
}
