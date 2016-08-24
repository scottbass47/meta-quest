package com.fullspectrum.game;

import static com.fullspectrum.game.GameVars.R_WORLD_HEIGHT;
import static com.fullspectrum.game.GameVars.R_WORLD_WIDTH;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.component.GraphicsComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.PhysicsComponent;
import com.fullspectrum.entity.Entity;
import com.fullspectrum.entity.player.Player;
import com.fullspectrum.entity.player.PlayerGraphicsComponent;
import com.fullspectrum.entity.player.PlayerInputComponent;
import com.fullspectrum.entity.player.PlayerPhysicsComponent;
import com.fullspectrum.input.GameInput;
import com.fullspectrum.level.Level;

public class GameScreen extends AbstractScreen {

	// Debug Graphics
	private ShapeRenderer sRenderer;

	// Player
	private Entity player;

	// Tile Map
	private Level level;

	// Box2D
	private World world;

	public GameScreen(OrthographicCamera worldCamera, OrthographicCamera hudCamera, Game game, ArrayMap<ScreenState, Screen> screens, GameInput input) {
		super(worldCamera, hudCamera, game, screens, input);
		sRenderer = new ShapeRenderer();
		world = new World(new Vector2(0, -23.0f), true);
		
		// Build Player
		InputComponent playerInput = new PlayerInputComponent(input);
		PhysicsComponent playerPhysics = new PlayerPhysicsComponent(world);
		GraphicsComponent playerGraphics = new PlayerGraphicsComponent(playerPhysics);
		player = new Player(playerInput, playerPhysics, playerGraphics);

		// Setup and Load Level
		level = new Level(world, worldCamera, batch);
		level.setPlayer(player);
		level.loadMap("map/Test.tmx");
	}

	@Override
	protected void init() {
		worldCamera.position.x = R_WORLD_WIDTH * 0.5f;
		worldCamera.position.y = R_WORLD_HEIGHT * 0.5f;
	}

	@Override
	public void update(float delta) {
		world.step(delta, 6, 2);
		level.update(delta);
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
