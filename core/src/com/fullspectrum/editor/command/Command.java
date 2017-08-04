package com.fullspectrum.editor.command;

import com.fullspectrum.editor.LevelEditor;

public abstract class Command {

	protected boolean discard = false;
	protected boolean editsTiles = false;
	
	public Command(boolean editsTiles){
		this.editsTiles = editsTiles;
	}
	
	public abstract void execute(LevelEditor editor);
	public abstract void undo(LevelEditor editor);

	/**
	 * Gets called AFTER command execution. Used to determine whether or not this command should be saved
	 * in the history stack or discarded.
	 * @return
	 */
	public boolean discard(){
		return discard;
	}
	
	public boolean editsTiles() {
		return editsTiles;
	}
}
