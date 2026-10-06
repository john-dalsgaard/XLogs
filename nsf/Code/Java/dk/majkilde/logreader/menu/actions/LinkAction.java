package dk.majkilde.logreader.menu.actions;

import java.io.IOException;
import java.io.Serializable;

import dk.majkilde.logreader.menu.IMenu;
import dk.xpages.log.LogManager;
import dk.xpages.log.Logger;
import dk.xpages.utils.XML;
import dk.xpages.utils.XSPUtils;

public class LinkAction implements IAction, Serializable {

	private final Logger log = LogManager.getLogger();
	private static final long serialVersionUID = 1L;

	private String url = "";
	private final IMenu parent;

	public LinkAction(XML config, IMenu parent) {
		dk.xpages.utils.XSPUtils.debug("LinkAction: constructor..");
		this.parent = parent;
		this.url = config.string("url");
	}

	@Override
	public IMenu getParent() {
		return parent;
	}

	@Override
	public void execute() {
		try {
			XSPUtils.getExternalContext().redirect(url);
		} catch (IOException e) {
			log.error(e);
		}

	}

	@Override
	public boolean isValid() {
		return !url.isEmpty();
	}

}
