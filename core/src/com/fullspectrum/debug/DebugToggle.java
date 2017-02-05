package com.fullspectrum.debug;

public enum DebugToggle {

	FPS('p'),
	SHOW_NAVMESH('l'),
	SHOW_PATH('k'),
	SHOW_MAP_COORDS('['),
	SHOW_HITBOXES('m'),
	SHOW_COMMANDS(';'),
	SHOW_RANGE('u'),
	SHOW_HEALTH('h'),
	SHOW_FLOW_FIELD(']'),
	SHOW_SWING('.'),
	SHOW_CHAIN_BOX('/');
	
	private final char character;
	
	private DebugToggle(char character){
		this.character = character;
	}
	
	public char getCharacter(){
		return character;
	}
	
	public static DebugToggle getToggle(char character){
		character = Character.toLowerCase(character);
		for(DebugToggle action : values()){
			if(action.character == character) return action;
		}
		return null;
	}
}
