package com.fullspectrum.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.level.tiles.MapTile;
import com.fullspectrum.level.tiles.Tileset;
import com.fullspectrum.level.tiles.TilesetTile;

public class MapRenderer {

	private Tileset tileset;
	private ExpandableGrid<MapTile> tileMap;
	
	private OrthographicCamera camera;
	private boolean gridLinesOn = false;
	private ShapeRenderer shapeRenderer;
	
	public MapRenderer() {
		 shapeRenderer = new ShapeRenderer();
	}
	
	public void setView(OrthographicCamera camera) {
		this.camera = camera;
	}
	
	// PERFORMANCE Should only render visible map
	public void render(SpriteBatch batch) {
		if(gridLinesOn) {
			shapeRenderer.setProjectionMatrix(camera.combined);
			
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(Color.LIGHT_GRAY);
			
			for(int row = tileMap.getMinRow(); row <= tileMap.getMaxRow() + 1; row++) {
				shapeRenderer.line(tileMap.getMinCol(), row, tileMap.getMaxCol() + 1, row);
			}
			
			for(int col = tileMap.getMinCol(); col <= tileMap.getMaxCol() + 1; col++) {
				shapeRenderer.line(col, tileMap.getMinRow(), col, tileMap.getMaxRow() + 1);
			}
			
			shapeRenderer.end();
		}

		Matrix4 old = batch.getProjectionMatrix();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		for(int row = tileMap.getMinRow(); row <= tileMap.getMaxRow(); row++) {
			for(int col = tileMap.getMinCol(); col <= tileMap.getMaxCol(); col++) {
				
				MapTile tile = tileMap.get(row, col);
				if(tile == null) continue;
				
				int tileID = tile.getID();
				TilesetTile tilesetTile = tileset.getTilesetTile(tileID);
				
				Texture tilesheet = tileset.getTilesheet().getTexture();
				batch.draw(tilesheet, col, row, 0f, 0f, 16f, 16f, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f, tilesetTile.getSheetX(), tilesetTile.getSheetY(), 16, 16, false, false);
			}
		}
		
		batch.end();
		batch.setProjectionMatrix(old);
		
	}
	
	public void setTileMap(ExpandableGrid<MapTile> tileMap) {
		this.tileMap = tileMap;
	}
	
	public void setTileset(Tileset tileset) {
		this.tileset = tileset;
	}
	
	public void setGridLinesOn(boolean gridLinesOn) {
		this.gridLinesOn = gridLinesOn;
	}
	
	
}
