package com.cpubrew.editor.action;

import com.cpubrew.gui.Action;
import com.cpubrew.gui.Component;

public class SwitchBind implements Action {

	private ActionManager manager;
	private EditorActions action;
	private boolean ignoreBlocking = false;
	
	public SwitchBind(ActionManager manager, EditorActions action, boolean ignoreBlocking) {
		this.manager = manager;
		this.action = action;
		this.ignoreBlocking = ignoreBlocking;
	}
	
	public SwitchBind(ActionManager manager, EditorActions action) {
		this(manager, action, false);
	}
	
	@Override
	public void onAction(Component source) {
		// If this switch doesn't ignore blocking actions, and the manager is blocking, do nothing
		if(!ignoreBlocking && manager.isBlocking()) return;
		manager.switchAction(action);
	}

}
