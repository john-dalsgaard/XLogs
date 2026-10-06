package dk.majkilde.logreader.files;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import dk.xpages.log.LogManager;
import dk.xpages.log.Logger;
import dk.xpages.utils.Download;
import dk.xpages.utils.NotesStrings;
import dk.xpages.utils.SystemConstants;

public class TextFile implements IFile, Serializable {
	protected static final String DEFAULT_ENCODING = "utf-8";
	protected String encoding = DEFAULT_ENCODING;
	private final Logger log = LogManager.getLogger();
	protected final File file;
	private Filters filters;

	public TextFile(final File file) {
		dk.xpages.utils.XSPUtils.debug("TextFile: file..");
		this.file = file;
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
		List<String> content;
		try {
			content = getStringList();
		} catch (IOException e) {
			this.log.error(e);
			return NotesStrings.messageFormat("Error reading file '{1}': {0}", e.getMessage(), this.file.getName());
		}

		if (content.size() > SystemConstants.getMaxTextLines()) {
			List<String> list = new LinkedList<String>();
			for (int i = content.size() - SystemConstants.getMaxTextLines(); i < content.size(); i++) {
				list.add(content.get(i));
			}
			if (!SystemConstants.isMaxTextLinesSilenced()) System.out.println("XLogs: Limit to last " + SystemConstants.getMaxTextLines() + " of " + content.size() + " lines from " + getFilename());
			content = list;
		}

		this.filters.apply(content);

		return NotesStrings.join("", content);
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

	private List<String> getStringList() throws IOException {
		List<String> list = new LinkedList<String>();

		List<String> lines = FileUtils.readLines(this.file, this.encoding);

		for (String line : lines) {
			list.add(StringEscapeUtils.escapeHtml4(line));
		}

		return list;
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
