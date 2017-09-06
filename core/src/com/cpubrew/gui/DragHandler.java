package com.cpubrew.gui;

import com.badlogic.gdx.math.MathUtils;

public class DragHandler extends MouseAdapter {

	private Component component;
	private int x;
	private int y;
	
	public DragHandler(Component component) {
		this.component = component;
	}
	
	@Override
	public void onMouseDown(int x, int y, int button) {
		this.x = x; 
		this.y = y;
	}
	
	@Override
	public void onMouseDrag(int x, int y) {
		int dx = x - this.x;
		int dy = y - this.y;
		
		int newX = MathUtils.clamp(dx + component.getX(), 0, component.getParent().getWidth() - component.getWidth());
		int newY = MathUtils.clamp(dy + component.getY(), 0, component.getParent().getHeight() - component.getHeight());
		
		component.setPosition(newX, newY);
	}
	
	@Override
	public void onMouseEnter(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
