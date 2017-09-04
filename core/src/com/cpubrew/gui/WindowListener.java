package com.cpubrew.gui;

public interface WindowListener extends EventListener {

	/** Called when the window's visibility is set to true */
	public void windowOpened(WindowEvent ev);
	
	/** Called when the window's visibility is set to false */
	public void windowHidden(WindowEvent ev);

	/** Called when the window is closed */
	public void windowClosed(WindowEvent ev);
	
}
