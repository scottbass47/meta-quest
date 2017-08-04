package com.fullspectrum.editor.command;

import com.fullspectrum.editor.LevelEditor;

public class EraseCommand extends Command{

	private int row;
	private int col;
	
	public EraseCommand(int row, int col) {
		super(true);
		this.row = row;
		this.col = col;
	}
	
	@Override
	public void execute(LevelEditor editor) {
		discard = editor.getTile(row, col) == null;
		
		editor.eraseTile(row, col);
	}

	@Override
	public void undo(LevelEditor editor) {
		editor.undoTile();
	}
	
	@Override
	public String toString() {
		return "Erasing Tile @ (" + row + ", " + col + ")";
	}

}
