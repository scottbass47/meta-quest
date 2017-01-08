package com.fullspectrum.game;

import static com.fullspectrum.game.GameVars.FRAMEBUFFER_HEIGHT;
import static com.fullspectrum.game.GameVars.FRAMEBUFFER_WIDTH;
import static com.fullspectrum.game.GameVars.PPM_INV;
import static com.fullspectrum.game.GameVars.SCREEN_HEIGHT;
import static com.fullspectrum.game.GameVars.SCREEN_WIDTH;
import static com.fullspectrum.game.GameVars.UPSCALE;

import java.util.Iterator;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.fullspectrum.ai.AIController;
import com.fullspectrum.ai.PathFinder;
import com.fullspectrum.ai.XYAlignHeuristic;
import com.fullspectrum.assets.Assets;
import com.fullspectrum.component.AIStateMachineComponent;
import com.fullspectrum.component.AbilityComponent;
import com.fullspectrum.component.BarrierComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.CameraComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.MoneyComponent;
import com.fullspectrum.component.PathComponent;
import com.fullspectrum.debug.DebugCycle;
import com.fullspectrum.debug.DebugInput;
import com.fullspectrum.debug.DebugKeys;
import com.fullspectrum.debug.DebugToggle;
import com.fullspectrum.entity.AbilityType;
import com.fullspectrum.entity.EntityFactory;
import com.fullspectrum.entity.EntityStats;
import com.fullspectrum.entity.EntityType;
import com.fullspectrum.entity.EntityUtils;
import com.fullspectrum.fsm.StateMachineSystem;
import com.fullspectrum.fsm.transition.RangeTransitionData;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionObject;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.GameInput;
import com.fullspectrum.input.Mouse;
import com.fullspectrum.level.FlowField;
import com.fullspectrum.level.Level;
import com.fullspectrum.level.NavMesh;
import com.fullspectrum.level.Node;
import com.fullspectrum.physics.WorldCollision;
import com.fullspectrum.systems.AbilitySystem;
import com.fullspectrum.systems.FlyingSystem;
import com.fullspectrum.systems.AnimationSystem;
import com.fullspectrum.systems.AttackingSystem;
import com.fullspectrum.systems.BarrierSystem;
import com.fullspectrum.systems.BlinkSystem;
import com.fullspectrum.systems.CameraSystem;
import com.fullspectrum.systems.CombustibleSystem;
import com.fullspectrum.systems.DeathSystem;
import com.fullspectrum.systems.DirectionSystem;
import com.fullspectrum.systems.DropMovementSystem;
import com.fullspectrum.systems.DropSpawnSystem;
import com.fullspectrum.systems.FacingSystem;
import com.fullspectrum.systems.FlowFollowSystem;
import com.fullspectrum.systems.FollowingSystem;
import com.fullspectrum.systems.ForceSystem;
import com.fullspectrum.systems.GroundMovementSystem;
import com.fullspectrum.systems.JumpSystem;
import com.fullspectrum.systems.LadderMovementSystem;
import com.fullspectrum.systems.PathFollowingSystem;
import com.fullspectrum.systems.PositioningSystem;
import com.fullspectrum.systems.RelativePositioningSystem;
import com.fullspectrum.systems.RemovalSystem;
import com.fullspectrum.systems.RenderingSystem;
import com.fullspectrum.systems.SimpleVelocitySystem;
import com.fullspectrum.systems.SwingingSystem;
import com.fullspectrum.systems.TextRenderingSystem;
import com.fullspectrum.systems.TimerSystem;
import com.fullspectrum.systems.VelocitySystem;
import com.fullspectrum.systems.WallSlideSystem;
import com.fullspectrum.systems.WanderingSystem;

public class GameScreen extends AbstractScreen {

	// Debug Graphics
	private ShapeRenderer sRenderer;
	private Box2DDebugRenderer b2dr;

	// Ashley
	private Engine engine;
	private RenderingSystem renderer;
	private TextRenderingSystem textRenderer;

	// Player
	private Entity playerOne;
	private Array<Entity> enemies;
	private Entity cameraEntity;
	private NavMesh playerMesh;
	private int index = 0;

	// Tile Map
	private Level level;
	private FlowField flowField;

	// Box2D
	private World world;

	// Rendering
	private FrameBuffer frameBuffer;
//	private FrameBuffer mainBuffer;
	private ShaderProgram mellowShader;
//	private ShaderProgram vignetteShader;
	private int previousZoom = 0;
	private Assets assets;
	private BitmapFont font;

	// Coin Stuff
//	private int ups = 0;

	public GameScreen(OrthographicCamera worldCamera, OrthographicCamera hudCamera, Game game, ArrayMap<ScreenState, Screen> screens, GameInput input) {
		super(worldCamera, hudCamera, game, screens, input);
		assets = Assets.getInstance();
		sRenderer = new ShapeRenderer();
		b2dr = new Box2DDebugRenderer();
		world = new World(new Vector2(0, GameVars.GRAVITY), true);
		world.setContactListener(new WorldCollision());
		enemies = new Array<Entity>();

		// Load Assets
		assets.loadHUD();
		assets.loadSprites();
		assets.loadFont();
		font = assets.getFont(Assets.font28);

		// Setup Shader
		mellowShader = new ShaderProgram(Gdx.files.internal("shaders/mellow.vsh"), Gdx.files.internal("shaders/mellow.fsh"));
		if (!mellowShader.isCompiled()) {
			throw new GdxRuntimeException(mellowShader.getLog());
		}
//		vignetteShader = new ShaderProgram(Gdx.files.internal("shaders/vignette.vsh"), Gdx.files.internal("shaders/vignette.fsh"));
//		if (!vignetteShader.isCompiled()) {
//			throw new GdxRuntimeException(vignetteShader.getLog());
//		}
//		vignetteShader.begin();
//		vignetteShader.setUniformf("u_resolution", 1280, 720);
//		vignetteShader.end();

		// Setup Frame Buffer
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, FRAMEBUFFER_WIDTH, FRAMEBUFFER_HEIGHT, false);
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

		renderer = new RenderingSystem();
		textRenderer = new TextRenderingSystem();
		engine.addSystem(renderer);
		engine.addSystem(textRenderer);

		// AI Systems
		engine.addSystem(new FollowingSystem());
		engine.addSystem(new WanderingSystem());
		engine.addSystem(new PathFollowingSystem());
		engine.addSystem(new FlowFollowSystem());
		engine.addSystem(new AttackingSystem());

		// State Machine System (transitions)
		engine.addSystem(StateMachineSystem.getInstance());

		// Other Systems
		engine.addSystem(new AbilitySystem());
		engine.addSystem(new TimerSystem());
		engine.addSystem(new DropSpawnSystem());
		engine.addSystem(new AnimationSystem());
		engine.addSystem(new JumpSystem());
		engine.addSystem(new ForceSystem());
		engine.addSystem(new DirectionSystem());
		engine.addSystem(new VelocitySystem());
		engine.addSystem(new SimpleVelocitySystem());
		engine.addSystem(new FlyingSystem());
		engine.addSystem(new GroundMovementSystem());
		engine.addSystem(new DropMovementSystem());
		engine.addSystem(new LadderMovementSystem());
		engine.addSystem(new WallSlideSystem());
		engine.addSystem(new PositioningSystem());
		engine.addSystem(new FacingSystem());
		engine.addSystem(new CameraSystem());
		engine.addSystem(new BarrierSystem());
		engine.addSystem(new CombustibleSystem());
		engine.addSystem(new RelativePositioningSystem());
		engine.addSystem(new SwingingSystem());
		engine.addSystem(new BlinkSystem());
		engine.addSystem(new DeathSystem());
		engine.addSystem(new RemovalSystem(world));

		// Setup and Load Level
		level = new Level("arena", world, worldCamera, batch);
		level.loadMap("map/ArenaMapv1.tmx");

		// Setup Nav Mesh
		EntityStats goblinStats = new EntityStats.Builder(EntityType.GOBLIN)
					.setAirSpeed(5.0f)
					.setRunSpeed(5.0f)
					.setClimbSpeed(5.0f)
					.setJumpForce(17.5f)
					.setHitBox(new Rectangle(0, 0, 15.0f * PPM_INV, 40 * PPM_INV))
					.build();
		playerMesh = NavMesh.createNavMesh(level, goblinStats);

		// Setup Flow Field
		flowField = new FlowField(level);
		
		// Spawn Player
		playerOne = EntityFactory.createPlayer(engine, level, input, world, level.getPlayerSpawnPoint().x, level.getPlayerSpawnPoint().y);
		engine.addEntity(playerOne);
		
		// Init Camera Position
		worldCamera.position.x = level.getPlayerSpawnPoint().x;
		worldCamera.position.y = level.getPlayerSpawnPoint().y;
		worldCamera.update();

		// Setup Camera
		cameraEntity = engine.createEntity();
		CameraComponent cameraComp = engine.createComponent(CameraComponent.class);
		cameraComp.camera = worldCamera;
		cameraComp.toFollow = playerOne;
		cameraComp.x = worldCamera.position.x;
		cameraComp.y = worldCamera.position.y;
		cameraComp.minX = 0f;
		cameraComp.minY = 0f;
		cameraComp.maxX = level.getWidth();
		cameraComp.maxY = level.getHeight();
		cameraComp.windowMinX = -2f;
		cameraComp.windowMinY = 0f;
		cameraComp.windowMaxX = 2f;
		cameraComp.windowMaxY = 0f;
		cameraEntity.add(cameraComp);
		engine.addEntity(cameraEntity);
		spawnFlyingEnemey();
	}

	private void spawnEnemy(Node node) {
		Entity enemy = EntityFactory.createAIPlayer(engine, level, new AIController(), playerOne, world, node.getCol() + 0.5f, node.getRow() + 1.0f, MathUtils.random(20, 50));
		PathFinder pathFinder = new PathFinder(playerMesh, node.getRow(), node.getCol(), node.getRow(), node.getCol());
		pathFinder.setHeuristic(new XYAlignHeuristic());
		enemy.add(engine.createComponent(PathComponent.class).set(pathFinder));
		enemies.add(enemy);
		engine.addEntity(enemy);
	}
	
	private void spawnFlyingEnemey(){
		int row = 0;
		int col = 0;
		do{
			row = MathUtils.random(0, level.getHeight());
		    col = MathUtils.random(0, level.getWidth());
		}while(level.isSolid(row, col) || row > 25);
		
		Entity enemy = EntityFactory.createFlyingEnemy(engine, world, level, flowField, col + 0.5f, row + 0.5f, playerOne, MathUtils.random(10, 25));
		enemies.add(enemy);
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
		worldCamera.update();
		batch.setProjectionMatrix(worldCamera.combined);
		engine.update(delta);

		world.step(delta, 6, 2);
		if (input.isJustPressed(Actions.SELECT)) {
			changePlayer();
		}

		Vector2 mousePos = Mouse.getWorldPosition(worldCamera);
		Node mouseNode = playerMesh.getNodeAt(mousePos.x, mousePos.y);
		if (mouseNode != null) {
			if (Mouse.isJustPressed()) {
				spawnEnemy(mouseNode);
			}
		} else if(!level.isSolid(mousePos.x, mousePos.y) && Mouse.isJustPressed()){
			Entity coin = EntityFactory.createCoin(engine, world, level, mousePos.x, mousePos.y, MathUtils.random(-5, 5), 0, MathUtils.random(1, 100));
			engine.addEntity(coin);
		}

		if (DebugInput.isPressed(DebugKeys.SPAWN)) {
			if(MathUtils.random() <= 0.5f){
				spawnFlyingEnemey();
			}else{
				Node spawnNode = playerMesh.getRandomNode();
				spawnEnemy(spawnNode);
			}
		}

		if (DebugInput.getCycle(DebugCycle.ZOOM) != previousZoom) {
			previousZoom = DebugInput.getCycle(DebugCycle.ZOOM);
			GameVars.resize(1 << previousZoom, worldCamera);
			resetFrameBuffer(FRAMEBUFFER_WIDTH, FRAMEBUFFER_HEIGHT);
		}
		
		BodyComponent bodyComp = Mappers.body.get(playerOne);
		flowField.setGoal((int)bodyComp.body.getPosition().y, (int)bodyComp.body.getPosition().x);
		
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
		for (Iterator<Entity> iter = enemies.iterator(); iter.hasNext();) {
			if (!EntityUtils.isValid(iter.next())) {
				iter.remove();
			}
		}
		
		// Respawn Player
//		if(!EntityUtils.isValid(playerOne)){
//			Node playerSpawn = playerMesh.getRandomNode();
//			playerOne = EntityFactory.createPlayer(engine, level, input, world, playerSpawn.getCol() + 0.5f, playerSpawn.getRow() + 1.5f);
//			engine.addEntity(playerOne);
//			index = -1;
//			changePlayer();
//		}
	}

	private void changePlayer() {
		CameraComponent cameraComp = Mappers.camera.get(cameraEntity);
		index++;
		if (index > enemies.size) index = 0;
		if (index == 0) {
			cameraComp.toFollow = playerOne;
		}
		else {
			cameraComp.toFollow = enemies.get(index - 1);
		}
	}

	@Override
	public void render() {
		Gdx.gl20.glEnable(GL20.GL_SCISSOR_TEST);
		HdpiUtils.glScissor(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		// Render Buffer
		frameBuffer.begin();

		Gdx.gl.glClearColor(0.1745f, 0.225f, 0.370f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		worldCamera.update();
		batch.setProjectionMatrix(worldCamera.combined);

		level.render();
		renderer.render(batch);
		if (DebugInput.isToggled(DebugToggle.SHOW_NAVMESH)) playerMesh.render(batch);
		if(DebugInput.isToggled(DebugToggle.SHOW_FLOW_FIELD)) flowField.render(batch);
		if (DebugInput.isToggled(DebugToggle.SHOW_PATH)) {
			for (Entity enemy : enemies) {
				if(Mappers.path.get(enemy) == null) continue;
				Mappers.path.get(enemy).pathFinder.render(batch);
			}
		}
		if (DebugInput.isToggled(DebugToggle.SHOW_HEALTH)) {
			renderHealth(batch, playerOne);
			for (Entity enemy : enemies) {
				renderHealth(batch, enemy);
			}
		}
		if (DebugInput.isToggled(DebugToggle.SHOW_HITBOXES)) b2dr.render(world, worldCamera.combined);
		if (DebugInput.isToggled(DebugToggle.SHOW_RANGE)){
			for(Entity enemy : enemies){
				if(Mappers.aism.get(enemy) != null){
					renderRange(batch, enemy);
				}
			}
		}

		frameBuffer.end();

		HdpiUtils.glViewport(UPSCALE / 2, UPSCALE / 2, SCREEN_WIDTH, SCREEN_HEIGHT);
		HdpiUtils.glScissor(UPSCALE / 2, UPSCALE / 2, SCREEN_WIDTH - UPSCALE, SCREEN_HEIGHT - UPSCALE);

		CameraComponent camera = Mappers.camera.get(cameraEntity);

//		mainBuffer.begin();
		batch.begin();
		batch.setShader(mellowShader);
		batch.setProjectionMatrix(hudCamera.combined);
		mellowShader.setUniformf("u_textureSizes", FRAMEBUFFER_WIDTH, FRAMEBUFFER_HEIGHT, UPSCALE, 0.0f);
		mellowShader.setUniformf("u_sampleProperties", camera.subpixelX, camera.subpixelY, camera.upscaleOffsetX, camera.upscaleOffsetY);
		batch.draw(frameBuffer.getColorBufferTexture(), 0, SCREEN_HEIGHT, SCREEN_WIDTH, -SCREEN_HEIGHT);
		batch.end();
		
//		batch.setShader(vignetteShader);
//		mainBuffer.end();
		
//		batch.begin();
//		batch.setShader(vignetteShader);
//		batch.setProjectionMatrix(hudCamera.combined);
//		batch.draw(mainBuffer.getColorBufferTexture(), 0, SCREEN_HEIGHT, SCREEN_WIDTH, -SCREEN_HEIGHT);
//		batch.end();

		batch.setShader(null);
		HdpiUtils.glScissor(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		batch.setProjectionMatrix(hudCamera.combined);
		if(DebugInput.isToggled(DebugToggle.SHOW_MAP_COORDS)){
			Vector2 mousePos = Mouse.getWorldPosition(worldCamera);
			Node mouseNode = playerMesh.getNodeAt(mousePos.x, mousePos.y);
			if(mouseNode != null){
				batch.begin();
				font.draw(batch, mouseNode.getRow() + ", " + mouseNode.getCol(), 10, 40);
				batch.end();
			}
		}
		textRenderer.render(batch, cameraEntity);
		renderHUD(batch, playerOne);

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
		
		TextureRegion healthEmpty = assets.getHUDElement(Assets.healthBarEmpty);
		TextureRegion healthFull = assets.getHUDElement(Assets.healthBarFull);
		TextureRegion staminaEmpty = assets.getHUDElement(Assets.staminaBarEmpty);
		TextureRegion staminaFull = assets.getHUDElement(Assets.staminaBarFull);
		TextureRegion coin = assets.getSpriteAnimation(Assets.goldCoin).getKeyFrame(0);

		float scale = 4.0f;

		batch.begin();
		// Abilities
		float abilityY = 150;
		float tallestIcon = 0.0f;
		for(AbilityType type : abilityComp.getAbilityMap().keys()){
			TextureRegion icon = abilityComp.getIcon(type);
			if(icon == null) continue;
			float iconWidth = icon.getRegionWidth();
			float iconHeight = icon.getRegionHeight();
			float x = GameVars.SCREEN_WIDTH * 0.5f - iconWidth * 0.5f;
			if(iconHeight > tallestIcon) tallestIcon = iconHeight;
			batch.draw(icon, x, abilityY, iconWidth * 0.5f, iconHeight * 0.5f, iconWidth, iconHeight, scale, scale, 0.0f);
			if(!abilityComp.isAbilityReady(type)){
				int timeLeft = (int)(abilityComp.getRechargeTime(type) - abilityComp.getElapsed(type) + 0.5f);
				font.setColor(Color.WHITE);
				font.draw(batch, "" + timeLeft, x - 4.0f, abilityY + 8.0f);
			}
		}
		
		// Health
		float healthEmptyWidth = healthEmpty.getRegionWidth();
		float healthEmptyHeight = healthEmpty.getRegionHeight();
		float healthY = abilityY - (tallestIcon + healthEmptyHeight) * scale;

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

		float coinY = staminaY - coin.getRegionHeight() * scale - 4 * scale;
		float coinWidth = coin.getRegionWidth();
		float coinHeight = coin.getRegionHeight();

		batch.draw(healthEmpty, GameVars.SCREEN_WIDTH * 0.5f - healthEmptyWidth * 0.5f, healthY, healthEmptyWidth * 0.5f, healthEmptyHeight * 0.5f, healthEmptyWidth, healthEmptyHeight, scale, scale, 0.0f);
		batch.draw(healthFull.getTexture(), GameVars.SCREEN_WIDTH * 0.5f - healthEmptyWidth * 0.5f, healthY, healthEmptyWidth * 0.5f, healthEmptyHeight * 0.5f, healthBarWidth, healthEmptyHeight, scale, scale, 0.0f, healthSrcX, healthSrcY, healthBarWidth, (int) (healthEmptyHeight), false, false);
		batch.draw(staminaEmpty, GameVars.SCREEN_WIDTH * 0.5f - staminaEmptyWidth * 0.5f, staminaY, staminaEmptyWidth * 0.5f, staminaEmptyHeight * 0.5f, staminaEmptyWidth, staminaEmptyHeight, scale, scale, 0.0f);
		batch.draw(staminaFull.getTexture(), GameVars.SCREEN_WIDTH * 0.5f - staminaEmptyWidth * 0.5f, staminaY, staminaEmptyWidth * 0.5f, staminaEmptyHeight * 0.5f, staminaBarWidth, staminaEmptyHeight, scale, scale, 0.0f, staminaSrcX, staminaSrcY, staminaBarWidth, (int) (staminaEmptyHeight), false, false);
		batch.draw(coin, GameVars.SCREEN_WIDTH * 0.5f - coin.getRegionWidth() * scale - 20, coinY, coinWidth * 0.5f, coinHeight * 0.5f, coinWidth, coinHeight, scale, scale, 0.0f);
		font.setColor(Color.WHITE);
		font.draw(batch, "" + moneyComp.money, GameVars.SCREEN_WIDTH * 0.5f - 10, coinY + 12);
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

		sRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		sRenderer.begin(ShapeType.Filled);
		// sRenderer.setColor(Color.BLACK);
		// sRenderer.rect(x, y, width, height);
		sRenderer.setColor(Color.WHITE);
		sRenderer.rect(x, y, width, height);
		sRenderer.setColor(Color.valueOf("e43b44"));
		sRenderer.rect(x, y, width * (healthComp.health / healthComp.maxHealth), height);
		sRenderer.end();
	}
	
	public void renderRange(SpriteBatch batch, Entity entity) {
		sRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		sRenderer.begin(ShapeType.Line);
		AIStateMachineComponent aismComp = Mappers.aism.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		LevelComponent levelComp = Mappers.level.get(entity);

		TransitionObject obj = aismComp.aism.getCurrentState().getFirstData(Transition.RANGE);
		RangeTransitionData rtd = (RangeTransitionData) obj.data;
		if (rtd == null || rtd.target == null || !EntityUtils.isValid(rtd.target)) return;
		BodyComponent otherBody = Mappers.body.get(rtd.target);

		Body b1 = bodyComp.body;
		Body b2 = otherBody.body;

		float x1 = b1.getPosition().x;
		float y1 = b1.getPosition().y;
		float x2 = b2.getPosition().x;
		float y2 = b2.getPosition().y;
		float r = rtd.distance * rtd.distance;
		
		float d = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
		
		Color color = new Color(1, 0, 0, 1);
		if(d <= r){
			color = new Color(0, 1, 0, 1);
		}
		sRenderer.setColor(color);
		sRenderer.circle(x1, y1, rtd.distance, 32);
		
		color = new Color(1, 0, 0, 1);
		if(levelComp.level.performRayTrace(x1, y1, x2, y2)){
			color = new Color(0, 1, 0, 1);
		}
		sRenderer.setColor(color);
		sRenderer.line(x1, y1, x2, y2);
		sRenderer.end();
	}

	@Override
	protected void destroy() {

	}

	@Override
	public void dispose() {
		super.dispose();
		sRenderer.dispose();
		mellowShader.dispose();
		frameBuffer.dispose();
	}
}
