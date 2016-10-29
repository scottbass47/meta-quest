package com.fullspectrum.game;

import static com.fullspectrum.game.GameVars.FRAMEBUFFER_HEIGHT;
import static com.fullspectrum.game.GameVars.FRAMEBUFFER_WIDTH;
import static com.fullspectrum.game.GameVars.PPM_INV;
import static com.fullspectrum.game.GameVars.SCREEN_HEIGHT;
import static com.fullspectrum.game.GameVars.SCREEN_WIDTH;
import static com.fullspectrum.game.GameVars.UPSCALE;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.fullspectrum.ai.AIController;
import com.fullspectrum.ai.PathFinder;
import com.fullspectrum.component.CameraComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PathComponent;
import com.fullspectrum.debug.DebugCycle;
import com.fullspectrum.debug.DebugInput;
import com.fullspectrum.debug.DebugKeys;
import com.fullspectrum.debug.DebugToggle;
import com.fullspectrum.entity.EntityFactory;
import com.fullspectrum.entity.player.PlayerAssets;
import com.fullspectrum.fsm.system.DivingSystem;
import com.fullspectrum.fsm.system.FallingSystem;
import com.fullspectrum.fsm.system.IdlingSystem;
import com.fullspectrum.fsm.system.JumpingSystem;
import com.fullspectrum.fsm.system.RunningSystem;
import com.fullspectrum.fsm.transition.AnimationFinishedTransition;
import com.fullspectrum.fsm.transition.FallingTransition;
import com.fullspectrum.fsm.transition.InputTransition;
import com.fullspectrum.fsm.transition.LandedTransition;
import com.fullspectrum.fsm.transition.RandomTransition;
import com.fullspectrum.fsm.transition.RangeTransition;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.GameInput;
import com.fullspectrum.input.Mouse;
import com.fullspectrum.level.Level;
import com.fullspectrum.level.NavMesh;
import com.fullspectrum.level.Node;
import com.fullspectrum.physics.WorldCollision;
import com.fullspectrum.systems.AnimationSystem;
import com.fullspectrum.systems.CameraSystem;
import com.fullspectrum.systems.DirectionSystem;
import com.fullspectrum.systems.FacingSystem;
import com.fullspectrum.systems.FollowingSystem;
import com.fullspectrum.systems.GroundMovementSystem;
import com.fullspectrum.systems.JumpSystem;
import com.fullspectrum.systems.PathFollowingSystem;
import com.fullspectrum.systems.PositioningSystem;
import com.fullspectrum.systems.RemovalSystem;
import com.fullspectrum.systems.RenderingSystem;
import com.fullspectrum.systems.VelocitySystem;
import com.fullspectrum.systems.WanderingSystem;

public class GameScreen extends AbstractScreen {

	// Debug Graphics
	private ShapeRenderer sRenderer;
	private Box2DDebugRenderer b2dr;

	// Ashley
	private Engine engine;
	private RenderingSystem renderer;

	// Player
	private Entity playerOne;
	private Array<Entity> enemies;
	private Entity cameraEntity;
	private NavMesh playerMesh;
	private int index = 0;

	// Tile Map
	private Level level;

	// Box2D
	private World world;

	// Rendering
	private FrameBuffer frameBuffer;
	private ShaderProgram mellowShader;
	private int previousZoom = 0;

	public GameScreen(OrthographicCamera worldCamera, OrthographicCamera hudCamera, Game game, ArrayMap<ScreenState, Screen> screens, GameInput input) {
		super(worldCamera, hudCamera, game, screens, input);
		sRenderer = new ShapeRenderer();
		b2dr = new Box2DDebugRenderer();
		world = new World(new Vector2(0, GameVars.GRAVITY), true);
		world.setContactListener(new WorldCollision());
		enemies = new Array<Entity>();

		// Setup Shader
		mellowShader = new ShaderProgram(Gdx.files.internal("shaders/mellow.vsh"), Gdx.files.internal("shaders/mellow.fsh"));
		if (!mellowShader.isCompiled()) {
			throw new GdxRuntimeException(mellowShader.getLog());
		}

		// Setup Frame Buffer
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, FRAMEBUFFER_WIDTH, FRAMEBUFFER_HEIGHT, false);
		frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

		// Setup Ashley
		engine = new Engine();
		renderer = new RenderingSystem();
		engine.addSystem(renderer);

		// AI Systems
		engine.addSystem(new FollowingSystem());
		engine.addSystem(new WanderingSystem());
		engine.addSystem(new PathFollowingSystem());

		// Transition Systems
		engine.addSystem(RangeTransition.getInstance());
		engine.addSystem(RandomTransition.getInstance());
		engine.addSystem(AnimationFinishedTransition.getInstance());
		engine.addSystem(FallingTransition.getInstance());
		engine.addSystem(LandedTransition.getInstance());
		engine.addSystem(InputTransition.getInstance());

		// State Systems
		engine.addSystem(IdlingSystem.getInstance());
		engine.addSystem(RunningSystem.getInstance());
		engine.addSystem(JumpingSystem.getInstance());
		engine.addSystem(FallingSystem.getInstance());
		engine.addSystem(DivingSystem.getInstance());

		// Other Systems
		engine.addSystem(new AnimationSystem());
		engine.addSystem(new JumpSystem());
		engine.addSystem(new DirectionSystem());
		engine.addSystem(new VelocitySystem());
		engine.addSystem(new GroundMovementSystem());
		engine.addSystem(new PositioningSystem());
		engine.addSystem(new FacingSystem());
		engine.addSystem(new CameraSystem());
		engine.addSystem(new RemovalSystem(world));

		// Setup and Load Level
		level = new Level(world, worldCamera, batch);
		level.loadMap("map/ArenaMapv1.tmx");

		// Setup Nav Mesh
		playerMesh = NavMesh.createNavMesh(level, new Rectangle(0, 0, 15.0f * PPM_INV, 40 * PPM_INV), 5.0f, 17.5f, 5.0f);

		// Spawn Player
		Node playerSpawn = playerMesh.getRandomNode();
		playerOne = EntityFactory.createPlayer(level, input, world, playerSpawn.getCol() + 0.5f, playerSpawn.getRow() + 1.5f);
		engine.addEntity(playerOne);

		// Spawn Enemy
		//spawnEnemy(playerMesh.getRandomNode());

		// Setup Camera
		cameraEntity = new Entity();
		CameraComponent cameraComp = new CameraComponent(worldCamera, playerOne);
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
	}

	private void spawnEnemy(Node node) {
		Entity enemy = EntityFactory.createAIPlayer(level, new AIController(), playerOne, world, node.getCol() + 0.5f, node.getRow() + 1.0f);
		PathFinder pathFinder = new PathFinder(playerMesh, node.getRow(), node.getCol(), node.getRow(), node.getCol());
		enemy.add(new PathComponent(pathFinder));
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
		}

		if (DebugInput.isJustPressed(DebugKeys.SPAWN)) {
			Node spawnNode = playerMesh.getRandomNode();
			spawnEnemy(spawnNode);
		}

		if (DebugInput.getCycle(DebugCycle.ZOOM) != previousZoom) {
			previousZoom = DebugInput.getCycle(DebugCycle.ZOOM);
			GameVars.resize(1 << previousZoom, worldCamera);
			resetFrameBuffer(FRAMEBUFFER_WIDTH, FRAMEBUFFER_HEIGHT);
		}
	}

	private void changePlayer() {
		CameraComponent cameraComp = Mappers.camera.get(cameraEntity);
		index++;
		if (index > enemies.size) index = 0;
		if (index == 0) {
			cameraComp.toFollow = playerOne;
		} else {
			cameraComp.toFollow = enemies.get(index - 1);
		}
	}

	@Override
	public void render() {
		Gdx.gl20.glEnable(GL20.GL_SCISSOR_TEST);
		HdpiUtils.glScissor(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		// Render Buffer
		frameBuffer.begin();

		Gdx.gl.glClearColor(0.4f, 0.4f, 0.8f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		worldCamera.update();
		batch.setProjectionMatrix(worldCamera.combined);

		level.render();
		renderer.render(batch);
		if (DebugInput.isToggled(DebugToggle.SHOW_NAVMESH)) playerMesh.render(batch);
		if (DebugInput.isToggled(DebugToggle.SHOW_PATH)) {
			for (Entity enemy : enemies) {
				Mappers.path.get(enemy).pathFinder.render(batch);
			}
		}
		if (DebugInput.isToggled(DebugToggle.SHOW_HITBOXES)) b2dr.render(world, worldCamera.combined);
		if (DebugInput.isToggled(DebugToggle.SHOW_RANGE)) RangeTransition.getInstance().render(batch);

		frameBuffer.end();

		HdpiUtils.glViewport(UPSCALE / 2, UPSCALE / 2, SCREEN_WIDTH, SCREEN_HEIGHT);
		HdpiUtils.glScissor(UPSCALE / 2, UPSCALE / 2, SCREEN_WIDTH - UPSCALE, SCREEN_HEIGHT - UPSCALE);

		CameraComponent camera = Mappers.camera.get(cameraEntity);

		batch.begin();
		batch.setShader(mellowShader);
		batch.setProjectionMatrix(hudCamera.combined);
		mellowShader.setUniformf("u_textureSizes", FRAMEBUFFER_WIDTH, FRAMEBUFFER_HEIGHT, UPSCALE, 0.0f);
		mellowShader.setUniformf("u_sampleProperties", camera.subpixelX, camera.subpixelY, camera.upscaleOffsetX, camera.upscaleOffsetY);
		batch.draw(frameBuffer.getColorBufferTexture(), 0, SCREEN_HEIGHT, SCREEN_WIDTH, -SCREEN_HEIGHT);
		batch.end();

		batch.setShader(null);
		HdpiUtils.glScissor(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

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

	@Override
	protected void destroy() {

	}

	@Override
	public void dispose() {
		super.dispose();
		sRenderer.dispose();
		mellowShader.dispose();
		frameBuffer.dispose();
		PlayerAssets.dispose();
	}
}
