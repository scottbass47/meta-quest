package com.cpubrew.debug;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.cpubrew.debug.DebugRender.RenderMode;

public class RectDef extends DebugRenderDef{

	private float x;
	private float y;
	private float width;
	private float height;
	
	public RectDef(RenderMode mode, Color color, float duration, float x, float y, float width, float height) {
		super(mode, color, duration);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public void render(ShapeRenderer renderer) {
		renderer.rect(x, y, width, height);
	}

}
