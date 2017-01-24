package com.fullspectrum.level;

import static com.fullspectrum.game.GameVars.PPM_INV;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.component.LevelSwitchComponent;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.level.Tile.Side;
import com.fullspectrum.level.Tile.TileType;
import com.fullspectrum.physics.CollisionBits;
import com.fullspectrum.utils.PhysicsUtils;

public class Level {

	// Physics
	private World world;
	private Array<Body> bodies;

	// Tile Map
	private TiledMap map;
	private TmxMapLoader loader;
	private OrthogonalTiledMapRenderer mapRenderer;
	private int width;
	private int height;
	private Tile[][] mapTiles;
	private Array<Tile> ladders;

	// Spawns
	private Vector2 playerSpawn;
	private Array<EntitySpawn> entitySpawns;
	
	// Engine
	private Engine engine;
	
	// Level Info
	private LevelManager manager;
	private LevelInfo info;
	private Array<EntityIndex> meshes;
	private boolean requiresFlowField;
	private boolean isCameraLocked;
	private float cameraZoom;
	
	public Level(LevelManager manager, LevelInfo info) {
		this.manager = manager;
		this.engine = manager.getEngine();
		this.world = manager.getWorld();
		this.info = info;
		loader = new TmxMapLoader();
		ladders = new Array<Tile>();
		entitySpawns = new Array<EntitySpawn>();
		bodies = new Array<Body>();
		meshes = new Array<EntityIndex>();
	}

	public void loadMap(SpriteBatch batch) {
		map = loader.load("map/" + info.toFileFormatExtension());
		mapRenderer = new OrthogonalTiledMapRenderer(map, PPM_INV, batch);
		if(map.getProperties().containsKey("meshes")){
			String meshList = (String) map.getProperties().get("meshes");
			meshList.replaceAll("\\s+", "");
			String[] parts = meshList.split(",");
			for(String part : parts){
				meshes.add(EntityIndex.get(part));
			}
		}
		MapProperties prop = map.getProperties();
		requiresFlowField = prop.containsKey("flow_field");
		isCameraLocked = prop.containsKey("camera_locked");
		cameraZoom = prop.containsKey("camera_zoom") ? (Float)prop.get("camera_zoom") : 3.0f;
		
		setupGround();
		setupLadders();
		setupSpawnPoints();
		setupLevelTriggers();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public LevelManager getManager(){
		return manager;
	}
	
	public LevelInfo getInfo(){
		return info;
	}
	
	public Array<EntityIndex> getMeshes(){
		return meshes;
	}
	
	public boolean requiresFlowField(){
		return requiresFlowField;
	}
	
	public boolean isCameraLocked(){
		return isCameraLocked;
	}
	
	public float getCameraZoom(){
		return cameraZoom;
	}
	
	public void render(OrthographicCamera worldCamera) {
		mapRenderer.setView(worldCamera);
		mapRenderer.render();
	}

	public boolean inBounds(int row, int col) {
		return row >= 0 && row < height && col >= 0 && col < width;
	}

	public boolean inBounds(float x, float y) {
		return inBounds((int) y, (int) x);
	}

	private void setupGround() {
		final TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("ground");
		width = layer.getWidth();
		height = layer.getHeight();

		// Init Physics Object
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.StaticBody;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.5f, 0.5f);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fdef.friction = 0.0f;
		fdef.filter.categoryBits = CollisionBits.TILE.getBit();
		fdef.filter.maskBits = CollisionBits.getOtherBits(CollisionBits.TILE);

		Array<Tile> tiles = new Array<Tile>();
		mapTiles = new Tile[height][width];
		Boolean[][] tileExists = new Boolean[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				Cell cell = layer.getCell(col, row);
				if (cell == null || cell.getTile() == null) {
					tileExists[row][col] = false;
					mapTiles[row][col] = new Tile(row, col, TileType.AIR);
					continue;
				}

				Tile tile = new Tile(row, col, TileType.getType((String) cell.getTile().getProperties().get("type")));

				// if(row > 0){
				// Cell c = layer.getCell(col, row - 1);
				// if(c == null || c.getTile() == null) {
				// tile.addSide(Side.SOUTH);
				// }
				// }
				// if(row < height - 1){
				// Cell c = layer.getCell(col, row + 1);
				// if(c == null || c.getTile() == null) {
				// tile.addSide(Side.NORTH);
				// }
				// }
				// if(col > 0){
				// Cell c = layer.getCell(col - 1, row);
				// if(c == null || c.getTile() == null) {
				// tile.addSide(Side.WEST);
				// }
				// }
				// if(col < width - 1){
				// Cell c = layer.getCell(col + 1, row);
				// if(c == null || c.getTile() == null) {
				// tile.addSide(Side.EAST);
				// }
				// }
				if (tile.getType() == TileType.GROUND) {
					tiles.add(tile);
					tileExists[row][col] = true;
				}
				else{
					if(tile.getType() == TileType.LADDER){
						ladders.add(tile);
					}
					tileExists[row][col] = false;
				}
				mapTiles[row][col] = tile;
			}
		}
		for (Tile tile : tiles) {
			int row = tile.getRow();
			int col = tile.getCol();
			if (row > 0) {
				Tile t = mapTiles[row - 1][col];
				if (!t.isSolid()) {
					tile.addSide(Side.SOUTH);
				}
			}
			if (row < height - 1) {
				Tile t = mapTiles[row + 1][col];
				if (!t.isSolid()) {
					tile.addSide(Side.NORTH);
				}
			}
			if (col > 0) {
				Tile t = mapTiles[row][col - 1];
				if (!t.isSolid()) {
					tile.addSide(Side.WEST);
				}
			}
			if (col < width - 1) {
				Tile t = mapTiles[row][col + 1];
				if (!t.isSolid()) {
					tile.addSide(Side.EAST);
				}
			}
		}
		tiles.sort(new Comparator<Tile>() {
			@Override
			public int compare(Tile o1, Tile o2) {
				if (o1.isSurrounded() && !o2.isSurrounded()) return 1;
				if (!o1.isSurrounded() && o2.isSurrounded()) return -1;
				return o1.getIndex(layer.getWidth()) < o2.getIndex(layer.getWidth()) ? -1 : 1;
			}
		});
		while (tiles.size > 0) {
			Tile t = tiles.first();
			if (!tileExists[t.getRow()][t.getCol()]) continue;
			tileExists[t.getRow()][t.getCol()] = false;
			int startCol = t.getCol();
			int startRow = t.getRow();
			int endCol = t.getCol();
			int endRow = t.getRow();

			// Do expansion
			if (!t.isSurrounded() && (t.isOpen(Side.WEST) || t.isOpen(Side.EAST))) {
				int[] coords = expandCol(startRow, t.getCol(), layer.getHeight(), tileExists);
				startRow = coords[0];
				endRow = coords[1];
				coords = expandRow(startRow, endRow, startCol, layer.getWidth(), tileExists);
				startCol = coords[0];
				endCol = coords[1];
			} else {
				int[] coords = expandRow(startCol, t.getRow(), layer.getWidth(), tileExists);
				startCol = coords[0];
				endCol = coords[1];
				coords = expandCol(startCol, endCol, startRow, layer.getHeight(), tileExists);
				startRow = coords[0];
				endRow = coords[1];
			}

			int width = endCol - startCol + 1;
			int height = endRow - startRow + 1;
			shape.setAsBox(width * 0.5f, height * 0.5f);
			bdef.position.set(startCol + width * 0.5f, startRow + height * 0.5f);
			Body body = world.createBody(bdef);
			body.createFixture(fdef).setUserData("ground");
			bodies.add(body);
			removeTiles(startCol, endCol, startRow, endRow, tiles);
		}
	}
	
	private void setupLadders(){
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.StaticBody;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.5f, 0.5f);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fdef.friction = 0.0f;
		fdef.filter.categoryBits = CollisionBits.TILE.getBit();
		fdef.filter.maskBits = CollisionBits.getOtherBits(CollisionBits.TILE);
		fdef.isSensor = true;
		
		while(ladders.size > 0){
			Tile ladder = ladders.first();
			int startCol = ladder.getCol();
			int startRow = ladder.getRow();
			int endCol = ladder.getCol();
			int endRow = ladder.getRow();
			
			// Traverse Up
			for(int row = startRow + 1; row < height; row++){
				if(mapTiles[row][startCol].getType() != TileType.LADDER){
					break;
				}
				endRow++;
			}
			
			// Traverse Down
			for(int row = startRow - 1; row >= 0; row--){
				if(mapTiles[row][startCol].getType() != TileType.LADDER){
					break;
				}
				startRow--;
			}
			
			// Remove Ladders
			for(Iterator<Tile> iter = ladders.iterator(); iter.hasNext();){
				Tile t = iter.next();
				if(t.getRow() >= startRow && t.getRow() <= endRow && t.getCol() >= startCol && t.getCol() <= endCol){
					iter.remove();
				}
			}
			
			int width = endCol - startCol + 1;
			int height = endRow - startRow + 1;
			shape.setAsBox(width * 0.5f - 0.4f, height * 0.5f);
			bdef.position.set(startCol + width * 0.5f, startRow + height * 0.5f);
			Body body = world.createBody(bdef);
			body.createFixture(fdef).setUserData("ladder");
			bodies.add(body);
		}
	}

	private void setupSpawnPoints() {
		MapObjects objects = map.getLayers().get("spawns").getObjects();
		for (MapObject o : objects) {
			float x = (Float) o.getProperties().get("x");
			float y = (Float) o.getProperties().get("y");
			float width = (Float) o.getProperties().get("width");
			float height = (Float) o.getProperties().get("height");
			Vector2 spawnPoint = new Vector2(x + width * 0.5f, y + height * 0.5f).scl(PPM_INV);
			if (o.getName().equals("player_spawn")) {
				playerSpawn = spawnPoint;
			}else if(o.getName().equals("spawner")){
				entitySpawns.add(new EntitySpawn(EntityIndex.SPAWNER, spawnPoint));
			}
		}
	}
	
	private void setupLevelTriggers() {
		MapObjects objects = map.getLayers().get("triggers").getObjects();
		for (MapObject o : objects) {
			float x = (Float) o.getProperties().get("x");
			float y = (Float) o.getProperties().get("y");
			float width = (Float) o.getProperties().get("width");
			float height = (Float) o.getProperties().get("height");
			Vector2 spawnPoint = new Vector2(x + width * 0.5f, y + height * 0.5f).scl(PPM_INV);
			
			Entity entity = engine.createEntity();
			entity.add(engine.createComponent(LevelSwitchComponent.class).set(o.getName()));
			Body body = PhysicsUtils.createPhysicsBody(Gdx.files.internal("body/level_trigger.json"), world, spawnPoint, entity, true);
			bodies.add(body);
			engine.addEntity(entity);
		}
	}
	
	public void destroy(){
		// Destroy Physics Bodies
		for(Iterator<Body> iter = bodies.iterator(); iter.hasNext();){
			world.destroyBody(iter.next());
			iter.remove();
		}
		
		
	}
	
	public Array<EntitySpawn> getEntitySpawns(){
		return entitySpawns;
	}
	
	public boolean isLadder(int row, int col){
		return tileAt(row, col).getType() == TileType.LADDER;
	}

	public Tile tileAt(int row, int col) {
		return mapTiles[row][col];
	}

	public boolean isSolid(int row, int col) {
		if (!inBounds(row, col)) return false;
		return mapTiles[row][col].isSolid();
	}

	public boolean isSolid(float x, float y) {
		return isSolid((int) y, (int) x);
	}

	public Vector2 getPlayerSpawnPoint() {
		return playerSpawn;
	}

	public boolean performRayTrace(float x1, float y1, float x2, float y2) {
		int startCol = (int) x1;
		int startRow = (int) y1;
		int endCol = (int) x2;
		int endRow = (int) y2;

		if (startCol == endCol && startRow == endRow) {
			return !isSolid(startRow, startCol);
		}

		boolean alongX = Math.abs(startCol - endCol) > Math.abs(startRow - endRow);

		float slope = 0.0f;
		if (alongX) {
			slope = (startRow - endRow) / (float) (startCol - endCol);
		} else {
			slope = (startCol - endCol) / (float) (startRow - endRow);
		}

		// y2 - y1 = m(x2 - x1)
		// startRow - y1 = m(startCol - x1)
		// startRow - y1 = m(startCol - col)
		// y1 = -m(startCol - col) + startRow

		if (alongX) {
			if (startCol < endCol) {
				for (int col = startCol; col <= endCol; col++) {
					if (isSolid((int) (-slope * (startCol - col) + startRow), col)) return false;
				}
			} else {
				for (int col = startCol; col >= endCol; col--) {
					if (isSolid((int) (-slope * (startCol - col) + startRow), col)) return false;
				}
			}
		} else {
			if (startRow < endRow) {
				for (int row = startRow; row <= endRow; row++) {
					if (isSolid(row, (int) (-slope * (startRow - row) + startCol))) return false;
				}
			} else {
				for (int row = startRow; row >= endRow; row--) {
					if (isSolid(row, (int) (-slope * (startRow - row) + startCol))) return false;
				}
			}
		}
		return true;
	}

	private int[] expandRow(int startCol, int row, int maxWidth, Boolean[][] tileExists) {
		int[] coords = new int[2];
		int sCol = startCol;
		int eCol = startCol;
		for (int col = startCol - 1; col >= 0; col--) {
			if (!tileExists[row][col]) break;
			sCol--;
			tileExists[row][col] = false;
		}
		for (int col = startCol + 1; col < maxWidth; col++) {
			if (!tileExists[row][col]) break;
			eCol++;
			tileExists[row][col] = false;
		}
		coords[0] = sCol;
		coords[1] = eCol;
		return coords;
	}

	private int[] expandCol(int startRow, int col, int maxHeight, Boolean[][] tileExists) {
		int[] coords = new int[2];
		int sRow = startRow;
		int eRow = startRow;
		for (int row = startRow - 1; row >= 0; row--) {
			if (!tileExists[row][col]) break;
			sRow--;
			tileExists[row][col] = false;
		}
		for (int row = startRow + 1; row < maxHeight; row++) {
			if (!tileExists[row][col]) break;
			eRow++;
			tileExists[row][col] = false;
		}
		coords[0] = sRow;
		coords[1] = eRow;
		return coords;
	}

	private int[] expandRow(int startRow, int endRow, int startCol, int maxWidth, Boolean[][] tileExists) {
		int[] coords = new int[2];
		int sCol = startCol;
		int eCol = startCol;
		for (int col = startCol - 1; col >= 0; col--) {
			if (!validCol(startRow, endRow, col, tileExists)) break;
			invalidateCol(startRow, endRow, col, tileExists);
			sCol--;
		}
		for (int col = startCol + 1; col < maxWidth; col++) {
			if (!validCol(startRow, endRow, col, tileExists)) break;
			invalidateCol(startRow, endRow, col, tileExists);
			eCol++;
		}
		coords[0] = sCol;
		coords[1] = eCol;
		return coords;
	}

	private int[] expandCol(int startCol, int endCol, int startRow, int maxHeight, Boolean[][] tileExists) {
		int[] coords = new int[2];
		int sRow = startRow;
		int eRow = startRow;
		for (int row = startRow - 1; row >= 0; row--) {
			if (!validRow(startCol, endCol, row, tileExists)) break;
			invalidateRow(startCol, endCol, row, tileExists);
			sRow--;
		}
		for (int row = startRow + 1; row < maxHeight; row++) {
			if (!validRow(startCol, endCol, row, tileExists)) break;
			invalidateRow(startCol, endCol, row, tileExists);
			eRow++;
		}
		coords[0] = sRow;
		coords[1] = eRow;
		return coords;
	}

	private boolean validRow(int startCol, int endCol, int row, Boolean[][] tileExists) {
		for (int i = startCol; i <= endCol; i++) {
			if (!tileExists[row][i]) return false;
		}
		return true;
	}

	private boolean validCol(int startRow, int endRow, int col, Boolean[][] tileExists) {
		for (int i = startRow; i <= endRow; i++) {
			if (!tileExists[i][col]) return false;
		}
		return true;
	}

	private void invalidateRow(int startCol, int endCol, int row, Boolean[][] tileExists) {
		for (int i = startCol; i <= endCol; i++) {
			tileExists[row][i] = false;
		}
	}

	private void invalidateCol(int startRow, int endRow, int col, Boolean[][] tileExists) {
		for (int i = startRow; i <= endRow; i++) {
			tileExists[i][col] = false;
		}
	}

	private void removeTiles(int startCol, int endCol, int startRow, int endRow, Array<Tile> tiles) {
		for (Iterator<Tile> iter = tiles.iterator(); iter.hasNext();) {
			Tile t = iter.next();
			if (t.getRow() >= startRow && t.getRow() <= endRow && t.getCol() >= startCol && t.getCol() <= endCol) {
				iter.remove();
			}
		}
	}
	
	public class EntitySpawn{
		private EntityIndex index;
		private Vector2 pos;
		
		public EntitySpawn(EntityIndex index, Vector2 pos){
			this.index = index;
			this.pos = pos;
		}
		
		public EntityIndex getIndex(){
			return index;
		}
		
		public Vector2 getPos(){
			return pos;
		}
	}
	
//	public class LevelTrigger{
//		private String data;
//		private Vector2 pos;
//		
//		public LevelTrigger(String data, Vector2 pos){
//			this.data = data;
//			this.pos = pos;
//		}
//		
//		public String getSwitchData(){
//			return data;
//		}
//		
//		public Vector2 getPos(){
//			return pos;
//		}
//	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		for(Tile[] row : mapTiles){
			for(Tile t : row){
				result = prime * result + t.hashCode();
			}
		}
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Level other = (Level) obj;
		if (height != other.height) return false;
		if (!Arrays.deepEquals(mapTiles, other.mapTiles)) return false;
		if (width != other.width) return false;
		return true;
	}
}
