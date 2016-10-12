package com.fullspectrum.debug;

public enum DebugToggle {

	FPS('p'),
	SHOW_NAVMESH('l'),
	SHOW_PATH('k');
	
	private final char character;
	
	private DebugToggle(char character){
		this.character = character;
	}
	
	public char getCharacter(){
		return character;
	}
	
	public static DebugToggle getToggle(char character){
		for(DebugToggle action : values()){
			if(action.character == character) return action;
		}
		return null;
	}
}
