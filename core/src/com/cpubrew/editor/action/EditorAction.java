package com.cpubrew.editor.action;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cpubrew.editor.LevelEditor;
import com.cpubrew.gui.KeyEvent;
import com.cpubrew.gui.KeyListener;
import com.cpubrew.gui.MouseEvent;
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
	public void onKeyPress(KeyEvent ev) {
	}

	@Override
	public void onKeyRelease(KeyEvent ev) {
	}

	@Override
	public void onKeyType(KeyEvent ev) {
	}

	@Override
	public void onMouseDown(MouseEvent ev) {
	}

	@Override
	public void onMouseUp(MouseEvent ev) {
	}
	
	@Override
	public void onMouseMove(MouseEvent ev) {
	}
	
	@Override
	public void onMouseDrag(MouseEvent ev) {
	}
	
	@Override
	public void onMouseEnter(MouseEvent ev) {
	}
	
	@Override
	public void onMouseExit(MouseEvent ev) {
	}
}
