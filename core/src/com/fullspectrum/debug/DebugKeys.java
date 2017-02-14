package com.fullspectrum.debug;

import com.badlogic.gdx.Input.Keys;

public enum DebugKeys {

	SPAWN(Keys.I),
	SHOOT(Keys.G),
	KNIGHT(Keys.NUM_1),
	ROGUE(Keys.NUM_2),
	MAGE(Keys.NUM_3);
	
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
