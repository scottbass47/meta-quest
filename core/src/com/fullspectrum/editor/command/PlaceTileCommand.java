package com.fullspectrum.editor.command;

import com.fullspectrum.editor.LevelEditor;
import com.fullspectrum.level.tiles.MapTile;
import com.fullspectrum.level.tiles.MapTile.TileType;

public class PlaceTileCommand extends Command{

	private int row;
	private int col;
	private int id;
	private TileType type;
	
	public PlaceTileCommand(int row, int col, int id, TileType type) {
		super(true);
		this.row = row;
		this.col = col;
		this.id = id;
		this.type = type;
	}

	@Override
	public void execute(LevelEditor editor) {
		MapTile mapTile = new MapTile();
		mapTile.setRow(row);
		mapTile.setCol(col);
		mapTile.setId(id);
		mapTile.setType(TileType.GROUND);

		MapTile oldTile = editor.getTile(row, col);
		if(oldTile != null) oldTile = new MapTile(oldTile);
		
		editor.placeTile(mapTile);
		
		// True if the map didn't change
		discard = oldTile != null && oldTile.equals(editor.getTile(row, col));
	}

	@Override
	public void undo(LevelEditor editor) {
		editor.undoTile();
	}
	
	@Override
	public String toString() {
		return "Place Tile @ (" + row + ", " + col + "), " + id + ", " + type;
	}
	
}
