package dk.majkilde.logreader.menu.actions;

import java.io.Serializable;

import dk.majkilde.logreader.files.FileList;
import dk.majkilde.logreader.files.FileListFactory;
import dk.majkilde.logreader.menu.IMenu;
import dk.xpages.utils.XML;
import dk.xpages.utils.XSPUtils;

public class FileAction implements IAction, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final IMenu parent;

	private FileList filelist = null;

	public FileAction(XML config, IMenu parent) throws Exception {
		dk.xpages.utils.XSPUtils.debug("FileAction: constructor..");
		this.parent = parent;
		filelist = FileListFactory.create(config);
	}

	@Override
	public IMenu getParent() {
		return parent;
	}

	@Override
	public void execute() {
		XSPUtils.getViewScope().put("filelist", filelist);
		XSPUtils.getViewScope().put("currentFile", filelist.getCurrent());
		XSPUtils.getViewScope().put("currentMenu", getParent());
	}

	@Override
	public boolean isValid() {
		try {
			return filelist.getCount() > 0;
		} catch (Exception e) {
			return false;
		}
	}

}
