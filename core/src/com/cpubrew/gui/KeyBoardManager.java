package com.cpubrew.gui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.cpubrew.gui.KeyBind.FocusType;
import com.cpubrew.gui.KeyBind.Modifiers;

public class KeyBoardManager {

	private static int shiftCount;
	private static int altCount;
	private static int ctrlCount;
	
	private static boolean[] pressed = new boolean[256];
	
	public static void onKeyPress(int keycode) {
		switch(keycode) {
		case Keys.SHIFT_LEFT:
		case Keys.SHIFT_RIGHT:
			shiftCount++;
			break;
		case Keys.ALT_LEFT:
		case Keys.ALT_RIGHT:
			altCount++;
			break;
		case Keys.CONTROL_LEFT:
		case Keys.CONTROL_RIGHT:
			ctrlCount++;
			break;
		}
		
		pressed[keycode] = true; 
	}

	public static void onKeyRelease(int keycode) {
		switch(keycode) {
		case Keys.SHIFT_LEFT:
		case Keys.SHIFT_RIGHT:
			shiftCount--;
			break;
		case Keys.ALT_LEFT:
		case Keys.ALT_RIGHT:
			altCount--;
			break;
		case Keys.CONTROL_LEFT:
		case Keys.CONTROL_RIGHT:
			ctrlCount--;
			break;
		}
		
		pressed[keycode] = false;
	}

	public static void onKeyType(char character) {
	}
	
	/**
	 * Checks the component to see if one of it's keybinds has been triggered and returns the action associated with it.
	 * If the component has no keybinds that are triggered, a null action is returned.
	 * 
	 * @param component
	 * @return null if no action is found
	 */
	public static Array<Action> getAction(Component component) {
		ArrayMap<KeyBind, Action> inputMap = component.getInputMap();
		Array<Action> ret = null; // Lazily instantiate because most of the time ret will stay null so no need to allocate an array every check
		
		for(KeyBind bind : inputMap.keys()) {
			// Check focus
			if(!validFocus(bind.getFocusType(), component)) continue;
			
			boolean valid = validKeybind(bind);
			if(valid) {
				if(ret == null) ret = new Array<Action>();
				ret.add(inputMap.get(bind));
			}
		}
		
		return ret;
	}
	
	private static boolean validFocus(FocusType type, Component component) {
		Window focusedWindow = UIManager.getFocusedWindow();
		if(focusedWindow == null) return false;
		
		switch(type) {
		case ANCESTOR_FOCUS:
			// If the component itself is the focused component then it's valid
			if(focusedWindow.getFocusedComponent() != null && component.equals(focusedWindow.getFocusedComponent())) return true;
			
			// If it doesn't have focus and isn't a container, it can't be the ancestor of the focused component
			if(!(component instanceof Container)) return false;
			
			// Recursively check children for focus
			Container container = (Container) component;
			return childHasFocus(container);
		case COMPONENT_FOCUS:
			Component focusedComponent = focusedWindow.getFocusedComponent();
			return (focusedComponent != null && component.equals(focusedComponent));
		case WINDOW_FOCUS:
			Window compWindow = component.getWindow();
			return (compWindow != null && compWindow.equals(focusedWindow));
		default:
			break;
		}
		return false;
	}
	
	private static boolean childHasFocus(Container container) {
		Window window = UIManager.getFocusedWindow();
		Component focused = window.getFocusedComponent();

		for(Component comp : container.getComponents()) {
			if(focused != null && focused.equals(comp)) return true;
			
			if(comp instanceof Container) {
				boolean hasFocus = childHasFocus((Container) comp);
				if(hasFocus) return true;
			}
		}
		return false;
	}
	
	private static boolean validKeybind(KeyBind bind) {
		// Check modifiers
		ObjectSet<Modifiers> mods = bind.getMods();
		for(Modifiers mod : mods) {
			switch(mod) {
			case ALT:
				if(!isAltDown()) return false;
				break;
			case CTRL:
				if(!isControlDown()) return false;
				break;
			case SHIFT:
				if(!isShiftDown()) return false;
				break;
			default:
				return false; // shouldn't get hit
			}
		}
		
		// Check key
		return pressed[bind.getKey()];
	}
	
	public static boolean isShiftDown() {
		return shiftCount > 0;
	}
	
	public static boolean isAltDown() {
		return altCount > 0;
	}
	
	public static boolean isControlDown() {
		return ctrlCount > 0;
	}
	
	public static boolean isPressed(int keycode) {
		return pressed[keycode];
	}

}
