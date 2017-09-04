package com.cpubrew.debug;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.cpubrew.debug.DebugRender.RenderMode;

public class LineDef extends DebugRenderDef{

	private float x1;
	private float y1;
	private float x2;
	private float y2;
	
	public LineDef(RenderMode mode, Color color, float duration, float x1, float y1, float x2, float y2) {
		super(mode, color, duration);
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	@Override
	public void render(ShapeRenderer renderer) {
		renderer.line(x1, y1, x2, y2);
	}

}
