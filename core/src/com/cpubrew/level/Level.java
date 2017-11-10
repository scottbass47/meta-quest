package com.cpubrew.level;

import java.util.Iterator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.cpubrew.component.Mappers;
import com.cpubrew.debug.DebugRender;
import com.cpubrew.editor.mapobject.MapObject;
import com.cpubrew.editor.mapobject.MapObjectType;
import com.cpubrew.editor.mapobject.data.SpawnpointData;
import com.cpubrew.entity.EntityIndex;
import com.cpubrew.factory.EntityFactory;
import com.cpubrew.level.tiles.MapTile;
import com.cpubrew.level.tiles.MapTile.Side;
import com.cpubrew.level.tiles.MapTile.TileType;
import com.cpubrew.level.tiles.TilesetLoader;
import com.cpubrew.utils.Maths;
import com.cpubrew.utils.PhysicsUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Level {

	// Physics
	private World world;
	private Array<Body> bodies;

	// Tile Map
	// private TiledMap map;
	// private TmxMapLoader loader;
	// private OrthogonalTiledMapRenderer mapRenderer;
	private MapRenderer mapRenderer;
	private ExpandableGrid<MapTile> tileMap;
	private FileHandle tilesetFile;
	// private Array<Tile> ladders;

	// Spawns
//	private EntitySpawn playerSpawn;
//	private Array<EntitySpawn> entitySpawns;
	
	// Map Objects
	private int currentID;
	private Array<MapObject> mapObjects;

	// Level Info
	private LevelManager manager;
	private ObjectSet<EntityIndex> meshes;
	private boolean requiresFlowField;
	private boolean isCameraLocked;
	private float cameraZoom;
	private String name;

	private ArrayMap<Integer, Array<GridPoint>> edgeGroups;

	public Level() {
		this(null);
	}

	public Level(LevelManager manager) {
		this.manager = manager;

//		entitySpawns = new Array<EntitySpawn>();
		mapObjects = new Array<MapObject>();
		bodies = new Array<Body>();
		meshes = new ObjectSet<EntityIndex>();
		tileMap = new ExpandableGrid<MapTile>();
		edgeGroups = new ArrayMap<Integer, Array<GridPoint>>();
		mapRenderer = new MapRenderer();
		tilesetFile = Gdx.files.internal("map/grassy.atlas");
	}

	/**
	 * Called once when level is loaded from disk. Do stuff in here that won't
	 * change when using editor mode.
	 */
	private void loadMap() {
		// playerSpawn = new Vector2(10, 10);
		cameraZoom = 3.0f;

		TilesetLoader loader = new TilesetLoader();

		mapRenderer.setTileMap(tileMap);
		mapRenderer.setTileset(loader.load(tilesetFile));
	}

	public void load() {
		this.world = manager.getWorld();
		loadMap();
	}

	/**
	 * Called every time the map is changed to and from editor mode.
	 */
	public void init() {
		// Load meshes
		for (MapObject obj : mapObjects) {
			if(obj.getType() != MapObjectType.SPAWNPOINT) continue;
			EntityIndex index = ((SpawnpointData) obj.getData()).getIndex()	;

			if (NavMesh.usesNavMesh(index)) {
				meshes.add(index);
			}

			if (index == EntityIndex.SPAWNER) {
				requiresFlowField = true;
			}
		}
		requiresFlowField = true; // TEMPORARY For ease of use
		// setupGround();
		mooreNeighborhood();

		// setupLadders();
		// setupSpawnPoints();
		// setupLevelTriggers();
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getWidth() {
		return tileMap.getWidth();
	}

	public int getHeight() {
		return tileMap.getHeight();
	}

	public int getMinRow() {
		return tileMap.getMinRow();
	}

	public int getMinCol() {
		return tileMap.getMinCol();
	}

	public int getMaxRow() {
		return tileMap.getMaxRow();
	}

	public int getMaxCol() {
		return tileMap.getMaxCol();
	}

	public void setManager(LevelManager manager) {
		this.manager = manager;
	}

	public LevelManager getManager() {
		return manager;
	}

	public ObjectSet<EntityIndex> getMeshes() {
		return meshes;
	}

	public boolean requiresFlowField() {
		return requiresFlowField;
	}

	public boolean isCameraLocked() {
		return isCameraLocked;
	}

	public float getCameraZoom() {
		return cameraZoom;
	}

	public void render(SpriteBatch batch, OrthographicCamera worldCamera) {
		mapRenderer.setView(worldCamera);
		mapRenderer.render(batch);

		boolean debug = false;
		if (debug) {
			for (Integer id : edgeGroups.keys()) {
				Color color = id == 0 ? Color.MAGENTA : (id == 1 ? Color.FIREBRICK : Color.CYAN);
				DebugRender.setColor(color);
				Array<GridPoint> run = edgeGroups.get(id);

				for (int i = 0; i < run.size; i++) {
					GridPoint p1 = run.get(i);
					MapTile tile = tileAt(p1.row, p1.col);

					boolean outlines = false;
					if (outlines) {
						DebugRender.setType(ShapeType.Filled);
						boolean north = !tileMap.contains(tile.getRow() + 1, tile.getCol())
								|| !isSolid(tile.getRow() + 1, tile.getCol());
						boolean south = !tileMap.contains(tile.getRow() - 1, tile.getCol())
								|| !isSolid(tile.getRow() - 1, tile.getCol());
						boolean east = !tileMap.contains(tile.getRow(), tile.getCol() + 1)
								|| !isSolid(tile.getRow(), tile.getCol() + 1);
						boolean west = !tileMap.contains(tile.getRow(), tile.getCol() - 1)
								|| !isSolid(tile.getRow(), tile.getCol() - 1);
						;

						float thickness = 0.05f;

						if (north) {
							drawEdge(p1, Side.NORTH, thickness);
						}
						if (south) {
							drawEdge(p1, Side.SOUTH, thickness);
						}
						if (east) {
							drawEdge(p1, Side.EAST, thickness);
						}
						if (west) {
							drawEdge(p1, Side.WEST, thickness);
						}
					} else {
						if (i == 0)
							DebugRender.setColor(Color.WHITE);
						else
							DebugRender.setColor(color);

						DebugRender.setType(ShapeType.Line);
						GridPoint p2 = i < run.size - 1 ? run.get(i + 1) : run.get(run.size - 1);
						DebugRender.line(p1.col + 0.5f, p1.row + 0.5f, p2.col + 0.5f, p2.row + 0.5f);
					}
				}
			}
		}
	}

	public void drawEdge(GridPoint point, Side side, float thickness) {
		switch (side) {
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
		return tileMap.contains(row, col);
	}

	public boolean inBounds(float x, float y) {
		return inBounds((int) y, (int) x);
	}

	private void mooreNeighborhood() {
		ArrayMap<GridPoint, ObjectSet<Side>> visited = new ArrayMap<GridPoint, ObjectSet<Side>>();
		int id = 0;

		for (int row = tileMap.getMinRow(); row <= tileMap.getMaxRow(); row++) {
			for (int col = tileMap.getMinCol(); col <= tileMap.getMaxCol(); col++) {
				GridPoint startPoint = new GridPoint(row, col);

				MapTile start = tileMap.get(row, col);
				if (start == null)
					continue;

				boolean touchingAir = isTouchingAir(start);
				if (!touchingAir || !isSolid(start.getRow(), start.getCol()))
					continue;

				// Pick previous and get it's moore point
				GridPoint previous = new GridPoint(row, col - 1);
				MoorePoint moorePoint = MoorePoint.getMoorePoint(startPoint, previous);

				// Find starting moore point (can't have already been used and
				// must have a non-null side
				boolean success = false;
				for (int i = 0; i < 4; i++) {
					if (moorePoint.getSide() == null)
						moorePoint = moorePoint.getNext();

					// Check if moore point is valid
					previous = moorePoint.add(startPoint);
					if ((!visited.containsKey(startPoint) || !visited.get(startPoint).contains(moorePoint.getSide()))
							&& !isSolid(previous.row, previous.col)) {
						success = true;
						previous = moorePoint.add(startPoint);
						break;
					}

					moorePoint = moorePoint.getNext();
				}

				// If no open moore points that haven't been checked yet are
				// found, skip to the next tile
				if (!success) {
					continue;
				}

				// -----------------------------
				// CONTOUR TRACING STARTS HERE
				// -----------------------------

				// Save start
				MoorePoint startingMoorePoint = moorePoint;
				if (!visited.containsKey(startPoint)) {
					visited.put(startPoint, new ObjectSet<Side>());
				}
				visited.get(startPoint).add(startingMoorePoint.getSide());

				Array<GridPoint> run = new Array<GridPoint>();
				run.add(startPoint);

				GridPoint center = new GridPoint(startPoint);

				// For case where platform is only one tile, if all moore points
				// have been traversed, the run should be ended
				ObjectSet<MoorePoint> mooreSet = new ObjectSet<MoorePoint>();
				mooreSet.add(moorePoint);

				Side side = null;
				GridPoint edgeStart = new GridPoint(startPoint);
				GridPoint edgeEnd = new GridPoint(startPoint);
				Array<Vector2> edgeVertices = new Array<Vector2>(Vector2.class);

				do {
					// Check if moore point is a solid tile
					if (isSolid(previous.row, previous.col)) {
						run.add(previous);
						if (!visited.containsKey(previous)) {
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
						if (moorePoint.getSide() != null) {
							if (moorePoint.getSide() == side) {
								edgeEnd.set(center);
							} else if (side != null) {
								// Edge is finished
								if (edgeVertices.size == 0) {
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

						// If all moore points have been visited, then you must
						// be on a single block platform
						// No need to handle edges, single block platforms work
						// as expected
						if (mooreSet.contains(moorePoint)) {
							break;
						}

						mooreSet.add(moorePoint);
					}
				} while (!(center.equals(startPoint) && moorePoint == startingMoorePoint));

				edgeGroups.put(id++, run);
				Entity tile = EntityFactory.createTile(null);
				Body body = PhysicsUtils.createTilePhysics(world, tile, edgeVertices.toArray());
				Mappers.body.get(tile).set(body);
				bodies.add(body);
			}
		}
	}

	/**
	 * Assumes clockwise traversal. Returns the position of the vertex at
	 * specified GridPoint. If start is true, then the first vertex will be
	 * returned (in the clockwise direction).
	 * 
	 * @param point
	 * @param side
	 * @param start
	 * @return
	 */
	private Vector2 getVertex(GridPoint point, Side side, boolean start) {
		switch (side) {
		case EAST:
			if (start) {
				return new Vector2(point.col + 1.0f, point.row + 1.0f);
			} else {
				return new Vector2(point.col + 1.0f, point.row);
			}
		case NORTH:
			if (start) {
				return new Vector2(point.col, point.row + 1.0f);
			} else {
				return new Vector2(point.col + 1.0f, point.row + 1.0f);
			}
		case SOUTH:
			if (start) {
				return new Vector2(point.col + 1.0f, point.row);
			} else {
				return new Vector2(point.col, point.row);
			}
		case WEST:
			if (start) {
				return new Vector2(point.col, point.row);
			} else {
				return new Vector2(point.col, point.row + 1.0f);
			}
		default:
			return null;
		}
	}

	// Moore points represent adjacent tiles. Traversal is in the clockwise
	// direction.
	//
	// Structure:
	// P1 P2 P3
	// P8 C P4
	// P7 P6 P5
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

			for (MoorePoint point : values()) {
				if (point.row == relRow && point.col == relCol)
					return point;
			}
			return null;
		}

		public static MoorePoint getMoorePoint(GridPoint center, GridPoint moore) {
			return getMoorePoint(center.row, center.col, moore.row, moore.col);
		}
	}

	private boolean isTouchingAir(MapTile tile) {
		return (!tileMap.contains(tile.getRow() - 1, tile.getCol()) || !isSolid(tile.getRow() - 1, tile.getCol()))
				|| (!tileMap.contains(tile.getRow() + 1, tile.getCol()) || !isSolid(tile.getRow() + 1, tile.getCol()))
				|| (!tileMap.contains(tile.getRow(), tile.getCol() - 1) || !isSolid(tile.getRow(), tile.getCol() - 1))
				|| (!tileMap.contains(tile.getRow(), tile.getCol() + 1) || !isSolid(tile.getRow(), tile.getCol() + 1));
	}

	public Platform getPlatform(float x, float y) {
		int row = Maths.toGridCoord(y);
		int col = Maths.toGridCoord(x);

		if (isSolid(row, col) || !isSolid(row - 1, col))
			return null;

		int startCol = col;
		for (int c = col - 1; c >= tileMap.getMinCol(); c--) {
			if (c == tileMap.getMinCol()) {
				startCol = c;
			}
			if (isSolid(row, c) || !isSolid(row - 1, c)) {
				startCol = c + 1;
				break;
			}
		}

		int endCol = col;
		for (int c = col + 1; c <= tileMap.getMaxCol(); c++) {
			if (c == tileMap.getMaxCol()) {
				endCol = c;
			}
			if (isSolid(row, c) || !isSolid(row - 1, c)) {
				endCol = c - 1;
				break;
			}
		}

		Platform platform = new Platform();
		platform.setStartCol(startCol);
		platform.setEndCol(endCol);
		platform.setRow(row);

		return platform;
	}

	public void destroy() {
		// Destroy Physics Bodies
		for (Iterator<Body> iter = bodies.iterator(); iter.hasNext();) {
			world.destroyBody(iter.next());
			iter.remove();
		}
	}
	
	public void setCurrentID(int currentID) {
		this.currentID = currentID;
	}
	
	/** Returns the current MapObject ID */
	public int getCurrentID() {
		return currentID;
	}
	
	public void addMapObject(MapObject mobj) {
		mapObjects.add(mobj);
	}
	
	public Array<MapObject> getMapObjects() {
		return mapObjects;
	}
	
	public void removeAllMapObjects() {
		mapObjects.clear();
	}

//	public void addEntitySpawn(EntityIndex index, Vector2 pos, boolean facingRight) {
//		EntitySpawn spawn = new EntitySpawn();
//		spawn.setIndex(index);
//		spawn.setPos(pos);
//		spawn.setFacingRight(facingRight);
//		entitySpawns.add(spawn);
//	}
//	
//	public void addEntitySpawn(EntitySpawn spawn) {
//		entitySpawns.add(spawn);
//	}
//
//	public Array<EntitySpawn> getEntitySpawns() {
//		return entitySpawns;
//	}
//
//	public void removeSpawn(EntitySpawn spawn) {
//		entitySpawns.removeValue(spawn, false);
//	}
//
//	public void removeAllSpawns(){
//		entitySpawns.clear();
//	}
	
	public boolean isLadder(int row, int col) {
		MapTile tile = tileAt(row, col);
		return tile != null && tile.getType() == TileType.LADDER;
	}

	public MapTile tileAt(int row, int col) {
		if (!tileMap.contains(row, col))
			return null;
		return tileMap.get(row, col);
	}

	public boolean isSolid(int row, int col) {
		if (!tileMap.contains(row, col) || tileMap.get(row, col) == null)
			return false;
		return tileMap.get(row, col).isSolid();
	}

	public boolean isSolid(float x, float y) {
		return isSolid(Maths.toGridCoord(y), Maths.toGridCoord(x));
	}

	/**
	 * Returns true if the bounding box centered at x, y is not colliding with
	 * any solid tiles
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isValidPoint(float x, float y, Rectangle boundingBox) {
		float hw = boundingBox.width * 0.5f;
		float hh = boundingBox.height * 0.5f;
		float minX = x - hw;
		float minY = y - hh;
		float maxX = x + hw;
		float maxY = y + hh;

		int minRow = Maths.toGridCoord(minY);
		int minCol = Maths.toGridCoord(minX);
		int maxRow = Maths.toGridCoord(maxY);
		int maxCol = Maths.toGridCoord(maxX);

		for (int row = minRow; row <= maxRow; row++) {
			for (int col = minCol; col <= maxCol; col++) {
				if (isSolid(row, col))
					return false;
			}
		}
		return true;
	}

	public boolean performRayTrace(float x1, float y1, float x2, float y2) {
		if(MathUtils.isEqual(x1, x2) && MathUtils.isEqual(y1, y2)) return true;
		MyRayCastCallback callback = new MyRayCastCallback();
		world.rayCast(callback, x1, y1, x2, y2);
		return !callback.hitWall();
	}

	public static class EntitySpawn {
		private EntityIndex index;
		private Vector2 pos;
		private boolean facingRight;

		public EntitySpawn() {
			pos = new Vector2();
			index = null;
			facingRight = false;
		}

		public EntitySpawn(EntityIndex index, Vector2 pos, boolean facingRight) {
			this.index = index;
			this.pos = pos;
			this.facingRight = facingRight;
		}
		
		public EntitySpawn(EntitySpawn spawn){
			index = spawn.index;
			pos = new Vector2(spawn.pos);
			facingRight = spawn.facingRight;
		}

		public EntityIndex getIndex() {
			return index;
		}

		public Vector2 getPos() {
			return pos;
		}

		public void setIndex(EntityIndex index) {
			this.index = index;
		}

		public void setPos(Vector2 pos) {
			this.pos = pos;
		}

		public void setFacingRight(boolean facingRight) {
			this.facingRight = facingRight;
		}

		public boolean isFacingRight() {
			return facingRight;
		}

		@Override
		public String toString() {
			return index + ", " + pos + ", " + facingRight;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((index == null) ? 0 : index.hashCode());
			result = prime * result + ((pos == null) ? 0 : pos.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			EntitySpawn other = (EntitySpawn) obj;
			if (index != other.index)
				return false;
			if (pos == null) {
				if (other.pos != null)
					return false;
			} else if (!pos.equals(other.pos))
				return false;
			return true;
		}
	}

	public ExpandableGrid<MapTile> getTileMap() {
		return tileMap;
	}

	public void setTileMap(ExpandableGrid<MapTile> tileMap) {
		this.tileMap = tileMap;
	}

//	public EntitySpawn getPlayerSpawn() {
//		return playerSpawn;
//	}
//
//	public void setPlayerSpawn(EntitySpawn playerSpawn) {
//		this.playerSpawn = playerSpawn;
//	}
//
//	public void setEntitySpawns(Array<EntitySpawn> entitySpawns) {
//		this.entitySpawns = entitySpawns;
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getHeight();
		result = prime * result + ((tileMap == null) ? 0 : tileMap.hashCode());
		result = prime * result + getWidth();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Level other = (Level) obj;
		if (getHeight() != other.getHeight())
			return false;
		if (tileMap == null) {
			if (other.tileMap != null)
				return false;
		} else if (!tileMap.equals(other.tileMap))
			return false;
		if (getWidth() != other.getWidth())
			return false;
		return true;
	}

	public static LevelSerializer getSerializer() {
		return new LevelSerializer();
	}

	public static class LevelSerializer extends Serializer<Level> {

		// Things to serialize:
		// 1. Tileset (done)
		// 2. Tilemap (done)
		// 3. Spawn points (done)
		// 4. Level triggers

		@Override
		public void write(Kryo kryo, Output output, Level object) {
			output.writeString(object.tilesetFile.path());

			ExpandableGrid<MapTile> tileMap = object.tileMap;
			output.writeInt(tileMap.getMinRow());
			output.writeInt(tileMap.getMinCol());
			output.writeInt(tileMap.getMaxRow());
			output.writeInt(tileMap.getMaxCol());

			for (int row = tileMap.getMinRow(); row <= tileMap.getMaxRow(); row++) {
				for (int col = tileMap.getMinCol(); col <= tileMap.getMaxCol(); col++) {
					kryo.writeObjectOrNull(output, tileMap.get(row, col), MapTile.class);
				}
			}

			output.writeInt(object.entitySpawns.size);
			for (EntitySpawn spawn : object.entitySpawns) {
				output.writeFloat(spawn.getPos().x);
				output.writeFloat(spawn.getPos().y);
				output.writeString(spawn.getIndex().name().toLowerCase());
				output.writeBoolean(spawn.facingRight);
			}

			EntitySpawn spawn = object.getPlayerSpawn();
			output.writeBoolean(spawn != null);
			
			if(spawn != null) {
				output.writeFloat(spawn.getPos().x);
				output.writeFloat(spawn.getPos().y);
				output.writeString(spawn.getIndex().name().toLowerCase());
				output.writeBoolean(spawn.facingRight);
			}
		}

		@Override
		public Level read(Kryo kryo, Input input, Class<Level> type) {
			Level level = new Level();
			level.tilesetFile = Gdx.files.local(input.readString());

			ExpandableGrid<MapTile> grid = new ExpandableGrid<MapTile>();
			int minRow = input.readInt();
			int minCol = input.readInt();
			int maxRow = input.readInt();
			int maxCol = input.readInt();

			for (int row = minRow; row <= maxRow; row++) {
				for (int col = minCol; col <= maxCol; col++) {
					MapTile mapTile = kryo.readObjectOrNull(input, MapTile.class);
					grid.add(row, col, mapTile);
				}
			}
			level.tileMap = grid;

			int size = input.readInt();
			Array<EntitySpawn> spawns = new Array<EntitySpawn>();
			for (int i = 0; i < size; i++) {
				EntitySpawn spawn = new EntitySpawn();
				spawn.setPos(new Vector2(input.readFloat(), input.readFloat()));
				spawn.index = EntityIndex.get(input.readString());
				spawn.facingRight = input.readBoolean();
				spawns.add(spawn);
			}
			level.entitySpawns = spawns;

			boolean playerExists = input.readBoolean();
			if(!playerExists) {
				System.out.println("No player spawn");
				return level;
			} else {
				EntitySpawn playerSpawn = new EntitySpawn();
				playerSpawn.setPos(new Vector2(input.readFloat(), input.readFloat()));
				playerSpawn.index = EntityIndex.get(input.readString());
				playerSpawn.facingRight = input.readBoolean();
				level.setPlayerSpawn(playerSpawn);
			}
			
			return level;
		}

	}

}
