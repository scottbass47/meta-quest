package com.fullspectrum.gui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class UIManager implements InputProcessor {

	private OrthographicCamera hudCamera;
	private Array<Window> windows;
	private Window focusedWindow;
	
	public UIManager(OrthographicCamera hudCamera) {
		this.hudCamera = hudCamera;
		windows = new Array<Window>();
	}
	
	public void render(SpriteBatch batch) {
		for(Window window : windows) {
			if(!window.isVisible()) continue;
			window.render(batch);
		}
	}
	
	public void update(float delta){
		for(Window window : windows) {
			if(!window.isVisible()) continue;
			window.update(delta);
		}
	}
	
	/**
	 * Creates an empty window and returns it.
	 * @return
	 */
	public Window newWindow() {
		return newWindow("");
	}
	
	/**
	 * Creates an empty window with the specified title and returns it.
	 * @param title
	 * @return
	 */
	public Window newWindow(String title) {
		Window window = new Window(title);
		window.setHudCamera(hudCamera);
		window.setManager(this);
		windows.add(window);
		return window;
	}

	public void removeWindow(Window window) {
		windows.removeValue(window, false);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		return focusedWindow == null ? false : focusedWindow.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		return focusedWindow == null ? false : focusedWindow.keyUp(keycode);
	}

	@Override
	public boolean keyTyped(char character) {
		return focusedWindow == null ? false : focusedWindow.keyTyped(character);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector3 coords = hudCamera.unproject(new Vector3(screenX, screenY, 0.0f));
		int x = (int) coords.x;
		int y = (int) coords.y;
		
		Window window = firstWindowAt(x, y);
		if(window == null) return false;
		
		return window.touchDown(x - window.getX(), y - window.getY(), pointer, button);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Vector3 coords = hudCamera.unproject(new Vector3(screenX, screenY, 0.0f));
		int x = (int) coords.x;
		int y = (int) coords.y;
		
		Window window = firstWindowAt(x, y);
		if(window == null) return false;
		
		if(window.isFocusable()) {
			focusedWindow = window;
			window.requestFocus();
		}
		
		return window.touchUp(x - window.getX(), y - window.getY(), pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Vector3 coords = hudCamera.unproject(new Vector3(screenX, screenY, 0.0f));
		int x = (int) coords.x;
		int y = (int) coords.y;
		
		Window window = firstWindowAt(x, y);
		if(window == null) return false;
		
		return window.touchDragged(x - window.getX(), y - window.getY(), pointer);
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		Vector3 coords = hudCamera.unproject(new Vector3(screenX, screenY, 0.0f));
		int x = (int) coords.x;
		int y = (int) coords.y;
		
		Window window = firstWindowAt(x, y);
		if(window == null) return false;
		
		return window.mouseMoved(x - window.getX(), y - window.getY());
	}

	@Override
	public boolean scrolled(int amount) {
		return focusedWindow == null ? false : focusedWindow.scrolled(amount);
	}
	
	private Window firstWindowAt(int x, int y) {
		for(int i = windows.size - 1; i >= 0; i--) {
			Window window = windows.get(i);
			if(!window.isVisible()) continue;
			if(window.getBounds().contains(x, y)) return window;
		}
		return null;
	}
	
	public void setHudCamera(OrthographicCamera hudCam) {
		this.hudCamera = hudCam;
	}
	
	public OrthographicCamera getHudCamera() {
		return hudCamera;
	}
}
