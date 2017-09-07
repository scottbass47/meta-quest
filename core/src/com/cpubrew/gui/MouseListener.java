package com.cpubrew.gui;

public interface MouseListener extends EventListener {

	public void onMouseMove(MouseEvent ev);
	public void onMouseDrag(MouseEvent ev);
	public void onMouseUp(MouseEvent ev);
	public void onMouseDown(MouseEvent ev);
	public void onMouseEnter(MouseEvent ev);
	public void onMouseExit(MouseEvent ev);
}
