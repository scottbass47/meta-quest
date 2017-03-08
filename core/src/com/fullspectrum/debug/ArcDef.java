package com.fullspectrum.debug;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.fullspectrum.debug.DebugRender.RenderMode;

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

	@Override
	public void render(ShapeRenderer renderer) {
		renderer.arc(x, y, radius, start, degrees, 32);
	}

}
