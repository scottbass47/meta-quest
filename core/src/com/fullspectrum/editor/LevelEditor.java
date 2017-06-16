package com.fullspectrum.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.fullspectrum.level.Level;

public class LevelEditor implements InputProcessor{

	private Level currentLevel;
	private TilePanel tilePanel;
	
	// Camera
	private OrthographicCamera worldCamera;
	private OrthographicCamera hudCamera;
	private float moveVel = 20.0f;
	
	public LevelEditor() {
		tilePanel = new TilePanel();
		tilePanel.setX(0.0f);
		tilePanel.setY(0.0f);
	}
	
	public Level getCurrentLevel() {
		return currentLevel;
	}
	
	public void setCurrentLevel(Level currentLevel) {
		this.currentLevel = currentLevel;
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
			worldCamera.position.y += delta * moveVel * worldCamera.zoom;
		}
		if(Gdx.input.isKeyPressed(Keys.A)) {
			worldCamera.position.x -= delta * moveVel * worldCamera.zoom;
		}
		if(Gdx.input.isKeyPressed(Keys.S)) {
			worldCamera.position.y -= delta * moveVel * worldCamera.zoom;
		}
		if(Gdx.input.isKeyPressed(Keys.D)) {
			worldCamera.position.x += delta * moveVel * worldCamera.zoom;
		}
	}
	
	public void render() {
		currentLevel.render(worldCamera);
		tilePanel.render(hudCamera);
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
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		worldCamera.zoom += amount * 0.02f;
		worldCamera.zoom = MathUtils.clamp(worldCamera.zoom, 0.25f, 2.0f);
		return false;
	}
	
}
