package com.cpubrew.gui;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Container extends Component {

	protected Array<Component> components;
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
	}
	
	public Array<Component> getComponents() {
		return components;
	}
	
	public void removeAll() {
		components.clear();
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
		if(renderingBackground()) renderBackground(batch);

		batch.end();
		Matrix4 old = batch.getProjectionMatrix();
		
		matrix.set(old);
		matrix.translate(x, y, 0);
		batch.setProjectionMatrix(matrix);
		batch.begin();
		
		
		for(Component component : components) {
			if(!component.isVisible()) return;
			if(component.renderingBackground()) component.renderBackground(batch);
			component.render(batch);
			if(component.isDebugRender()) {
				component.debugRender(batch);
			}
		}
		
		batch.end();
		
		batch.setProjectionMatrix(old);
		batch.begin();

		if(isDebugRender()) debugRender(batch);
	}
	
	public Component getFirstComponentAt(int x, int y) {
		for(int i = components.size - 1; i >= 0; i--) {
			Component comp = components.get(i);
			if(!comp.isEnabled() || !comp.isVisible()) continue;
			Rectangle bounds = comp.getBounds();
			if(bounds.contains(x, y)) {
				if(comp instanceof Container) {
					return ((Container)comp).getFirstComponentAt(x - comp.getX(), y - comp.getY());
				}
				return comp;
			}
		}
		return this;
	}
}
