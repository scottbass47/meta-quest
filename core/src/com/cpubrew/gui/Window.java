package com.cpubrew.gui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public final class Window extends Container implements InputProcessor {

	private OrthographicCamera hudCamera;
	private UIManager manager;
	private Component focusedComponent;
	private Component mouseComponent;
	private String title;
	
	protected Window(String title) {
		this.title = title;
		setFocusable(true);
	}
	
	protected Window() {
		this("");
	}
	
	@Override
	public void render(SpriteBatch batch) {
		if(components.size == 0) return;
		batch.setProjectionMatrix(hudCamera.combined);
		batch.begin();
		super.render(batch);
		batch.end();
	}
	
	@Override
	public void add(Component component) {
		super.add(component);
		setSize(Math.max(width, component.getX() + component.getWidth()), Math.max(height, component.getY() + component.getHeight()));
	}
	
	public void setManager(UIManager manager) {
		this.manager = manager;
	}
	
	public UIManager getManager() {
		return manager;
	}
	
	public void destroy() {
		manager.removeWindow(this);
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
			KeyListener listener = focusedComponent.getKeyListener();
			if(listener != null) {
				listener.onKeyPress(keycode);
			}
		}
		return true;
	}

	@Override
	public final boolean keyUp(int keycode) {
		if(interactable(focusedComponent)) {
			KeyListener listener = focusedComponent.getKeyListener();
			if(listener != null) {
				listener.onKeyRelease(keycode);
			}
		}
		return true;
	}

	@Override
	public final boolean keyTyped(char character) {
		if(interactable(focusedComponent)) {
			KeyListener listener = focusedComponent.getKeyListener();
			if(listener != null) {
				listener.onKeyType(character);
			}
		}
		return true;
	}

	@Override
	public final boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Component comp = getFirstComponentAt(screenX, screenY);
		MouseListener listener = comp.getMouseListener();
		if(listener != null){
			listener.onMouseDown(screenX - comp.getX(), screenY - comp.getY(), button);
		}
		return true;
	}

	@Override
	public final boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Component comp = getFirstComponentAt(screenX, screenY);
		MouseListener listener = comp.getMouseListener();
		
		if(listener != null) {
			listener.onMouseUp(screenX - comp.getX(), screenY - comp.getY(), button);
		}
		
		giveFocus(comp);
		return true;
	}

	@Override
	public final boolean touchDragged(int screenX, int screenY, int pointer) {
		Component comp = getFirstComponentAt(screenX, screenY);
		MouseListener listener = comp.getMouseListener();
		
		if(listener != null) {
			listener.onMouseDrag(screenX - comp.getX(), screenY - comp.getY());
		}
		
		// If the mouse is on a new component, do mouse enter / exit
		if(!same(comp, mouseComponent)) {
			if(mouseComponent != null) {
				MouseListener oldListener = mouseComponent.getMouseListener();
				if(oldListener != null) {
					oldListener.onMouseExit(screenX - mouseComponent.getX(), screenY - mouseComponent.getY());
				}
			}
			
			MouseListener newListener = comp.getMouseListener();
			if(newListener != null) {
				newListener.onMouseEnter(screenX - comp.getX(), screenY - comp.getY());
			}
			
			mouseComponent = comp;
		}
		
		return true;
	}

	@Override
	public final boolean mouseMoved(int screenX, int screenY) {
		Component comp = getFirstComponentAt(screenX, screenY);
		MouseListener listener = comp.getMouseListener();
		
		if(listener != null) {
			listener.onMouseMove(screenX - comp.getX(), screenY - comp.getY());
		}
		
		// If the mouse is on a new component, do mouse enter / exit
		if(!same(comp, mouseComponent)) {
			if(mouseComponent != null) {
				MouseListener oldListener = mouseComponent.getMouseListener();
				if(oldListener != null) {
					oldListener.onMouseExit(screenX - mouseComponent.getX(), screenY - mouseComponent.getY());
				}
			}
			
			MouseListener newListener = comp.getMouseListener();
			if(newListener != null) {
				newListener.onMouseEnter(screenX - comp.getX(), screenY - comp.getY());
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
			FocusListener listener = focusedComponent.getFocusListener();
			if(listener != null) {
				listener.focusLost(new FocusEvent(component));
			}
		}
		
		FocusListener listener = component.getFocusListener();
		if(listener != null) {
			listener.focusGained(new FocusEvent(focusedComponent));
		}

		focusedComponent = component;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	@Override
	public String toString() {
		return title;
	}
}
