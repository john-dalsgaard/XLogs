package dk.majkilde.logreader.files;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import dk.xpages.log.LogManager;
import dk.xpages.log.Logger;
import dk.xpages.utils.NotesStrings;
import dk.xpages.utils.XSPUtils;

public class FileList implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Logger log = LogManager.getLogger();

	private IFile current = null;

	private final List<IFile> files;
	private final String pattern;

	public FileList(List<IFile> files, String pattern) {
		dk.xpages.utils.XSPUtils.debug("FileList: Constructor...");
		this.files = files;
		this.pattern = pattern;
	}

	private final java.util.Map<String, Object> selected = new java.util.HashMap<String, Object>();

	public java.util.Map<String, Object> getSelected() { return selected; }

	private boolean isSelected(String path) {
		// XPages checkboxes can submit the checked value as a String.
		return Boolean.parseBoolean(String.valueOf(selected.get(path)));
	}

	/** Plain HTML inputs deliberately avoid the repeat control's per-row UIInput state. */
	public String getSelectionHtml(IFile file, String submitId) {
		int index = files.indexOf(file);
		String safeId = submitId.replace("&", "&amp;").replace("'", "&#39;").replace("\"", "&quot;");
		return "<input type=\"hidden\" name=\"xlogsZipVisible\" value=\"" + index + "\" />"
			+ "<input type=\"checkbox\" name=\"xlogsZipSelected\" value=\"" + index + "\""
			+ (isSelected(file.getPath()) ? " checked=\"checked\"" : "")
			+ " title=\"Select file for ZIP download\" style=\"margin:0 4px 0 0;flex:0 0 auto;\""
			+ " onchange=\"document.getElementById('" + safeId + "').click();\" />";
	}

	public void updateSelection() {
		java.util.Map<?, ?> parameters = javax.faces.context.FacesContext.getCurrentInstance()
			.getExternalContext().getRequestParameterValuesMap();
		String[] visible = (String[]) parameters.get("xlogsZipVisible");
		String[] checked = (String[]) parameters.get("xlogsZipSelected");
		java.util.Set<String> checkedIndices = new java.util.HashSet<String>();
		if (checked != null) java.util.Collections.addAll(checkedIndices, checked);
		if (visible == null) return;
		for (String value : visible) {
			try {
				int index = Integer.parseInt(value);
				if (index >= 0 && index < files.size()) {
					String path = files.get(index).getPath();
					if (checkedIndices.contains(value)) selected.put(path, Boolean.TRUE);
					else selected.remove(path);
				}
			} catch (NumberFormatException ignored) { /* Ignore invalid request values. */ }
		}
	}

	public int getSelectedCount() {
		int count = 0;
		for (IFile file : files) {
			if (isSelected(file.getPath())) count++;
		}
		return count;
	}

	public void clearSelection() { selected.clear(); }

	public boolean isZipSupported() {
		for (IFile file : files) if (!(file instanceof TextFile)) return false;
		return !files.isEmpty();
	}

	public void downloadSelected() {
		if (!isZipSupported()) return;
		java.util.List<File> sources = new java.util.ArrayList<File>();
		for (IFile file : files) {
			if (isSelected(file.getPath())) sources.add(new File(file.getPath()));
		}
		if (sources.isEmpty()) {
			XSPUtils.addFatal("Select at least one file to download.");
			return;
		}
		File archive = null;
		try {
			archive = File.createTempFile("xlogs-", ".zip");
			java.util.zip.ZipOutputStream zip = new java.util.zip.ZipOutputStream(new java.io.FileOutputStream(archive));
			try {
				java.util.Set<String> names = new java.util.HashSet<String>();
				byte[] buffer = new byte[65536];
				for (File source : sources) {
					String name = source.getName();
					int suffix = 2;
					while (!names.add(name)) name = suffix++ + "-" + source.getName();
					java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry(name);
					entry.setTime(source.lastModified());
					zip.putNextEntry(entry);
					java.io.InputStream input = new java.io.FileInputStream(source);
					try {
						int count;
						while ((count = input.read(buffer)) != -1) zip.write(buffer, 0, count);
					} finally { input.close(); }
					zip.closeEntry();
				}
			} finally { zip.close(); }
			new dk.xpages.utils.Download().save(archive, "xlogs-diagnostics.zip", "application/zip");
		} catch (java.io.IOException e) {
			log.error(e);
			XSPUtils.addFatal("Unable to create or download ZIP. Check server logs.");
		} finally {
			if (archive != null) archive.delete();
		}
	}

	public String getPattern() {
		return Directory.getCleanPattern(pattern);
	}

	public String getPath() {
		return NotesStrings.strLeftBack(getPattern(), File.separator);
	}

	public IFile getNewest() {
		if (files.size() > 0) {
			return files.get(0);
		} else {
			return null;
		}
	}

	public List<IFile> getFiles() {
		log.trace("Trace: getFiles({0})", pattern);
		if (files.size() == 0) return null;
		return files;
	}

	public int getCount() {
		return files.size();
	}

	public void setCurrent(IFile current) {
		this.current = current;
		XSPUtils.getViewScope().put("currentFile", current);
	}

	public IFile getCurrent() {
		if (current == null) {
			current = getNewest();
		}
		return current;
	}

}
