package com.cpubrew.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

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
	protected EventListeners listeners;
	
	// Debug
	private boolean debugRender = false;
	protected final ShapeRenderer shape = new ShapeRenderer();
	
	// Key Binds
	private ArrayMap<KeyBind, Action> inputMap;
	
	// Color
	private boolean renderBackground = true;
	protected Color backgroundColor = Color.WHITE;
	protected Color foregroundColor = Color.BLACK;
	
	public Component() {
		listeners = new EventListeners();
		inputMap = new ArrayMap<KeyBind, Action>();
	}
	
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
	
	public void renderBackground(SpriteBatch batch) {
		batch.end();
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
	    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		shape.setProjectionMatrix(batch.getProjectionMatrix());
		shape.begin(ShapeType.Filled);
		shape.setColor(backgroundColor);
		shape.rect(x, y, width, height);
		shape.end();
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
		batch.begin();
	}
	
	/**
	 * Requests focus from window. If this component hasn't been added to a <code>Window</code> yet,
	 * the component will NOT receive focus.
	 */
	public void requestFocus() {
		Window window = getWindow();
		if(window == null) return;
		window.giveFocus(this);
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
		listeners.addListener(KeyListener.class, keyListener);
	}
	
	public Array<KeyListener> getKeyListeners() {
		return listeners.getListeners(KeyListener.class);
	}
	
	public void removeKeyListener(KeyListener keyListener) {
		listeners.removeListener(keyListener);
	}
	
	public void addMouseListener(MouseListener mouseListener) {
		listeners.addListener(MouseListener.class, mouseListener);
	}
	
	public Array<MouseListener> getMouseListeners() {
		return listeners.getListeners(MouseListener.class);
	}
	
	public void removeMouseListener(MouseListener mouseListener) {
		listeners.removeListener(mouseListener);
	}
	
	public void addFocusListener(FocusListener focusListener) {
		listeners.addListener(FocusListener.class, focusListener);
	}
	
	public Array<FocusListener> getFocusListeners() {
		return listeners.getListeners(FocusListener.class);
	}
	
	public void removeFocusListener(FocusListener focusListener) {
		listeners.removeListener(focusListener);
	}
	
	public void addKeyBind(KeyBind bind, Action action) {
		if(action == null || bind == null) throw new IllegalArgumentException("Keybinds and Actions must have non-null values.");
		inputMap.put(bind, action);
	}
	
	public void removeKeyBind(KeyBind bind) {
		inputMap.removeKey(bind);
	}

	public Action getAction(KeyBind bind) {
		return inputMap.get(bind);
	}
	
	public ArrayMap<KeyBind, Action> getInputMap() {
		return inputMap;
	}
	
	/**
	 * Gets the <code>Window</code> that contains this component. Returns null if the component hasn't been added to a window yet.
	 * @return
	 */
	public Window getWindow() {
		Component comp = parent;
		while(comp != null && !(comp instanceof Window)) {
			comp = comp.parent;
		}
		if(comp == null) return null;
		return (Window) comp;
	}
	
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	
	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}
	
	public Color getForegroundColor() {
		return foregroundColor;
	}
	
	public void setRenderBackground(boolean renderBackground) {
		this.renderBackground = renderBackground;
	}
	
	public boolean renderingBackground() {
		return renderBackground;
	}
	
}