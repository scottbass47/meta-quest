package com.fullspectrum.editor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class LevelTriggerPanel {

	private ShapeRenderer shape;
	
	private float x;
	private float y;
	private float width;
	private float height;
	
	public LevelTriggerPanel() {
		shape = new ShapeRenderer();
		
		width = 500;
		height = 500;
	}
	
	public void update(float delta) {
	}

	public void render(SpriteBatch batch) {
		shape.begin(ShapeType.Filled);
		shape.setColor(0.0f, 0.0f, 0.0f, 0.9f);
		shape.rect(x, y, width, height);
		shape.end();
	}

	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
}
