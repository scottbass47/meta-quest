package com.fullspectrum.level;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
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
import com.fullspectrum.component.RemoveComponent;
import com.fullspectrum.debug.ConsoleCommands;
import com.fullspectrum.editor.LevelEditor;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.entity.EntityLoader;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.factory.EntityFactory;
import com.fullspectrum.game.PauseMenu;
import com.fullspectrum.input.GameInput;
import com.fullspectrum.level.Level.EntitySpawn;
import com.fullspectrum.level.LevelInfo.LevelType;
import com.fullspectrum.level.tiles.MapTile;
import com.fullspectrum.systems.FlowFieldSystem;

public class LevelManager{

	// Gloabal vars
	private Engine engine;
	private World world;
	private SpriteBatch batch;
	private Entity camera;
	private OrthographicCamera worldCamera;
	private OrthographicCamera hudCamera;
	private GameInput input;
	private Level currentLevel;
	private FlowFieldManager flowManager;
	private Entity player;
	
	// Level
	private LevelInfo previous;
	private LevelInfo lastLevel;

	// Editor
	private LevelEditor editor;
	private boolean editorActive = false;
	
	public LevelManager(Engine engine, World world, SpriteBatch batch, OrthographicCamera worldCamera, OrthographicCamera hudCamera, GameInput input){
		this.engine = engine;
		this.world = world;
		this.batch = batch;
		this.worldCamera = worldCamera;
		this.hudCamera = hudCamera;
		this.input = input;
		
		// Create Camera
		camera = EntityFactory.createCamera(worldCamera);
		EntityManager.addEntity(camera);
		
		editor = new LevelEditor();
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
	public void switchLevel(Theme theme, LevelType type, int level, int secret, int section){
		if(currentLevel != null){
			
			// 1. Destroy old level
			currentLevel.destroy();
			
			// 2. Remove all entities excluding player or entities whose parent is the player (AND ALSO CAMERA!)
			for(Entity entity : engine.getEntities()){
				if(Mappers.player.get(entity) != null || (Mappers.parent.get(entity) != null && Mappers.player.get(Mappers.parent.get(entity).parent) != null)
						|| Mappers.camera.get(entity) != null) continue;
				entity.add(engine.createComponent(RemoveComponent.class));
			}
		}
		
		// 3. Load in new level
		LevelInfo info = new LevelInfo(theme, type, level, secret, section);
		
		// Load from disk if you're changing levels
		Level newLevel = currentLevel;
		if(currentLevel == null || !currentLevel.getInfo().equals(info)) {
			newLevel = new Level(this, info);
			newLevel.load();
		}
		newLevel.init();
		
		EntityFactory.level = newLevel;
		
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
			Entity enemy = spawn.getIndex().create(spawnPoint.x, spawnPoint.y);
			engine.addEntity(enemy);
		}
		
		// 6. Spawn in player
		spawnPlayer(newLevel);
		Body body = Mappers.body.get(player).body;
		
		// 7. Initialize camera (zoom, position, bounds, etc...)
		CameraComponent cameraComp = Mappers.camera.get(camera);
		cameraComp.x = body.getPosition().x;
		cameraComp.y = body.getPosition().y;
		
		ExpandableGrid<MapTile> tileMap = newLevel.getTileMap();
		cameraComp.minX = tileMap.getMinCol();
		cameraComp.minY = tileMap.getMinRow();
		cameraComp.maxX = tileMap.getMaxCol() + 1;
		cameraComp.maxY = tileMap.getMaxRow() + 1;
		
		cameraComp.locked = newLevel.isCameraLocked();
		cameraComp.toFollow = player;
		cameraComp.zoom = newLevel.getCameraZoom();
		cameraComp.update();
		
		if(currentLevel != null){
			if(currentLevel.getInfo().isLevel()){
				lastLevel = currentLevel.getInfo();
			}
			previous = currentLevel.getInfo();
		}
		currentLevel = newLevel;
	}
	
	public void switchToEditorMode() {
		// Assume current level is non-null, editor mode should only be available in levels...
		currentLevel.destroy();

		// Despawn all entities except for the camera
		for(int i = 0; i < engine.getEntities().size(); i++) {
			Entity entity = engine.getEntities().get(i);
			if(Mappers.camera.get(entity) != null) continue;
			EntityManager.cleanUp(entity);
			i--;
		}
		
		// Setup editor
		editor.setCurrentLevel(currentLevel);
		editor.setWorldCamera(worldCamera);
		editor.setHudCamera(hudCamera);
		input.getRawInput().addInput(editor);
		
		editorActive = true;
	}
	
	public void switchToPlayMode() {
		editorActive = false;
		input.getRawInput().removeInput(editor);
		switchLevel(currentLevel.getInfo());
	}
	
	@SuppressWarnings("unchecked")
	public void spawnPlayer(Level newLevel){
		Entity player = null;
		if(engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).size() == 0){
			player = EntityIndex.KNIGHT.create(newLevel.getPlayerSpawnPoint().x, newLevel.getPlayerSpawnPoint().y);
			player.getComponent(InputComponent.class).set(input);
			engine.addEntity(player);
			this.player = player;
		}else{
			player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
		}
		Body body = Mappers.body.get(player).body;
		body.setTransform(newLevel.getPlayerSpawnPoint().x, newLevel.getPlayerSpawnPoint().y, 0.0f);
		Mappers.level.get(player).level = newLevel;
		
		ConsoleCommands.setPlayer(player);
		PauseMenu.setPlayer(player);
		
		Mappers.camera.get(camera).toFollow = player;
	}
	
	public void switchHub(Theme theme){
		switchLevel(theme, LevelType.HUB, -1, -1, -1);
	}
	
	public void switchLevel(LevelInfo info){
		switchLevel(info.getTheme(), info.getLevelType(), info.getLevel(), info.getSecret(), info.getSection());
	}
	
	public void switchLevel(Theme theme, int level, int section){
		switchLevel(theme, LevelType.LEVEL, level, -1, section);
	}
	
	public void switchNext(){
		LevelInfo currentInfo = currentLevel.getInfo();
		Theme nextTheme = currentInfo.getTheme().getNext();
		if(nextTheme == null && currentInfo.isHub()) throw new RuntimeException("Can't switch to next level when in last section in hub.");
	
		if(currentInfo.isHub()){
			switchHub(nextTheme);
		} else if(currentInfo.isSecret()){
			LevelInfo newInfo = new LevelInfo(currentInfo.getTheme(), LevelType.SECRET, currentInfo.getLevel(), currentInfo.getSecret(), currentInfo.getSecret() + 1);
			if(!levelExists(newInfo)){
				switchLevel(lastLevel);
			}else{
				switchLevel(newInfo);
			}
		}else{
			LevelInfo newInfo = new LevelInfo(currentInfo.getTheme(), LevelType.LEVEL, currentInfo.getLevel(), 1, currentInfo.getSection() + 1);
			if(!levelExists(newInfo)){
				switchHub(currentInfo.getTheme());
			}else{
				switchLevel(newInfo);
			}
		}
	}
	
	public boolean levelExists(LevelInfo info){
		return Gdx.files.internal(info.toFileFormatExtension()).exists();
	}
	
	public void update(float delta) {
		if(editorActive) {
			editor.update(delta);
		}
	}
	
	public void render(){
		if(editorActive) {
			editor.render(batch);
		}
		else if(currentLevel != null){
			currentLevel.render(batch, worldCamera);
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
	
	// CLEANUP Only for debug purposes
	public void switchPlayer(EntityIndex index){
		Body body = Mappers.body.get(player).body;
	
		float x = body.getPosition().x;
		float y = body.getPosition().y;
		
		EntityManager.cleanUp(player);
		
		player = index.create(x, y);
		player.getComponent(InputComponent.class).set(input);
		engine.addEntity(player);
		
		ConsoleCommands.setPlayer(player);
		PauseMenu.setPlayer(player);

		Mappers.camera.get(camera).toFollow = player;
	}
	
}
