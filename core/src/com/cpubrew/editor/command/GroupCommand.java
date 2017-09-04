package com.cpubrew.editor.command;

import com.badlogic.gdx.utils.Array;
import com.cpubrew.editor.LevelEditor;

public class GroupCommand<T extends Command> extends Command {

	private Array<T> commands;
	
	public GroupCommand(boolean editsTiles) {
		super(editsTiles);
		commands = new Array<T>();
	}
	
	@Override
	public void execute(LevelEditor editor) {
		discard = true;
		for(T command : commands) {
			command.execute(editor);
			discard = discard && command.discard;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Commands: ");
		for(Command command : commands) {
			builder.append("\n\t" + command);
		}
		return builder.toString();
	}
	
}
