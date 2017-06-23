package com.fullspectrum.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.fullspectrum.editor.action.ActionManager;
import com.fullspectrum.editor.action.EditorActions;
import com.fullspectrum.editor.action.SelectAction;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.level.ExpandableGrid;
import com.fullspectrum.level.Level;
import com.fullspectrum.level.Level.EntitySpawn;
import com.fullspectrum.level.MapRenderer;
import com.fullspectrum.level.tiles.MapTile;

public class LevelEditor extends InputMultiplexer{

	private Level currentLevel;
	private ExpandableGrid<MapTile> tileMap;
	private TilePanel tilePanel;
	private MapRenderer mapRenderer;
	private Texture eraseTexture;
	private Texture selectTexture;
	private ActionManager actionManager;
	
	// Camera
	private OrthographicCamera worldCamera;
	private OrthographicCamera hudCamera;
	private float moveVel = 20.0f;
	
	private boolean mouseOnMap = false;
	private Vector2 mousePos;
	private boolean mouseDown = false;
	private int ctrlCount = 0;
	private int shiftCount = 0;
	private float animTime;
	
	public LevelEditor() {
		tilePanel = new TilePanel();
		tilePanel.setX(0.0f);
		tilePanel.setY(0.0f);
		
		mapRenderer = new MapRenderer();
		mapRenderer.setGridLinesOn(true);
		mapRenderer.setTileset(tilePanel.getTileset());

		setupTextures();

		actionManager = new ActionManager(this);
		addProcessor(actionManager);
	}
	
	private void setupTextures() {
		Pixmap pixmap = new Pixmap(16, 16, Format.RGBA8888);
		Color color = new Color(Color.DARK_GRAY);
		pixmap.setColor(color.mul(1.0f, 1.0f, 1.0f, 0.65f));
		pixmap.fillRectangle(0, 0, 16, 16);
		selectTexture = new Texture(pixmap);
		
		color = new Color(Color.PINK);
		pixmap.setColor(color.mul(1.0f, 1.0f, 1.0f, 0.65f));
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
		actionManager.setWorldCamera(camera);
	}
	
	public void setHudCamera(OrthographicCamera hudCamera) {
		this.hudCamera = hudCamera;
		actionManager.setHudCamera(hudCamera);
	}

	public void update(float delta) {
		animTime += delta;
		
		actionManager.update(delta);
		moveCamera(delta);
	}
	
	private void moveCamera(float delta) {
		if(ctrlDown() || shiftDown() || actionManager.isBlocking()){
			return;
		}
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
	
	private void handleInput() {
		// Adding rows/cols
		if(actionManager.isBlocking()) return;
		
		if (ctrlDown()) {
			if (Gdx.input.isKeyJustPressed(Keys.UP)) {
				tileMap.addRow(true);
			}
			if (Gdx.input.isKeyJustPressed(Keys.DOWN)) {
				tileMap.addRow(false);
			}
			if (Gdx.input.isKeyJustPressed(Keys.LEFT)) {
				tileMap.addCol(false);
			}
			if (Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
				tileMap.addCol(true);
			}
		}

		// Removing rows/cols
		if (shiftDown()) {
			if (Gdx.input.isKeyJustPressed(Keys.DOWN)) {
				tileMap.removeRow(true, true);
			}
			if (Gdx.input.isKeyJustPressed(Keys.UP)) {
				tileMap.removeRow(false, true);
			}
			if (Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
				tileMap.removeCol(false, true);
			}
			if (Gdx.input.isKeyJustPressed(Keys.LEFT)) {
				tileMap.removeCol(true, true);
			}
		}
	}
	
	public void render(SpriteBatch batch) {
		handleInput();
		worldCamera.update();
		hudCamera.update();
	
		mapRenderer.setView(worldCamera);
		mapRenderer.render(batch);
		
		// Render entity spawns
		batch.begin();
		
		SelectAction selectAction = null;
		if(actionManager.getCurrentAction() == EditorActions.SELECT) {
			selectAction = (SelectAction) actionManager.getCurrentActionInstance();
		}
		
		for(int i = 0; i < currentLevel.getEntitySpawns().size + 1; i++ ) { 
			EntitySpawn entitySpawn = null;
			if(i == 0 && currentLevel.getPlayerSpawn() != null) {
				entitySpawn = currentLevel.getPlayerSpawn();
			} else if(i == 0 && currentLevel.getPlayerSpawn() == null) {
				continue;
			} else {
				entitySpawn = currentLevel.getEntitySpawns().get(i - 1);
			}
			
			boolean selected = selectAction == null ? false : selectAction.isSelected(entitySpawn);
			EntityIndex index = entitySpawn.getIndex();
			Vector2 pos = entitySpawn.getPos();
			Animation animation = index.getIdleAnimation();
			TextureRegion frame = animation.getKeyFrame(animTime);
			
			float w = frame.getRegionWidth();
			float h = frame.getRegionHeight();
			
			float x = pos.x - w * 0.5f;
			float y = pos.y - h * 0.5f;
			
			if(!entitySpawn.isFacingRight()) {
				frame.flip(true, false);
			}
			
			if(selected) {
				batch.setColor(Color.WHITE);
			} else {
				batch.setColor(1.0f, 1.0f, 1.0f, 0.75f);
			}
			batch.draw(frame, x, y, w * 0.5f, h * 0.5f, w, h, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f);
			frame.flip(frame.isFlipX(), false);
		}
		batch.setColor(Color.WHITE);
		batch.end();
		
		if(actionManager.renderInFront()) {
			tilePanel.render(hudCamera, batch);
			actionManager.render(batch);
		} else {
			actionManager.render(batch);
			tilePanel.render(hudCamera, batch);
		}
	}
		

	////////////////////////
	// 		  INPUT		  //
	////////////////////////
	
	@Override
	public boolean keyDown(int keycode) {
//		actionManager.keyDown(keycode);
		if(keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) {
			shiftCount++;
		}
		if(keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT) {
			ctrlCount++;
		}
		return super.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
//		actionManager.keyUp(keycode);
		if(keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) {
			shiftCount--;
		}
		if(keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT) {
			ctrlCount--;
		}
		return super.keyUp(keycode);
	}

	@Override
	public boolean keyTyped(char character) {
//		actionManager.keyTyped(character);
		return super.keyTyped(character);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//		actionManager.touchDown(screenX, screenY, pointer, button);
		mouseDown = true;
		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//		actionManager.touchUp(screenX, screenY, pointer, button);
		mouseDown = false;
		return super.touchUp(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
//		actionManager.touchDragged(screenX, screenY, pointer);
		mousePos = toHudCoords(screenX, screenY);
		mouseOnMap = !onTilePanel(mousePos.x, mousePos.y);
		return super.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
//		actionManager.mouseMoved(screenX, screenY);
		mousePos = toHudCoords(screenX, screenY);
		mouseOnMap = !onTilePanel(mousePos.x, mousePos.y);
		return super.mouseMoved(screenX, screenY);
	}

	@Override
	public boolean scrolled(int amount) {
//		actionManager.scrolled(amount);
		worldCamera.zoom += amount * 0.02f;
		worldCamera.zoom = MathUtils.clamp(worldCamera.zoom, 0.25f, 2.0f);
		return super.scrolled(amount);
	}
	
	public boolean onTilePanel(float screenX, float screenY) {
		return screenX >= tilePanel.getX() && screenX <= tilePanel.getX() + tilePanel.getWidth() &&
				screenY >= tilePanel.getY() && screenY <= tilePanel.getY() + tilePanel.getHeight();
	}
	
	public TilePanel getTilePanel() {
		return tilePanel;
	}
	
	public ExpandableGrid<MapTile> getTileMap() {
		return tileMap;
	}
	
	public Vector2 toHudCoords(int screenX, int screenY) {
		Vector3 result = hudCamera.unproject(new Vector3(screenX, screenY, 0));
		return new Vector2(result.x, result.y);
	}
	
	public Vector2 toHudCoords(Vector2 coords) {
		return toHudCoords((int)coords.x, (int)coords.y);
	}
	
	public Vector2 toWorldCoords(float mouseX, float mouseY) {
		float x = mouseX * GameVars.PPM_INV * worldCamera.zoom;
		float y = mouseY * GameVars.PPM_INV * worldCamera.zoom;

		x += worldCamera.position.x - worldCamera.viewportWidth * 0.5f * worldCamera.zoom;
		y += worldCamera.position.y - worldCamera.viewportHeight * 0.5f * worldCamera.zoom;
		
		return new Vector2(x, y);
	}
	
	public Vector2 toWorldCoords(Vector2 coords) {
		return toWorldCoords(coords.x, coords.y);
	}
	
	public boolean shiftDown() {
		return shiftCount > 0;
	}
	
	public boolean ctrlDown() {
		return ctrlCount > 0;
	}
	
	public boolean isMouseOnMap() {
		return mouseOnMap;
	}
	
	public boolean isMouseDown() {
		return mouseDown;
	}
	
	public Vector2 getMousePos() {
		return mousePos;
	}
	
	public Texture getEraseTexture() {
		return eraseTexture;
	}
	
	public Texture getSelectTexture() {
		return selectTexture;
	}
	
}