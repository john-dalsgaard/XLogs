package dk.majkilde.logreader.files;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import dk.xpages.log.LogManager;
import dk.xpages.log.Logger;
import dk.xpages.utils.NotesStrings;
import dk.xpages.utils.XSPUtils;

public class XMLFile extends TextFile implements Serializable {
	private final Logger log = LogManager.getLogger();
	private String xlsFilename = null;
	private static final long serialVersionUID = 1L;

	public XMLFile(final File file, final String xlsFilename) {
		super(file);
		dk.xpages.utils.XSPUtils.debug("XMLFile: file..");
		this.xlsFilename = xlsFilename;
	}

	public XMLFile(final String filename, final String xlsFilename) {
		super(filename);
		dk.xpages.utils.XSPUtils.debug("XMLFile: filename..");
		this.xlsFilename = xlsFilename;
	}

	@Override
	public boolean isPaged() { return false; }

	@Override
	public String getHtml() {
		if (this.file.length() == 0) {
			return "Empty file (size=0)";
		}

		if (NotesStrings.isBlank(this.xlsFilename)) {
			// no XSL file specified - just return the content as a plain text file
			return super.getHtml();
		}

		// XSL transform
		String stage = "load stylesheet";
		XSPUtils.debug("XLogs XML start file=" + getFilename() + " stylesheet=" + this.xlsFilename);
		try (InputStream stylesheet = XSPUtils.getResourceAsStream(this.xlsFilename)) {
			if (stylesheet == null) {
				throw new IOException("Stylesheet resource not found: " + this.xlsFilename);
			}
			stage = "compile stylesheet";
			TransformerFactory tFactory = TransformerFactory.newInstance();
			// Input XML
			// StreamSource inputStream = new StreamSource(new BufferedReader(new FileReader(file)));
			StreamSource inputStream = new StreamSource(this.file);

			// Transformer XLS
			Transformer transformer = tFactory.newTransformer(new StreamSource(stylesheet));

			// Output stream
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);

			// Do the transformation
			stage = "transform XML";
			transformer.transform(inputStream, result);
			XSPUtils.debug("XLogs XML success file=" + getFilename() + " stylesheet=" + this.xlsFilename);

			return output.toString(this.encoding);
		} catch (Exception e) {
			XSPUtils.debug("XLogs XML failed file=" + getFilename() + " stylesheet=" + this.xlsFilename
					+ " stage=" + stage + " exception=" + e.toString());
			this.log.error(e);
			return super.getHtml();
		}
	}

}
