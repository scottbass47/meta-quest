package com.fullspectrum.debug;

import com.badlogic.gdx.Input.Keys;

public enum DebugKeys {

	SPAWN(Keys.I);
	
	private final int key;
	
	private DebugKeys(int key){
		this.key = key;
	}
	
	public int getKey(){
		return key;
	}
	
	public static DebugKeys getTrigger(int key){
		for(DebugKeys action : values()){
			if(action.key == key) return action;
		}
		return null;
	}
	
}
