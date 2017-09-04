package com.cpubrew.physics.collision;

public enum CollisionBodyType {
	ALL, MOB, PROJECTILE, TILE, SENSOR, ITEM;
	
	public static CollisionBodyType get(String name){
		for(CollisionBodyType type : values()){
			if(type.name().equalsIgnoreCase(name)) return type;
		}
		return null;
	}
}