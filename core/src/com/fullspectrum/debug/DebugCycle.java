package com.fullspectrum.debug;

public enum DebugCycle {

	ZOOM('o', 3),
	SLOW('y', 3);
	
	private final char character;
	private final int cycles;
	
	private DebugCycle(char character, int cycles){
		this.character = character;
		this.cycles = cycles;
	}
	
	public char getCharacter(){
		return character;
	}
	
	public int getNumCycles(){
		return cycles;
	}
	
	public static DebugCycle getCycle(char character){
		for(DebugCycle cycle : values()){
			if(cycle.character == character) return cycle;
		}
		return null;
	}
	
	
}
