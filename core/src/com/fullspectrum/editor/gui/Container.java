package com.fullspectrum.editor.gui;

import java.awt.Rectangle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;

public abstract class Container extends Component implements KeyListener, MouseListener{

	protected Array<Component> components;
	private Component focusedComponent;
	private Component mouseOnComponent;
	private Matrix4 matrix;
	
	public Container() {
		components = new Array<Component>();
		matrix = new Matrix4();
	}
	
	public void add(Component component) {
		component.setParent(this);
		components.add(component);
	}
	
	public void remove(Component component) {
		components.removeValue(component, false);
		if(focusedComponent == component) focusedComponent = null;
		if(mouseOnComponent == component) mouseOnComponent = null;
	}
	
	public Array<Component> getComponents() {
		return components;
	}
	
	public void removeAll() {
		components.clear();
		focusedComponent = null;
		mouseOnComponent = null;
	}
	
	@Override
	public void setSize(int width, int height) {
		if(width == this.width && height == this.height) return;
		super.setSize(width, height);
	}
	
	@Override
	public void update(float delta) {
		for(Component comp : components) {
			if(!comp.isEnabled() || !comp.isVisible()) continue;
			comp.update(delta);
		}
	}
	
	@Override
	public void render(SpriteBatch batch) {
		if(!isVisible()) return;
		batch.end();
		Matrix4 old = batch.getProjectionMatrix();
		
		matrix.set(old);
		matrix.translate(x, y, 0);
		batch.setProjectionMatrix(matrix);
		batch.begin();
		for(Component component : components) {
			if(!component.isVisible()) return;
			component.render(batch);
		}
		batch.end();
		
		batch.setProjectionMatrix(old);
		batch.begin();
	}
	
	public void giveFocus(Component comp) {
		if(!components.contains(comp, false)) throw new IllegalArgumentException("This container does not contain the specified component.");
		if(focusedComponent != null && focusedComponent.isEnabled()) {
			focusedComponent.setFocus(false);
		}
		comp.setFocus(true);
		focusedComponent = comp;
	}
	
	@Override
	public void onMouseUp(int x, int y, int button) {
		boolean hitComponent = false;
		// Determine component that is pressed
		for(int i = components.size - 1; i >= 0; i--) {
			Component comp = components.get(i);
			if(!comp.isEnabled() || !comp.isVisible()) continue;
			Rectangle bounds = comp.getBounds();
			if(bounds.contains(x, y)) {
				giveFocus(comp);
				hitComponent = true;
				break;
			}
		}
		
		if(!hitComponent) {
			if(focusedComponent != null) {
				focusedComponent.setFocus(false);
			}
			focusedComponent = null;
		} else {
			if(focusedComponent instanceof MouseListener) {
				MouseListener listener = (MouseListener) focusedComponent;
				listener.onMouseUp(x - focusedComponent.getX(), y - focusedComponent.getY(), button);
			}
		}
	}
	
	@Override
	public void onMouseDown(int x, int y, int button) {
		Component comp = getFirstComponentAt(x, y);
		if(comp instanceof MouseListener) {
			MouseListener listener = (MouseListener) comp;
			listener.onMouseDown(x - comp.getX(), y - comp.getY(), button);
		}
		
//		if(focusedComponent != null && focusedComponent.isEnabled() && focusedComponent.isVisible() && focusedComponent.getBounds().contains(x, y) && focusedComponent instanceof MouseListener){
//			MouseListener listener = (MouseListener) focusedComponent;
//			listener.onMouseDown(x - focusedComponent.getX(), y - focusedComponent.getY(), button);
//		}
	}
	
	@Override
	public void onMouseDrag(int x, int y) {
		Component comp = getFirstComponentAt(x, y);
		
		// New component
		if(comp != mouseOnComponent) {
			if(comp != null && comp instanceof MouseListener) {
				MouseListener listener = (MouseListener) comp;
				listener.onMouseEnter(x - comp.getX(), y - comp.getY());
			}
			
			if(mouseOnComponent != null && mouseOnComponent instanceof MouseListener) {
				MouseListener listener = (MouseListener) mouseOnComponent;
				listener.onMouseExit(x - mouseOnComponent.getX(), y - mouseOnComponent.getY());
			}
			
			mouseOnComponent = comp;
		}
		
		if(comp instanceof MouseListener) {
			MouseListener listener = (MouseListener) comp;
			listener.onMouseDrag(x - comp.getX(), y - comp.getY());
		}
		
//		if(focusedComponent != null && focusedComponent.isEnabled() && focusedComponent.isVisible() && focusedComponent.getBounds().contains(x, y) && focusedComponent instanceof MouseListener){
//			MouseListener listener = (MouseListener) focusedComponent;
//			listener.onMouseDrag(x - focusedComponent.getX(), y - focusedComponent.getY());
//		}
	}
	
	@Override
	public void onMouseMove(int x, int y) {
		Component comp = getFirstComponentAt(x, y);
		
		// New component
		if(comp != mouseOnComponent) {
			if(comp != null && comp instanceof MouseListener) {
				MouseListener listener = (MouseListener) comp;
				listener.onMouseEnter(x - comp.getX(), y - comp.getY());
			}
			
			if(mouseOnComponent != null && mouseOnComponent instanceof MouseListener) {
				MouseListener listener = (MouseListener) mouseOnComponent;
				listener.onMouseExit(x - mouseOnComponent.getX(), y - mouseOnComponent.getY());
			}
			
			mouseOnComponent = comp;
		}
		
		if(comp instanceof MouseListener) {
			MouseListener listener = (MouseListener) comp;
			listener.onMouseMove(x - comp.getX(), y - comp.getY());
		}
		
//		if(focusedComponent != null && focusedComponent.isEnabled() && focusedComponent.isVisible() && focusedComponent.getBounds().contains(x, y) &&  focusedComponent instanceof MouseListener){
//			MouseListener listener = (MouseListener) focusedComponent;
//			listener.onMouseMove(x - focusedComponent.getX(), y - focusedComponent.getY());
//		}
	}
	
	@Override
	public void onKeyPress(int keycode) {
		if(focusedComponent != null && focusedComponent.isEnabled() && focusedComponent.isVisible() && focusedComponent instanceof KeyListener){
			KeyListener listener = (KeyListener) focusedComponent;
			listener.onKeyPress(keycode);
		}
	}
	
	@Override
	public void onKeyRelease(int keycode) {
		if(focusedComponent != null && focusedComponent.isEnabled() && focusedComponent.isVisible() && focusedComponent instanceof KeyListener){
			KeyListener listener = (KeyListener) focusedComponent;
			listener.onKeyRelease(keycode);
		}
	}
	
	@Override
	public void onKeyType(char character) {
		if(focusedComponent != null && focusedComponent.isEnabled() && focusedComponent.isVisible() && focusedComponent instanceof KeyListener){
			KeyListener listener = (KeyListener) focusedComponent;
			listener.onKeyType(character);
		}
	}
	
	public Component getFirstComponentAt(int x, int y) {
		for(int i = components.size - 1; i >= 0; i--) {
			Component comp = components.get(i);
			if(!comp.isEnabled() || !comp.isVisible()) continue;
			Rectangle bounds = comp.getBounds();
			if(bounds.contains(x, y)) {
				return comp;
			}
		}
		return null;
	}
	
	@Override
	public void onMouseEnter(int x, int y) {
		// idk what to do here....
	}
	
	@Override
	public void onMouseExit(int x, int y) {
		// idk what to do here....
	}
	
}
