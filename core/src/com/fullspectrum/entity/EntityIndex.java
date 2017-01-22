package com.fullspectrum.entity;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.World;
import com.fullspectrum.level.Level;

public enum EntityIndex {

	// Player
	PLAYER {
		@Override
		public Entity create(Engine engine, World world, Level level, float x, float y, int money) {
			return EntityFactory.createPlayer(engine, world, level, null, x, y);
		}
	},
	KNIGHT {
		@Override
		public Entity create(Engine engine, World world, Level level, float x, float y, int money) {
			return null;
		}
	},
	ROGUE {
		@Override
		public Entity create(Engine engine, World world, Level level, float x, float y, int money) {
			return null;
		}
	},
	MAGE {
		@Override
		public Entity create(Engine engine, World world, Level level, float x, float y, int money) {
			return null;
		}
	},
	
	// Enemies
	SPITTER {
		@Override
		public Entity create(Engine engine, World world, Level level, float x, float y, int money) {
			return EntityFactory.createSpitter(engine, world, level, x, y, money);
		}
	},
	SLIME {
		@Override
		public Entity create(Engine engine, World world, Level level, float x, float y, int money) {
			return EntityFactory.createSlime(engine, world, level, x, y, money);
		}
	},
	AI_PLAYER {
		@Override
		public Entity create(Engine engine, World world, Level level, float x, float y, int money) {
			return EntityFactory.createAIPlayer(engine, world, level, x, y, money);
		}
	};
	
	public abstract Entity create(Engine engine, World world, Level level, float x, float y, int money);
	
	public String getName(){
		return name().toLowerCase();
	}
	
	public short shortIndex(){
		return (short)ordinal();
	}
	
}
