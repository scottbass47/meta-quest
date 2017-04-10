package com.fullspectrum.entity;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.factory.EntityFactory;

public enum EntityIndex {

	// Player
//	PLAYER {
//		@Override
//		public Entity create(float x, float y) {
//			return EntityFactory.createPlayer(null, x, y);
//		}
//	},
	KNIGHT {
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createKnight(x, y);
		}
	},
	ROGUE {
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createRogue(x, y);
		}
	},
	MAGE {
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createMage(x, y);
		}
	},
	
	// Enemies
	SPITTER {
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createSpitter(x, y);
		}
	},
	SLIME {
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createSlime(x, y);
		}
	},
	AI_PLAYER {
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createAIPlayer(x, y);
		}
	},
	SPAWNER{
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createSpawner(x, y);
		}
	};
	
	// TODO Consider including Input as a needed argument
	public abstract Entity create(float x, float y);
	
	public String getName(){
		return name().toLowerCase();
	}
	
	public short shortIndex(){
		return (short)ordinal();
	}
	
	public static EntityIndex get(String name){
		for(EntityIndex index : EntityIndex.values()){
			if(index.name().equalsIgnoreCase(name)) return index;
		}
		return null;
	}
	
}
