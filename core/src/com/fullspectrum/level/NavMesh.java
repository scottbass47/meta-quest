package com.fullspectrum.level;

import static com.fullspectrum.game.GameVars.PPM_INV;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.entity.EntityStats;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.level.NavLink.LinkType;
import com.fullspectrum.level.Node.NodeType;
import com.fullspectrum.level.tiles.MapTile;
import com.fullspectrum.level.tiles.MapTile.TileType;
import com.fullspectrum.utils.Maths;
import com.fullspectrum.utils.RenderUtils;
import com.fullspectrum.utils.StringUtils;

public class NavMesh{

	// Version
	private static final int VERSION = 3;
	
	// Nodes
	private Array<Node> nodes;
	private ArrayMap<Point, Node> nodeMap;
	private Array<Node> edgeNodes;

	// Debug Renderer
	private ShapeRenderer sRender;

	// Data
	private Level level;
	private Rectangle boundingBox;
	private EntityStats stats;

	// Jump Stats
	private float maxAirSpeed;
	private float maxJumpForce;

	// Run Stats
	private float maxRunSpeed;
	
	// Climb Stats
	private float climbSpeed;
	
	// Nav Meshes
	private static ArrayMap<EntityIndex, NavMesh> meshMap;
	private static ArrayMap<EntityIndex, Rectangle> hitBoxes;
	
	static{
		meshMap = new ArrayMap<EntityIndex, NavMesh>();
		hitBoxes = new ArrayMap<EntityIndex, Rectangle>();
		
		// Manually put in hitbox info
		hitBoxes.put(EntityIndex.GUN_GREMLIN,   Maths.scl(EntityIndex.GUN_GREMLIN.getHitBox(),   GameVars.PPM_INV));
		hitBoxes.put(EntityIndex.GRUNT_GREMLIN, Maths.scl(EntityIndex.GRUNT_GREMLIN.getHitBox(), GameVars.PPM_INV));
	}
	
	private NavMesh(Level level, EntityStats stats) {
		init(level, stats);
		calculate();
	}
	
	private NavMesh(EntityStats stats){
		init(null, stats);	
	}
	
	private void init(Level level, EntityStats stats){
		this.level = level;
		this.stats = stats;
		this.boundingBox = hitBoxes.get(stats.getEntityIndex());
		
		if(stats != null) updateStats();
		
		nodes = new Array<Node>();
		nodeMap = new ArrayMap<Point, Node>();
		edgeNodes = new Array<Node>();
		sRender = new ShapeRenderer();
		
		meshMap.put(stats.getEntityIndex(), this);
	}
	
	private void calculate(){
		createNodes();
		setupNodeTypes();
		setupRunConnections();
		setupFallAndOverConnections();
		setupJumpAndOverConnections();
		setupJumpConnections();
		setupLadderConnections();
	}
	
	private void updateStats(){
		this.maxAirSpeed = stats.get("air_speed");
		this.maxJumpForce = stats.get("jump_force");
		this.maxRunSpeed = stats.get("ground_speed");
		this.climbSpeed = stats.get("air_speed");
	}

	public static NavMesh createNavMesh(Level level, EntityStats stats) {
		String fileName = stats.getEntityIndex().getName() + "-" + level.getName() + ".nav";
		final Level levelCopy = level;
		Rectangle boundingBox = hitBoxes.get(stats.getEntityIndex());
		
		Kryo kryo = new Kryo();
		kryo.setReferences(false);
		kryo.register(NavMesh.class, getSerializer(), 10);
		kryo.register(Node.class, Node.getSerializer(), 11);
		kryo.register(NavLink.class, NavLink.getSerializer(), 12);
		kryo.register(TrajectoryData.class, TrajectoryData.getSerializer(), 13);
		kryo.register(JumpOverData.class, JumpOverData.getSerializer(), 14);
		kryo.register(EntityStats.class, EntityStats.getSerializer(), 15);
		if(Gdx.files.local(fileName).exists()){
			try {
				Input input = new Input(new FileInputStream(Gdx.files.local(fileName).path()));
				int version = input.readInt();
				int levelHash = input.readInt();
				int statsHash = input.readInt();
				Rectangle rect = new Rectangle(input.readFloat(), input.readFloat(), input.readFloat(), input.readFloat());
				if(version == VERSION && levelHash == level.hashCode() && statsHash == stats.hashCode() && rect.equals(boundingBox)){
					NavMesh mesh = kryo.readObject(input, NavMesh.class);
					Gdx.app.log("Nav Mesh", "Loaded " + StringUtils.toTitleCase(stats.getEntityIndex().getName()) + " mesh for level " + level.getName());
					input.close();
					mesh.level = levelCopy;
					for(Node n : mesh.nodes){
						n.tile = levelCopy.tileAt(n.row, n.col);
					}
					return mesh;
				}
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		Gdx.app.log("Nav Mesh", "Creating " + StringUtils.toTitleCase(stats.getEntityIndex().getName()) + " mesh for level " + level.getName() + "...");
		NavMesh mesh = new NavMesh(level, stats);
		try {
			Output output = new Output(new FileOutputStream(Gdx.files.local(fileName).path()));
			output.writeInt(VERSION);
			output.writeInt(level.hashCode());
			output.writeInt(stats.hashCode());
			output.writeFloat(boundingBox.x);
			output.writeFloat(boundingBox.y);
			output.writeFloat(boundingBox.width);
			output.writeFloat(boundingBox.height);
			kryo.writeObject(output, mesh);
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return mesh;
	}

	public void render(SpriteBatch batch) {
		sRender.setProjectionMatrix(batch.getProjectionMatrix());
		sRender.begin(ShapeType.Line);

		// Render NavLinks first
		for (Node node : nodes) {
			for (NavLink link : node.getLinks()) {
				switch (link.type) {
				case RUN:
					sRender.setColor(Color.BLUE);
					sRender.line(node.col + 0.5f, node.row + 0.5f, link.toNode.col + 0.5f, link.toNode.row + 0.5f);
					break;
				case FALL:
					sRender.setColor(Color.CHARTREUSE);
					TrajectoryData fallData = (TrajectoryData) link.data;
					RenderUtils.renderTrajectory(sRender, link.isDirRight() ? node.col + 1.5f : node.col - 0.5f, node.row + 0.5f, link.isDirRight(), fallData.time, fallData.jumpForce, fallData.speed, 50);
					break;
				case FALL_OVER:
					sRender.setColor(Color.GOLD);
					sRender.line(node.col + 0.5f, node.row + 0.5f, link.toNode.col + 0.5f, link.toNode.row + 0.5f);
					break;
				case JUMP:
					TrajectoryData jumpData = (TrajectoryData) link.data;
					sRender.setColor(Color.BLACK);
					RenderUtils.renderTrajectory(sRender, node.col + 0.5f, node.row + 0.5f, link.isDirRight(), jumpData.time, jumpData.jumpForce, jumpData.speed, 50);
					break;
				case JUMP_OVER:
					sRender.setColor(Color.BROWN);
					sRender.line(node.col + 0.5f, node.row + 0.5f, node.col + 0.5f, link.toNode.row + 0.5f);
					sRender.line(node.col + 0.5f, link.toNode.row + 0.5f, link.toNode.col + 0.5f, link.toNode.row + 0.5f);
					break;
				case CLIMB:
					sRender.setColor(Color.MAROON);
					sRender.line(node.col + 0.5f, node.row + 0.5f, link.toNode.col + 0.5f, link.toNode.row + 0.5f);
				default:
					break;
				}
			}
		}
		sRender.end();
		sRender.begin(ShapeType.Filled);
		// Render Nodes
		for (Node node : nodes) {
			switch (node.type) {
			case MIDDLE:
				sRender.setColor(Color.RED);
				break;
			case LEFT_EDGE:
			case RIGHT_EDGE:
				sRender.setColor(Color.CYAN);
				break;
			case SOLO:
				sRender.setColor(Color.FOREST);
				break;
			case LADDER:
				sRender.setColor(Color.PURPLE);
				break;
			}
			float x1 = node.col;
			float y1 = node.row;
			float width = 8.0f * PPM_INV;
			float height = width;
			sRender.rect(x1 + (0.5f - width * 0.5f), y1 + (0.5f - height * 0.5f), width * 0.5f, height * 0.5f, width, height, 1.0f, 1.0f, 45f);
		}
		sRender.end();
	}
	
	public static boolean usesNavMesh(EntityIndex index) {
		return hitBoxes.containsKey(index);
	}
	
	public static NavMesh get(EntityIndex index){
		if(!meshMap.containsKey(index)) throw new RuntimeException("No mesh loaded for entity " + index);
		return meshMap.get(index);
	}
	
	private void createNodes() {
		ExpandableGrid<MapTile> tileMap = level.getTileMap();
		for (int row = tileMap.getMinRow(); row <= tileMap.getMaxRow(); row++) {
			for (int col = tileMap.getMinCol(); col <= tileMap.getMaxCol(); col++) {
				if (isValidNode(row, col, level, boundingBox) || (row + 1 < level.getMaxRow() && level.isLadder(row + 1, col))) {
					Node node = new Node();
					node.row = row + 1;
					node.col = col;
					node.tile = level.tileAt(row + 1, col);
					nodes.add(node);
					nodeMap.put(new Point(node.row, node.col), node);
				}
			}
		}
	}

	private void setupNodeTypes() {
		for (int i = 0; i < nodes.size; i++) {
			Node node = nodes.get(i);
			boolean tileOnRight = false;
			boolean tileOnLeft = false;

			tileOnLeft = getAdjacentNode(node, Direction.LEFT) != null && isSolidNode(getAdjacentNode(node, Direction.LEFT));
			tileOnRight = getAdjacentNode(node, Direction.RIGHT) != null && isSolidNode(getAdjacentNode(node, Direction.RIGHT));
			
			if(node.getTile() != null && node.getTile().getType() == TileType.LADDER)
				node.type = NodeType.LADDER;
			else if (tileOnLeft && tileOnRight)
				node.type = NodeType.MIDDLE;
			else if (tileOnLeft)
				node.type = NodeType.RIGHT_EDGE;
			else if (tileOnRight)
				node.type = NodeType.LEFT_EDGE;
			else
				node.type = NodeType.SOLO;

			if (node.type != NodeType.MIDDLE && node.type != NodeType.LADDER) edgeNodes.add(node);
		}
	}

	private void setupRunConnections() {
		for (int i = 0; i < nodes.size; i++) {
			Node node = nodes.get(i);
			if (node.type == NodeType.LEFT_EDGE || node.type == NodeType.MIDDLE || ((node.type == NodeType.RIGHT_EDGE || node.type == NodeType.SOLO) && compareAdjacentNode(node, Direction.RIGHT, NodeType.LADDER))) {
				node.addLink(new NavLink(NavLink.LinkType.RUN, null, node, getAdjacentNode(node, Direction.RIGHT), 1.0f / maxRunSpeed));
			}
			if (node.type == NodeType.RIGHT_EDGE || node.type == NodeType.MIDDLE || ((node.type == NodeType.LEFT_EDGE || node.type == NodeType.SOLO) && compareAdjacentNode(node, Direction.LEFT, NodeType.LADDER))) {
				node.addLink(new NavLink(NavLink.LinkType.RUN, null, node, getAdjacentNode(node, Direction.LEFT), 1.0f / maxRunSpeed));
			}
		}
	}

	private void setupFallAndOverConnections() {
		ArrayMap<Integer, Array<Node>> nodeCols = new ArrayMap<Integer, Array<Node>>();
		for (Node node : nodes) {
			if(isLadderNode(node) && !isSolidNode(node)) continue;
			if (!nodeCols.containsKey(node.col)) nodeCols.put(node.col, new Array<Node>());
			nodeCols.get(node.col).add(node);
		}
		for (Node edgeNode : edgeNodes) {
			if (edgeNode.type == NodeType.LEFT_EDGE || edgeNode.type == NodeType.SOLO) {
				if (edgeNode.col - 1 < level.getMinCol() || level.isSolid(edgeNode.row, edgeNode.col - 1) || compareAdjacentNode(edgeNode, Direction.LEFT, NodeType.LADDER)) {
					if (edgeNode.type != NodeType.SOLO) continue;
				} else {
					Array<Node> fallToNodes = nodeCols.get(edgeNode.col - 1);
					
					// If fallToNodes is null that means that you're at a cliff edge that falls off the map
					if(fallToNodes != null) {
						fallToNodes.sort(new Comparator<Node>() {
							@Override
							public int compare(Node o1, Node o2) {
								return o1.row < o2.row ? 1 : -1;
							}
						});
						for (Node fallNode : fallToNodes) {
							if (fallNode.row > edgeNode.row) continue;
							edgeNode.addLink(new NavLink(NavLink.LinkType.FALL_OVER, null, edgeNode, fallNode, getFallingCost(edgeNode.row - fallNode.row)));
							break;
						}
					}
				}
			}
			if (edgeNode.type == NodeType.RIGHT_EDGE || edgeNode.type == NodeType.SOLO) {
				if (edgeNode.col + 1 > level.getMaxCol() || level.isSolid(edgeNode.row, edgeNode.col + 1) || compareAdjacentNode(edgeNode, Direction.RIGHT, NodeType.LADDER)) continue;
				Array<Node> fallToNodes = nodeCols.get(edgeNode.col + 1);
				
				// If fallToNodes is null that means that you're at a cliff edge that falls off the map
				if(fallToNodes != null) {
					fallToNodes.sort(new Comparator<Node>() {
						@Override
						public int compare(Node o1, Node o2) {
							return o1.row < o2.row ? 1 : -1;
						}
					});
					for (Node fallNode : fallToNodes) {
						if (fallNode.row > edgeNode.row) continue;
						edgeNode.addLink(new NavLink(NavLink.LinkType.FALL_OVER, null, edgeNode, fallNode, getFallingCost(edgeNode.row - fallNode.row)));
						break;
					}
				}
			}
		}
	}
	
	private void setupJumpAndOverConnections(){
		float maxJumpHeight = getMaxJumpHeight();
		for(Node edgeNode : edgeNodes){
			if(!edgeNode.hasLinkType(LinkType.FALL_OVER)) continue;
			Array<NavLink> fallLinks = edgeNode.getLinks(LinkType.FALL_OVER);
			for(NavLink fallLink : fallLinks){
				Node jumpTo = fallLink.fromNode;
				Node jumpFrom = fallLink.toNode;
				
				int height = jumpTo.row - jumpFrom.row;
				float tx = 1.0f / maxAirSpeed;
				float totalHeight = height - (GameVars.GRAVITY * 0.5f * tx * tx);
				if(totalHeight > maxJumpHeight) continue;
				float jumpForce = (float)Math.sqrt(-2 * GameVars.GRAVITY * totalHeight);
				float ty = -jumpForce / GameVars.GRAVITY; // vf = v0 + at (vf = 0 b/c top of jump)
				jumpFrom.addLink(new NavLink(LinkType.JUMP_OVER, new JumpOverData(jumpForce), jumpFrom, jumpTo, tx + ty));
			}
		}
	}

	private void setupJumpConnections() {
		int linksCreated = 0;
		
		for(Node edgeNode : edgeNodes){
			float fromX = edgeNode.col + 0.5f;
			float fromY = edgeNode.row + boundingBox.height * 0.5f;
			
			float totalTime = (-maxJumpForce - (float)Math.sqrt(maxJumpForce * maxJumpForce - 2 * GameVars.GRAVITY * fromY)) / GameVars.GRAVITY;
			float maxHeight = getMaxJumpHeight();
			float maxDistance = totalTime * maxAirSpeed;
			float vertexX = (-maxJumpForce / GameVars.GRAVITY) * maxAirSpeed; // x-coordinate of highest point along arc
			
			Array<Node> toNodes = new Array<Node>();
			for(Node node : nodes){
				if(isLadderNode(node)) continue;
				float toX = node.col + 0.5f;
				float toY = node.row + boundingBox.height * 0.5f;
				
				if(Math.abs(toX - fromX) > maxDistance || toX == fromX) continue;
			
				if(toY - fromY > maxHeight) continue;
				float tx = Math.abs((toX - fromX) / maxAirSpeed); // The shortest time it takes to get to this x position
				float maxY = maxJumpForce * tx + 0.5f * GameVars.GRAVITY * tx * tx; // The maximum height you could be at a given time
				if(maxY + fromY < toY && toX > vertexX + fromX) continue;
				
				toNodes.add(node);
			}
			for(Node toNode : toNodes){
				float toX = toNode.col + 0.5f;
				float toY = toNode.row + boundingBox.height * 0.5f;
				float fallX = toX > fromX ? fromX + 1.0f : fromX - 1.0f; // you have to fall one meter from the node
				boolean fallRight = toX > fromX;
				
				// Falling
				boolean canFall = true;
				float t1 = (float)Math.sqrt((-2 * fromY) / GameVars.GRAVITY);
				float maxFallDistance = maxAirSpeed * t1 + 1;
				if(maxFallDistance < Math.abs(toX - fallX) || toY > fromY || (fallX >= toX && fallRight) || (fallX <= toX && !fallRight)) canFall = false;
				boolean right = false;
				boolean left = false;
				if(canFall){
					float t2 = (float)Math.sqrt((2 * (toY - fromY)) / GameVars.GRAVITY);
					float speedNeeded = Math.abs((toX - fallX) / t2);
					if(speedNeeded < maxAirSpeed){
						float cost = 1.0f / maxAirSpeed + t2;
						if(edgeNode.type == NodeType.RIGHT_EDGE || edgeNode.type == NodeType.SOLO){
							TrajectoryData data = getTrajectory(fallX, fromY, toX, toY, speedNeeded, 0, boundingBox, true);
							if(data != null){
								edgeNode.addLink(new NavLink(LinkType.FALL, data, edgeNode, toNode, cost));
								linksCreated++;
								if(edgeNode.type == NodeType.RIGHT_EDGE) continue;
								right = true;
							}
						}
						if(edgeNode.type == NodeType.LEFT_EDGE || edgeNode.type == NodeType.SOLO){
							TrajectoryData data = getTrajectory(fallX, fromY, toX, toY, speedNeeded, 0, boundingBox, false);
							if(data != null){
								edgeNode.addLink(new NavLink(LinkType.FALL, data, edgeNode, toNode, cost));
								linksCreated++;
								if(edgeNode.type == NodeType.LEFT_EDGE) continue;
								left = true;
							}
						}
						if(right && left) continue;
					}
				}
					
				// Jumping
				int intervals = 10;
				for(int i = 1; i <= intervals; i++){
					if(right && left) break;
					float jForce = i * (maxJumpForce / intervals);
					// vf^2 = v0^2 + 2a * deltaY
					// 0 = jForce^2 + 2grav * deltaY
					// deltaY = -jForce^2 / 2grav
					float jHeight = (-jForce * jForce) / (2 * GameVars.GRAVITY);
					if(jHeight < toY - fromY) continue;
					float t = (-jForce - (float)Math.sqrt(jForce * jForce - 2 * GameVars.GRAVITY * (fromY - toY))) / GameVars.GRAVITY; 
					float speedNeeded = (float)Math.abs((toX - fromX) / t);
					if(speedNeeded > maxAirSpeed) continue;
					
					if(edgeNode.type == NodeType.RIGHT_EDGE || edgeNode.type == NodeType.SOLO && !right){
						TrajectoryData data = getTrajectory(fromX, fromY, toX, toY, speedNeeded, jForce, boundingBox, true);
						if(data != null){
							edgeNode.addLink(new NavLink(LinkType.JUMP, data, edgeNode, toNode, t));
							linksCreated++;
							right = true;
							if(edgeNode.type == NodeType.RIGHT_EDGE) break;
						}
					}
					if(edgeNode.type == NodeType.LEFT_EDGE || edgeNode.type == NodeType.SOLO && !left){
						TrajectoryData data = getTrajectory(fromX, fromY, toX, toY, speedNeeded, jForce, boundingBox, false);
						if(data != null){
							edgeNode.addLink(new NavLink(LinkType.JUMP, data, edgeNode, toNode, t));
							linksCreated++;
							left = true;
							if(edgeNode.type == NodeType.LEFT_EDGE) break;
						}
					}
				}
			}
		}
		

		Gdx.app.debug("NavMesh", "Links created - " + linksCreated);
	}

	private TrajectoryData getTrajectory(float fromX, float fromY, float toX, float toY, float speed, float jumpForce, Rectangle boundingBox, boolean right) {
		float interval = 0.0015f;
		Node targetNode = nodeMap.get(new Point((int)(toY - boundingBox.height * 0.5f), Maths.toGridCoord(toX)));
		boolean finished = false;
		Node toNode = null;
		float time = 0;
		while (!finished) {
			Point2f point = new Point2f(fromX + speed * time * (right ? 1.0f : -1.0f), fromY + jumpForce * time + 0.5f * GameVars.GRAVITY * time * time);
			if (point.y < level.getMaxRow() && !level.inBounds(point.x, point.y)) return null;
			if (!level.isValidPoint(point.x, point.y, boundingBox)) {
				return null;
			}
			time += interval;
			float deriv = jumpForce + GameVars.GRAVITY * time;
			if (deriv >= 0) continue;
			Node node = nodeMap.get(new Point(Maths.toGridCoord(point.y - boundingBox.height * 0.5f), Maths.toGridCoord(point.x)));
			if (node != null && node.row == targetNode.row && node.col == targetNode.col) {
				toNode = node;
				finished = true;
			}
		}
		return new TrajectoryData(time, toNode, speed, jumpForce);
	}
	
	private void setupLadderConnections(){
		for(Node node : nodes){
			if(!isLadderNode(node))continue;
			Node top = getAdjacentNode(node, Direction.UP);
			Node bottom = getAdjacentNode(node, Direction.DOWN);
			Node left = getAdjacentNode(node, Direction.LEFT);
			Node right = getAdjacentNode(node, Direction.RIGHT);
			if(top != null){
				node.addLink(new NavLink(LinkType.CLIMB, null, node, top, 1.0f / climbSpeed));
			}
			if(bottom != null){
				node.addLink(new NavLink(LinkType.CLIMB, null, node, bottom, 1.0f / climbSpeed));
			}
			if(left != null){
				node.addLink(new NavLink(LinkType.RUN, null, node, left, 1.0f / maxRunSpeed));
			}
			if(right != null){
				node.addLink(new NavLink(LinkType.RUN, null, node, right, 1.0f / maxRunSpeed));
			}
		}
	}

	// -distance = (1/2) * gravity * t^2
	// -2distance / gravity = t ^ 2
	// sqrt(-2distance / gravity) = t
	private float getFallingCost(float distance) {
		float cost = 1.0f / maxRunSpeed;
		return cost + (float) Math.sqrt(-2.0f * distance / GameVars.GRAVITY);
	}

	/**
	 * Row and col represent the position of the TILE underneath. This method returns true if the TILE at position row, col 
	 * has space above it to accommodate the bounding box.
	 * 
	 * @param row
	 * @param col
	 * @param level
	 * @param boundingBox
	 * @return
	 */
	private static boolean isValidNode(int row, int col, Level level, Rectangle boundingBox) {
		if (row + 1 >= level.getMaxRow() || level.isSolid(row + 1, col) || !level.isSolid(row, col)) return false;
		int tilesTall = (int) (boundingBox.height + 1.0f);
		for (int i = 1; i <= tilesTall; i++) {
			if (!level.inBounds(row + i, col)) return true;
			if (level.isSolid(row + i, col)) return false;
		}
		return true;
	}
	
	// vf^2 = v0^2 + 2a*y
	// 0 = v0^2 + 2ay
	// -v0^2 = 2ay
	// y = -v0^2 / 2a
	public float getMaxJumpHeight(){
		return (-maxJumpForce * maxJumpForce) / (2 * GameVars.GRAVITY);
	}

	public Node getNodeAt(int row, int col) {
		return nodeMap.get(new Point(row, col));
	}

	public Node getNodeAt(float x, float y) {
		return nodeMap.get(new Point(Maths.toGridCoord(y), Maths.toGridCoord(x)));
	}
	
	public boolean compareAdjacentNode(Node node, Direction direction, NodeType type){
		Node adjacent = getAdjacentNode(node, direction);
		if(adjacent == null) return false;
		return type == adjacent.type;
	}
	
	public Node getAdjacentNode(Node node, Direction direction){
		int row = node.row;
		int col = node.col;
		switch(direction){
		case DOWN:
			row--;
			break;
		case LEFT:
			col--;
			break;
		case RIGHT:
			col++;
			break;
		case UP:
			row++;
			break;
		}
		return nodeMap.get(new Point(row, col));
	}
	
	public boolean isSolidNode(Node node){
		return level.isSolid(node.row - 1, node.col);
	}
	
	public boolean isLadderNode(Node node){
		return node.type == NodeType.LADDER;
	}
	
	/**
	 * If the body is not grounded, this function will always return null.
	 * Otherwise, it will return the closest node to the body.
	 * 
	 * <br><br>Note: This method only checks one block to the right or left to find what node the body's on.
	 * 
	 * @param body
	 * @return
	 */
	public Node getNearestNode(Body body){
		return getNearestNode(body, 0.0f, 0.0f, true);
	}
	
	public Node getNearestNode(Body body, float xOff, float yOff, boolean grounded){
		if(body.getLinearVelocity().y != 0 && grounded) return null;
		Vector2 position = body.getPosition();
		float x = position.x + xOff;
		float y = position.y + yOff;
		Node node = getNodeAt(x, y);
		if(node != null) return node;
		int closestCol = Maths.toGridCoord(x + 0.5f);
		Node right = getNodeAt(x + 1.0f, y);
		Node left = getNodeAt(x - 1.0f, y);
		if(closestCol > x){
			node = right != null ? right : left;
		}
		else{
			node = left != null ? left : right;
		}
		return node;
	}
	
	public Node getShadowNode(Body body, float xOff, float yOff){
		while(body.getPosition().y + yOff > 0){
			Node next = getNearestNode(body, xOff, yOff, false);
			if(next != null) return next;
			yOff -= 1.0f;
		}
		return null;
	}
	
	public Array<Node> getNodes(Node fromNode, int radius){
		Array<Node> ret = new Array<Node>();
		for(Node node : nodes){
			// x^2 + y^2 = r^2
			int xSqr = (fromNode.col - node.col) * (fromNode.col - node.col);
			int ySqr = (fromNode.row - node.row) * (fromNode.row - node.row);
			if(xSqr + ySqr < radius * radius) ret.add(node);
		}
		return ret;
	}
	
	public Node getRandomNode(Node fromNode, int radius){
		Array<Node> possibleNodes = getNodes(fromNode, radius);
		return possibleNodes.get((int)(Math.random() * possibleNodes.size));
	}
	
	public Node getRandomNode(){
		return nodes.get((int)(Math.random() * nodes.size));
	}
	
	public Array<Node> getEdgeNodes() {
		return edgeNodes;
	}

	public Array<Node> getNodes() {
		return nodes;
	}

	public ArrayMap<Point, Node> getNodeMap() {
		return nodeMap;
	}

//	public Entity getEntity() {
//		return entity;
//	}

	public Level getLevel() {
		return level;
	}
	
	public float getMaxJumpForce(){
		return maxJumpForce;
	}
	
	public float getMaxSpeed(){
		return maxAirSpeed;
	}
	
	public enum Direction{
		RIGHT,
		LEFT,
		UP,
		DOWN
	}
	
	public static NavMeshSerializer getSerializer(){
		return new NavMeshSerializer();
	}
	
	public static class NavMeshSerializer extends Serializer<NavMesh>{
		@Override
		public void write(Kryo kryo, Output output, NavMesh object) {
			kryo.writeObject(output, object.stats);
			
			output.writeShort((short)object.nodes.size);
			for(Node n : object.nodes){
				kryo.writeObject(output, n);
				output.writeByte((byte)n.getLinks().size);
				for(NavLink link : n.getLinks()){
					kryo.writeObject(output, link);
				}
			}
		}

		@Override
		public NavMesh read(Kryo kryo, Input input, Class<NavMesh> type) {
			NavMesh mesh = new NavMesh(kryo.readObject(input, EntityStats.class));
			int size = input.readShort();
			for(int i = 0; i < size; i++){
				Node node = kryo.readObject(input, Node.class);
				mesh.nodes.add(node);
				mesh.nodeMap.put(new Point(node.row, node.col), node);
				if(node.type == NodeType.LEFT_EDGE || node.type == NodeType.SOLO || node.type == NodeType.RIGHT_EDGE) {
					mesh.edgeNodes.add(node);
				}
				byte numLinks = input.readByte();
				for(int j = 0; j < numLinks; j++){
					NavLink link = kryo.readObject(input, NavLink.class);
					node.addLink(link);
					link.fromNode = node;
				}
			}
			for(Node n : mesh.nodes){
				for(NavLink link : n.getLinks()){
					link.toNode = mesh.nodeMap.get(new Point(link.toRow, link.toCol));
					if(link.data instanceof TrajectoryData){
						TrajectoryData data = (TrajectoryData) link.data;
						data.toNode = mesh.nodeMap.get(new Point(data.toRow, data.toCol));
					}
				}
			}
			return mesh;
		}
	}
}