package com.fullspectrum.editor.action;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.editor.TilePanel;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.level.ExpandableGrid;
import com.fullspectrum.level.tiles.MapTile;
import com.fullspectrum.level.tiles.MapTile.Side;
import com.fullspectrum.level.tiles.MapTile.TileType;
import com.fullspectrum.level.tiles.TileSlot;
import com.fullspectrum.level.tiles.Tileset;
import com.fullspectrum.level.tiles.TilesetTile;
import com.fullspectrum.utils.Maths;

public class AutoPlaceAction extends Action {

	private boolean erasing = false;
	

	@Override
	public void init() {
	}
	
	@Override
	public void update(float delta) {
		if (!editor.isMouseDown() || !editor.isMouseOnMap()) return;

		Vector2 worldCoords = editor.toWorldCoords(editor.getMousePos());

		int row = Maths.toGridCoord(worldCoords.y);
		int col = Maths.toGridCoord(worldCoords.x);

		ExpandableGrid<MapTile> tileMap = editor.getTileMap();
		TilePanel tilePanel = editor.getTilePanel();

		if (erasing) {
			if (tileMap.contains(row, col) && tileMap.get(row, col) != null) {
				tileMap.set(row, col, null);
				updateSurroundingTiles(row, col);
			}
		}
		else {
			MapTile mapTile = new MapTile();
			mapTile.setRow(row);
			mapTile.setCol(col);
			
			TilesetTile tilesetTile = calculateTilesetTileAt(row, col, tilePanel.getActiveTile().getClusterID());
			mapTile.setId(tilesetTile.getID());
			mapTile.setType(TileType.GROUND);
			
			tileMap.add(row, col, mapTile);
			
			updateSurroundingTiles(row, col);
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		Vector2 worldCoords = editor.toWorldCoords(editor.getMousePos());

		int row = Maths.toGridCoord(worldCoords.y);
		int col = Maths.toGridCoord(worldCoords.x);

		Tileset tileset = editor.getTilePanel().getTileset();
		TilesetTile tile = editor.getTilePanel().getActiveTile();

		batch.setProjectionMatrix(worldCamera.combined);
		batch.begin();

		batch.setColor(Color.DARK_GRAY);
		
		if(erasing) {
			Texture eraseTexture = editor.getEraseTexture();
			batch.draw(eraseTexture, col, row, 0.0f, 0.0f, eraseTexture.getWidth(), eraseTexture.getHeight(), GameVars.PPM_INV, GameVars.PPM_INV, 0.0f, 0, 0, eraseTexture.getWidth(), eraseTexture.getHeight(), false, false);
		} else {
			batch.draw(tileset.getTilesheet().getTexture(), col, row, 0.0f, 0.0f, 16.0f, 16.0f, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f, tile.getSheetX(), tile.getSheetY(), 16, 16, false, false);
		}
		
		batch.setColor(Color.WHITE);

		batch.end();
	}

	public void updateSurroundingTiles(int row, int col) {
		ExpandableGrid<MapTile> tileMap = editor.getTileMap();
		TilePanel tilePanel = editor.getTilePanel();

		boolean erasing = tileMap.get(row, col) == null;
		MapTile centerTile = tileMap.get(row, col);
		for (int r = row - 1; r <= row + 1; r++) {
			for (int c = col - 1; c <= col + 1; c++) {
				if (!tileMap.contains(r, c) || tileMap.get(r, c) == null || (r == row && c == col)) continue;
				MapTile mapTile = tileMap.get(r, c);
				Tileset tileset = tilePanel.getTileset();

				int clusterID = tileset.getClusterID(mapTile.getID());

				// Don't update surrounding tiles if they are apart of another
				// clusters unless you're erasing
				if (!erasing && tileset.getClusterID(centerTile.getID()) != clusterID) continue;

				TilesetTile tile = calculateTilesetTileAt(r, c, clusterID);
				mapTile.setId(tile.getID());
				tileMap.set(r, c, mapTile);
			}
		}
	}

	private TilesetTile calculateTilesetTileAt(int row, int col, int clusterID) {
		TilePanel tilePanel = editor.getTilePanel();

		Array<Side> sidesOpen = new Array<Side>(Side.class);

		if (isOpen(row + 1, col, clusterID)) sidesOpen.add(Side.NORTH);
		if (isOpen(row - 1, col, clusterID)) sidesOpen.add(Side.SOUTH);
		if (isOpen(row, col + 1, clusterID)) sidesOpen.add(Side.EAST);
		if (isOpen(row, col - 1, clusterID)) sidesOpen.add(Side.WEST);

		TileSlot slot = TileSlot.getSlot(sidesOpen.toArray());

		Tileset tileset = tilePanel.getTileset();
		TilesetTile tilesetTile = tileset.getTile(clusterID, slot);
		return tilesetTile;
	}

	private boolean isOpen(int row, int col, int clusterID) {
		ExpandableGrid<MapTile> tileMap = editor.getTileMap();
		TilePanel tilePanel = editor.getTilePanel();
		return !tileMap.contains(row, col) || tileMap.get(row, col) == null || tileMap.get(row, col).getType() != TileType.GROUND || tilePanel.getTileset().getClusterID(tileMap.get(row, col).getID()) != clusterID;
	}

	public void setErasing(boolean erasing) {
		this.erasing = erasing;
	}

	public boolean isErasing() {
		return erasing;
	}
}
