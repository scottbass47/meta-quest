package com.fullspectrum.editor.command;

import com.badlogic.gdx.utils.Array;
import com.fullspectrum.editor.LevelEditor;

public class GroupCommand<T extends Command> extends Command {

	private Array<T> commands;
	
	public GroupCommand() {
		super(false);
		commands = new Array<T>();
	}
	
	@Override
	public void execute(LevelEditor editor) {
		discard = true;
		editsTiles = false;
		for(T command : commands) {
			command.execute(editor);
			discard = discard && command.discard;
			editsTiles = editsTiles || command.editsTiles;
		}
	}

	@Override
	public void undo(LevelEditor editor) {
		for(int i = commands.size - 1; i >= 0; i--) {
			Command command = commands.get(i);
			command.undo(editor);
		}
	}

	public void addCommand(T command) {
		this.commands.add(command);
	}
	
	public void removeCommand(T command) {
		this.commands.removeValue(command, false);
	}

	public Array<T> getCommands() {
		return commands;
	}

	// TODO Fill this out
	@Override
	public String toString() {
		return super.toString();
	}
	
}
