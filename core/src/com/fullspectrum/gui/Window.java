package com.fullspectrum.gui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class Window extends Container implements InputProcessor {

	private OrthographicCamera hudCamera;
	
	@Override
	public void render(SpriteBatch batch) {
		if(components.size == 0) return;
		batch.setProjectionMatrix(hudCamera.combined);
		batch.begin();
		super.render(batch);
		batch.end();
	}
	
	@Override
	public void add(Component component) {
		super.add(component);
		setSize(Math.max(width, component.getX() + component.getWidth()), Math.max(height, component.getY() + component.getHeight()));
	}
	
	public void setHudCamera(OrthographicCamera hudCamera) {
		this.hudCamera = hudCamera;
	}
	
	public OrthographicCamera getHudCamera() {
		return hudCamera;
	}

	@Override
	public boolean keyDown(int keycode) {
		super.onKeyPress(keycode);
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		super.onKeyRelease(keycode);
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		super.onKeyType(character);
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector3 coords = hudCamera.unproject(new Vector3(screenX, screenY, 0.0f));
		super.onMouseDown((int)coords.x - getX(), (int)coords.y - getY(), button);
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Vector3 coords = hudCamera.unproject(new Vector3(screenX, screenY, 0.0f));
		super.onMouseUp((int)coords.x - getX(), (int)coords.y - getY(), button);
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Vector3 coords = hudCamera.unproject(new Vector3(screenX, screenY, 0.0f));
		super.onMouseDrag((int)coords.x - getX(), (int)coords.y - getY());
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		Vector3 coords = hudCamera.unproject(new Vector3(screenX, screenY, 0.0f));
		super.onMouseMove((int)coords.x - getX(), (int)coords.y - getY());
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		return true;
	}
}
