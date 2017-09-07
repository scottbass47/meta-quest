package com.cpubrew.gui;

import com.badlogic.gdx.math.MathUtils;

public class DragHandler extends MouseAdapter {

	private int x;
	private int y;
	
	@Override
	public void onMouseDown(MouseEvent ev) {
		this.x = ev.getX(); 
		this.y = ev.getY();
	}
	
	@Override
	public void onMouseDrag(MouseEvent ev) {
		int x = ev.getX();
		int y = ev.getY();
		
		int dx = x - this.x;
		int dy = y - this.y;
		
		Component component = ev.getSource();
		int newX = MathUtils.clamp(dx + component.getX(), 0, component.getParent().getWidth() - component.getWidth());
		int newY = MathUtils.clamp(dy + component.getY(), 0, component.getParent().getHeight() - component.getHeight());
		
		component.setPosition(newX, newY);
	}
	
	@Override
	public void onMouseEnter(MouseEvent ev) {
		this.x = ev.getX();
		this.y = ev.getY();
	}
}
