package com.cpubrew.gui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public final class Window extends Container implements InputProcessor {

	private OrthographicCamera hudCamera;
	private Component focusedComponent;
	private Component mouseComponent;
	private String title;
	
	public Window(String title) {
		this.title = title;
		setFocusable(true);
		setVisible(false);
		UIManager.addWindow(this);
		setHudCamera(UIManager.getHudCamera());
	}
	
	public Window() {
		this("");
	}
	
	@Override
	public void render(SpriteBatch batch) {
		batch.setProjectionMatrix(hudCamera.combined);
		batch.begin();
		if(components.size == 0) return;
		super.render(batch);
		batch.end();
	}
	
	@Override
	public void add(Component component) {
		super.add(component);
		setSize(Math.max(width, component.getX() + component.getWidth()), Math.max(height, component.getY() + component.getHeight()));
	}
	
	public void close() {
		for(WindowListener listener : getWindowListeners()) {
			listener.windowClosed(new WindowEvent(this));
		}
		UIManager.removeWindow(this);
	}
	
	public void setHudCamera(OrthographicCamera hudCamera) {
		this.hudCamera = hudCamera;
	}
	
	public OrthographicCamera getHudCamera() {
		return hudCamera;
	}

	@Override
	public final boolean keyDown(int keycode) {
		if(interactable(focusedComponent)) {
			for(KeyListener listener : focusedComponent.getKeyListeners()) {
				listener.onKeyPress(new KeyEvent(focusedComponent, keycode));
			}
		}
		return true;
	}

	@Override
	public final boolean keyUp(int keycode) {
		if(interactable(focusedComponent)) {
			for(KeyListener listener : focusedComponent.getKeyListeners()) {
				listener.onKeyRelease(new KeyEvent(focusedComponent, keycode));
			}
		}
		return true;
	}

	@Override
	public final boolean keyTyped(char character) {
		if(interactable(focusedComponent)) {
			for(KeyListener listener : focusedComponent.getKeyListeners()) {
				listener.onKeyType(new KeyEvent(focusedComponent, character));
			}
		}
		return true;
	}

	@Override
	public final boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Component comp = getFirstComponentAt(screenX, screenY);
		for(MouseListener listener : comp.getMouseListeners()) {
			listener.onMouseDown(new MouseEvent(comp, screenX - comp.getX(), screenY - comp.getY(), button));
		}
		return true;
	}

	@Override
	public final boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Component comp = getFirstComponentAt(screenX, screenY);
		for(MouseListener listener : comp.getMouseListeners()) {
			listener.onMouseUp(new MouseEvent(comp, screenX - comp.getX(), screenY - comp.getY(), button));
		}
		
		giveFocus(comp);
		return true;
	}

	@Override
	public final boolean touchDragged(int screenX, int screenY, int pointer) {
		Component comp = getFirstComponentAt(screenX, screenY);
		for(MouseListener listener : comp.getMouseListeners()) {
			listener.onMouseDrag(new MouseEvent(comp, screenX - comp.getX(), screenY - comp.getY()));
		}
		
		// If the mouse is on a new component, do mouse enter / exit
		if(!same(comp, mouseComponent)) {
			if(mouseComponent != null) {
				for(MouseListener listener : mouseComponent.getMouseListeners()) {
					listener.onMouseExit(new MouseEvent(comp, screenX - comp.getX(), screenY - comp.getY()));
				}
			}
			
			for(MouseListener listener : comp.getMouseListeners()) {
				listener.onMouseEnter(new MouseEvent(comp, screenX - comp.getX(), screenY - comp.getY()));
			}
			
			mouseComponent = comp;
		}
		
		return true;
	}

	@Override
	public final boolean mouseMoved(int screenX, int screenY) {
		Component comp = getFirstComponentAt(screenX, screenY);
		for(MouseListener listener : comp.getMouseListeners()) {
			listener.onMouseMove(new MouseEvent(comp, screenX - comp.getX(), screenY - comp.getY()));
		}
		
		// If the mouse is on a new component, do mouse enter / exit
		if(!same(comp, mouseComponent)) {
			if(mouseComponent != null) {
				for(MouseListener listener : mouseComponent.getMouseListeners()) {
					listener.onMouseExit(new MouseEvent(comp, screenX - comp.getX(), screenY - comp.getY()));
				}
			}
			
			for(MouseListener listener : comp.getMouseListeners()) {
				listener.onMouseEnter(new MouseEvent(comp, screenX - comp.getX(), screenY - comp.getY()));
			}
			
			mouseComponent = comp;
		}
		
		return true;
	}

	@Override
	public final boolean scrolled(int amount) {
		return true;
	}
	
	/**
	 * Returns true if component is not null, is enabled, and is visible
	 * @param component
	 * @return
	 */
	private boolean interactable(Component component) {
		return component != null && component.isEnabled() && component.isVisible();
	}
	
	/**
	 * Returns true if comp1 == comp2 or comp1.equals(comp2)
	 * @param comp1
	 * @param comp2
	 * @return
	 */
	private boolean same(Component comp1, Component comp2) {
		return comp1 == comp2 || (comp1 != null && comp2 != null && comp1.equals(comp2));
	}

	@Override
	public void requestFocus() {
		giveFocus(this);
	}
	
	/**
	 * Attempts to give focus to the specified component.
	 * @param component
	 */
	protected void giveFocus(Component component) {
		if(component == null || !component.isFocusable() || !interactable(component)) return;

		// Fire event for focus lost
		if(focusedComponent != null) {
			for(FocusListener listener : focusedComponent.getFocusListeners()) {
				listener.focusLost(new FocusEvent(component));
			}
		}
		
		for(FocusListener listener : component.getFocusListeners()) {
			listener.focusGained(new FocusEvent(focusedComponent));
		}
		
		focusedComponent = component;
	}
	
	public Component getFocusedComponent() {
		return focusedComponent;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void addWindowListener(WindowListener listener) {
		listeners.addListener(WindowListener.class, listener);
	}
	
	public void removeWindowListener(WindowListener listener) {
		listeners.removeListener(listener);
	}
	
	public Array<WindowListener> getWindowListeners() {
		return listeners.getListeners(WindowListener.class);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		for(WindowListener listener : getWindowListeners()) {
			if(visible) listener.windowOpened(new WindowEvent(this));
			else listener.windowHidden(new WindowEvent(this));
		}
	}
	
	@Override
	public Window getWindow() {
		return this;
	}
	
	@Override
	public String toString() {
		return title;
	}
}
