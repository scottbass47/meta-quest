package com.fullspectrum.game;

import static com.fullspectrum.game.GameVars.BIT_BALL;
import static com.fullspectrum.game.GameVars.BIT_BOX;
import static com.fullspectrum.game.GameVars.BIT_GROUND;
import static com.fullspectrum.game.GameVars.R_WORLD_HEIGHT;
import static com.fullspectrum.game.GameVars.R_WORLD_WIDTH;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.entity.player.Player;
import com.fullspectrum.input.GameInput;
import com.fullspectrum.level.Level;

public class GameScreen extends AbstractScreen {

	// Debug Graphics
	private ShapeRenderer sRenderer;

	// Player
	private Player player;

	// Tile Map
	private Level level;

	// Box2D
	private World world;

	public GameScreen(OrthographicCamera worldCamera, OrthographicCamera hudCamera, Game game, ArrayMap<ScreenState, Screen> screens, GameInput input) {
		super(worldCamera, hudCamera, game, screens, input);
		sRenderer = new ShapeRenderer();
		world = new World(new Vector2(0, -9.81f), true);

		player = new Player(world);

		// Setup and Load Level
		level = new Level(world, worldCamera, batch);
		level.setPlayer(player);
		level.loadMap("map/Test.tmx");
		
		// Create Platform
		BodyDef bdef = new BodyDef();
		bdef.position.set(8,1); // position is defined to be the CENTER of the object
		bdef.type = BodyType.StaticBody; 
		Body body = world.createBody(bdef);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(5, 0.25f);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fdef.filter.categoryBits = BIT_GROUND;
		fdef.filter.maskBits = BIT_BOX | BIT_BALL;
		body.createFixture(fdef);
		
		// Create Falling Box
		bdef.position.set(8, 9);
		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);
		
		shape.setAsBox(1, 1);
		fdef.shape = shape;
		fdef.restitution = 0.25f;
		fdef.filter.categoryBits = BIT_BOX;
		fdef.filter.maskBits = BIT_GROUND;
		body.createFixture(fdef);
		
		// Create Ball
		bdef.position.set(8f, 10);
		body = world.createBody(bdef);
		
		CircleShape cshape = new CircleShape();
		cshape.setRadius(0.25f);
		fdef.shape = cshape;
		fdef.filter.categoryBits = BIT_BALL;
		fdef.filter.maskBits = BIT_GROUND;
		body.createFixture(fdef);
	}

	@Override
	protected void init() {
		worldCamera.position.x = R_WORLD_WIDTH * 0.5f;
		worldCamera.position.y = R_WORLD_HEIGHT * 0.5f;
	}

	@Override
	public void handleInput() {
		player.handleInput(input);
	}

	@Override
	public void update(float delta) {
		world.step(delta, 6, 2);
		level.update(delta);
//		player.update(delta);
		// worldCamera.position.x += 20.0f * delta;
	}

	@Override
	public void render() {
		level.render();
	}

	@Override
	protected void destroy() {

	}

	@Override
	public void dispose() {
		super.dispose();
		sRenderer.dispose();
		player.dispose();
	}
}
