package dk.majkilde.logreader.beans;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import dk.majkilde.logreader.files.Directory;
import dk.xpages.log.LogManager;
import dk.xpages.log.Logger;
import dk.xpages.utils.Download;

public class ExtBean implements Serializable {

	private static Logger log = LogManager.getLogger();

	private static final long serialVersionUID = 1L;
	private static final String[] JAR_LOCATIONS = {
		"[program]/ndext/*.jar",
		"[program]/jvm/lib/ext/*.jar",
		"[java]/lib/ext/*.jar",
		"[config]/jvm/lib/ext/*.jar"
	};

	public ExtBean() {
		dk.xpages.utils.XSPUtils.debug("ExtBean: Constructor...");
	}

	public List<File> getFileList() {
		Map<String, File> unique = new LinkedHashMap<String, File>();
		for (String pattern : JAR_LOCATIONS) {
			for (File file : Directory.getFiles(pattern)) {
				String key;
				try {
					key = file.getCanonicalPath();
				} catch (IOException e) {
					key = file.getAbsolutePath();
				}
				if (!unique.containsKey(key)) unique.put(key, file);
			}
		}
		List<File> files = new ArrayList<File>(unique.values());
		Collections.sort(files, new Comparator<File>() {
			public int compare(File first, File second) {
				int name = first.getName().compareToIgnoreCase(second.getName());
				return name != 0 ? name : first.getAbsolutePath().compareTo(second.getAbsolutePath());
			}
		});
		return files;
	}

	private byte[] getByteArray(File file) throws IOException {
		return FileUtils.readFileToByteArray(file);
	}

	public void download(File file) {
		Download download = new Download();
		try {
			download.save(getByteArray(file), file.getAbsolutePath());
		} catch (IOException e) {
			log.error(e);
		}

	}

}
