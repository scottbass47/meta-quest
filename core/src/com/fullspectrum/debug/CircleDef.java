package com.fullspectrum.debug;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.fullspectrum.debug.DebugRender.RenderMode;

public class CircleDef extends DebugRenderDef{

	private float x;
	private float y;
	private float radius;
	
	public CircleDef(RenderMode mode, Color color, float duration, float x, float y, float radius) {
		super(mode, color, duration);
		this.x = x;
		this.y = y;
		this.radius = radius;
	}

	@Override
	public void render(ShapeRenderer renderer) {
		renderer.circle(x, y, radius, 32);
	}
}
