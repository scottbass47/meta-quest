package com.fullspectrum.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.level.Level;
import com.fullspectrum.level.MapRenderer;
import com.fullspectrum.level.tiles.Tileset;
import com.fullspectrum.level.tiles.TilesetTile;

public class LevelEditor implements InputProcessor{

	private Level currentLevel;
	private TilePanel tilePanel;
	private MapRenderer mapRenderer;
	
	// Camera
	private OrthographicCamera worldCamera;
	private OrthographicCamera hudCamera;
	private float moveVel = 20.0f;
	
	private boolean mouseOnMap = false;
	private float mouseX;
	private float mouseY;
	
	public LevelEditor() {
		tilePanel = new TilePanel();
		tilePanel.setX(0.0f);
		tilePanel.setY(0.0f);
		
		mapRenderer = new MapRenderer();
		mapRenderer.setGridLinesOn(true);
		mapRenderer.setTileset(tilePanel.getTileset());
	}
	
	public Level getCurrentLevel() {
		return currentLevel;
	}
	
	public void setCurrentLevel(Level currentLevel) {
		this.currentLevel = currentLevel;
		mapRenderer.setTileMap(currentLevel.getTileMap());
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
//		currentLevel.render(batch, worldCamera);
		worldCamera.update();
		hudCamera.update();
		
		
		mapRenderer.setView(worldCamera);
		mapRenderer.render(batch);
		tilePanel.render(hudCamera, batch);
		
		if(mouseOnMap) {
			Gdx.input.setCursorCatched(true);
			batch.setProjectionMatrix(worldCamera.combined);
			batch.begin();
			
			float x = mouseX * GameVars.PPM_INV * worldCamera.zoom;
			float y = mouseY * GameVars.PPM_INV * worldCamera.zoom;

			x += worldCamera.position.x - worldCamera.viewportWidth * 0.5f * worldCamera.zoom;
			y += worldCamera.position.y - worldCamera.viewportHeight * 0.5f * worldCamera.zoom;
			
			drawTile(batch, tilePanel.getActiveTile(), x - 0.5f, y - 0.5f);
			batch.end();
		} else {
			Gdx.input.setCursorCatched(false);
		}
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
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector2 screenPos = toHudCoords(screenX, screenY);
		if(onTilePanel(screenPos.x, screenPos.y)) System.out.println("On panel");
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
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
	
}