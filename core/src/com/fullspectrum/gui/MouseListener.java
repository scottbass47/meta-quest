package com.fullspectrum.gui;

public interface MouseListener {

	public void onMouseMove(int x, int y);
	public void onMouseDrag(int x, int y);
	public void onMouseUp(int x, int y, int button);
	public void onMouseDown(int x, int y, int button);
	public void onMouseEnter(int x, int y);
	public void onMouseExit(int x, int y);
}
