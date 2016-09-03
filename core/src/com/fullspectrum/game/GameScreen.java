package com.fullspectrum.game;

import static com.fullspectrum.game.GameVars.*;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.component.AnimationComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.DirectionComponent;
import com.fullspectrum.component.FSMComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.GroundMovementComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.JumpComponent;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.RenderComponent;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.component.TextureComponent;
import com.fullspectrum.component.VelocityComponent;
import com.fullspectrum.entity.player.Player;
import com.fullspectrum.entity.player.PlayerAnim;
import com.fullspectrum.fsm.EntityStateMachine;
import com.fullspectrum.fsm.PlayerStates;
import com.fullspectrum.fsm.transition.AnimationFinishedTransition;
import com.fullspectrum.fsm.transition.FallingTransition;
import com.fullspectrum.fsm.transition.InputTransition;
import com.fullspectrum.fsm.transition.InputTransitionData;
import com.fullspectrum.fsm.transition.LandedTransition;
import com.fullspectrum.fsm.transition.RandomTransition;
import com.fullspectrum.fsm.transition.RandomTransitionData;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionTag;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.GameInput;
import com.fullspectrum.level.Level;
import com.fullspectrum.systems.AnimationSystem;
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

	// Tile Map
	private Level level;

	// Box2D
	private World world;

	public GameScreen(OrthographicCamera worldCamera, OrthographicCamera hudCamera, Game game, ArrayMap<ScreenState, Screen> screens, GameInput input) {
		super(worldCamera, hudCamera, game, screens, input);
		sRenderer = new ShapeRenderer();
		b2dr = new Box2DDebugRenderer();
		world = new World(new Vector2(0, -23.0f), true);
		
		// Setup Ashley
		engine = new Engine();
		renderer = new RenderingSystem(batch);
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
			.addAnimation(PlayerAnim.RISE, Player.animations.get(PlayerAnim.RISE)));
		
		EntityStateMachine fsm = new EntityStateMachine(player);
		fsm.createState(PlayerStates.RUNNING)
			.add(new SpeedComponent(10.0f))
			.add(new DirectionComponent())
			.add(new GroundMovementComponent())
			.addTag(TransitionTag.GROUND_STATE)
			.withAnimation(PlayerAnim.RUNNING);

		fsm.createState(PlayerStates.IDLING)
			.add(new SpeedComponent(0.0f))
			.add(new DirectionComponent())
			.add(new GroundMovementComponent())
			.addTag(TransitionTag.GROUND_STATE)
			.withAnimation(PlayerAnim.IDLE);
		
		fsm.createState(PlayerStates.FALLING)
			.add(new SpeedComponent(10.0f))
			.add(new DirectionComponent())
			.add(new GroundMovementComponent())
			.addTag(TransitionTag.AIR_STATE)
			.withAnimation(PlayerAnim.RISE);
		
		fsm.createState(PlayerStates.JUMPING)
			.add(new SpeedComponent(10.0f))
			.add(new DirectionComponent())
			.add(new GroundMovementComponent())
			.add(new JumpComponent(1000.0f))
			.addTag(TransitionTag.AIR_STATE)
			.withAnimation(PlayerAnim.JUMP);
		
		fsm.createState(PlayerStates.RISING)
			.add(new SpeedComponent(10.0f))
			.add(new DirectionComponent())
			.add(new GroundMovementComponent())
			.addTag(TransitionTag.AIR_STATE)
			.withAnimation(PlayerAnim.RISE);
		
		fsm.createState(PlayerStates.RANDOM_IDLING)
			.add(new SpeedComponent(0.0f))
			.add(new DirectionComponent())
			.add(new GroundMovementComponent())
			.addTag(TransitionTag.GROUND_STATE)
			.withAnimation(PlayerAnim.RANDOM_IDLE);
		
		RandomTransitionData rtd = new RandomTransitionData();
		rtd.waitTime = 4.0f;
		rtd.probability = 1.0f;
		
		InputTransitionData runningData = new InputTransitionData();
		runningData.triggers.add(Actions.MOVE_LEFT);
		runningData.triggers.add(Actions.MOVE_RIGHT);
		
		InputTransitionData jumpData = new InputTransitionData();
		jumpData.triggers.add(Actions.JUMP);
		
		InputTransitionData idleData = new InputTransitionData();
		idleData.triggers.add(Actions.MOVE_LEFT);
		idleData.triggers.add(Actions.MOVE_RIGHT);
		idleData.all = true;
		idleData.pressed = false;
		
		fsm.addTransition(TransitionTag.GROUND_STATE, Transition.FALLING, PlayerStates.FALLING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(PlayerStates.RUNNING), Transition.INPUT, runningData, PlayerStates.RUNNING);
		fsm.addTransition(TransitionTag.GROUND_STATE, Transition.INPUT, jumpData, PlayerStates.JUMPING);
		fsm.addTransition(PlayerStates.JUMPING, Transition.ANIMATION_FINISHED, PlayerStates.RISING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(PlayerStates.FALLING), Transition.FALLING, PlayerStates.FALLING);
		fsm.addTransition(PlayerStates.FALLING, Transition.LANDED, PlayerStates.IDLING);
		fsm.addTransition(PlayerStates.IDLING, Transition.RANDOM, rtd, PlayerStates.RANDOM_IDLING);
		fsm.addTransition(PlayerStates.RANDOM_IDLING, Transition.ANIMATION_FINISHED, PlayerStates.IDLING);
		fsm.addTransition(PlayerStates.RUNNING, Transition.INPUT, idleData, PlayerStates.IDLING);
		
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
		shape.setAsBox(20 / PPM * 0.5f, 32 / PPM * 0.5f, new Vector2(0f, 0f), 0);
		fdef.shape = shape;

		body.createFixture(fdef);

		// Feet
		CircleShape cshape = new CircleShape();
		cshape.setRadius(0.1f);
		cshape.setPosition(new Vector2(0.59f, -1.35f));
		fdef.shape = cshape;
		fdef.friction = 1.0f;
		body.createFixture(fdef);

		cshape.setPosition(new Vector2(-0.4f, -1.35f));
		body.createFixture(fdef);
		
		player.add(new BodyComponent(body));
		engine.addEntity(player);
		
		// Setup and Load Level
		level = new Level(world, worldCamera, batch);
//		level.setPlayer(player);
		level.loadMap("map/Test.tmx");
	}

	@Override
	protected void init() {
		
	}

	@Override
	public void update(float delta) {
		worldCamera.position.x = R_WORLD_WIDTH * 0.5f;
		worldCamera.position.y = R_WORLD_HEIGHT * 0.5f;
		worldCamera.update();
		batch.setProjectionMatrix(worldCamera.combined);
		engine.update(delta);
		
		world.step(delta, 6, 2);
//		level.update(delta);
	}

	@Override
	public void render() {
		renderer.render();
		b2dr.render(world, worldCamera.combined);
//		level.render();
	}

	@Override
	protected void destroy() {

	}

	@Override
	public void dispose() {
		super.dispose();
		sRenderer.dispose();
//		player.dispose();
	}
}
