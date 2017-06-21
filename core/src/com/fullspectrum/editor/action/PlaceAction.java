package com.fullspectrum.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.editor.TilePanel;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.level.ExpandableGrid;
import com.fullspectrum.level.tiles.MapTile;
import com.fullspectrum.level.tiles.Tileset;
import com.fullspectrum.level.tiles.TilesetTile;
import com.fullspectrum.level.tiles.MapTile.TileType;
import com.fullspectrum.utils.Maths;

public class PlaceAction extends Action {

	@Override
	public void init() {
	}
	
	@Override
	public void update(float delta) {
		if(!editor.isMouseDown() || !editor.isMouseOnMap()) return;
		
		Vector2 worldCoords = editor.toWorldCoords(editor.getMousePos());

		int row = Maths.toGridCoord(worldCoords.y);
		int col = Maths.toGridCoord(worldCoords.x);
		
		ExpandableGrid<MapTile> tileMap = editor.getTileMap();
		TilePanel tilePanel = editor.getTilePanel();
		
		if(!tileMap.contains(row, col) || tileMap.get(row, col) == null || tileMap.get(row, col).getID() != tilePanel.getActiveTile().getID()) {
			MapTile mapTile = new MapTile();
			mapTile.setRow(row);
			mapTile.setCol(col);
			mapTile.setId(tilePanel.getActiveTile().getID());
			mapTile.setType(TileType.GROUND);
			
			tileMap.add(row, col, mapTile);
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		if (editor.isMouseOnMap()) {
			Vector2 worldCoords = editor.toWorldCoords(editor.getMousePos());

			int row = Maths.toGridCoord(worldCoords.y);
			int col = Maths.toGridCoord(worldCoords.x);

			Tileset tileset = editor.getTilePanel().getTileset();
			TilesetTile tile = editor.getTilePanel().getActiveTile();

			batch.setProjectionMatrix(worldCamera.combined);
			batch.begin();
			batch.draw(tileset.getTilesheet().getTexture(), col, row, 0.0f, 0.0f, 16.0f, 16.0f, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f, tile.getSheetX(), tile.getSheetY(), 16, 16, false, false);
			batch.end();
		}
	}

}
