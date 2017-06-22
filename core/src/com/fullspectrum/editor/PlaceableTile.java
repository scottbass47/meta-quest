package com.fullspectrum.editor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.level.ExpandableGrid;
import com.fullspectrum.level.tiles.MapTile;
import com.fullspectrum.level.tiles.MapTile.TileType;
import com.fullspectrum.level.tiles.Tileset;
import com.fullspectrum.level.tiles.TilesetTile;
import com.fullspectrum.utils.Maths;

public class PlaceableTile implements Placeable {

	@Override
	public void onClick(Vector2 mousePos, LevelEditor editor) {
		int row = Maths.toGridCoord(mousePos.y);
		int col = Maths.toGridCoord(mousePos.x);
		
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
	public void render(Vector2 mousePos, SpriteBatch batch, LevelEditor editor) {
		int row = Maths.toGridCoord(mousePos.y);
		int col = Maths.toGridCoord(mousePos.x);

		Tileset tileset = editor.getTilePanel().getTileset();
		TilesetTile tile = editor.getTilePanel().getActiveTile();

		batch.draw(tileset.getTilesheet().getTexture(), col, row, 0.0f, 0.0f, 16.0f, 16.0f, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f, tile.getSheetX(), tile.getSheetY(), 16, 16, false, false);
	}

	@Override
	public boolean placeOnClick() {
		return false;
	}

	@Override
	public void update(float delta) {
		
	}

}
