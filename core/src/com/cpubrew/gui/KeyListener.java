package com.cpubrew.gui;

public interface KeyListener extends EventListener{

	public void onKeyPress(KeyEvent ev);
	public void onKeyRelease(KeyEvent ev);
	public void onKeyType(KeyEvent ev);
	
}
