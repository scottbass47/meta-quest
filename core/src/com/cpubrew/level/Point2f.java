package com.cpubrew.level;

public class Point2f {

	public final float x;
	public final float y;
	
	public Point2f(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "x: " + x + ", y: " + y;
	}
	
}
