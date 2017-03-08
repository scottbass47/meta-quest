package com.fullspectrum.debug;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TypeComponent;
import com.fullspectrum.component.TypeComponent.EntityType;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.level.EntityGrabber;
import com.fullspectrum.level.Level;
import com.fullspectrum.level.LevelHelper;
import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.HiddenCommand;
import com.strongjoshua.console.LogLevel;

public class ConsoleCommands extends CommandExecutor {

	private static Entity player;
	private static Engine engine;
	private static World world;
	private static Level level;

	public void show(String name) {
		if (name.equalsIgnoreCase("navmesh")) {
			DebugVars.NAVMESH_ON = !DebugVars.NAVMESH_ON;
		}
		else if (name.equalsIgnoreCase("paths")) {
			DebugVars.PATHS_ON = !DebugVars.PATHS_ON;
		}
		else if (name.equalsIgnoreCase("ranges")) {
			DebugVars.RANGES_ON = !DebugVars.RANGES_ON;
		}
		else if (name.equalsIgnoreCase("flow_field")) {
			DebugVars.FLOW_FIELD_ON = !DebugVars.FLOW_FIELD_ON;
		}
		else if (name.equalsIgnoreCase("hitboxes")) {
			DebugVars.HITBOXES_ON = !DebugVars.HITBOXES_ON;
		}
		else if (name.equalsIgnoreCase("health")) {
			DebugVars.HEALTH_ON = !DebugVars.HEALTH_ON;
		}
	}
	
	public void fps(){
		DebugVars.FPS_ON = !DebugVars.FPS_ON;
	}
	
	public void slow(int amount){
		DebugVars.SLOW = amount;
	}

	public void disable(String name) {
		if (name.equalsIgnoreCase("spawners")) {
			DebugVars.SPAWNERS_DISABLED = !DebugVars.SPAWNERS_DISABLED;
		}
		else if (name.equalsIgnoreCase("ai")) {
			DebugVars.AI_DISABLED = !DebugVars.AI_DISABLED;
		}
	}

	public void invincible() {
		DebugVars.PLAYER_INVINCIBILITY = !DebugVars.PLAYER_INVINCIBILITY;
	}

	public void spawn(String type) {
		spawn(type, 1);
	}

	// INCOMPLETE Add in navmesh and flow field generation for enemies it
	public void spawn(String type, int amount){
		EntityIndex index = EntityIndex.get(type);
		if(index == null){
			console.log("Entity type is not valid.", LogLevel.ERROR);
		}
		for(int i = 0; i < amount; i++){
			int row = MathUtils.random(level.getHeight());
			int col = MathUtils.random(level.getWidth());
			while(level.isSolid(row, col)){
				row = MathUtils.random(level.getHeight());
				col = MathUtils.random(level.getWidth());
			}
			Entity entity = index.create(engine, world, level, col + 0.5f, row + 0.5f);
			engine.addEntity(entity);
		}
	}
	
	// BUG Doesn't work properly when used on enemies that have effects attached to them
	public void kill(){
		LevelHelper helper = Mappers.level.get(player).levelHelper;
		Array<Entity> entities = helper.getEntities(new EntityGrabber() {
			@Override
			public boolean validEntity(Entity me, Entity other) {
				TypeComponent typeComp = Mappers.type.get(other);
				
				// Only kill enemies
				if(typeComp.type != EntityType.ENEMY) return false;
				
				// Don't kill spawners
				if(Mappers.spawnerPool.get(other) != null) return false;
				
				return true;
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public Family componentsNeeded() {
				return Family.all(HealthComponent.class, TypeComponent.class).get();
			}
		});
		
		for(Entity entity : entities){
			Mappers.death.get(entity).triggerDeath();
		}
	}

	@HiddenCommand
	public static void setPlayer(Entity p) {
		player = p;
		engine = Mappers.engine.get(player).engine;
		world = Mappers.world.get(player).world;
		level = Mappers.level.get(player).level;
	}

	public void reset() {
		DebugVars.resetAll();
	}

}
