package dk.majkilde.logreader.menu.actions;

import java.io.Serializable;

import dk.majkilde.logreader.menu.IMenu;
import dk.xpages.log.LogManager;
import dk.xpages.log.Logger;
import dk.xpages.utils.NotesObjects;
import dk.xpages.utils.XML;
import dk.xpages.utils.XSPUtils;
import lotus.domino.Database;

public class NSFAction implements IAction, Serializable {

	private final Logger log = LogManager.getLogger();
	private static final long serialVersionUID = 1L;

	private final String filename;
	private final String viewname;

	private final IMenu parent;

	public NSFAction(XML config, IMenu parent) {
		dk.xpages.utils.XSPUtils.debug("NSFAction: constructor..");
		this.parent = parent;
		this.filename = config.child("filename").content();
		this.viewname = config.child("viewname").content();

	}

	@Override
	public IMenu getParent() {
		return this.parent;
	}

	@Override
	public void execute() {
		XSPUtils.getViewScope().put("filename", this.filename);
		XSPUtils.getViewScope().put("viewname", this.viewname);
	}

	@Override
	public boolean isValid() {
		Database db = NotesObjects.getDatabase("*!!" + this.filename);
		boolean isopen = false;
		try {
			if (!db.isOpen()) {
				db.open();
			}
			isopen = db.isOpen();
		} catch (Exception e) {
			System.err.println("ERROR: opening db: " + filename);
			this.log.error(e);
		}

		return isopen;
	}
}
