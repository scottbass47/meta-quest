package com.fullspectrum.gui;

import java.awt.Rectangle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public abstract class Component {

	// Positioning is relative to parent
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	
	private Component parent;
	private boolean visible = true;
	private boolean enabled = true;
	private boolean focus = false;
	private boolean debugRender = false;
	
	public abstract void update(float delta);
	
	/** Begin/end don't need to be called. Also, the correct projection matrix will be loaded into the batch */
	public abstract void render(SpriteBatch batch);
	
	public final void debugRender(SpriteBatch batch) {
		batch.end();
		
		ShapeRenderer shape = new ShapeRenderer();
		shape.setProjectionMatrix(batch.getProjectionMatrix());
		shape.begin(ShapeType.Line);
		shape.setColor(Color.RED);
		shape.rect(x, y, width, height);
		shape.end();
		
		batch.begin();
	}
	
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}
	
	public void setParent(Component parent) {
		this.parent = parent;
	}
	
	public Component getParent() {
		return parent;
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public int getX() {
		return x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setFocus(boolean focus) {
		this.focus = focus;
	}
	
	public boolean hasFocus() {
		return focus;
	}
	
	public void setDebugRender(boolean debugRender) {
		this.debugRender = debugRender;
	}
	
	public boolean isDebugRender() {
		return debugRender;
	}
	
}
