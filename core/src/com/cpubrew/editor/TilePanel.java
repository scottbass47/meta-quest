package com.cpubrew.editor;

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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.IntMap;
import com.cpubrew.level.tiles.TileSlot;
import com.cpubrew.level.tiles.Tileset;
import com.cpubrew.level.tiles.TilesetLoader;
import com.cpubrew.level.tiles.TilesetTile;

public class TilePanel {

	private float x;
	private float y;
	private float width = 1280;
	private float height = 200;
	private float tileScale = 2.0f;
	private float tileSpacing = 2.0f;
	
	private Color backgroundColor = new Color(Color.BLACK).mul(1.0f, 1.0f, 1.0f, 0.9f);
	private ShapeRenderer shapeRenderer;
	private Tileset tileset;
	private TilesetTile activeTile;
	
	private IntMap<Rectangle> clusterArea;
	private ArrayMap<Rectangle, TilesetTile> tileMap;
	
	public TilePanel() {
		shapeRenderer = new ShapeRenderer();
		clusterArea = new IntMap<Rectangle>();
		tileMap = new ArrayMap<Rectangle, TilesetTile>();

		TilesetLoader loader = new TilesetLoader();
		tileset = loader.load(Gdx.files.internal("map/grassy.atlas"));
		
		setupClusters();
	}

	private void setupClusters() {
		IntMap<Array<TilesetTile>> clusterMap = tileset.getClusterIDMap();
		
		float x = tileSpacing;
		float y = height - tileSpacing;
		for(Iterator<IntMap.Entry<Array<TilesetTile>>> iter = clusterMap.iterator(); iter.hasNext(); ) {
			IntMap.Entry<Array<TilesetTile>> entry = iter.next();
			
			int id = entry.key;
			Array<TilesetTile> tiles = entry.value;
			
			Rectangle rect = new Rectangle();
			
			int tilesWide = 5;
			int tilesHigh = 4;
			
			// Assumes all cluster types are the same
			rect.x = x;
			rect.width = tilesWide * 16.0f * tileScale + (tilesWide - 1) * tileSpacing;
			rect.height = tilesHigh * 16.0f * tileScale + (tilesHigh - 1) * tileSpacing;
			rect.y = y - rect.height;
			
			x += rect.width + 8 * tileSpacing;
			
			clusterArea.put(id, rect);
			
			for(TilesetTile tile : tiles) {
				Vector2 tilePos = getTilePosition(tile.getSlot());
				
				float tx = tilePos.x * 16.0f * tileScale + tilePos.x * tileSpacing;
				float ty = tilePos.y * 16.0f * tileScale + tilePos.y * tileSpacing;
				
				float drawX = rect.x + tx;
				float drawY = rect.y + ty;
				
				Rectangle tileRect = new Rectangle();
				tileRect.x = drawX;
				tileRect.y = drawY;
				tileRect.width = 16.0f * tileScale;
				tileRect.height = 16.0f * tileScale;
				
				tileMap.put(tileRect, tile);
			}
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
		
		Gdx.gl.glDisable(GL11.GL_BLEND);
	}

	private void renderTileset(SpriteBatch batch) {
		batch.begin();
		
		for(Rectangle rect : tileMap.keys()) {
			TilesetTile tile = tileMap.get(rect);
			batch.draw(tileset.getTilesheet().getTexture(), rect.x, rect.y, 0f, 0f, 16f, 16f, 2.0f, 2.0f, 0.0f, tile.getSheetX(), tile.getSheetY(), 16, 16, false, false);
		}
		batch.end();
	}
	
	public TilesetTile getTileAt(float x, float y) {
		for(Rectangle rect : tileMap.keys()) {
			if(rect.contains(x, y)) {
				return tileMap.get(rect);
			}
		}
		return null;
	}
	
	private Vector2 getTilePosition(TileSlot slot) {
		float tileX = 0.0f;
		float tileY = 0.0f;
		switch(slot) {
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
		return new Vector2(tileX, tileY);
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
	
	public void setActiveTile(TilesetTile activeTile) {
		this.activeTile = activeTile;
	}
}
