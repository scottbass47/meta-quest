package com.fullspectrum.game;

import static com.fullspectrum.game.GameVars.R_WORLD_HEIGHT;
import static com.fullspectrum.game.GameVars.R_WORLD_WIDTH;

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
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.RenderComponent;
import com.fullspectrum.component.TextureComponent;
import com.fullspectrum.entity.player.Player;
import com.fullspectrum.entity.player.PlayerAnim;
import com.fullspectrum.input.GameInput;
import com.fullspectrum.level.Level;
import com.fullspectrum.systems.PositioningSystem;
import com.fullspectrum.systems.RenderingSystem;

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
		engine.addSystem(new PositioningSystem());
		
		
		// Setup Player
		player = new Entity();
		player.add(new PositionComponent(5, 5));
		player.add(new RenderComponent());
		player.add(new TextureComponent(Player.animations.get(PlayerAnim.RUNNING).getKeyFrame(0)));
		
		// Player physics
		Body body;
		BodyDef bdef = new BodyDef();
		bdef.active = true;
		bdef.position.set(10.0f, 10.0f);
		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);

		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox((15.0f / 8.0f) * 0.3f, 3f * 0.4f, new Vector2(0.1f, -0.1f), 0);
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
		
//		world.step(delta, 6, 2);
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
