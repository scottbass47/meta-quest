package com.fullspectrum.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.level.ExpandableGrid;
import com.fullspectrum.level.Level;
import com.fullspectrum.level.LevelUtils;
import com.fullspectrum.level.MapRenderer;
import com.fullspectrum.level.tiles.MapTile;
import com.fullspectrum.level.tiles.MapTile.Side;
import com.fullspectrum.level.tiles.MapTile.TileType;
import com.fullspectrum.level.tiles.TileSlot;
import com.fullspectrum.level.tiles.Tileset;
import com.fullspectrum.level.tiles.TilesetTile;

public class LevelEditor implements InputProcessor{

	private Level currentLevel;
	private ExpandableGrid<MapTile> tileMap;
	private TilePanel tilePanel;
	private MapRenderer mapRenderer;
	private Texture selectTexture;
	private Texture eraseTexture;
	
	// Camera
	private OrthographicCamera worldCamera;
	private OrthographicCamera hudCamera;
	private float moveVel = 20.0f;
	
	private boolean mouseOnMap = false;
	private float mouseX;
	private float mouseY;
	private boolean mouseDown = false;
	private int ctrlCount = 0;
	private int shiftCount = 0;
	
	private EditorActions currentAction = EditorActions.SELECT;
	
	public LevelEditor() {
		tilePanel = new TilePanel();
		tilePanel.setX(0.0f);
		tilePanel.setY(0.0f);
		
		mapRenderer = new MapRenderer();
		mapRenderer.setGridLinesOn(true);
		mapRenderer.setTileset(tilePanel.getTileset());
		
		Pixmap pixmap = new Pixmap(16, 16, Format.RGBA8888);
		pixmap.setColor(Color.DARK_GRAY.mul(1.0f, 1.0f, 1.0f, 0.65f));
		pixmap.fillRectangle(0, 0, 16, 16);
		selectTexture = new Texture(pixmap);
		
		pixmap.setColor(Color.PINK.mul(1.0f, 1.0f, 1.0f, 0.65f));
		pixmap.fillRectangle(0, 0, 16, 16);
		eraseTexture = new Texture(pixmap);
		
		pixmap.dispose();
	}
	
	public Level getCurrentLevel() {
		return currentLevel;
	}
	
	public void setCurrentLevel(Level currentLevel) {
		this.currentLevel = currentLevel;
		mapRenderer.setTileMap(currentLevel.getTileMap());
		tileMap = currentLevel.getTileMap();
	}
	
	public void setWorldCamera(OrthographicCamera camera) {
		this.worldCamera = camera;
	}
	
	public void setHudCamera(OrthographicCamera hudCamera) {
		this.hudCamera = hudCamera;
	}

	public void update(float delta) {
		moveCamera(delta);
	}
	
	private void moveCamera(float delta) {
		if(ctrlDown() || shiftDown()) return;
		if(Gdx.input.isKeyPressed(Keys.W)) {
			worldCamera.position.y += delta * moveVel * worldCamera.zoom * 0.5f + 0.15f;
		}
		if(Gdx.input.isKeyPressed(Keys.A)) {
			worldCamera.position.x -= delta * moveVel * worldCamera.zoom * 0.5f + 0.15f;
		}
		if(Gdx.input.isKeyPressed(Keys.S)) {
			worldCamera.position.y -= delta * moveVel * worldCamera.zoom * 0.5f + 0.15f; 
		}
		if(Gdx.input.isKeyPressed(Keys.D)) {
			worldCamera.position.x += delta * moveVel * worldCamera.zoom * 0.5f + 0.15f;
		}
	}
	
	public void render(SpriteBatch batch) {
		worldCamera.update();
		hudCamera.update();
		
		mapRenderer.setView(worldCamera);
		mapRenderer.render(batch);
		tilePanel.render(hudCamera, batch);
		
		if(mouseOnMap) {
			batch.setProjectionMatrix(worldCamera.combined);
			batch.begin();
			
			float x = mouseX * GameVars.PPM_INV * worldCamera.zoom;
			float y = mouseY * GameVars.PPM_INV * worldCamera.zoom;

			x += worldCamera.position.x - worldCamera.viewportWidth * 0.5f * worldCamera.zoom;
			y += worldCamera.position.y - worldCamera.viewportHeight * 0.5f * worldCamera.zoom;
			
			int row = (int) (y < 0 ? y - 1 : y);
			int col = (int) (x < 0 ? x - 1 : x);
			
			switch(currentAction) {
			case AUTO_PLACE:
				batch.setColor(Color.DARK_GRAY);
				drawTile(batch, tilePanel.getActiveTile(), col, row);
				batch.setColor(Color.WHITE);
				break;
			case AUTO_ERASE:
				batch.setColor(Color.DARK_GRAY);
				batch.draw(eraseTexture, col, row, 0.0f, 0.0f, selectTexture.getWidth(), selectTexture.getHeight(), GameVars.PPM_INV, GameVars.PPM_INV, 0.0f, 0, 0, selectTexture.getWidth(), selectTexture.getHeight(), false, false);
				batch.setColor(Color.WHITE);
				break;
			case ERASE:
				batch.draw(eraseTexture, col, row, 0.0f, 0.0f, selectTexture.getWidth(), selectTexture.getHeight(), GameVars.PPM_INV, GameVars.PPM_INV, 0.0f, 0, 0, selectTexture.getWidth(), selectTexture.getHeight(), false, false);
				break;
			case PLACE:
				drawTile(batch, tilePanel.getActiveTile(), col, row);
				break;
			case SELECT:
				batch.draw(selectTexture, col, row, 0.0f, 0.0f, selectTexture.getWidth(), selectTexture.getHeight(), GameVars.PPM_INV, GameVars.PPM_INV, 0.0f, 0, 0, selectTexture.getWidth(), selectTexture.getHeight(), false, false);
				break;
			default:
				break;
			}
			
			batch.end();
			
			if(mouseDown) {
				switch (currentAction) {
				case AUTO_PLACE:
					// Local variable 'mapTile' has naming conflict with another switch statement...
					if(true) {
						MapTile mapTile = new MapTile();
						mapTile.setRow(row);
						mapTile.setCol(col);
						
						
						TilesetTile tilesetTile = calculateTilesetTileAt(row, col, tilePanel.getActiveTile().getClusterID());
						mapTile.setId(tilesetTile.getID());
						mapTile.setType(TileType.GROUND);
						
						tileMap.add(row, col, mapTile);
						
						updateSurroundingTiles(row, col);
					}
					break;
				case AUTO_ERASE:
					if(tileMap.contains(row, col) && tileMap.get(row, col) != null) {
						tileMap.set(row, col, null);
						updateSurroundingTiles(row, col);
					}
					break;
				case ERASE:
					if(tileMap.contains(row, col)) {
						tileMap.set(row, col, null);
					}
					break;
				case PLACE:
					if(!tileMap.contains(row, col) || tileMap.get(row, col) == null || tileMap.get(row, col).getID() != tilePanel.getActiveTile().getID()) {
						MapTile mapTile = new MapTile();
						mapTile.setRow(row);
						mapTile.setCol(col);
						mapTile.setId(tilePanel.getActiveTile().getID());
						mapTile.setType(TileType.GROUND);
						
						tileMap.add(row, col, mapTile);
					}
					break;
				case SELECT:
					break;
				default:
					break;
				}
			}
			
		} else {
			if(mouseDown) {
				TilesetTile tile = tilePanel.getTileAt(mouseX, mouseY);
				if(tile != null) {
					tilePanel.setActiveTile(tile);
					currentAction = EditorActions.PLACE;
				}
			}
		}
		
		// Adding rows/cols
		if(ctrlDown()) {
			if(Gdx.input.isKeyJustPressed(Keys.UP)) {
				tileMap.addRow(true);
			}
			if(Gdx.input.isKeyJustPressed(Keys.DOWN)) {
				tileMap.addRow(false);	
			}
			if(Gdx.input.isKeyJustPressed(Keys.LEFT)) {
				tileMap.addCol(false);
			}
			if(Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
				tileMap.addCol(true);
			}
			if(Gdx.input.isKeyJustPressed(Keys.E)) {
				tilePanel.setActiveTile(null);
				if(currentAction == EditorActions.AUTO_PLACE) {
					currentAction = EditorActions.AUTO_ERASE;
				} else{
					currentAction = EditorActions.ERASE;
				}
			}
			if(Gdx.input.isKeyJustPressed(Keys.M)) {
				if(currentAction == EditorActions.PLACE) {
					currentAction = EditorActions.AUTO_PLACE;
				} else if(currentAction == EditorActions.ERASE) {
					currentAction = EditorActions.AUTO_ERASE;
				}
			}
			if(Gdx.input.isKeyJustPressed(Keys.S)){
				System.out.println("Saving level " + currentLevel.getInfo());
				LevelUtils.saveLevel(currentLevel);
			}
		}
		
		// Removing rows/cols
		if(shiftDown()) {
			if(Gdx.input.isKeyJustPressed(Keys.DOWN)) {
				tileMap.removeRow(true, true);
			}
			if(Gdx.input.isKeyJustPressed(Keys.UP)) {
				tileMap.removeRow(false, true);	
			}
			if(Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
				tileMap.removeCol(false, true);
			}
			if(Gdx.input.isKeyJustPressed(Keys.LEFT)) {
				tileMap.removeCol(true, true);			}
		}
		
//		System.out.println("Min Row: " + tileMap.getMinRow() + ", Min Col: " + tileMap.getMinCol() + ", Max Row: " + tileMap.getMaxRow() + ", Max Col: " + tileMap.getMaxCol());
	}
	
	private void updateSurroundingTiles(int row, int col) {
		boolean erasing = tileMap.get(row, col) == null;
		MapTile centerTile = tileMap.get(row, col);
		for(int r = row - 1; r <= row + 1; r++) {
			for(int c = col - 1; c <= col + 1; c++) {
				if(!tileMap.contains(r, c) || tileMap.get(r, c) == null || (r == row && c == col)) continue;
				MapTile mapTile = tileMap.get(r, c);
				Tileset tileset = tilePanel.getTileset();
				
				int clusterID = tileset.getClusterID(mapTile.getID());
				
				// Don't update surrounding tiles if they are apart of another clusters unless you're erasing
				if(!erasing && tileset.getClusterID(centerTile.getID()) != clusterID) continue; 
				
				TilesetTile tile = calculateTilesetTileAt(r, c, clusterID);
				mapTile.setId(tile.getID());
				tileMap.set(r, c, mapTile);
			}
		}
		
	}
	
	private TilesetTile calculateTilesetTileAt(int row, int col, int clusterID){
		Array<Side> sidesOpen = new Array<Side>(Side.class);
		
		if(isOpen(row + 1, col, clusterID)) sidesOpen.add(Side.NORTH);
		if(isOpen(row - 1, col, clusterID)) sidesOpen.add(Side.SOUTH);
		if(isOpen(row, col + 1, clusterID)) sidesOpen.add(Side.EAST);
		if(isOpen(row, col - 1, clusterID)) sidesOpen.add(Side.WEST);
		
		TileSlot slot = TileSlot.getSlot(sidesOpen.toArray());
		
		Tileset tileset = tilePanel.getTileset();
		TilesetTile tilesetTile = tileset.getTile(clusterID, slot);
		return tilesetTile;
	}

	private boolean isOpen(int row, int col, int clusterID) {
		return !tileMap.contains(row, col) || tileMap.get(row, col) == null || tileMap.get(row, col).getType() != TileType.GROUND || tilePanel.getTileset().getClusterID(tileMap.get(row, col).getID()) != clusterID;
	}
	
	private void drawTile(SpriteBatch batch, TilesetTile tile, float x, float y) {
		Tileset tileset = tilePanel.getTileset();
		batch.draw(tileset.getTilesheet().getTexture(), x, y, 0.0f, 0.0f, 16.0f, 16.0f, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f, tile.getSheetX(), tile.getSheetY(), 16, 16, false, false);
	}

	////////////////////////
	// 		  INPUT		  //
	////////////////////////
	
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) {
			shiftCount++;
		}
		if(keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT) {
			ctrlCount++;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) {
			shiftCount--;
		}
		if(keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT) {
			ctrlCount--;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		mouseDown = true;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		mouseDown = false;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Vector2 screenPos = toHudCoords(screenX, screenY);
		mouseX = screenPos.x;
		mouseY = screenPos.y;
		mouseOnMap = !onTilePanel(screenPos.x, screenPos.y);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		Vector2 screenPos = toHudCoords(screenX, screenY);
		mouseX = screenPos.x;
		mouseY = screenPos.y;
		mouseOnMap = !onTilePanel(screenPos.x, screenPos.y);
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		worldCamera.zoom += amount * 0.02f;
		worldCamera.zoom = MathUtils.clamp(worldCamera.zoom, 0.25f, 2.0f);
		return false;
	}
	
	private boolean onTilePanel(float screenX, float screenY) {
		return screenX >= tilePanel.getX() && screenX <= tilePanel.getX() + tilePanel.getWidth() &&
				screenY >= tilePanel.getY() && screenY <= tilePanel.getY() + tilePanel.getHeight();
	}
	
	private Vector2 toHudCoords(int screenX, int screenY) {
		Vector3 result = hudCamera.unproject(new Vector3(screenX, screenY, 0));
		return new Vector2(result.x, result.y);
	}
	
	private boolean shiftDown() {
		return shiftCount > 0;
	}
	
	private boolean ctrlDown() {
		return ctrlCount > 0;
	}
}