package com.fullspectrum.entity;

public enum EntityIndex {

	// Player
	PLAYER,
	KNIGHT,
	ROGUE,
	MAGE,
	
	// Enemies
	SPITTER,
	SLIME,
	AI_PLAYER;
	
	public String getName(){
		return name().toLowerCase();
	}
	
	public short shortIndex(){
		return (short)ordinal();
	}
}
