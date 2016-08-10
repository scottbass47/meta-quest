package com.fullspectrum.input;

public enum Actions {
	MOVE_UP,
	MOVE_DOWN,
	MOVE_RIGHT,
	MOVE_LEFT,
	JUMP,
	ATTACK,
	BLOCK,
	MOVEMENT,
	CHARGE,
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
