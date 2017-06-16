package com.fullspectrum.editor;

import org.lwjgl.opengl.GL11;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class TilePanel {

	private float x;
	private float y;
	private float width = 1280;
	private float height = 200;
	
	private Color backgroundColor = Color.BLACK.mul(1.0f, 1.0f, 1.0f, 0.9f);
	private ShapeRenderer shapeRenderer;
	
	public TilePanel() {
		shapeRenderer = new ShapeRenderer();
	}
	
	public void render(OrthographicCamera hudCamera) {
		Gdx.gl.glEnable(GL11.GL_BLEND);
		Gdx.gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		shapeRenderer.setProjectionMatrix(hudCamera.combined);
		
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(backgroundColor);
		shapeRenderer.rect(x, y, width, height);
		shapeRenderer.end();
		
		Gdx.gl.glEnable(GL11.GL_BLEND);
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
}
