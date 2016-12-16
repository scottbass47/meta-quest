package com.fullspectrum.utils;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.fullspectrum.game.GameVars;

public class RenderUtils {

	public static void renderTrajectory(ShapeRenderer sRender, float x, float y, boolean right, float time, float jumpForce, float speed, int segments){
		float step = time / segments;
		for(float t = 0; t < time; t+=step){
			float x1 = x + t * speed * (right ? 1.0f : -1.0f);
			float x2 = x + (t + step) * speed * (right ? 1.0f : -1.0f);
			float y1 = y + jumpForce * t + 0.5f * GameVars.GRAVITY * t * t;
			float y2 = y + jumpForce * (t + step) + 0.5f * GameVars.GRAVITY * (t + step) * (t + step);
			sRender.line(x1, y1, x2, y2);
		}
	}
	
}
