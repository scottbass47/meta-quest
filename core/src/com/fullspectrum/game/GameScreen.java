package com.fullspectrum.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.ability.Ability;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.AISMComponent;
import com.fullspectrum.component.AbilityComponent;
import com.fullspectrum.component.BarrierComponent;
import com.fullspectrum.component.BlacksmithComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.MoneyComponent;
import com.fullspectrum.component.PathComponent;
import com.fullspectrum.component.TargetComponent;
import com.fullspectrum.debug.ConsoleCommands;
import com.fullspectrum.debug.DebugCycle;
import com.fullspectrum.debug.DebugInput;
import com.fullspectrum.debug.DebugKeys;
import com.fullspectrum.debug.DebugRender;
import com.fullspectrum.debug.DebugRender.RenderMode;
import com.fullspectrum.debug.DebugToggle;
import com.fullspectrum.debug.DebugVars;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.entity.EntityType;
import com.fullspectrum.factory.EntityFactory;
import com.fullspectrum.fsm.StateMachineSystem;
import com.fullspectrum.fsm.transition.RangeTransitionData;
import com.fullspectrum.fsm.transition.TransitionObject;
import com.fullspectrum.fsm.transition.Transitions;
import com.fullspectrum.input.GameInput;
import com.fullspectrum.input.Mouse;
import com.fullspectrum.level.LevelManager;
import com.fullspectrum.level.NavMesh;
import com.fullspectrum.level.Node;
import com.fullspectrum.level.Theme;
import com.fullspectrum.level.Tile;
import com.fullspectrum.physics.collision.WorldCollision;
import com.fullspectrum.systems.AbilitySystem;
import com.fullspectrum.systems.AnimationSystem;
import com.fullspectrum.systems.AttackingSystem;
import com.fullspectrum.systems.BarrierSystem;
import com.fullspectrum.systems.BehaviorSystem;
import com.fullspectrum.systems.BlinkSystem;
import com.fullspectrum.systems.BobSystem;
import com.fullspectrum.systems.CameraSystem;
import com.fullspectrum.systems.CombustibleSystem;
import com.fullspectrum.systems.ControlledMovementSystem;
import com.fullspectrum.systems.DeathSystem;
import com.fullspectrum.systems.DirectionSystem;
import com.fullspectrum.systems.DropMovementSystem;
import com.fullspectrum.systems.FacingSystem;
import com.fullspectrum.systems.FlowFieldSystem;
import com.fullspectrum.systems.FlowFollowSystem;
import com.fullspectrum.systems.FlyingSystem;
import com.fullspectrum.systems.FollowingSystem;
import com.fullspectrum.systems.ForceSystem;
import com.fullspectrum.systems.FrameMovementSystem;
import com.fullspectrum.systems.GroundMovementSystem;
import com.fullspectrum.systems.JumpSystem;
import com.fullspectrum.systems.KnockBackSystem;
import com.fullspectrum.systems.LadderMovementSystem;
import com.fullspectrum.systems.LevelSwitchSystem;
import com.fullspectrum.systems.PathFollowingSystem;
import com.fullspectrum.systems.PositioningSystem;
import com.fullspectrum.systems.RelativePositioningSystem;
import com.fullspectrum.systems.RemovalSystem;
import com.fullspectrum.systems.RenderingSystem;
import com.fullspectrum.systems.RotationSystem;
import com.fullspectrum.systems.SpawnerSystem;
import com.fullspectrum.systems.SwingingSystem;
import com.fullspectrum.systems.TargetingSystem;
import com.fullspectrum.systems.TextRenderingSystem;
import com.fullspectrum.systems.TimerSystem;
import com.fullspectrum.systems.VelocitySystem;
import com.fullspectrum.systems.WanderingSystem;
import com.fullspectrum.utils.EntityUtils;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;

public class GameScreen extends AbstractScreen {

	// Debug Graphics
	private Box2DDebugRenderer b2dr;

	// Ashley
	private Engine engine;
	private RenderingSystem renderer;
	private TextRenderingSystem textRenderer;

	// Tile Map
	private LevelManager levelManager;
	
	// Box2D
	private World world;
	private WorldCollision worldCollision;

	// Rendering
	private FrameBuffer frameBuffer;
//	private FrameBuffer mainBuffer;
//	private ShaderProgram mellowShader;
//	private ShaderProgram glowShader;
	private int previousZoom = 0;
	private AssetLoader assets;
	private BitmapFont font;
//	private DebugConsole console;
	private Console console;
	private boolean pauseMenuOpen = false;
	private PauseMenu pauseMenu;

//	private int ups = 0;

	public GameScreen(OrthographicCamera worldCamera, OrthographicCamera hudCamera, Game game, ArrayMap<ScreenState, Screen> screens, GameInput input) {
		super(worldCamera, hudCamera, game, screens, input);
		assets = AssetLoader.getInstance();
		b2dr = new Box2DDebugRenderer();
		world = new World(new Vector2(0, GameVars.GRAVITY), true);
		worldCollision = new WorldCollision();
		world.setContactListener(worldCollision);

		// Load Assets
		assets.loadHUD();
		assets.loadSprites();
		assets.loadFont();
		font = assets.getFont(AssetLoader.font28);
		
		// Setup Debug Console
//		int width = (int)(GameVars.SCREEN_WIDTH * 0.5f);
//		int height = 300;
//		console = new DebugConsole((int)(GameVars.SCREEN_WIDTH * 0.5f - width * 0.5f), GameVars.SCREEN_HEIGHT - 20 - height, width, height);
//		input.getRawInput().addInput(console);
		
		// Setup Shader
//		mellowShader = new ShaderProgram(Gdx.files.internal("shaders/mellow.vsh"), Gdx.files.internal("shaders/mellow.fsh"));
//		if (!mellowShader.isCompiled()) {
//			throw new GdxRuntimeException(mellowShader.getLog());
//		}
//		glowShader = new ShaderProgram(Gdx.files.internal("shaders/glow.vsh"), Gdx.files.internal("shaders/glow.fsh"));
//		if (!glowShader.isCompiled()) {
//			throw new GdxRuntimeException(glowShader.getLog());
//		}
//		ShaderProgram.pedantic = false;

		// Setup Frame Buffer
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, GameVars.FRAMEBUFFER_WIDTH, GameVars.FRAMEBUFFER_HEIGHT, false);
		frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
//		mainBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, SCREEN_WIDTH, SCREEN_HEIGHT, false);
//		mainBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

		// Setup Ashley
		// engine = new PooledEngine(16, 64, 64, 512);
		engine = new Engine();
		engine.addEntityListener(new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				EntityUtils.setValid(entity, false);
			}

			@Override
			public void entityAdded(Entity entity) {
				EntityUtils.setValid(entity, true);
			}
		});

		EntityFactory.engine = engine;
		EntityFactory.world = world;
		
		renderer = new RenderingSystem();
		textRenderer = new TextRenderingSystem(hudCamera);
		engine.addSystem(renderer);
		engine.addSystem(textRenderer);

		// AI Systems
		engine.addSystem(new TargetingSystem());
		engine.addSystem(new BehaviorSystem());
		engine.addSystem(new FollowingSystem());
		engine.addSystem(new WanderingSystem());
		engine.addSystem(new PathFollowingSystem());
		engine.addSystem(new FlowFieldSystem());
		engine.addSystem(new FlowFollowSystem());
		engine.addSystem(new AttackingSystem());

		// State Machine System (transitions)
		engine.addSystem(StateMachineSystem.getInstance());

		// Other Systems
		engine.addSystem(new AbilitySystem());
		engine.addSystem(new TimerSystem());
		engine.addSystem(new SpawnerSystem());
		engine.addSystem(new AnimationSystem());
		engine.addSystem(new JumpSystem());
		engine.addSystem(new BobSystem());
		engine.addSystem(new ForceSystem());
		engine.addSystem(new DirectionSystem());
		
		// Movement Systems
		engine.addSystem(new FlyingSystem());
		engine.addSystem(new GroundMovementSystem());
		engine.addSystem(new DropMovementSystem());
		engine.addSystem(new LadderMovementSystem());
		engine.addSystem(new FrameMovementSystem());
		engine.addSystem(new ControlledMovementSystem());
//		engine.addSystem(new WallSlideSystem());
		
		engine.addSystem(new KnockBackSystem()); // order matters, knockback has to come after movement systems
		engine.addSystem(new VelocitySystem());
		engine.addSystem(new PositioningSystem());
		engine.addSystem(new RotationSystem());
		engine.addSystem(new FacingSystem());
		engine.addSystem(new CameraSystem());
		engine.addSystem(new BarrierSystem());
		engine.addSystem(new CombustibleSystem());
		engine.addSystem(new RelativePositioningSystem());
		engine.addSystem(new SwingingSystem());
		engine.addSystem(new BlinkSystem());
		engine.addSystem(new LevelSwitchSystem());
		engine.addSystem(new DeathSystem());
		engine.addSystem(new RemovalSystem(world));

		// Setup and Load Level
		batch.setProjectionMatrix(worldCamera.combined);
		levelManager = new LevelManager(engine, world, batch, worldCamera, input);
//		levelManager.switchHub(Theme.GRASSY);
		levelManager.switchLevel(Theme.GRASSY, 1, 1);
		
		ConsoleCommands.setPlayer(levelManager.getPlayer());
		console = new GUIConsole();
		console.setSizePercent(75, 50);
		console.setPositionPercent(12.5f, 50);
		console.setCommandExecutor(new ConsoleCommands());
		
		PauseMenu.setPlayer(levelManager.getPlayer());
		pauseMenu = new PauseMenu(hudCamera);
	}
	
	private void spawnEnemy(Node node) {
		Entity enemy = EntityIndex.AI_PLAYER.create(node.getCol() + 0.5f, node.getRow() + 1.0f);
		engine.addEntity(enemy);
	}
	
	private void spawnFlyingEnemey(){
		int row = 0;
		int col = 0;
		do{
			row = MathUtils.random(0, levelManager.getCurrentLevel().getHeight());
		    col = MathUtils.random(0, levelManager.getCurrentLevel().getWidth());
		}while(levelManager.getCurrentLevel().isSolid(row, col) || row > 25);
		spawnSpitter(row, col);
	}
	
	private void spawnSpitter(int row, int col){
		Entity enemy = EntityIndex.SPITTER.create(col + 0.5f, row + 0.5f);
		engine.addEntity(enemy);
	}
	
	private void spawnSlime(Node node){
		spawnSlime(node.getRow(), node.getCol());
	}
	
	private void spawnSlime(int row, int col){
		Entity enemy = EntityIndex.SLIME.create(col + 0.5f, row + 0.5f);
		engine.addEntity(enemy);
	}
	
	public void resetFrameBuffer(int width, int height) {
		frameBuffer.dispose();
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
		frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
	}

	@Override
	protected void init() {

	}

	@Override
	public void update(float delta) {
//		ups++;
//		if(DebugInput.isToggled(DebugToggle.CONSOLE)){
////			console.setOpen(true);
////			console.update(delta);
//			return;
//		} else{
////			console.setOpen(false);
//		}
		if(console.isVisible()){
			return;
		}
		
		if(DebugInput.isJustPressed(DebugKeys.PAUSE_WINDOW)){
			pauseMenuOpen = !pauseMenuOpen;
		}
		
		if(pauseMenuOpen){
			pauseMenu.update(delta);
			return;
		}
		
		DebugRender.update(delta);
		DebugRender.setMode(RenderMode.UPDATE);

		worldCamera.update();
		batch.setProjectionMatrix(worldCamera.combined);

		// Update the Engine
		EntityUtils.engineUpdating = true;
		engine.update(delta);
		EntityUtils.engineUpdating = false;
		
		world.step(delta, 6, 2);
		worldCollision.update();
		EntityManager.update(delta);
		
		if(DebugInput.isJustPressed(DebugKeys.KNIGHT)){
			Entity player = levelManager.getPlayer();
			
			// If you're not the knight currently, then switch
			if(!Mappers.entity.get(player).type.equals(EntityType.KNIGHT)){
				levelManager.switchPlayer(EntityIndex.KNIGHT);
			}
		} else if(DebugInput.isJustPressed(DebugKeys.ROGUE)){
			Entity player = levelManager.getPlayer();
			
			// If you're not the rogue currently, then switch
			if(!Mappers.entity.get(player).type.equals(EntityType.ROGUE)){
				levelManager.switchPlayer(EntityIndex.ROGUE);
			}
		} else if(DebugInput.isJustPressed(DebugKeys.MAGE)){
			Entity player = levelManager.getPlayer();
			
			// If you're not the mage currently, then switch
			if(!Mappers.entity.get(player).type.equals(EntityType.MAGE)){
				levelManager.switchPlayer(EntityIndex.MAGE);
			}
		}
		
		// Spawning
		if(DebugVars.SPAWN_ON_CLICK_ENABLED && DebugVars.SPAWN_TYPE != null && DebugVars.SPAWN_AMOUNT > 0 && Mouse.isJustPressed()){
			Vector2 mousePos = Mouse.getWorldPosition(worldCamera);
			for(int i = 0; i < DebugVars.SPAWN_AMOUNT; i++){
				engine.addEntity(DebugVars.SPAWN_TYPE.create( 
						mousePos.x + MathUtils.random(1.0f) - 0.5f, 
						mousePos.y + MathUtils.random(1.0f) - 0.5f));
			}
		}
		
		//		if (input.isJustPressed(Actions.SELECT)) {
//			changePlayer();
//		}

//		Vector2 mousePos = Mouse.getWorldPosition(worldCamera);
//		Node mouseNode = NavMesh.get(EntityIndex.AI_PLAYER).getNodeAt(mousePos.x, mousePos.y);
//		if (mouseNode != null) {
//			if (Mouse.isJustPressed()) {
//				spawnEnemy(mouseNode);
//			}
//		} else if(!level.isSolid(mousePos.x, mousePos.y) && Mouse.isJustPressed()){
//			spawnSpitter((int)mousePos.y, (int)mousePos.x);
//		}
//
//		if (DebugInput.isPressed(DebugKeys.SPAWN)) {
//			float random = MathUtils.random();
//			if(random <= 0.33f){
//				spawnFlyingEnemey();
//			}else if(random <= 0.66f){
//				Node spawnNode = playerMesh.getRandomNode();
//				spawnSlime(spawnNode);
//			}else{
//				Node spawnNode = playerMesh.getRandomNode();
//				spawnEnemy(spawnNode);
//			}
//		}
//
		if (DebugInput.getCycle(DebugCycle.ZOOM) != previousZoom) {
			previousZoom = DebugInput.getCycle(DebugCycle.ZOOM);
			Mappers.camera.get(levelManager.getCameraEntity()).zoom = previousZoom + 1;
		}
		
//		BodyComponent bodyComp = Mappers.body.get(playerOne);
		
//		if(DebugInput.isPressed(DebugKeys.SHOOT) && ups % 5 == 0){
//			BulletFactory.spawnBullet(playerOne, 5.0f, 0.0f, 20.0f, 25.0f);
//		}

		// Random Coin Spawning
//		if (ups % 6 == 0) {
//			int row = MathUtils.random(0, level.getHeight());
//			int col = MathUtils.random(0, level.getWidth());
//			while (!level.isAir(row, col)) {
//				row = MathUtils.random(0, level.getHeight());
//				col = MathUtils.random(0, level.getWidth());
//			}
//			Entity coin = EntityFactory.createCoin(engine, world, col + MathUtils.random(0, 1.0f), row + MathUtils.random(0.5f, 1.5f), /*MathUtils.random(-1000, 1000)*/0, 0, MathUtils.random(1, 100));
//			engine.addEntity(coin);
//		}

		// Remove Invalid Enemies
//		for (Iterator<Entity> iter = enemies.iterator(); iter.hasNext();) {
//			if (!EntityUtils.isValid(iter.next())) {
//				iter.remove();
//			}
//		}
		
		// Respawn Player
//		if(!EntityUtils.isValid(playerOne)){
//			Node playerSpawn = playerMesh.getRandomNode();
//			playerOne = EntityFactory.createPlayer(engine, level, input, world, playerSpawn.getCol() + 0.5f, playerSpawn.getRow() + 1.5f);
//			engine.addEntity(playerOne);
//			index = -1;
//			changePlayer();
//		}
	}

//	private void changePlayer() {
//		CameraComponent cameraComp = Mappers.camera.get(cameraEntity);
//		index++;
//		if (index > enemies.size) index = 0;
//		if (index == 0) {
//			cameraComp.toFollow = playerOne;
//		}
//		else {
//			cameraComp.toFollow = enemies.get(index - 1);
//		}
//	}

	@SuppressWarnings("unchecked")
	@Override
	public void render() {
		DebugRender.setMode(RenderMode.RENDER);
//		Gdx.gl20.glEnable(GL20.GL_SCISSOR_TEST);
//		HdpiUtils.glScissor(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		// Render Buffer
		frameBuffer.begin();

		Color bc = new Color(0x6885e3ff);
		Gdx.gl.glClearColor(bc.r, bc.g, bc.b, bc.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		worldCamera.update();
		batch.setProjectionMatrix(worldCamera.combined);
		
		levelManager.render();
		if (DebugVars.NAVMESH_ON) {
			if(levelManager.getCurrentLevel().getMeshes().size > 0){
				NavMesh.get(EntityIndex.AI_PLAYER).render(batch);
			}
		}
		if(DebugVars.FLOW_FIELD_ON){
			if(levelManager.getCurrentLevel().requiresFlowField()){
				levelManager.getFlowFieldManager().render(batch);
			}
		}
		if (DebugVars.PATHS_ON) {
			for (Entity enemy : engine.getEntitiesFor(Family.all(PathComponent.class).get())) {
				Mappers.path.get(enemy).pathFinder.render(batch);
			}
		}
		if (DebugVars.HEALTH_ON) {
			for (Entity entity : engine.getEntitiesFor(Family.all(HealthComponent.class).get())) {
				renderHealth(batch, entity);
			}
		}
		if (DebugVars.HITBOXES_ON) b2dr.render(world, worldCamera.combined);
		if (DebugVars.RANGES_ON){
			for(Entity enemy : engine.getEntitiesFor(Family.all(AISMComponent.class).get())){
				renderRange(batch, enemy);
			}
		}

		DebugRender.render(batch);
		renderer.render(batch);
		textRenderer.render(batch, levelManager.getCameraEntity());

		frameBuffer.end();

//		HdpiUtils.glViewport(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
//		HdpiUtils.glScissor(0, 0, SCREEN_WIDTH - 1, SCREEN_HEIGHT - 1);

//		CameraComponent camera = Mappers.camera.get(cameraEntity);

//		mainBuffer.begin();
		batch.begin();
//		batch.setShader(mellowShader);
//		batch.setShader(glowShader);
		batch.setProjectionMatrix(hudCamera.combined);
//		batch.setProjectionMatrix(batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
//		mellowShader.setUniformf("u_textureSizes", FRAMEBUFFER_WIDTH, FRAMEBUFFER_HEIGHT, 1.0f, 0.0f);
//		mellowShader.setUniformf("u_sampleProperties", camera.subpixelX, camera.subpixelY, camera.upscaleOffsetX, camera.upscaleOffsetY);
		batch.draw(frameBuffer.getColorBufferTexture(), 0, GameVars.SCREEN_HEIGHT, GameVars.SCREEN_WIDTH, -GameVars.SCREEN_HEIGHT);
		batch.end();
		
//		batch.setShader(vignetteShader);
//		mainBuffer.end();
		
//		batch.begin();
//		batch.setShader(vignetteShader);
//		batch.setProjectionMatrix(hudCamera.combined);
//		batch.draw(mainBuffer.getColorBufferTexture(), 0, SCREEN_HEIGHT, SCREEN_WIDTH, -SCREEN_HEIGHT);
//		batch.end();

		batch.setShader(null);
//		HdpiUtils.glScissor(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		batch.setProjectionMatrix(hudCamera.combined);
		if(DebugVars.MAP_COORDS_ON){
			Vector2 mousePos = Mouse.getWorldPosition(worldCamera);
			Tile mouseTile = levelManager.getCurrentLevel().tileAt((int)mousePos.y, (int)mousePos.x);
			if(mouseTile != null){
				batch.begin();
				font.draw(batch, mouseTile.getRow() + ", " + mouseTile.getCol(), 10, 40);
				batch.end();
			}
		}
		renderHUD(batch, levelManager.getPlayer());
		
		if(DebugVars.COMMANDS_ON){
			batch.begin();
			int startY = DebugToggle.values().length > DebugCycle.values().length ? (DebugToggle.values().length + 1) * 20 : (DebugCycle.values().length + 1) * 20;
			startY += 50;
			int toggleX = 900;
			int cycleX = 1100;
			int keyX = 700;
			font.getData().setScale(0.5f);
			font.draw(batch, "Toggles:", toggleX, startY);
			font.draw(batch, "Cycles:", cycleX, startY);
			font.draw(batch, "Keys:", keyX, startY);
			int counter = 1;
			for(DebugToggle toggle : DebugToggle.values()){
				font.draw(batch, toggle.name() + " - '" + toggle.getCharacter() + "'", toggleX, startY - counter * 20);
				counter++;
			}
			counter = 1;
			for(DebugCycle cycle : DebugCycle.values()){
				font.draw(batch, cycle.name() + " - '" + cycle.getCharacter() + "'", cycleX, startY - counter * 20);
				counter++;
			}
			counter = 1;
			for(DebugKeys key : DebugKeys.values()){
				font.draw(batch, key.name() + " - '" + Keys.toString(key.getKey()) + "'", keyX, startY - counter * 20);
				counter++;
			}
			batch.end();
			font.getData().setScale(1.0f);
		}
		
		if(pauseMenuOpen){
			pauseMenu.render(batch);
		}
		
		// Render the console
		if(console.isVisible()){
//			console.render(batch);
			console.draw();
		}
		
		if(DebugVars.FPS_ON){
			batch.begin();
			font.draw(batch, "" + Gdx.graphics.getFramesPerSecond(), 10, 710);
			batch.end();
		}
		
		// sRenderer.setProjectionMatrix(worldCamera.combined);
		// sRenderer.begin(ShapeType.Line);
		// sRenderer.setColor(Color.RED);
		// sRenderer.line(camera.camera.position.x + camera.windowMinX,
		// camera.camera.position.y + camera.windowMinY,
		// camera.camera.position.x + camera.windowMinX,
		// camera.camera.position.y + camera.windowMaxY);
		// sRenderer.line(camera.camera.position.x + camera.windowMinX,
		// camera.camera.position.y + camera.windowMinY,
		// camera.camera.position.x + camera.windowMaxX,
		// camera.camera.position.y + camera.windowMinY);
		// sRenderer.line(camera.camera.position.x + camera.windowMaxX,
		// camera.camera.position.y + camera.windowMaxY,
		// camera.camera.position.x + camera.windowMinX,
		// camera.camera.position.y + camera.windowMaxY);
		// sRenderer.line(camera.camera.position.x + camera.windowMaxX,
		// camera.camera.position.y + camera.windowMaxY,
		// camera.camera.position.x + camera.windowMaxX,
		// camera.camera.position.y + camera.windowMinY);
		// sRenderer.end();
	}

	private void renderHUD(SpriteBatch batch, Entity entity) {
		if (!EntityUtils.isValid(entity)) return;
		AbilityComponent abilityComp = Mappers.ability.get(entity);
		HealthComponent healthComp = Mappers.heatlh.get(entity);
		BarrierComponent staminaComp = Mappers.barrier.get(entity);
		MoneyComponent moneyComp = Mappers.money.get(entity);
		
		TextureRegion healthEmpty = assets.getRegion(Asset.HEALTH_BAR_EMPTY);
		TextureRegion healthFull = assets.getRegion(Asset.HEALTH_BAR_FULL);
		TextureRegion staminaEmpty = assets.getRegion(Asset.STAMINA_BAR_EMPTY);
		TextureRegion staminaFull = assets.getRegion(Asset.STAMINA_BAR_FULL);
		TextureRegion coin = assets.getAnimation(Asset.COIN_GOLD).getKeyFrame(0);
		
		float scale = 3.0f;

		batch.begin();
		// Abilities
		float abilityY = 150;
		float iconWidth = 18.0f;
		float iconHeight = 18.0f;
		GlyphLayout layout = new GlyphLayout();
		float spacing = 3;
		int numIcons = 3;
		float totalWidth = (numIcons * iconWidth + spacing * (numIcons - 1)) * scale;
		float startX = GameVars.SCREEN_WIDTH * 0.5f - totalWidth * 0.5f;
		int counter = 0;
		for(AbilityType type : abilityComp.getAbilityMap().keys()){
			Ability ability = abilityComp.getAbility(type);
			if(!ability.isActivated()) continue;
			TextureRegion icon = ability.getIcon();
			if(icon == null) continue;
			float x = startX + (iconWidth + spacing) * counter * spacing;
			if(ability.isReady()){
				batch.setColor(Color.WHITE);
			} else{
				batch.setColor(Color.DARK_GRAY);
			}
			batch.draw(icon, x, abilityY, iconWidth * 0.5f, iconHeight * 0.5f, iconWidth, iconHeight, scale, scale, 0.0f);
			if(!ability.isReady() && !ability.inUse()){
				int timeLeft = (int)(ability.getCooldown() - ability.getTimeElapsed() + 0.99f);
				String num = "" + timeLeft;
				layout.setText(font, num);
				font.setColor(Color.WHITE);
				font.draw(batch, num, x + iconWidth * 0.5f - layout.width * 0.5f, abilityY + iconHeight * 0.5f + layout.height * 0.5f);
			} 
			counter++;
		}
		
		// Health
		float healthEmptyWidth = healthEmpty.getRegionWidth();
		float healthEmptyHeight = healthEmpty.getRegionHeight();
		float healthY = abilityY - iconHeight * scale + 4.0f;

		int healthSrcX = healthFull.getRegionX();
		int healthSrcY = healthFull.getRegionY();

		int healthBarWidth = (int) (healthEmptyWidth * (healthComp.health / healthComp.maxHealth));

		// Stamina
		float staminaEmptyWidth = staminaEmpty.getRegionWidth();
		float staminaEmptyHeight = staminaEmpty.getRegionHeight();
		float staminaY = healthY - staminaEmptyHeight * scale + 2.0f * scale;

		int staminaSrcX = staminaFull.getRegionX();
		int staminaSrcY = staminaFull.getRegionY();

		// int staminaBarWidth = (int)(staminaEmptyWidth * (healthComp.health /
		// healthComp.maxHealth));
		int staminaBarWidth = (int)(staminaEmptyWidth * (staminaComp.barrier / staminaComp.maxBarrier));

		float coinX = GameVars.SCREEN_WIDTH * 0.5f - coin.getRegionWidth() * scale - 20;
		float coinY = staminaY - coin.getRegionHeight() * scale - 4 * scale;
		float coinWidth = coin.getRegionWidth();
		float coinHeight = coin.getRegionHeight();

		batch.setColor(Color.WHITE);
		batch.draw(healthEmpty, GameVars.SCREEN_WIDTH * 0.5f - healthEmptyWidth * 0.5f, healthY, healthEmptyWidth * 0.5f, healthEmptyHeight * 0.5f, healthEmptyWidth, healthEmptyHeight, scale, scale, 0.0f);
		batch.draw(healthFull.getTexture(), GameVars.SCREEN_WIDTH * 0.5f - healthEmptyWidth * 0.5f, healthY, healthEmptyWidth * 0.5f, healthEmptyHeight * 0.5f, healthBarWidth, healthEmptyHeight, scale, scale, 0.0f, healthSrcX, healthSrcY, healthBarWidth, (int) (healthEmptyHeight), false, false);
		batch.draw(staminaEmpty, GameVars.SCREEN_WIDTH * 0.5f - staminaEmptyWidth * 0.5f, staminaY, staminaEmptyWidth * 0.5f, staminaEmptyHeight * 0.5f, staminaEmptyWidth, staminaEmptyHeight, scale, scale, 0.0f);
		batch.draw(staminaFull.getTexture(), GameVars.SCREEN_WIDTH * 0.5f - staminaEmptyWidth * 0.5f, staminaY, staminaEmptyWidth * 0.5f, staminaEmptyHeight * 0.5f, staminaBarWidth, staminaEmptyHeight, scale, scale, 0.0f, staminaSrcX, staminaSrcY, staminaBarWidth, (int) (staminaEmptyHeight), false, false);
		batch.draw(coin, coinX, coinY, coinWidth * 0.5f, coinHeight * 0.5f, coinWidth, coinHeight, scale, scale, 0.0f);
		
		// new hud
//		batch.draw(newHud, GameVars.SCREEN_WIDTH * 0.5f - newHud.getRegionWidth() * 0.5f, 20.0f, newHud.getRegionWidth() * 0.5f, newHud.getRegionHeight() * 0.5f, newHud.getRegionWidth(), newHud.getRegionHeight(), 2.0f, 2.0f, 0.0f);
		
		font.setColor(Color.WHITE);
		layout.setText(font, "" + moneyComp.money);
		font.draw(batch, "" + moneyComp.money, GameVars.SCREEN_WIDTH * 0.5f - 10, coinY + layout.height * 0.5f + 2.0f);

		if(Mappers.blacksmith.get(entity) != null){
			BlacksmithComponent blacksmithComp = Mappers.blacksmith.get(entity);
			String toDraw = "Blacksmith: " + (int)blacksmithComp.shield;
			layout.setText(font, toDraw);
			font.draw(batch, toDraw, GameVars.SCREEN_WIDTH * 0.5f - 40, 30);
		}
		
		batch.end();
	}

	private void renderHealth(SpriteBatch batch, Entity entity) {
		if (!EntityUtils.isValid(entity)) return;
		HealthComponent healthComp = Mappers.heatlh.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);

		float width = 1.0f;
		float height = 0.2f;
		float x = bodyComp.body.getPosition().x - width * 0.5f;
		float y = bodyComp.body.getPosition().y + bodyComp.getAABB().height * 0.5f + 0.1f;

		DebugRender.setType(ShapeType.Filled);
		// sRenderer.setColor(Color.BLACK);
		// sRenderer.rect(x, y, width, height);
		DebugRender.setColor(Color.WHITE);
		DebugRender.rect(x, y, width, height);
		DebugRender.setColor(Color.valueOf("e43b44"));
		DebugRender.rect(x, y, width * (healthComp.health / healthComp.maxHealth), height);
	}

	
	private void renderRange(SpriteBatch batch, Entity entity) {
		AISMComponent aismComp = Mappers.aism.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		LevelComponent levelComp = Mappers.level.get(entity);

		TransitionObject obj = aismComp.first().getCurrentStateObject().getFirstData(Transitions.RANGE);
		if(obj == null) return;
		TargetComponent targetComp = Mappers.target.get(entity);
		RangeTransitionData rtd = (RangeTransitionData) obj.data;
		if (rtd == null || targetComp == null || !EntityUtils.isValid(targetComp.target)) return;
		FacingComponent facingComp = Mappers.facing.get(entity);
		BodyComponent otherBody = Mappers.body.get(targetComp.target);

		Body b1 = bodyComp.body;
		Body b2 = otherBody.body;

		float x1 = b1.getPosition().x;
		float y1 = b1.getPosition().y;
		float x2 = b2.getPosition().x;
		float y2 = b2.getPosition().y;
		float r = rtd.distance * rtd.distance;
		
		float d = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
		
		float opposite = y2 - y1;
		float adjacent = facingComp.facingRight ? x2 - x1 : x1 - x2;
		float angle = MathUtils.atan2(opposite, adjacent) * MathUtils.radiansToDegrees;
		angle = angle < 0 ? angle + 360 : angle;

		float halfFov = rtd.fov * 0.5f;
		boolean inFov = angle < halfFov || angle > 360 - halfFov;
		
		Color color = new Color(1, 0, 0, 1);
		if(d <= r && inFov){
			color = new Color(0, 1, 0, 1);
		}
		DebugRender.setType(ShapeType.Line);
		DebugRender.setColor(color);
		if(MathUtils.isEqual(rtd.fov, 360)){
			DebugRender.circle(x1, y1, rtd.distance);
		}else{
			DebugRender.arc(x1, y1, rtd.distance, facingComp.facingRight ? 360 - halfFov : 180 - halfFov, rtd.fov);
		}
		
		color = new Color(1, 0, 0, 1);
		if(levelComp.level.performRayTrace(x1, y1, x2, y2)){
			color = new Color(0, 1, 0, 1);
		}
		DebugRender.setColor(color);
		DebugRender.line(x1, y1, x2, y2);
	}
	
//	private void renderChainBox(SpriteBatch batch, Entity player){
//		BodyComponent bodyComp = Mappers.body.get(player);
//		FacingComponent facingComp = Mappers.facing.get(player);
//		
//		if(Mappers.esm.get(player).first().getCurrentState() == EntityStates.SWING_ANTICIPATION || true){
//			Vector2 pos = bodyComp.body.getPosition();
//			
//			float myX = pos.x;
//			float myY = pos.y;
//			float minX = 0.5f;
//			float maxX = 7.0f;
//			float yRange = 1.5f;
//			
//			// Construct box in front of you
//			float closeX = facingComp.facingRight ? myX + minX : myX - minX;
//			float farX = facingComp.facingRight ? myX + maxX : myX - maxX;
//			float top = myY + yRange;
//			float bottom = myY - yRange;
//			
//			// Draw Raytraces
//			// Ray Trace
//			Array<Entity> entities = Mappers.level.get(player).levelHelper.getEntities(new EntityGrabber() {
//				@Override
//				public boolean validEntity(Entity me, Entity other) {
//					if(Mappers.type.get(me).same(Mappers.type.get(other))) return false;
//					return true;
//				}
//				
//				@Override
//				public Family componentsNeeded() {
//					return Family.all(HealthComponent.class, TypeComponent.class).get();
//				}
//			});
//			if(entities.size == 0) return;
//		
//			final Entity copy = player;
//			Sort.instance().sort(entities, new Comparator<Entity>() {
//				@Override
//				public int compare(Entity o1, Entity o2) {
//					float d1 = PhysicsUtils.getDistanceSqr(Mappers.body.get(copy).body, Mappers.body.get(o1).body);
//					float d2 = PhysicsUtils.getDistanceSqr(Mappers.body.get(copy).body, Mappers.body.get(o2).body);
//					return d1 == d2 ? 0 : (d1 < d2 ? -1 : 1);
//				}
//			});
//			
//			Body otherBody = Mappers.body.get(entities.first()).body;
//			float otherX = otherBody.getPosition().x;
//			float otherY = otherBody.getPosition().y;
//			
//			float angle = MathUtils.atan2(otherY - myY, otherX - myX) * MathUtils.radiansToDegrees;
//			
//			// Check to see what quadrant the angle is in and select two vertices of hit box
//			Rectangle myHitbox = Mappers.body.get(player).getAABB();
//			Rectangle otherHitbox = Mappers.body.get(entities.first()).getAABB();
//			
//			float x1 = 0.0f;
//			float y1 = 0.0f;
//			float x2 = 0.0f;
//			float y2 = 0.0f;
//			
//			float off = 0.25f;
//			
//			float toX1 = 0.0f;
//			float toY1 = 0.0f;
//			float toX2 = 0.0f;
//			float toY2 = 0.0f;
//
//			// Quadrant 1 or 3
//			if((angle >= 0 && angle <= 90) || (angle >= -180 && angle <= -90)){
//				// Use upper left and lower right
//				x1 = myX - myHitbox.width * 0.5f + off;
//				y1 = myY + myHitbox.height * 0.5f - off;
//				x2 = myX + myHitbox.width * 0.5f - off;
//				y2 = myY - myHitbox.height * 0.5f + off;
//				toX1 = otherX - otherHitbox.width * 0.5f;
//				toY1 = otherY + otherHitbox.height * 0.5f;
//				toX2 = otherX + otherHitbox.width * 0.5f;
//				toY2 = otherY - otherHitbox.height * 0.5f;
//			}
//			// Quadrant 2 or 4
//			else{
//				// Use lower left and upper right
//				x1 = myX - myHitbox.width * 0.5f + off;
//				y1 = myY - myHitbox.height * 0.5f + off;
//				x2 = myX + myHitbox.width * 0.5f - off;
//				y2 = myY + myHitbox.height * 0.5f - off;
//				toX1 = otherX - otherHitbox.width * 0.5f;
//				toY1 = otherY - otherHitbox.height * 0.5f;
//				toX2 = otherX + otherHitbox.width * 0.5f;
//				toY2 = otherY + otherHitbox.height * 0.5f;
//			}
//			
//			DebugRender.setType(ShapeType.Line);
//			DebugRender.setColor(Color.CYAN);
//			DebugRender.rect(facingComp.facingRight ? closeX : farX, bottom, Math.abs(farX - closeX), top - bottom);
//			DebugRender.line(x1, y1, toX1, toY1);
//			DebugRender.line(x2, y2, toX2, toY2);
//			
//		}
//	}

	@Override
	protected void destroy() {
		// INCOMPLETE Should probably fill this out one day...
	}

	@Override
	public void dispose() {
		super.dispose();
		frameBuffer.dispose();
		assets.dispose();
	}
}
