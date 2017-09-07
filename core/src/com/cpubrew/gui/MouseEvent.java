package com.cpubrew.gui;

public class MouseEvent {

	private Component source;
	private int x;
	private int y;
	private int button;
	
	public MouseEvent(Component source, int x, int y, int button) {
		this.source = source;
		this.x = x;
		this.y = y;
		this.button = button;
	}
	
	public MouseEvent(Component source, int x, int y) {
		this(source, x, y, -1);
	}
	
	public Component getSource() {
		return source;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getButton() {
		return button;
	}
	
}
