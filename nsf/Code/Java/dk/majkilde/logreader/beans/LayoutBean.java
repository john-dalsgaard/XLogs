package dk.majkilde.logreader.beans;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import com.ibm.xsp.designer.context.XSPUrl;

import dk.majkilde.logreader.files.FileList;
import dk.majkilde.logreader.menu.IMenu;
import dk.majkilde.logreader.menu.MenuItem;
import dk.xpages.utils.NotesStrings;
import dk.xpages.utils.SystemConstants;
import dk.xpages.utils.XML;
import dk.xpages.utils.XSPUtils;

public class LayoutBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private IMenu menu = null;

	public LayoutBean() {
		try {
			dk.xpages.utils.XSPUtils.debug("Layoutbeen: construct...");
			InputStream input = XSPUtils.getResourceAsStream("config.xml");
			if (input == null) {
				dk.xpages.utils.XSPUtils.debug("Layoutbeen: File nor found: config.xml");
				XSPUtils.addFatal("File nor found: config.xml");
			} else {
				XML configXML = new XML(input, "config");
				if (configXML.hasChild("menu")) {
					dk.xpages.utils.XSPUtils.debug("Layoutbeen: Has menu config");
					menu = MenuItem.createFromXML(configXML.child("menu"));
					dk.xpages.utils.XSPUtils.debug("Layoutbeen: Menu created from XML");
				} else {
					XSPUtils.addFatal("config.xml: No <menu>");
				}
				// 2022.11.23/Jda Added controls for limits
				if (configXML.hasChild("limits")) {
					dk.xpages.utils.XSPUtils.debug("Layoutbeen: Has limits config");
					SystemConstants.loadLimits(configXML.child("limits"));
				}

			}
		} catch (Exception e) {
			XSPUtils.addFatal(e);
		}
	}

	public IMenu getMenu() {
		return menu;
	}

	public List<IMenu> getTabs() {
		try {
			dk.xpages.utils.XSPUtils.debug("Layoutbeen: getTabs");
			return menu.getChildren();
		} catch (Exception e) {
			XSPUtils.addFatal(e);
			return null;
		}
	}

	private String getSelectedTabId() {
		String selectedId = "";
		try {
			XSPUrl url = XSPUtils.getUrl();
			selectedId = url.getParameter("tab");
			if (NotesStrings.isBlank(selectedId)) {
				selectedId = getTabs().get(0).getId();
			}
		} catch (Exception e) {
			XSPUtils.addFatal(e);
		}
		return selectedId;
	}

	private IMenu getSelectedMenu() {
		//Check the viewscope
		IMenu currentMenu = (IMenu) XSPUtils.getViewScope().get("currentMenu");
		if (currentMenu != null) {
			return currentMenu;
		}

		//defaults to the first menu
		try {
			return getSelectedTab().getChildren().get(0);
		} catch (Exception e) {
			XSPUtils.addFatal(e);
			return null;
		}

	}

	private String getSelectedMenuId() {
		IMenu selectedMenu = getSelectedMenu();
		if (selectedMenu != null) {
			return selectedMenu.getId();
		}

		//Check the URL
		XSPUrl url = XSPUtils.getUrl();
		String urlId = url.getParameter("menu");
		if (!NotesStrings.isBlank(urlId)) {
			return urlId;
		}

		return "";
	}

	public boolean isTabSelected(String tabid) {
		return getSelectedTabId().equals(tabid);
	}

	public boolean isMenuSelected(String menuid) {
		boolean selected = getSelectedMenuId().equals(menuid);
		return selected;
	}

	public IMenu getSelectedTab() {
		try {
			return menu.getChild(getSelectedTabId());
		} catch (Exception e) {
			XSPUtils.addFatal(e);
			return null;
		}
	}

	public FileList getCurrentFilelist() {
		try {
			dk.xpages.utils.XSPUtils.debug("Layoutbeen: getCurrentFilelist - start");
			getSelectedMenu().executeAction();
		} catch (Exception e) {
			XSPUtils.addFatal(e);
		}
		dk.xpages.utils.XSPUtils.debug("Layoutbeen: getCurrentFilelist - end");

		return null;
	}

}
