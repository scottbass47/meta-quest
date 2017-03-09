package com.fullspectrum.debug;

public enum DebugToggle {

	FPS('p') {
		@Override
		public void onToggle() {
			DebugVars.FPS_ON = !DebugVars.FPS_ON;
		}
	},
	SHOW_NAVMESH('l') {
		@Override
		public void onToggle() {
			DebugVars.NAVMESH_ON = !DebugVars.NAVMESH_ON;
		}
	},
	SHOW_PATH('k') {
		@Override
		public void onToggle() {
			DebugVars.PATHS_ON = !DebugVars.PATHS_ON;
		}
	},
	SHOW_MAP_COORDS('[') {
		@Override
		public void onToggle() {
			DebugVars.MAP_COORDS_ON = !DebugVars.MAP_COORDS_ON;
		}
	},
	SHOW_HITBOXES('m') {
		@Override
		public void onToggle() {
			DebugVars.HITBOXES_ON = !DebugVars.HITBOXES_ON;
		}
	},
	SHOW_COMMANDS(';') {
		@Override
		public void onToggle() {
			DebugVars.COMMANDS_ON = !DebugVars.COMMANDS_ON;
		}
	},
	SHOW_RANGE('u') {
		@Override
		public void onToggle() {
			DebugVars.RANGES_ON = !DebugVars.RANGES_ON;
		}
	},
	SHOW_HEALTH('h') {
		@Override
		public void onToggle() {
			DebugVars.HEALTH_ON = !DebugVars.HEALTH_ON;
		}
	},
	SHOW_FLOW_FIELD(']') {
		@Override
		public void onToggle() {
			DebugVars.FLOW_FIELD_ON = !DebugVars.FLOW_FIELD_ON;
		}
	},
	SHOW_SWING('.') {
		@Override
		public void onToggle() {
			DebugVars.FPS_ON = !DebugVars.FPS_ON;
		}
	};
	
	private final char character;
	
	private DebugToggle(char character){
		this.character = character;
	}
	
	public char getCharacter(){
		return character;
	}
	
	public abstract void onToggle();
	
	public static DebugToggle getToggle(char character){
		character = Character.toLowerCase(character);
		for(DebugToggle action : values()){
			if(action.character == character) return action;
		}
		return null;
	}
}
