package com.fullspectrum.editor.command;

import com.badlogic.gdx.utils.Array;
import com.fullspectrum.editor.LevelEditor;
import com.fullspectrum.level.GridPoint;
import com.fullspectrum.level.tiles.MapTile.TileType;

public class FillCommand extends GroupCommand<PlaceTileCommand>{
	
	public FillCommand(Array<GridPoint> points, int id, TileType type) {
		super(true);
		for(GridPoint point : points) {
			addCommand(new PlaceTileCommand(point.row, point.col, id, type));
		}
	}
	
	@Override
	public void undo(LevelEditor editor) {
		editor.undoTile();
	}
	
}
