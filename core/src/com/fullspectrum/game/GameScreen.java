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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.fullspectrum.component.AnimationComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.CameraComponent;
import com.fullspectrum.component.DirectionComponent;
import com.fullspectrum.component.FSMComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.GroundMovementComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.JumpComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.RenderComponent;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.component.TextureComponent;
import com.fullspectrum.component.VelocityComponent;
import com.fullspectrum.entity.player.Player;
import com.fullspectrum.entity.player.PlayerAnim;
import com.fullspectrum.fsm.EntityState;
import com.fullspectrum.fsm.EntityStateMachine;
import com.fullspectrum.fsm.PlayerStates;
import com.fullspectrum.fsm.transition.AnimationFinishedTransition;
import com.fullspectrum.fsm.transition.FallingTransition;
import com.fullspectrum.fsm.transition.InputTransition;
import com.fullspectrum.fsm.transition.InputTransitionData;
import com.fullspectrum.fsm.transition.InputTrigger;
import com.fullspectrum.fsm.transition.LandedTransition;
import com.fullspectrum.fsm.transition.RandomTransition;
import com.fullspectrum.fsm.transition.RandomTransitionData;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionTag;
import com.fullspectrum.fsm.transition.InputTransitionData.Type;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.GameInput;
import com.fullspectrum.level.Level;
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
	private Entity player;
	private Entity cameraEntity;

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
		world = new World(new Vector2(0, -23.0f), true);
		
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
		
		// Setup Player
		player = new Entity();
		player.add(new PositionComponent(5, 5));
		player.add(new VelocityComponent());
		player.add(new RenderComponent());
		player.add(new TextureComponent(Player.animations.get(PlayerAnim.IDLE).getKeyFrame(0)));
		player.add(new InputComponent(input));
		player.add(new FacingComponent());
		player.add(new AnimationComponent()
			.addAnimation(PlayerAnim.IDLE, Player.animations.get(PlayerAnim.IDLE))
			.addAnimation(PlayerAnim.RUNNING, Player.animations.get(PlayerAnim.RUNNING))
			.addAnimation(PlayerAnim.JUMP, Player.animations.get(PlayerAnim.JUMP))
			.addAnimation(PlayerAnim.FALLING, Player.animations.get(PlayerAnim.FALLING))
			.addAnimation(PlayerAnim.RANDOM_IDLE, Player.animations.get(PlayerAnim.RANDOM_IDLE))
			.addAnimation(PlayerAnim.RISE, Player.animations.get(PlayerAnim.RISE))
			.addAnimation(PlayerAnim.JUMP_APEX, Player.animations.get(PlayerAnim.JUMP_APEX)));
		
		EntityStateMachine fsm = new EntityStateMachine(player);
		fsm.setDebugName("Entity State Machine");
		EntityState runningState = fsm.createState(PlayerStates.RUNNING)
			.add(new SpeedComponent(8.0f))
			.add(new DirectionComponent())
			.add(new GroundMovementComponent())
			.addAnimation(PlayerAnim.RUNNING);
		runningState.addTag(TransitionTag.GROUND_STATE);
			
		RandomTransitionData rtd = new RandomTransitionData();
		rtd.waitTime = 4.0f;
		rtd.probability = 1.0f;
		
		EntityState idleState = fsm.createState(PlayerStates.IDLING)
			.add(new SpeedComponent(0.0f))
			.add(new DirectionComponent())
			.add(new GroundMovementComponent())
			.addAnimation(PlayerAnim.IDLE)
			.addAnimation(PlayerAnim.RANDOM_IDLE)
			.addAnimTransition(PlayerAnim.IDLE, Transition.RANDOM, rtd, PlayerAnim.RANDOM_IDLE)
			.addAnimTransition(PlayerAnim.RANDOM_IDLE, Transition.ANIMATION_FINISHED, PlayerAnim.IDLE);
		idleState.addTag(TransitionTag.GROUND_STATE);
		
		EntityState fallingState = fsm.createState(PlayerStates.FALLING)
			.add(new SpeedComponent(8.0f))
			.add(new DirectionComponent())
			.add(new GroundMovementComponent())
			.addAnimation(PlayerAnim.JUMP_APEX)
			.addAnimation(PlayerAnim.FALLING)
			.addAnimTransition(PlayerAnim.JUMP_APEX, Transition.ANIMATION_FINISHED, PlayerAnim.FALLING);
		fallingState.addTag(TransitionTag.AIR_STATE);
		
		EntityState jumpingState = fsm.createState(PlayerStates.JUMPING)
			.add(new SpeedComponent(8.0f))
			.add(new DirectionComponent())
			.add(new GroundMovementComponent())
			.add(new JumpComponent(1000.0f))
			.addAnimation(PlayerAnim.JUMP)
			.addAnimation(PlayerAnim.RISE)
			.addAnimTransition(PlayerAnim.JUMP, Transition.ANIMATION_FINISHED, PlayerAnim.RISE);
		jumpingState.addTag(TransitionTag.AIR_STATE);
		
		InputTransitionData runningData = new InputTransitionData(Type.ONLY_ONE, true);
		runningData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		runningData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));
		
		InputTransitionData jumpData = new InputTransitionData(Type.ALL, true);
		jumpData.triggers.add(new InputTrigger(Actions.JUMP, true));
		
		InputTransitionData idleData = new InputTransitionData(Type.ALL, false);
		idleData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		idleData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));
		
		InputTransitionData bothData = new InputTransitionData(Type.ALL, true);
		bothData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		bothData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));
		
		fsm.addTransition(TransitionTag.GROUND_STATE, Transition.FALLING, PlayerStates.FALLING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(PlayerStates.RUNNING), Transition.INPUT, runningData, PlayerStates.RUNNING);
		fsm.addTransition(TransitionTag.GROUND_STATE, Transition.INPUT, jumpData, PlayerStates.JUMPING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(PlayerStates.FALLING), Transition.FALLING, PlayerStates.FALLING);
		fsm.addTransition(PlayerStates.FALLING, Transition.LANDED, PlayerStates.IDLING);
		fsm.addTransition(PlayerStates.RUNNING, Transition.INPUT, idleData, PlayerStates.IDLING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(PlayerStates.IDLING), Transition.INPUT, bothData, PlayerStates.IDLING);
		
		System.out.print(fsm.printTransitions());
		
		fsm.changeState(PlayerStates.IDLING);
		
		player.add(new FSMComponent(fsm));
		
		// Player physics
		Body body;
		BodyDef bdef = new BodyDef();
		bdef.active = true;
		bdef.position.set(10.0f, 10.0f);
		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);

		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(20 * PPM_INV * 0.5f, 40 * PPM_INV * 0.5f, new Vector2(0f, 0f), 0);
		fdef.shape = shape;

		body.createFixture(fdef);

//		// Feet
//		CircleShape cshape = new CircleShape();
//		cshape.setRadius(0.1f);
//		cshape.setPosition(new Vector2(0.59f, -1.35f));
//		fdef.shape = cshape;
//		fdef.friction = 1.0f;
//		body.createFixture(fdef);
//
//		cshape.setPosition(new Vector2(-0.4f, -1.35f));
//		body.createFixture(fdef);
		
		player.add(new BodyComponent(body));
		engine.addEntity(player);
		
		// Setup and Load Level
		level = new Level(world, worldCamera, batch);
//		level.setPlayer(player);
		level.loadMap("map/TestMap2.tmx");
		
		// Setup Camera
		cameraEntity = new Entity();
		CameraComponent cameraComp = new CameraComponent(worldCamera, player);
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
	}

	@Override
	public void render() {
		Gdx.gl20.glEnable(GL20.GL_SCISSOR_TEST);
		HdpiUtils.glScissor(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		frameBuffer.begin();
		Gdx.gl.glClearColor(0.4f, 0.4f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		worldCamera.update();
		batch.setProjectionMatrix(worldCamera.combined);
		level.render();
		renderer.render(batch);
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
