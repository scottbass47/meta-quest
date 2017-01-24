package com.fullspectrum.level;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.component.CameraComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PlayerComponent;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.entity.EntityLoader;
import com.fullspectrum.input.GameInput;
import com.fullspectrum.level.Level.EntitySpawn;
import com.fullspectrum.level.LevelInfo.LevelType;
import com.fullspectrum.systems.FlowFieldSystem;

public class LevelManager {

	private Engine engine;
	private World world;
	private SpriteBatch batch;
	private Entity camera;
	private OrthographicCamera worldCamera;
	private GameInput input;
	private Level currentLevel;
	private FlowFieldManager flowManager;
	private Entity player;
	
	public LevelManager(Engine engine, World world, SpriteBatch batch, OrthographicCamera worldCamera, GameInput input){
		this.engine = engine;
		this.world = world;
		this.batch = batch;
		this.worldCamera = worldCamera;
		this.input = input;
		
		// Create Camera
		camera = engine.createEntity();
		CameraComponent cameraComp = engine.createComponent(CameraComponent.class);
		cameraComp.locked = true;
		cameraComp.camera = worldCamera;
		cameraComp.x = worldCamera.position.x;
		cameraComp.y = worldCamera.position.y;
		cameraComp.minX = 0f;
		cameraComp.minY = 0f;
		cameraComp.windowMinX = -2f;
		cameraComp.windowMinY = 0f;
		cameraComp.windowMaxX = 2f;
		cameraComp.windowMaxY = 0f;
		cameraComp.zoom = 3.0f;
		camera.add(cameraComp);
		engine.addEntity(camera);
	}
	
	// SWITCHING LEVELS
	// -----------------
	// 1. Destroy old level
	// 2. Remove all entities (excluding player or entities whose parent is the player)
	// 3. Load in new level
	// 4. Setup nav meshes and flow field for new level
	// 5. Spawn in entities in new level
	// 6. Spawn in player
	// 7. Initialize camera (zoom, position, bounds, etc...)
	@SuppressWarnings({ "unchecked" })
	public void switchLevel(Theme theme, LevelType type, int level, int secret, int section){
		if(currentLevel != null){
			
			// 1. Destroy old level
			currentLevel.destroy();
			
			// 2. Remove all entities excluding player or entities whose parent is the player
			for(Entity entity : engine.getEntities()){
				if(Mappers.player.get(entity) != null || (Mappers.parent.get(entity) != null && Mappers.player.get(Mappers.parent.get(entity).parent) != null)) continue;
				engine.removeEntity(entity);
			}
		}
		// 3. Load in new level
		LevelInfo info = new LevelInfo(theme, type, level, secret, section);
		Level newLevel = new Level(this, info);
		newLevel.loadMap(batch);
		
		// 4. Setup nav meshes and flow field for new level
		Array<EntityIndex> meshes = newLevel.getMeshes();
		for(EntityIndex index : meshes){
			NavMesh.createNavMesh(newLevel, EntityLoader.get(index));
		}
		if(newLevel.requiresFlowField()){
			flowManager = new FlowFieldManager(newLevel, 15);
			engine.getSystem(FlowFieldSystem.class).setFlowManager(flowManager);
		}
		
		// 5. Spawn in entities in new level
		for(EntitySpawn spawn : newLevel.getEntitySpawns()){
			Vector2 spawnPoint = spawn.getPos();
			Entity enemy = spawn.getIndex().create(engine, world, newLevel, spawnPoint.x, spawnPoint.y);
			engine.addEntity(enemy);
		}
		
		// 6. Spawn in player
		Entity player = null;
		if(engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).size() == 0){
			player = EntityIndex.PLAYER.create(engine, world, newLevel, newLevel.getPlayerSpawnPoint().x, newLevel.getPlayerSpawnPoint().y);
			player.getComponent(InputComponent.class).set(input);
			engine.addEntity(player);
			this.player = player;
		}else{
			player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
		}
		Body body = Mappers.body.get(player).body;
		body.setTransform(newLevel.getPlayerSpawnPoint().x, newLevel.getPlayerSpawnPoint().y, 0.0f);
		
		// 7. Initialize camera (zoom, position, bounds, etc...)
		CameraComponent cameraComp = Mappers.camera.get(camera);
		cameraComp.x = body.getPosition().x;
		cameraComp.y = body.getPosition().y;
		cameraComp.maxX = newLevel.getWidth();
		cameraComp.maxY = newLevel.getHeight();
		cameraComp.locked = newLevel.isCameraLocked();
		cameraComp.toFollow = player;
		cameraComp.zoom = newLevel.getCameraZoom();
		cameraComp.update();
		
		currentLevel = newLevel;
	}
	
	public void render(){
		if(currentLevel != null){
			currentLevel.render(worldCamera);
		}
	}
	
	public Engine getEngine(){
		return engine;
	}
	
	public World getWorld(){
		return world;
	}
	
	public Level getCurrentLevel(){
		return currentLevel;
	}
	
	public FlowFieldManager getFlowFieldManager(){
		return flowManager;
	}
	
	public Entity getCameraEntity(){
		return camera;
	}
	
	public Entity getPlayer(){
		return player;
	}
	
}
