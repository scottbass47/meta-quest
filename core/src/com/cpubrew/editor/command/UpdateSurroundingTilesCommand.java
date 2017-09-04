package com.cpubrew.editor.command;

import com.cpubrew.editor.LevelEditor;
import com.cpubrew.level.tiles.MapTile;

public class UpdateSurroundingTilesCommand extends Command {

	private int row;
	private int col;
	
	public UpdateSurroundingTilesCommand(int row, int col) {
		super(true);
		this.row = row;
		this.col = col;
	}
	
	@Override
	public void execute(LevelEditor editor) {
		updateSurroundingTiles(editor, row, col);
	}

	@Override
	public void undo(LevelEditor editor) {
		editor.undoTile();
	}
	
	public void updateSurroundingTiles(LevelEditor editor, int row, int col) {
		discard = true;
		for (int r = row - 1; r <= row + 1; r++) {
			for (int c = col - 1; c <= col + 1; c++) {
				if(r == row && c == col) continue;
				
				MapTile old = editor.getTile(r, c);
				if(old != null) old = new MapTile(old);
				editor.updateTile(editor, r, c);
				
				// Only true if all tiles placed end up being the same
				// To be true -> both the old tile and new tile are null OR old tile == new tile
				discard = discard && ((old == null && editor.getTile(r, c) == null) || old.equals(editor.getTile(r, c)));
			}
		}
	}
	
	@Override
	public String toString() {
		return "Update Surrounding Tiles @ (" + row + ", " + col + ")";
	}

}
