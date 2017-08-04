package com.fullspectrum.editor.command;

import com.fullspectrum.editor.LevelEditor;

public class DoNothingCommand extends Command {

	public DoNothingCommand() {
		super(false);
		discard = true;
	}
	
	@Override
	public void execute(LevelEditor editor) {
	}

	@Override
	public void undo(LevelEditor editor) {
	}
}
