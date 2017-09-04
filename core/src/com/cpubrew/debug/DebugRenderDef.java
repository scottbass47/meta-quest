package com.cpubrew.debug;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.cpubrew.debug.DebugRender.RenderMode;

public abstract class DebugRenderDef {

	protected RenderMode mode;
	protected Color color;
	protected float duration;
	protected float elapsed = 0.0f;
	
	public DebugRenderDef(RenderMode mode, Color color, float duration){
		this.mode = mode;
		this.color = color;
		this.duration = duration;
	}
	
	public abstract void render(ShapeRenderer renderer);
	
}
