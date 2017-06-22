package com.fullspectrum.editor.gui;

import java.awt.Rectangle;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;

public abstract class Container extends Component implements KeyListener, MouseListener{

	protected Array<Component> components;
	private Component focusedComponent;
	private FrameBuffer fbo;
	private OrthographicCamera cam;
	private Matrix4 matrix;
	
	public Container() {
		components = new Array<Component>();
		cam = new OrthographicCamera();
		matrix = new Matrix4();
	}
	
	public void add(Component component) {
		component.setParent(this);
		components.add(component);
	}
	
	@Override
	public void setSize(int width, int height) {
		if(width == this.width && height == this.height) return;
		super.setSize(width, height);
		fbo = new FrameBuffer(Format.RGBA8888, width, height, false);
		fbo.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		cam.setToOrtho(false, width, height);
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
		batch.end();
		Matrix4 old = batch.getProjectionMatrix();
		
//		fbo.begin();
//		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		
//		batch.setProjectionMatrix(cam.combined);
//		batch.begin();
//		
//		for(Component component : components) {
//			if(!component.isVisible()) return;
//			component.render(batch);
//		}
//		
//		batch.end();
//		fbo.end();
//		
//		batch.setProjectionMatrix(old);
//		batch.begin();
//		batch.draw(fbo.getColorBufferTexture(), x, y, width, height);
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
	
	@Override
	public void onMouseUp(int x, int y, int button) {
		boolean hitComponent = false;
		// Determine component that is pressed
		for(int i = components.size - 1; i >= 0; i--) {
			Component comp = components.get(i);
			Rectangle bounds = comp.getBounds();
			if(bounds.contains(x, y)) {
				if(focusedComponent != null && focusedComponent.isEnabled()) {
					focusedComponent.setFocus(false);
				}
				comp.setFocus(true);
				focusedComponent = comp;
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
		if(focusedComponent != null && focusedComponent.isEnabled() && focusedComponent instanceof MouseListener){
			MouseListener listener = (MouseListener) focusedComponent;
			listener.onMouseDown(x - focusedComponent.getX(), y - focusedComponent.getY(), button);
		}
	}
	
	@Override
	public void onMouseDrag(int x, int y) {
		if(focusedComponent != null && focusedComponent.isEnabled() && focusedComponent instanceof MouseListener){
			MouseListener listener = (MouseListener) focusedComponent;
			listener.onMouseDrag(x - focusedComponent.getX(), y - focusedComponent.getY());
		}
	}
	
	@Override
	public void onMouseMove(int x, int y) {
		if(focusedComponent != null && focusedComponent.isEnabled() && focusedComponent instanceof MouseListener){
			MouseListener listener = (MouseListener) focusedComponent;
			listener.onMouseMove(x - focusedComponent.getX(), y - focusedComponent.getY());
		}
	}
	
	@Override
	public void onKeyPress(int keycode) {
		if(focusedComponent != null && focusedComponent.isEnabled() && focusedComponent instanceof KeyListener){
			KeyListener listener = (KeyListener) focusedComponent;
			listener.onKeyPress(keycode);
		}
	}
	
	@Override
	public void onKeyRelease(int keycode) {
		if(focusedComponent != null && focusedComponent.isEnabled() && focusedComponent instanceof KeyListener){
			KeyListener listener = (KeyListener) focusedComponent;
			listener.onKeyRelease(keycode);
		}
	}
	
	@Override
	public void onKeyType(char character) {
		if(focusedComponent != null && focusedComponent.isEnabled() && focusedComponent instanceof KeyListener){
			KeyListener listener = (KeyListener) focusedComponent;
			listener.onKeyType(character);
		}
	}
	
}
