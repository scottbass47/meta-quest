package com.fullspectrum.input;

public enum Actions {
	MOVE_UP,
	MOVE_DOWN,
	MOVE_RIGHT,
	MOVE_LEFT,
	CYCLE_RIGHT,
	CYCLE_LEFT,
	JUMP,
	MOVEMENT,
	ATTACK,
	ABILITY_1,
	ABILITY_2,
	ABILITY_3,
	SELECT,
	PAUSE;
	
	public static Actions getAction(String name){
		for(Actions a : Actions.values()){
			if(a.name().equals(name)) return a;
		}
		return null;
	}
	
	@Override
	public String toString(){
		return name();
	}
}
