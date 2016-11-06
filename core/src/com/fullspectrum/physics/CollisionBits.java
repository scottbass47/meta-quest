package com.fullspectrum.physics;

public enum CollisionBits {

	TILE,
	ENTITY,
	SENSOR,
	ITEM;
	
	public short getBit(){
		return (short) (1 << ordinal());
	}
	
	public static short getOtherBits(CollisionBits bits){
		short ret = 0;
		for(CollisionBits value : CollisionBits.values()){
			if(value.equals(bits)) continue;
			ret |= value.getBit();
		}
		return ret;
	}
	
	public static CollisionBits getValue(String name){
		for(CollisionBits value : CollisionBits.values()){
			if(value.name().equalsIgnoreCase(name)) return value;
		}
		return null;
	}
	
}
