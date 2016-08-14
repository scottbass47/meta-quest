package com.fullspectrum.level;

import static com.fullspectrum.game.GameVars.*;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.fullspectrum.entity.player.Player;

public class Level {

	// Physics
	private World world;
	private Box2DDebugRenderer b2dr;

	// Tile Map
	private TiledMap map;
	private TmxMapLoader loader;
	private OrthogonalTiledMapRenderer mapRenderer;

	// Player
	private Player player;

	// Camera
	private OrthographicCamera cam;

	// Rendering
	private SpriteBatch batch;

	public Level(World world, OrthographicCamera cam, SpriteBatch batch) {
		this.world = world;
		this.cam = cam;
		this.batch = batch;
		b2dr = new Box2DDebugRenderer();
		loader = new TmxMapLoader();
	}

	public void loadMap(String path) {
		map = loader.load(path);
		mapRenderer = new OrthogonalTiledMapRenderer(map, TILE_SCALE / PPM);
		setupTilePhysics();
	}

	private void setupTilePhysics() {
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("ground");

		// Init Physics Object
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.StaticBody;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.5f, 0.5f);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fdef.friction = 0.0f;

		for (int row = 0; row < layer.getHeight(); row++) {
			for (int col = 0; col < layer.getWidth(); col++) {
				Cell cell = layer.getCell(col, row);
				if (cell == null) continue;
				if (cell.getTile() == null) continue;

				bdef.position.set(col + 0.5f, row + 0.5f);
				world.createBody(bdef).createFixture(fdef);
			}
		}
	}

	public void update(float delta) {
		if (player != null) player.update(delta);
		cam.position.x = R_WORLD_WIDTH * 0.5f;
		cam.position.y = R_WORLD_HEIGHT * 0.5f;
	}

	public void render() {
		cam.update();
		batch.setProjectionMatrix(cam.combined);

		mapRenderer.setView(cam);
		mapRenderer.render();

//		b2dr.render(world, cam.combined);

		if (player != null) {
			batch.begin();
			player.render(batch);
			batch.end();
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
