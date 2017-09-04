package com.cpubrew.gui;

public interface KeyListener extends EventListener{

	public void onKeyPress(int keycode);
	public void onKeyRelease(int keycode);
	public void onKeyType(char character);
	
}
