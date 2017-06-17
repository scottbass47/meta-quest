package com.fullspectrum.editor;

import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.fullspectrum.level.tiles.Tileset;
import com.fullspectrum.level.tiles.TilesetLoader;
import com.fullspectrum.level.tiles.TilesetTile;

public class TilePanel {

	private float x;
	private float y;
	private float width = 1280;
	private float height = 200;
	private float tileScale = 2.0f;
	
	private Color backgroundColor = Color.BLACK.mul(1.0f, 1.0f, 1.0f, 0.9f);
	private ShapeRenderer shapeRenderer;
	private Tileset tileset;
	private TilesetTile activeTile;
	
	private IntMap<Rectangle> clusterArea;
	
	public TilePanel() {
		shapeRenderer = new ShapeRenderer();
		clusterArea = new IntMap<Rectangle>();

		TilesetLoader loader = new TilesetLoader();
		tileset = loader.load(Gdx.files.internal("map/grassy.atlas"));
		
		setupClusters();
	}

	private void setupClusters() {
		IntMap<Array<TilesetTile>> clusterMap = tileset.getClusterIDMap();
		
		for(Iterator<Entry<Array<TilesetTile>>> iter = clusterMap.iterator(); iter.hasNext(); ) {
			Entry<Array<TilesetTile>> entry = iter.next();
			
			int id = entry.key;
//			Array<TilesetTile> tiles = entry.value;
			
			Rectangle rect = new Rectangle();
			
			// Assumes all cluster types are the same
			rect.width = 80;
			rect.height = 64;
			
			clusterArea.put(id, rect);
		}
	}
	
	public void render(OrthographicCamera hudCamera, SpriteBatch batch) {
		Gdx.gl.glEnable(GL11.GL_BLEND);
		Gdx.gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		shapeRenderer.setProjectionMatrix(hudCamera.combined);
		
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(backgroundColor);
		shapeRenderer.rect(x, y, width, height);
		shapeRenderer.end();
		
		Matrix4 old = batch.getProjectionMatrix();
		
		batch.setProjectionMatrix(hudCamera.combined);
		renderTileset(batch);
		batch.setProjectionMatrix(old);
		
		Gdx.gl.glEnable(GL11.GL_BLEND);
	}

	private void renderTileset(SpriteBatch batch) {
		batch.begin();
		
		float x = 0;
		float y = height;
		for(Iterator<Entry<Rectangle>> iter = clusterArea.iterator(); iter.hasNext(); ) {
			Entry<Rectangle> entry = iter.next();
			
			int id = entry.key;
			Rectangle rect = entry.value;
			Array<TilesetTile> tiles = tileset.getClusterIDMap().get(id);
			
			for(TilesetTile tile : tiles) {
				if(activeTile == null) activeTile = tile;
				int tileX = 0;
				int tileY = 0;
				switch(tile.getSlot()) {
				case BOTTOM_LEFT:
					tileX = 0;
					tileY = 1;
					break;
				case BOTTOM_MIDDLE:
					tileX = 1;
					tileY = 1;
					break;
				case BOTTOM_RIGHT:
					tileX = 2;
					tileY = 1;
					break;
				case HORIZ_LEFT:
					tileX = 0;
					tileY = 0;
					break;
				case HORIZ_MIDDLE:
					tileX = 1;
					tileY = 0;
					break;
				case HORIZ_RIGHT:
					tileX = 2;
					tileY = 0;
					break;
				case INSIDE:
					tileX = 4;
					tileY = 3;
					break;
				case LEFT_MIDDLE:
					tileX = 0;
					tileY = 2;
					break;
				case MIDDLE:
					tileX = 1;
					tileY = 2;
					break;
				case RIGHT_MIDDLE:
					tileX = 2;
					tileY = 2;
					break;
				case SOLO:
					tileX = 3;
					tileY = 0;
					break;
				case TOP_LEFT:
					tileX = 0;
					tileY = 3;
					break;
				case TOP_MIDDLE:
					tileX = 1;
					tileY = 3;
					break;
				case TOP_RIGHT:
					tileX = 2;
					tileY = 3;
					break;
				case VERT_BOTTOM:
					tileX = 3;
					tileY = 1;
					break;
				case VERT_MIDDLE:
					tileX = 3;
					tileY = 2;
					break;
				case VERT_TOP:
					tileX = 3;
					tileY = 3;
					break;
				default:
					break;
				}
				
				float tx = tileX * 16.0f * tileScale;
				float ty = tileY * 16.0f * tileScale;
				
				ty = rect.height * tileScale - ty;
				
				float drawX = x + tx;
				float drawY = y - ty;
				
				batch.draw(tileset.getTilesheet().getTexture(), drawX, drawY, 0f, 0f, 16f, 16f, 2.0f, 2.0f, 0.0f, tile.getSheetX(), tile.getSheetY(), 16, 16, false, false);
				
			}
			x += rect.width * tileScale + 16;
		}
		
		batch.end();
	}
	
	public Tileset getTileset() {
		return tileset;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public TilesetTile getActiveTile() {
		return activeTile;
	}
}
