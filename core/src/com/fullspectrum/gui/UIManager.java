package com.fullspectrum.gui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class UIManager {

	private OrthographicCamera hudCamera;
	private Array<Window> windows;
	
	public void setHudCamera(OrthographicCamera hudCam) {
		this.hudCamera = hudCam;
	}
	
	public void render(SpriteBatch batch) {
		for(Window window : windows) {
			window.render(batch);
		}
	}
	
	public void update(float delta){
		for(Window window : windows) {
			window.update(delta);
		}
	}
	
	public Window newWindow() {
		Window window = new Window();
		window.setHudCamera(hudCamera);
		windows.add(window);
		return window;
	}
}
