package dk.majkilde.logreader.files;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.lang3.StringEscapeUtils;

import dk.xpages.log.LogManager;
import dk.xpages.log.Logger;
import dk.xpages.utils.Download;
import dk.xpages.utils.NotesStrings;
import dk.xpages.utils.XSPUtils;

public class TextFile implements IFile, Serializable {
	private static final AtomicLong TIMING_SEQUENCE = new AtomicLong();
	protected static final String DEFAULT_ENCODING = "utf-8";
	protected String encoding = DEFAULT_ENCODING;
	private final Logger log = LogManager.getLogger();
	protected final File file;
	private Filters filters;
	private String pagingKey = "text";
	private long snapshotBytes = -1;
	private String snapshotFileKey;
	private int snapshotLines;
	private int olderLines;
	private int shownStart;
	private int shownEnd;
	private String pagingNotice = "";

	public TextFile setPagingKey(String key) { this.pagingKey = key; return this; }
	private boolean isArchive() { return getFilename().toLowerCase(java.util.Locale.ROOT).endsWith(".zip"); }
	public boolean isPaged() { return !isArchive(); }
	public int getPageSize() {
		Object value = XSPUtils.getSessionScope().get("xlogs.pageSize." + pagingKey);
		return value instanceof Integer && PagingOptions.isAllowed(((Integer) value).intValue())
				? ((Integer) value).intValue() : PagingOptions.getDefaultSize();
	}
	public java.util.List<javax.faces.model.SelectItem> getPageSizeOptions() { return PagingOptions.getSelectItems(); }
	public boolean isShowBottomPaging() { return (isHasOlder() || isHasNewer()) && PagingOptions.showBottomNavigation(shownEnd - shownStart); }
	// Select-one controls validate against String item values before updating the model.
	public String getPageSizeSelection() { return Integer.toString(getPageSize()); }
	public void setPageSizeSelection(String size) { setPageSize(Integer.parseInt(size)); }
	public void setPageSize(int size) {
		if (!PagingOptions.isAllowed(size)) {
			throw new IllegalArgumentException("Unsupported page size: " + size);
		}
		XSPUtils.getSessionScope().put("xlogs.pageSize." + pagingKey, Integer.valueOf(size));
		if (snapshotLines <= size) olderLines = 0;
	}
	public void older() {
		if (shownStart > 0) olderLines = snapshotLines - shownStart;
	}
	public void newer() { olderLines = Math.max(0, olderLines - getPageSize()); }
	public void latest() { snapshotBytes = -1; snapshotFileKey = null; olderLines = 0; pagingNotice = ""; }
	public boolean isHasOlder() { return shownStart > 0; }
	public boolean isHasNewer() { return olderLines > 0; }
	public String getPageStatus() {
		if (snapshotLines == 0) return "Empty file (0 lines). " + pagingNotice;
		return "Lines " + (shownStart + 1) + "–" + shownEnd + " of " + snapshotLines
				+ " in snapshot. " + pagingNotice;
	}

	public TextFile(final File file) {
		dk.xpages.utils.XSPUtils.debug("TextFile: file..");
		this.file = file;
		this.filters = new Filters(null);
	}

	protected TextFile(final String filename) {
		this(filename, new Filters(null));
		dk.xpages.utils.XSPUtils.debug("TextFile: filename..");
	}

	public TextFile(final String filename, final Filters filters) {
		this.file = new File(filename);
		this.filters = filters;
	}

	public TextFile setEncoding(String encoding) {
		if (NotesStrings.isBlank(encoding)) {
			this.encoding = DEFAULT_ENCODING;
		} else {
			this.encoding = encoding;
		}

		return this;
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Date getDate() {
		return new Date(this.file.lastModified());
	}

	@Override
	public String getFilename() {
		return this.file.getName();
	}

	@Override
	public String getHtml() {
		if (isArchive()) return "Archived log. Use the download link above to open the ZIP file.";
		final long timingId = TIMING_SEQUENCE.incrementAndGet();
		final long totalStart = java.lang.System.nanoTime();
		boolean completed = false;
		dk.xpages.utils.XSPUtils.debug("XLogs timing [" + timingId + "] start file=" + getFilename()
				+ " bytes=" + getSize() + " encoding=" + this.encoding
				+ " pageSize=" + getPageSize()
				+ " filters=" + this.filters.getFilterList().size());
		try {
			List<String> content;
			try {
				content = getStringList(timingId);
			} catch (IOException e) {
				this.log.error(e);
				return NotesStrings.messageFormat("Error reading file '{1}': {0}", e.getMessage(), this.file.getName());
			}

			long stageStart = timingStart(timingId, "select", content.size());
			snapshotLines = content.size();
			olderLines = Math.min(olderLines, Math.max(0, snapshotLines - 1));
			shownEnd = snapshotLines - olderLines;
			shownStart = Math.max(0, shownEnd - getPageSize());
			content = new ArrayList<String>(content.subList(shownStart, shownEnd));
			timingEnd(timingId, "select", stageStart, content.size());

			stageStart = timingStart(timingId, "escape", content.size());
			for (int i = 0; i < content.size(); i++) {
				content.set(i, StringEscapeUtils.escapeHtml4(content.get(i)));
			}
			timingEnd(timingId, "escape", stageStart, content.size());

			stageStart = timingStart(timingId, "filter", content.size());
			this.filters.apply(content);
			timingEnd(timingId, "filter", stageStart, content.size());

			stageStart = timingStart(timingId, "join", content.size());
			String html = NotesStrings.join("", content);
			timingEnd(timingId, "join", stageStart, content.size());
			dk.xpages.utils.XSPUtils.debug("XLogs timing [" + timingId + "] result htmlChars=" + html.length());
			completed = true;
			return html;
		} finally {
			dk.xpages.utils.XSPUtils.debug("XLogs timing [" + timingId + "] total ms="
					+ elapsedMillis(totalStart) + " status=" + (completed ? "ok" : "failed"));
		}
	}

	private long timingStart(long timingId, String stage, int lines) {
		dk.xpages.utils.XSPUtils.debug("XLogs timing [" + timingId + "] " + stage + " start lines=" + lines);
		return java.lang.System.nanoTime();
	}

	private void timingEnd(long timingId, String stage, long start, int lines) {
		long elapsed = java.lang.System.nanoTime() - start;
		dk.xpages.utils.XSPUtils.debug("XLogs timing [" + timingId + "] " + stage
				+ " ms=" + (elapsed / 1000000.0) + " lines=" + lines);
	}

	private double elapsedMillis(long start) {
		return (java.lang.System.nanoTime() - start) / 1000000.0;
	}

	@Override
	public long getSize() {
		return this.file.length();
	}

	@Override
	public int compareTo(IFile compareFile) {
		return (-1) * this.getDate().compareTo(compareFile.getDate());
	}

	@Override
	public String getPath() {
		return this.file.getPath();
	}

	private byte[] getByteArray() throws IOException {
		return FileUtils.readFileToByteArray(this.file);
	}

	private List<String> getStringList(long timingId) throws IOException {
		long stageStart = timingStart(timingId, "read", 0);
		BasicFileAttributes attributes = Files.readAttributes(this.file.toPath(), BasicFileAttributes.class);
		long size = attributes.size();
		String fileKey = attributes.fileKey() == null ? null : attributes.fileKey().toString();
		if (snapshotBytes >= 0 && (size < snapshotBytes
				|| (snapshotFileKey != null && !snapshotFileKey.equals(fileKey)))) {
			latest();
			pagingNotice = "File was shortened or rotated; showing latest content.";
		}
		if (snapshotBytes < 0) {
			snapshotBytes = size;
			snapshotFileKey = fileKey;
		}
		try (InputStreamReader reader = new InputStreamReader(
				new BoundedInputStream(new FileInputStream(this.file), snapshotBytes), this.encoding)) {
			List<String> lines = new ArrayList<String>(IOUtils.readLines(reader));
			timingEnd(timingId, "read", stageStart, lines.size());
			return lines;
		}
	}

	@Override
	public void download() {
		Download download = new Download();
		download.setCharset(this.encoding);
		try {
			download.save(getByteArray(), getFilename());
		} catch (IOException e) {
			this.log.error(e);
		}

	}

	@Override
	public Filters getFilters() {
		return this.filters;
	}
}
