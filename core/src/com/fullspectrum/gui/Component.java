package com.fullspectrum.gui;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

public abstract class Component {

	// Positioning is relative to parent
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	
	private Container parent;
	private boolean visible = true;
	private boolean enabled = true;
	private boolean focusable = false;
	
	// Listeners
	private KeyListener keyListener;
	private MouseListener mouseListener;
	private FocusListener focusListener;
	
	// Debug
	private boolean debugRender = false;
	private final ShapeRenderer shape = new ShapeRenderer();
	
	public abstract void update(float delta);
	
	/** Begin/end don't need to be called. Also, the correct projection matrix will be loaded into the batch */
	public abstract void render(SpriteBatch batch);
	
	public final void debugRender(SpriteBatch batch) {
		batch.end();
		
		shape.setProjectionMatrix(batch.getProjectionMatrix());
		shape.begin(ShapeType.Line);
		shape.setColor(Color.RED);
		shape.rect(x, y, width, height);
		shape.end();
		
		batch.begin();
	}
	
	/**
	 * Requests focus from window. If this component hasn't been added to a <code>Window</code> yet,
	 * the component will NOT receive focus.
	 */
	public void requestFocus() {
		Component comp = parent;
		while(comp != null && !(comp instanceof Window)) {
			comp = comp.parent;
		}
		if(comp == null) return; // No window was found, do nothing
		((Window) comp).giveFocus(this);
	}
	
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}
	
	public void setParent(Container parent) {
		this.parent = parent;
	}
	
	public Container getParent() {
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
	
	public void setFocusable(boolean focus) {
		this.focusable = focus;
	}
	
	public boolean isFocusable() {
		return focusable;
	}
	
	public void setDebugRender(boolean debugRender) {
		this.debugRender = debugRender;
	}
	
	public boolean isDebugRender() {
		return debugRender;
	}
	
	public void addKeyListener(KeyListener keyListener) {
		this.keyListener = keyListener;
	}
	
	public KeyListener getKeyListener() {
		return keyListener;
	}
	
	public void addMouseListener(MouseListener mouseListener) {
		this.mouseListener = mouseListener;
	}
	
	public MouseListener getMouseListener() {
		return mouseListener;
	}
	
	public void addFocusListener(FocusListener focusListener) {
		this.focusListener = focusListener;
	}
	
	public FocusListener getFocusListener() {
		return focusListener;
	}
}
