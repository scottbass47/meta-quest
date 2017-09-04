package com.cpubrew.editor.action;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cpubrew.editor.LevelEditor;
import com.cpubrew.gui.KeyListener;
import com.cpubrew.gui.MouseListener;

public abstract class EditorAction implements KeyListener, MouseListener {

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
	public void onKeyPress(int keycode) {
	}

	@Override
	public void onKeyRelease(int keycode) {
	}

	@Override
	public void onKeyType(char character) {
	}

	@Override
	public void onMouseDown(int x, int y, int button) {
	}

	@Override
	public void onMouseUp(int x, int y, int button) {
	}
	
	@Override
	public void onMouseMove(int x, int y) {
	}
	
	@Override
	public void onMouseDrag(int x, int y) {
	}
	
	@Override
	public void onMouseEnter(int x, int y) {
	}
	
	@Override
	public void onMouseExit(int x, int y) {
	}
}
