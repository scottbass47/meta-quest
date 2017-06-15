package com.fullspectrum.level;

import static com.fullspectrum.game.GameVars.PPM_INV;

import java.util.Iterator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TmxMapLoader.Parameters;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.debug.DebugInput;
import com.fullspectrum.debug.DebugRender;
import com.fullspectrum.debug.DebugToggle;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.factory.EntityFactory;
import com.fullspectrum.level.Tile.Side;
import com.fullspectrum.level.Tile.TileType;
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
	private ExpandableGrid<Tile> tileMap;
//	private Array<Tile> ladders;

	// Spawns
	private Vector2 playerSpawn;
	private Array<EntitySpawn> entitySpawns;
	
	// Level Info
	private LevelManager manager;
	private LevelInfo info;
	private Array<EntityIndex> meshes;
	private boolean requiresFlowField;
	private boolean isCameraLocked;
	private float cameraZoom;
	
	private ArrayMap<Integer, Array<GridPoint>> edgeGroups;
	
	public Level(LevelManager manager, LevelInfo info) {
		this.manager = manager;
		this.world = manager.getWorld();
		this.info = info;
		loader = new TmxMapLoader();
//		ladders = new Array<Tile>();
		entitySpawns = new Array<EntitySpawn>();
		bodies = new Array<Body>();
		meshes = new Array<EntityIndex>();
		tileMap = new ExpandableGrid<Tile>();
		edgeGroups = new ArrayMap<Integer, Array<GridPoint>>();
	}

	public void loadMap(SpriteBatch batch) {
		Parameters params = new Parameters();
		params.textureMagFilter = TextureFilter.Nearest;
		params.textureMinFilter = TextureFilter.Nearest;
		
		map = loader.load("map/" + info.toFileFormatExtension(), params);
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
		mooreNeighborhood();
		
//		setupLadders();
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
		if(!DebugInput.isToggled(DebugToggle.SHOW_HITBOXES)) {
			mapRenderer.setView(worldCamera);
			mapRenderer.render();
		}
		
//		for(Integer id : edgeGroups.keys()) {
//			Color color = id == 0 ? Color.MAGENTA : (id == 1 ? Color.FIREBRICK : Color.CYAN);
//			DebugRender.setColor(color);
//			Array<GridPoint> run = edgeGroups.get(id);
//
//			for(int i = 0; i < run.size; i++) {
//				GridPoint p1 = run.get(i);
//				Tile tile = tileAt(p1.row, p1.col);
//				
//				boolean outlines = false;
//				if(outlines) {
//					DebugRender.setType(ShapeType.Filled);
//					boolean north = !tileMap.contains(tile.getRow() + 1, tile.getCol()) || !isSolid(tile.getRow() + 1, tile.getCol());
//					boolean south = !tileMap.contains(tile.getRow() - 1, tile.getCol()) || !isSolid(tile.getRow() - 1, tile.getCol());
//					boolean east  = !tileMap.contains(tile.getRow(), tile.getCol() + 1) || !isSolid(tile.getRow(), tile.getCol() + 1);
//					boolean west  = !tileMap.contains(tile.getRow(), tile.getCol() - 1) || !isSolid(tile.getRow(), tile.getCol() - 1);;
//					
//					float thickness = 0.05f;
//					
//					if(north) {
//						drawEdge(p1, Side.NORTH, thickness);
//					}
//					if(south) {
//						drawEdge(p1, Side.SOUTH, thickness);
//					}
//					if(east) {
//						drawEdge(p1, Side.EAST, thickness);
//					}
//					if(west) {
//						drawEdge(p1, Side.WEST, thickness);
//					}
//				} else {
//					if(i == 0) DebugRender.setColor(Color.WHITE);
//					else DebugRender.setColor(color);
//
//					DebugRender.setType(ShapeType.Line);
//					GridPoint p2 = i < run.size - 1 ? run.get(i + 1) : run.get(run.size - 1);
//					DebugRender.line(p1.col + 0.5f, p1.row + 0.5f, p2.col + 0.5f, p2.row + 0.5f);
//				}
//			}
//		}
	}
	
	public void drawEdge(GridPoint point, Side side, float thickness) {
		switch(side) {
		case NORTH:
			DebugRender.rect(point.col, point.row + 1.0f - thickness, 1.0f, thickness);
			break;
		case SOUTH:
			DebugRender.rect(point.col, point.row, 1.0f, thickness);
			break;
		case EAST:
			DebugRender.rect(point.col + 1.0f - thickness, point.row, thickness, 1.0f);
			break;
		case WEST:
			DebugRender.rect(point.col, point.row, thickness, 1.0f);
			break;
		default:
			break;
		}
	}

	public boolean inBounds(int row, int col) {
		return row >= 0 && row < height && col >= 0 && col < width;
	}

	public boolean inBounds(float x, float y) {
		return inBounds((int) y, (int) x);
	}

	private void mooreNeighborhood() {
		ArrayMap<GridPoint, ObjectSet<Side>> visited = new ArrayMap<GridPoint, ObjectSet<Side>>();
		int id = 0;
		
		for(int row = 0; row < tileMap.getRows(); row++) {
			for(int col = 0; col < tileMap.getCols(); col++) {
				GridPoint startPoint = new GridPoint(row, col);

				Tile start = tileMap.get(row, col);
				boolean touchingAir = isTouchingAir(start);
				if(!touchingAir || !isSolid(start.getRow(), start.getCol())) continue;

				
				// Pick previous and get it's moore point
				GridPoint previous = new GridPoint(row, col - 1);
				MoorePoint moorePoint = MoorePoint.getMoorePoint(startPoint, previous);
				
				// Find starting moore point (can't have already been used and must have a non-null side
				boolean success = false;
				for(int i = 0; i < 4; i++) {
					if(moorePoint.getSide() == null) moorePoint = moorePoint.getNext();
					
					// Check if moore point is valid
					previous = moorePoint.add(startPoint);
					if((!visited.containsKey(startPoint) || !visited.get(startPoint).contains(moorePoint.getSide())) && !isSolid(previous.row, previous.col)) {
						success = true;
						previous = moorePoint.add(startPoint);
						break;
					}
					
					moorePoint = moorePoint.getNext();
				}
				
				// If no open moore points that haven't been checked yet are found, skip to the next tile
				if(!success) {
					continue;
				}

				// -----------------------------
				// CONTOUR TRACING STARTS HERE
				// -----------------------------
				
				// Save start
				MoorePoint startingMoorePoint = moorePoint;
				if(!visited.containsKey(startPoint)) {
					visited.put(startPoint, new ObjectSet<Side>());
				}
				visited.get(startPoint).add(startingMoorePoint.getSide());
				
				Array<GridPoint> run = new Array<GridPoint>();
				run.add(startPoint);
				
				GridPoint center = new GridPoint(startPoint);
				
				// For case where platform is only one tile, if all moore points have been traversed, the run should be ended
				ObjectSet<MoorePoint> mooreSet = new ObjectSet<MoorePoint>();
				mooreSet.add(moorePoint);
				
				Side side = null;
				GridPoint edgeStart = new GridPoint(startPoint);
				GridPoint edgeEnd = new GridPoint(startPoint);
				Array<Vector2> edgeVertices = new Array<Vector2>(Vector2.class);
				
				do {
					// Check if moore point is a solid tile
					if(isSolid(previous.row, previous.col)) {
						run.add(previous);
						if(!visited.containsKey(previous)) {
							visited.put(previous, new ObjectSet<Side>());
						}

						previous = moorePoint.getPrevious().add(center);
						center = new GridPoint(moorePoint.add(center));
						
						// Update previous
						moorePoint = MoorePoint.getMoorePoint(center, previous);
						
						mooreSet.clear();
						mooreSet.add(moorePoint);
					} else {
						// Check if moore point is axis-aligned
						if(moorePoint.getSide() != null) {
							if(moorePoint.getSide() == side) {
								edgeEnd.set(center);
							} else if(side != null) {
								// Edge is finished
								if(edgeVertices.size == 0) {
									edgeVertices.add(getVertex(edgeStart, side, true));
								}
								edgeVertices.add(getVertex(edgeEnd, side, false));
								edgeStart.set(center);
								edgeEnd.set(center);
							}
							side = moorePoint.getSide();
							visited.get(center).add(side);
						}
						
						moorePoint = moorePoint.getNext();
						previous = moorePoint.add(center);
						
						// If all moore points have been visited, then you must be on a single block platform
						// No need to handle edges, single block platforms work as expected
						if(mooreSet.contains(moorePoint)){
							break;
						}
						
						mooreSet.add(moorePoint);
					}
				} while(!(center.equals(startPoint) && moorePoint == startingMoorePoint));

				edgeGroups.put(id++, run);
				Entity tile = EntityFactory.createTile(null);
				Body body = PhysicsUtils.createTilePhysics(world, tile, edgeVertices.toArray());
				Mappers.body.get(tile).set(body);
				bodies.add(body);
			}
		}
	}
	
	/**
	 * Assumes clockwise traversal. Returns the position of the vertex at specified GridPoint. If start is true, 
	 * then the first vertex will be returned (in the clockwise direction). 
	 * 
	 * @param point
	 * @param side
	 * @param start
	 * @return
	 */
	private Vector2 getVertex(GridPoint point, Side side, boolean start) {
		switch (side) {
		case EAST:
			if(start) {
				return new Vector2(point.col + 1.0f, point.row + 1.0f);
			} else {
				return new Vector2(point.col + 1.0f, point.row);
			}
		case NORTH:
			if(start) {
				return new Vector2(point.col, point.row + 1.0f);
			} else {
				return new Vector2(point.col + 1.0f, point.row + 1.0f);
			}
		case SOUTH:
			if(start) {
				return new Vector2(point.col + 1.0f, point.row);
			} else {
				return new Vector2(point.col, point.row);
			}
		case WEST:
			if(start) {
				return new Vector2(point.col, point.row);
			} else {
				return new Vector2(point.col, point.row + 1.0f);
			}
		default:
			return null;
		}
	}
	
	private enum MoorePoint {
		P1(1, -1, null) {
			@Override
			public MoorePoint getNext() {
				return P2;
			}

			@Override
			public MoorePoint getPrevious() {
				return P8;
			}
		},
		P2(1, 0, Side.NORTH) {
			@Override
			public MoorePoint getNext() {
				return P3;
			}

			@Override
			public MoorePoint getPrevious() {
				return P1;
			}
		},
		P3(1, 1, null) {
			@Override
			public MoorePoint getNext() {
				return P4;
			}

			@Override
			public MoorePoint getPrevious() {
				return P2;
			}
		},
		P4(0, 1, Side.EAST) {
			@Override
			public MoorePoint getNext() {
				return P5;
			}

			@Override
			public MoorePoint getPrevious() {
				return P3;
			}
		},
		P5(-1, 1, null) {
			@Override
			public MoorePoint getNext() {
				return P6;
			}

			@Override
			public MoorePoint getPrevious() {
				return P4;
			}
		},
		P6(-1, 0, Side.SOUTH) {
			@Override
			public MoorePoint getNext() {
				return P7;
			}

			@Override
			public MoorePoint getPrevious() {
				return P5;
			}
		},
		P7(-1, -1, null) {
			@Override
			public MoorePoint getNext() {
				return P8;
			}

			@Override
			public MoorePoint getPrevious() {
				return P6;
			}
		},
		P8(0, -1, Side.WEST) {
			@Override
			public MoorePoint getNext() {
				return P1;
			}

			@Override
			public MoorePoint getPrevious() {
				return P7;
			}
		};
		
		// Relative positioning
		private int row;
		private int col;
		private Side side;
		
		private MoorePoint(int row, int col, Side side) {
			this.row = row;
			this.col = col;
			this.side = side;
		}
		
		public Side getSide() {
			return side;
		}
		
		public abstract MoorePoint getNext();
		public abstract MoorePoint getPrevious();
		
		public GridPoint add(GridPoint point) {
			return new GridPoint(row + point.row, col + point.col);
		}
		
		public static MoorePoint getMoorePoint(int centerRow, int centerCol, int mooreRow, int mooreCol) {
			int relRow = mooreRow - centerRow;
			int relCol = mooreCol - centerCol;
			
			for(MoorePoint point : values()) {
				if(point.row == relRow && point.col == relCol) return point;
			}
			return null;
		}
		
		public static MoorePoint getMoorePoint(GridPoint center, GridPoint moore) {
			return getMoorePoint(center.row, center.col, moore.row, moore.col);
		}
	}
	
	private boolean isTouchingAir(Tile tile) {
		return (!tileMap.contains(tile.getRow() - 1, tile.getCol()) || !isSolid(tile.getRow() - 1, tile.getCol())) ||
			   (!tileMap.contains(tile.getRow() + 1, tile.getCol()) || !isSolid(tile.getRow() + 1, tile.getCol())) ||
			   (!tileMap.contains(tile.getRow(), tile.getCol() - 1) || !isSolid(tile.getRow(), tile.getCol() - 1)) ||
			   (!tileMap.contains(tile.getRow(), tile.getCol() + 1) || !isSolid(tile.getRow(), tile.getCol() + 1));
	}
	
	private void setupGround() {
		final TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("ground");
		width = layer.getWidth();
		height = layer.getHeight();

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				Cell cell = layer.getCell(col, row);
				if (cell == null || cell.getTile() == null) {
					tileMap.add(row, col, new Tile(row, col, TileType.AIR));
					continue;
				}
				Tile tile = new Tile(row, col, TileType.getType((String) cell.getTile().getProperties().get("type")));
				tileMap.add(row, col, tile);
			}
		}
	}
	
	// CLEANUP WON'T WORK ANYMORE
//	private void setupLadders(){
//		BodyDef bdef = new BodyDef();
//		bdef.type = BodyType.StaticBody;
//		PolygonShape shape = new PolygonShape();
//		shape.setAsBox(0.5f, 0.5f);
//		FixtureDef fdef = new FixtureDef();
//		fdef.shape = shape;
//		fdef.friction = 0.0f;
//		fdef.filter.categoryBits = CollisionBits.TILE.getBit();
//		fdef.filter.maskBits = CollisionBits.getOtherBits(CollisionBits.TILE);
//		fdef.isSensor = true;
//		
//		while(ladders.size > 0){
//			Tile ladder = ladders.first();
//			int startCol = ladder.getCol();
//			int startRow = ladder.getRow();
//			int endCol = ladder.getCol();
//			int endRow = ladder.getRow();
//			
//			// Traverse Up
//			for(int row = startRow + 1; row < height; row++){
//				if(tileMap.get(row, startCol).getType() != TileType.LADDER){
//					break;
//				}
//				endRow++;
//			}
//			
//			// Traverse Down
//			for(int row = startRow - 1; row >= 0; row--){
//				if(tileMap.get(row, startCol).getType() != TileType.LADDER){
//					break;
//				}
//				startRow--;
//			}
//			
//			// Remove Ladders
//			for(Iterator<Tile> iter = ladders.iterator(); iter.hasNext();){
//				Tile t = iter.next();
//				if(t.getRow() >= startRow && t.getRow() <= endRow && t.getCol() >= startCol && t.getCol() <= endCol){
//					iter.remove();
//				}
//			}
//			
//			int width = endCol - startCol + 1;
//			int height = endRow - startRow + 1;
//			shape.setAsBox(width * 0.5f - 0.4f, height * 0.5f);
//			bdef.position.set(startCol + width * 0.5f, startRow + height * 0.5f);
//			Body body = world.createBody(bdef);
//			body.createFixture(fdef).setUserData("ladder");
//			bodies.add(body);
//		}
//	}

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
			}else {
				entitySpawns.add(new EntitySpawn(EntityIndex.get(o.getName()), spawnPoint));
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
			EntityManager.addEntity(EntityFactory.createLevelTrigger(spawnPoint.x, spawnPoint.y, o.getName()));
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
		return tileMap.get(row, col);
	}

	public boolean isSolid(int row, int col) {
		if (!tileMap.contains(row, col)) return false;
		return tileMap.get(row, col).isSolid();
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

	public ExpandableGrid<Tile> getTileMap() {
		return tileMap;
	}

	public void setTileMap(ExpandableGrid<Tile> tileMap) {
		this.tileMap = tileMap;
	}

	public Vector2 getPlayerSpawn() {
		return playerSpawn;
	}

	public void setPlayerSpawn(Vector2 playerSpawn) {
		this.playerSpawn = playerSpawn;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setEntitySpawns(Array<EntitySpawn> entitySpawns) {
		this.entitySpawns = entitySpawns;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + ((tileMap == null) ? 0 : tileMap.hashCode());
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
		if (tileMap == null) {
			if (other.tileMap != null) return false;
		}
		else if (!tileMap.equals(other.tileMap)) return false;
		if (width != other.width) return false;
		return true;
	}
	
}
