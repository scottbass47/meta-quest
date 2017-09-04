package com.cpubrew.debug;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.cpubrew.debug.DebugRender.RenderMode;

public class ArcDef extends DebugRenderDef{

	private float x;
	private float y;
	private float radius;
	private float start;
	private float degrees;
	
	public ArcDef(RenderMode mode, Color color, float duration, float x, float y, float radius, float start, float degrees) {
		super(mode, color, duration);
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.start = start;
		this.degrees = degrees;
	}

	// BUG ArrayOutOfBoundsException 20003 when rendering ranges.
	@Override
	public void render(ShapeRenderer renderer) {
		try{
			renderer.arc(x, y, radius, start, degrees, 32);
		} catch(Exception e){
			System.out.println("Out of bounds exception when rendering arc...");
		}
	}
}
