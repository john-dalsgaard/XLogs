package dk.majkilde.logreader.menu.actions;

import java.io.Serializable;

import dk.majkilde.logreader.menu.IMenu;
import dk.xpages.utils.XSPUtils;

public abstract class Action implements IAction, Serializable {

	private static final long serialVersionUID = 1L;
	private final IMenu parent;

	public Action(IMenu parent) {
		dk.xpages.utils.XSPUtils.debug("Action: constructor..");
		this.parent = parent;
	}

	@Override
	public IMenu getParent() {
		return parent;
	}

	//protected abstract void initialize();

	protected abstract void executeAction();

	protected void updateMenu() {
		XSPUtils.getViewScope().put("currentMenu", getParent());
	}

	@Override
	public void execute() {
		//	initialize();
		executeAction();
		updateMenu();
	}
}
