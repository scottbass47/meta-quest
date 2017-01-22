package com.fullspectrum.entity;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.World;
import com.fullspectrum.level.Level;

public enum EntityIndex {

	// Player
	PLAYER {
		@Override
		public Entity create(Engine engine, World world, Level level, float x, float y) {
			return EntityFactory.createPlayer(engine, world, level, null, x, y);
		}
	},
	KNIGHT {
		@Override
		public Entity create(Engine engine, World world, Level level, float x, float y) {
			return null;
		}
	},
	ROGUE {
		@Override
		public Entity create(Engine engine, World world, Level level, float x, float y) {
			return null;
		}
	},
	MAGE {
		@Override
		public Entity create(Engine engine, World world, Level level, float x, float y) {
			return null;
		}
	},
	
	// Enemies
	SPITTER {
		@Override
		public Entity create(Engine engine, World world, Level level, float x, float y) {
			return EntityFactory.createSpitter(engine, world, level, x, y);
		}
	},
	SLIME {
		@Override
		public Entity create(Engine engine, World world, Level level, float x, float y) {
			return EntityFactory.createSlime(engine, world, level, x, y);
		}
	},
	AI_PLAYER {
		@Override
		public Entity create(Engine engine, World world, Level level, float x, float y) {
			return EntityFactory.createAIPlayer(engine, world, level, x, y);
		}
	},
	SPAWNER{
		@Override
		public Entity create(Engine engine, World world, Level level, float x, float y) {
			return EntityFactory.createSpawner(engine, world, level, x, y);
		}
	};
	
	public abstract Entity create(Engine engine, World world, Level level, float x, float y);
	
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
