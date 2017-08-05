package com.fullspectrum.editor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.level.tiles.MapTile;
import com.fullspectrum.level.tiles.Tileset;
import com.fullspectrum.level.tiles.TilesetTile;
import com.fullspectrum.utils.Maths;

public class SelectableTile implements Selectable<MapTile>{

	private MapTile tile;
	
	public SelectableTile(MapTile tile) {
		this.tile = new MapTile(tile);
	}
	
	@Override
	public void update(float delta, LevelEditor editor) {
	}

	@Override
	public void render(SpriteBatch batch, Vector2 worldPos, LevelEditor editor) {
		int row = Maths.toGridCoord(worldPos.y);
		int col = Maths.toGridCoord(worldPos.x);

		Tileset tileset = editor.getTilePanel().getTileset();
		TilesetTile tilesetTile = tileset.getTilesetTile(tile.getID());

//		batch.draw(editor.getSelectTexture(), tile.getCol(), tile.getRow(), 0.0f, 0.0f, 16.0f, 16.0f, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f, 0, 0, 16, 16, false, false);
		batch.draw(tileset.getTilesheet().getTexture(), col, row, 0.0f, 0.0f, 16.0f, 16.0f, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f, tilesetTile.getSheetX(), tilesetTile.getSheetY(), 16, 16, false, false);
	}

	@Override
	public Selectable<MapTile> copy(LevelEditor editor) {
		return new SelectableTile(new MapTile(tile));
	}

	@Override
	public void remove(LevelEditor editor) {
		editor.unsafeSetTile(tile.getRow(), tile.getCol(), null);
	}

	@Override
	public Vector2 getPosition(Vector2 offset) {
		Vector2 pos = new Vector2(tile.getCol(), tile.getRow());
		return pos.add(offset);
	}

	@Override
	public boolean contentsEqual(MapTile value) {
		return tile.equals(value);
	}

	@Override
	public void move(Vector2 position, LevelEditor editor) {
		int row = Maths.toGridCoord(position.y);
		int col = Maths.toGridCoord(position.x);
		
		tile.setRow(row);
		tile.setCol(col);
		
		editor.unsafeSetTile(row, col, new MapTile(tile));
	}
	
	@Override
	public void add(Vector2 position, LevelEditor editor) {
		int row = Maths.toGridCoord(position.y);
		int col = Maths.toGridCoord(position.x);
		
		editor.unsafeSetTile(row, col, new MapTile(tile));
	}
	
	public MapTile getTile() {
		return tile;
	}
	
}
