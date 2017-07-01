package com.fullspectrum.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Maths {

	private Maths() {}
	
	public static float getOverflow(float amount, float max){
		if(amount <= max) return 0.0f;
		return amount - max;
	}

	public static float atan2(Entity e1, Entity e2){
		Vector2 pos1 = PhysicsUtils.getPos(e1);
		Vector2 pos2 = PhysicsUtils.getPos(e2);
		return MathUtils.atan2(pos1.y - pos2.y, pos1.x - pos2.x);
	}
	
	/** Angle in degrees */
	public static boolean inQuad(int quad, float angle){
		if(quad <= 0 || quad > 4) throw new IllegalArgumentException("Quadrant must be a value in the range 1-4.");
		
		return getQuad(angle) == quad;
	}
	
	public static int getQuad(float angle){
		angle = normaliseAngle(angle);
		if(angle <= 90 && angle >= 0) return 1;
		if(angle >= 90 && angle <= 180) return 2;
		if(angle >= 180 && angle <= 270) return 3;
		if(angle >= 270 && angle <= 360) return 4;
		throw new RuntimeException("Method \"getQuad\" failed.");
	}
	
	/** Angle in degrees. Returns an angle between 0-360 */
	public static float normaliseAngle(float angle){
		// PERFORMANCE Not efficient for big angles
		if(angle < 0){
			while(angle < 0) angle += 360;
		} else if(angle > 360){
			while(angle > 360) angle -= 360;
		}
		return angle;
	}
	
	/** Scales the width and height of the specified rectangle and returns a new rectangle with the new dimensions. */
	public static Rectangle scl(Rectangle rectangle, float scale) {
		Rectangle rect = new Rectangle(rectangle);
		rect.width *= scale;
		rect.height *= scale;
		return rect;
	}
	
	/** 
	 * Use when converting floating point coordinates to grid tiles<br><br>
	 * returns (int)(x < 0 ? x - 1 : x) 
	 */
	public static int toGridCoord(float x) {
		if(x < 0 && MathUtils.isEqual(x, (int)x)) {
			return (int)x;
		}
		return (int)(x < 0 ? x - 1 : x);
	}
}
