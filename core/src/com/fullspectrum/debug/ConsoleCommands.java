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
		else if (name.equalsIgnoreCase("map_coords")) {
			DebugVars.MAP_COORDS_ON = !DebugVars.MAP_COORDS_ON;
		}
		else if (name.equalsIgnoreCase("swing")) {
			DebugVars.SWING_ON = !DebugVars.SWING_ON;
		} else{
			console.log("No debug rendering found for '" + name + "'. Use command 'help show' to get a list of possible parameters.", LogLevel.ERROR);
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
		} else {
			console.log("No option to disable '" + name + "'. Use command 'help disable' to get a list of possible options.", LogLevel.ERROR);
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
		spawn(type, amount, false);
	}
	
	public void spawn(String type, boolean on_click){
		spawn(type, 1, on_click);
	}
	
	public void spawn(String type, int amount, boolean on_click){
		EntityIndex index = EntityIndex.get(type);
		if(index == null){
			console.log("Entity type is not valid.", LogLevel.ERROR);
			return;
		}
		DebugVars.SPAWN_ON_CLICK_ENABLED = on_click;
		DebugVars.SPAWN_TYPE = index;
		DebugVars.SPAWN_AMOUNT = amount;
		if(!on_click){
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
	}
	
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
	
	public void help(String function){
		console.log("");
		if(function.equalsIgnoreCase("show")){
			console.log("Toggles debug rendering for some command.");
			console.log("Options:");
			console.log("    navmesh");
			console.log("    paths");
			console.log("    ranges");
			console.log("    flow_field");
			console.log("    health");
			console.log("    hitboxes");
			console.log("    map_coords");
			console.log("    swing");
		} else if(function.equalsIgnoreCase("fps")){
			console.log("Toggles fps counter in upper left.");
		} else if(function.equalsIgnoreCase("slow")){
			console.log("Slows down the game by some factor (i.e. if the input is 3, the game would be 3x slower).");
		} else if(function.equalsIgnoreCase("kill")){
			console.log("Kills all enemies EXCEPT spawners.");
		} else if(function.equalsIgnoreCase("disable")){
			console.log("Toggle used to disable/enable certain features.");
			console.log("List of possible parameters:");
			console.log("    ai");
			console.log("    spawners");
		} else if(function.equalsIgnoreCase("invincible")){
			console.log("Toggles invincibility for player.");
		} else if(function.equalsIgnoreCase("reset")){
			console.log("Resets a command to its default values");
			console.log("Parameters:");
			console.log("    command - command to reset (default value is all commands).");
		} else if(function.equalsIgnoreCase("spawn")){
			console.log("Spawns enemies randomly throughout the level.");
			console.log("Parameters:");
			console.log("    type - entity type (EntityIndex name).");
			console.log("    amount - number of entities to spawn (default value is 1).");
			console.log("    on_click - true/false value for whether or not on click spawning is enabled (default value is false).");
		}
		console.log("");
	}

	public void reset(String command){
		if(command.equalsIgnoreCase("spawn")){
			DebugVars.SPAWN_AMOUNT = 1;
			DebugVars.SPAWN_TYPE = null;
			DebugVars.SPAWN_ON_CLICK_ENABLED = false;
		} else {
			console.log("No option to reset command '" + command + "'. Use command 'help reset' to get a list of possible options");
		}
	}
	
	public void reset() {
		DebugVars.resetAll();
	}

	@HiddenCommand
	public static void setPlayer(Entity p) {
		player = p;
		engine = Mappers.engine.get(player).engine;
		world = Mappers.world.get(player).world;
		level = Mappers.level.get(player).level;
	}
}
