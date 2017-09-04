package com.cpubrew.gui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class UIManager {

	private static OrthographicCamera hudCamera;
	private static Array<Window> windows;
	private static Window focusedWindow;
	private static StyleContext styleContext;
	private static InputProcessor processor;
	private static WindowListener windowListener;
	
	static {
		windows = new Array<Window>();
		
		processor = new InputProcessor() {
			@Override
			public boolean keyDown(int keycode) {
				KeyBoardManager.onKeyPress(keycode);
				
				// Check keybinds
				if(focusedWindow == null) return false;
				
				// Check window for keybinds, then children
				Array<Action> actions = KeyBoardManager.getAction(focusedWindow);
				if(actions != null) {
					for(Action action : actions) action.onAction(focusedWindow);
				}
				checkKeyBindingsRecursive(focusedWindow);
				
				return focusedWindow.keyDown(keycode);
			}

			/** Recursively checks keybindings */
			private void checkKeyBindingsRecursive(Container container) {
				for(Component comp : container.getComponents()) {
					Array<Action> actions = KeyBoardManager.getAction(comp);
					if(actions != null) {
						for(Action action : actions) action.onAction(comp);
					}
					
					if(comp instanceof Container) {
						checkKeyBindingsRecursive((Container) comp);
					}
				}
			}

			@Override
			public boolean keyUp(int keycode) {
				KeyBoardManager.onKeyRelease(keycode);
				return focusedWindow == null ? false : focusedWindow.keyUp(keycode);
			}

			@Override
			public boolean keyTyped(char character) {
				KeyBoardManager.onKeyType(character);
				return focusedWindow == null ? false : focusedWindow.keyTyped(character);
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				Vector3 coords = hudCamera.unproject(new Vector3(screenX, screenY, 0.0f));
				int x = (int) coords.x;
				int y = (int) coords.y;
				
				Window window = firstWindowAt(x, y);
				if(window == null) return false;
				
				return window.touchDown(x - window.getX(), y - window.getY(), pointer, button);
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				Vector3 coords = hudCamera.unproject(new Vector3(screenX, screenY, 0.0f));
				int x = (int) coords.x;
				int y = (int) coords.y;
				
				Window window = firstWindowAt(x, y);
				if(window == null) return false;
				
				if(window.isFocusable() && (focusedWindow == null || !focusedWindow.equals(window))) {
					focusedWindow = window;
					window.requestFocus();
				}
				
				return window.touchUp(x - window.getX(), y - window.getY(), pointer, button);
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				Vector3 coords = hudCamera.unproject(new Vector3(screenX, screenY, 0.0f));
				int x = (int) coords.x;
				int y = (int) coords.y;
				
				Window window = firstWindowAt(x, y);
				if(window == null) return false;
				
				return window.touchDragged(x - window.getX(), y - window.getY(), pointer);
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				Vector3 coords = hudCamera.unproject(new Vector3(screenX, screenY, 0.0f));
				int x = (int) coords.x;
				int y = (int) coords.y;
				
				Window window = firstWindowAt(x, y);
				if(window == null) return false;
				
				return window.mouseMoved(x - window.getX(), y - window.getY());
			}

			@Override
			public boolean scrolled(int amount) {
				return focusedWindow == null ? false : focusedWindow.scrolled(amount);
			}
		};
		
		windowListener = new WindowListener() {
			@Override
			public void windowOpened(WindowEvent ev) {
				focusedWindow = ev.getWindow();
			}
			
			@Override
			public void windowHidden(WindowEvent ev) {
				if(focusedWindow != null && ev.getWindow().equals(focusedWindow)) {
					focusedWindow = null;
					
					// If the window with focus is hidden, give focus to the next available window
					for(int i = windows.size - 1; i >= 0; i--) {
						Window window = windows.get(i);
						if(window.isVisible() && window.isFocusable()) {
							focusedWindow = window;
							break;
						}
					}
				}
			}
			
			@Override
			public void windowClosed(WindowEvent ev) {
				if(focusedWindow != null && ev.getWindow().equals(focusedWindow)) {
					focusedWindow = null;
					
					// If the window with focus is closed, give focus to the next available window
					for(int i = windows.size - 1; i >= 0; i--) {
						Window window = windows.get(i);
						if(window.isVisible() && window.isFocusable()) {
							focusedWindow = window;
							break;
						}
					}
				}
			}
		};
	}

	public static void setHudCamera(OrthographicCamera hudCamera){ 
		UIManager.hudCamera = hudCamera;
	}
	
	public static void render(SpriteBatch batch) {
		for(Window window : windows) {
			if(!window.isVisible()) continue;
			window.render(batch);
		}
	}
	
	public static void update(float delta){
		for(Window window : windows) {
			if(!window.isVisible()) continue;
			window.update(delta);
		}
	}
	
//	/**
//	 * Creates an empty window and returns it.
//	 * @return
//	 */
//	public static Window newWindow() {
//		return newWindow("");
//	}
//	
//	/**
//	 * Creates an empty window with the specified title and returns it.
//	 * @param title
//	 * @return
//	 */
//	public static Window newWindow(String title) {
//		Window window = new Window(title);
//		window.setHudCamera(hudCamera);
//		windows.add(window);
//		return window;
//	}
	
	public static void addWindow(Window window) {
		windows.add(window);
		window.addWindowListener(windowListener);
	}

	public static void removeWindow(Window window) {
		windows.removeValue(window, false);
		window.removeWindowListener(windowListener);
	}
	
	private static Window firstWindowAt(int x, int y) {
		for(int i = windows.size - 1; i >= 0; i--) {
			Window window = windows.get(i);
			if(!window.isVisible()) continue;
			if(window.getBounds().contains(x, y)) return window;
		}
		return null;
	}
	
	public static OrthographicCamera getHudCamera() {
		return hudCamera;
	}
	
	public static void setStyleContext(StyleContext context) {
		styleContext = context;
	}
	
	public static StyleContext getStyleContext() {
		return styleContext;
	}
	
	public static InputProcessor getInputProcessor() {
		return processor;
	}
	
	public static Window getFocusedWindow() {
		return focusedWindow;
	}
}
