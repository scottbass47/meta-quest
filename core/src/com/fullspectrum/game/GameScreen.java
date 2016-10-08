package com.fullspectrum.game;

import static com.fullspectrum.game.GameVars.FRAMEBUFFER_HEIGHT;
import static com.fullspectrum.game.GameVars.FRAMEBUFFER_WIDTH;
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.fullspectrum.component.CameraComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.entity.EntityFactory;
import com.fullspectrum.fsm.PlayerStates;
import com.fullspectrum.fsm.transition.AnimationFinishedTransition;
import com.fullspectrum.fsm.transition.FallingTransition;
import com.fullspectrum.fsm.transition.InputTransition;
import com.fullspectrum.fsm.transition.LandedTransition;
import com.fullspectrum.fsm.transition.RandomTransition;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.GameInput;
import com.fullspectrum.level.Level;
import com.fullspectrum.level.NavMesh;
import com.fullspectrum.systems.AnimationSystem;
import com.fullspectrum.systems.CameraSystem;
import com.fullspectrum.systems.DirectionSystem;
import com.fullspectrum.systems.FacingSystem;
import com.fullspectrum.systems.GroundMovementSystem;
import com.fullspectrum.systems.JumpSystem;
import com.fullspectrum.systems.PositioningSystem;
import com.fullspectrum.systems.RenderingSystem;
import com.fullspectrum.systems.VelocitySystem;

public class GameScreen extends AbstractScreen {

	// Debug Graphics
	private ShapeRenderer sRenderer;
	private Box2DDebugRenderer b2dr;

	// Ashley
	private Engine engine;
	private RenderingSystem renderer;

	// Player
	private Entity playerOne;
	private Entity playerTwo;
	private Entity cameraEntity;
	private boolean onPlayerOne = true;
	private NavMesh playerMesh;

	// Tile Map
	private Level level;

	// Box2D
	private World world;
	
	// Rendering
	private FrameBuffer frameBuffer;
	private ShaderProgram mellowShader;

	public GameScreen(OrthographicCamera worldCamera, OrthographicCamera hudCamera, Game game, ArrayMap<ScreenState, Screen> screens, GameInput input) {
		super(worldCamera, hudCamera, game, screens, input);
		sRenderer = new ShapeRenderer();
		b2dr = new Box2DDebugRenderer();
		world = new World(new Vector2(0, GameVars.GRAVITY), true);
		
		// Setup Shader
		mellowShader = new ShaderProgram(
				Gdx.files.internal("shaders/mellow.vsh"),
				Gdx.files.internal("shaders/mellow.fsh"));
		if (!mellowShader.isCompiled()) {
			throw new GdxRuntimeException(mellowShader.getLog());
		}
		
		// Setup Frame Buffer
		frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, FRAMEBUFFER_WIDTH, FRAMEBUFFER_HEIGHT, false);
		frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		
		// Setup Ashley
		engine = new Engine();
		renderer = new RenderingSystem();
		engine.addSystem(renderer);
		engine.addSystem(RandomTransition.getInstance());
		engine.addSystem(AnimationFinishedTransition.getInstance());
		engine.addSystem(FallingTransition.getInstance());
		engine.addSystem(LandedTransition.getInstance());
		engine.addSystem(InputTransition.getInstance());
		engine.addSystem(new AnimationSystem());
		engine.addSystem(new JumpSystem());
		engine.addSystem(new DirectionSystem());
		engine.addSystem(new VelocitySystem());
		engine.addSystem(new GroundMovementSystem());
		engine.addSystem(new PositioningSystem());
		engine.addSystem(new FacingSystem());
		engine.addSystem(new CameraSystem());
		
		playerOne = EntityFactory.createPlayer(input, world, 10.0f, 10.0f);
		playerTwo = EntityFactory.createPlayer(input, world, 10.0f, 13.0f);
		engine.addEntity(playerOne);
		engine.addEntity(playerTwo);
		
		// Setup and Load Level
		level = new Level(world, worldCamera, batch);
//		level.setPlayer(player);
		level.loadMap("map/TestMap2.tmx");
		playerMesh = NavMesh.createNavMesh(playerOne, level, PlayerStates.RUNNING, PlayerStates.JUMPING);
		
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
		cameraComp.camera.zoom = 1f;
		cameraEntity.add(cameraComp);
		engine.addEntity(cameraEntity);
		
		changePlayer(true);
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
		// level.update(delta);
		if(input.isJustPressed(Actions.SELECT)){
			changePlayer(!onPlayerOne);
		}
	}
	
	private void changePlayer(boolean one){
		CameraComponent cameraComp = Mappers.camera.get(cameraEntity);
		cameraComp.toFollow = one ? playerOne : playerTwo;
		InputComponent oneInputComp = Mappers.input.get(playerOne);
		InputComponent twoInputComp = Mappers.input.get(playerTwo);
		oneInputComp.enabled = one;
		twoInputComp.enabled = !one;
		onPlayerOne = one;
	}

	@Override
	public void render() {
		Gdx.gl20.glEnable(GL20.GL_SCISSOR_TEST);
		HdpiUtils.glScissor(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		// Render Buffer
		frameBuffer.begin();
		
		Gdx.gl.glClearColor(0.4f, 0.4f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		worldCamera.update();
		batch.setProjectionMatrix(worldCamera.combined);
		
		level.render();
		renderer.render(batch);
		playerMesh.render(batch);
		
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
//		b2dr.render(world, worldCamera.combined);
		
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
		// player.dispose();
	}
}
