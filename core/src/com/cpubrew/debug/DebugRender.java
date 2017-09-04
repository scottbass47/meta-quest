package com.cpubrew.debug;

import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.cpubrew.game.GameVars;

public class DebugRender {

	private static RenderMode mode = RenderMode.UPDATE;
	private static ShapeType type = ShapeType.Line;
	private static Color color = Color.WHITE;
	private static ShapeRenderer renderer = new ShapeRenderer();

	// Separate queues for filled shapes and lines
	private static Array<DebugRenderDef> lineRenderQueue = new Array<DebugRenderDef>();
	private static Array<DebugRenderDef> fillRenderQueue = new Array<DebugRenderDef>();
	
	public static void setMode(RenderMode mode){
		DebugRender.mode = mode;
	}
	
	public static void render(SpriteBatch batch){
		renderer.setProjectionMatrix(batch.getProjectionMatrix());
		renderer.begin(ShapeType.Line);
		for(DebugRenderDef def : lineRenderQueue){
			renderer.setColor(def.color);
			def.render(renderer);
		}
		renderer.end();
		
		renderer.begin(ShapeType.Filled);
		for(DebugRenderDef def : fillRenderQueue){
			renderer.setColor(def.color);
			def.render(renderer);
		}
		renderer.end();
	}
	
	public static void update(float delta){
		for(Iterator<DebugRenderDef> iter = lineRenderQueue.iterator(); iter.hasNext();){
			DebugRenderDef def = iter.next();
			def.elapsed += delta;
			if(def.elapsed >= def.duration) iter.remove();
		}
		
		for(Iterator<DebugRenderDef> iter = fillRenderQueue.iterator(); iter.hasNext();){
			DebugRenderDef def = iter.next();
			def.elapsed += delta;
			if(def.elapsed >= def.duration) iter.remove();
		}
	}
	
	public static void setColor(Color color){
		DebugRender.color = color;
	}
	
	public static void setType(ShapeType type){
		DebugRender.type = type;
	}
	
	private static void addToQueue(DebugRenderDef def){
		switch(DebugRender.type){
		case Filled:
			fillRenderQueue.add(def);
			break;
		case Line:
			lineRenderQueue.add(def);
			break;
		case Point:
			break;
		default:
			break;
		}
	}
	
	private static float getDuration(){
		return mode == RenderMode.UPDATE ? GameVars.UPS_INV : 0.0f;
	}
	
	public static void line(float x1, float y1, float x2, float y2, float duration){
		addToQueue(new LineDef(mode, color, duration, x1, y1, x2, y2));
	}
	
	public static void line(float x1, float y1, float x2, float y2){
		line(x1, y1, x2, y2, getDuration());
	}
	
	public static void rect(float x, float y, float width, float height, float duration){
		addToQueue(new RectDef(mode, color, duration, x, y, width, height));
	}
	
	public static void rect(float x, float y, float width, float height){
		rect(x, y, width, height, getDuration());
	}
	
	public static void arc(float x, float y, float radius, float start, float degrees, float duration){
		addToQueue(new ArcDef(mode, color, duration, x, y, radius, start, degrees));
	}
	
	public static void arc(float x, float y, float radius, float start, float degrees){
		arc(x, y, radius, start, degrees, getDuration());
	}
	
	public static void circle(float x, float y, float radius, float duration){
		addToQueue(new CircleDef(mode, color, duration, x, y, radius));
	}
	
	public static void circle(float x, float y, float radius){
		circle(x, y, radius, getDuration());
	}
	
	public enum RenderMode{
		UPDATE,
		RENDER
	}
	
}
