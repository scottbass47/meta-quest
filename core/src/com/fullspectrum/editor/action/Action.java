package com.fullspectrum.editor.action;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fullspectrum.editor.LevelEditor;

public abstract class Action implements InputProcessor {

	protected LevelEditor editor;
	protected OrthographicCamera worldCamera;
	protected OrthographicCamera hudCamera;
	protected ActionManager actionManager;
	
	/** Called after all fields have been set. Use this instead of constructor. */
	public abstract void onEnter();
	public abstract void update(float delta);
	public abstract void render(SpriteBatch batch);
	public abstract void onExit();
	
	public boolean isBlocking() {
		return false;
	}
	
	public boolean renderInFront() {
		return false;
	}
	
	public void setEditor(LevelEditor editor) {
		this.editor = editor;
	}
	
	public void setWorldCamera(OrthographicCamera worldCamera) {
		this.worldCamera = worldCamera;
	}
	
	public void setHudCamera(OrthographicCamera hudCamera) {
		this.hudCamera = hudCamera;
	}
	
	public void setActionManager(ActionManager actionManager) {
		this.actionManager = actionManager;
	}
	
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
		return false;
	}
	
}
