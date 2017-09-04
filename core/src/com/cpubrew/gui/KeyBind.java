package com.cpubrew.gui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.ObjectSet;

public class KeyBind {

	private FocusType type;
	private ObjectSet<Modifiers> mods;
	private int key = -2;
	
	public KeyBind() {
		this(FocusType.COMPONENT_FOCUS, -2);
	}

	public KeyBind(FocusType type, int key, Modifiers... modifiers) {
		this.type = type;
		this.key = key;
		
		mods = new ObjectSet<KeyBind.Modifiers>();
		mods.addAll(modifiers);
	}
	
	public KeyBind(FocusType type, int key) {
		this(type, key, new Modifiers[]{});
	}
	
	public KeyBind setKey(int key) {
		this.key = key;
		return this;
	}	
	
	public KeyBind addMod(Modifiers mod) {
		mods.add(mod);
		return this;
	}
	
	public KeyBind removeMod(Modifiers mod) {
		mods.remove(mod);
		return this;
	}

	public KeyBind setFocus(FocusType type) {
		this.type = type;
		return this;
	}
	
	public FocusType getFocusType() {
		return type;
	}
	
	public int getKey() {
		return key;
	}
	
	public ObjectSet<Modifiers> getMods() {
		return mods;
	}
	
	public static enum FocusType {
		/** When the window that contains the component has focus */
		WINDOW_FOCUS,
		
		/** When the component is the ancestor of the component that has focus */
		ANCESTOR_FOCUS,
		
		/** When the component itself has focus */
		COMPONENT_FOCUS
	}
	
	public static enum Modifiers {
		ALT,
		CTRL,
		SHIFT
	}
	
	@Override
	public String toString() {
		if(mods.size == 0) {
			return type + ", " + Keys.toString(key);
		}
		return type + ", " + mods + " - " + Keys.toString(key);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key;
		result = prime * result + ((mods == null) ? 0 : mods.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyBind other = (KeyBind) obj;
		if (key != other.key)
			return false;
		if (mods == null) {
			if (other.mods != null)
				return false;
		} else if (!mods.equals(other.mods))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
