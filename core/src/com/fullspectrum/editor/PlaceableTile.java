package com.fullspectrum.editor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.editor.command.Command;
import com.fullspectrum.editor.command.PlaceTileCommand;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.level.tiles.MapTile.TileType;
import com.fullspectrum.level.tiles.Tileset;
import com.fullspectrum.level.tiles.TilesetTile;
import com.fullspectrum.utils.Maths;

public class PlaceableTile implements Placeable {

	@Override
	public Command onClick(Vector2 mousePos, LevelEditor editor) {
		int row = Maths.toGridCoord(mousePos.y);
		int col = Maths.toGridCoord(mousePos.x);
		
		TilePanel tilePanel = editor.getTilePanel();
		return new PlaceTileCommand(row, col, tilePanel.getActiveTile().getID(), TileType.GROUND);
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
