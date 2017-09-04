package com.cpubrew.gui;

public interface MouseListener extends EventListener {

	public void onMouseMove(int x, int y);
	public void onMouseDrag(int x, int y);
	public void onMouseUp(int x, int y, int button);
	public void onMouseDown(int x, int y, int button);
	public void onMouseEnter(int x, int y);
	public void onMouseExit(int x, int y);
}
