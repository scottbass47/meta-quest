package com.fullspectrum.editor.gui;

public interface KeyListener {

	public void onKeyPress(int keycode);
	public void onKeyRelease(int keycode);
	public void onKeyType(char character);
	
}