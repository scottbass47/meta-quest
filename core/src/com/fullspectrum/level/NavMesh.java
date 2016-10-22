package com.fullspectrum.level;

import static com.fullspectrum.game.GameVars.PPM_INV;

import java.awt.Point;
import java.util.Comparator;

import com.badlogic.ashley.core.Entity;
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
import com.fullspectrum.component.FSMComponent;
import com.fullspectrum.component.JumpComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.fsm.EntityState;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.level.NavLink.LinkType;
import com.fullspectrum.level.Node.NodeType;
import com.fullspectrum.level.Tile.Side;
import com.fullspectrum.utils.PhysicsUtils;

public class NavMesh {

	// Nodes
	private Array<Node> nodes;
	private ArrayMap<Point, Node> nodeMap;
	private Array<Node> edgeNodes;

	// Debug Renderer
	private ShapeRenderer sRender;

	// Data
	private Entity entity;
	private Level level;

	// Jump Stats
	private float maxSpeed;
	private float maxJumpForce;

	// Run Stats
	private float runSpeed;

	// States
	private EntityState runningState;
	private EntityState jumpingState;

	private NavMesh(Entity entity, Level level, EntityStates running, EntityStates jumping) {
		this.entity = entity;
		this.level = level;

		nodes = new Array<Node>();
		nodeMap = new ArrayMap<Point, Node>();
		edgeNodes = new Array<Node>();
		sRender = new ShapeRenderer();

		FSMComponent fsmComp = Mappers.fsm.get(entity);

		assert (fsmComp != null);

		runningState = fsmComp.fsm.getState(running);
		jumpingState = fsmComp.fsm.getState(jumping);

		maxSpeed = jumpingState.getComponent(SpeedComponent.class).maxSpeed;
		maxJumpForce = jumpingState.getComponent(JumpComponent.class).maxForce;
		runSpeed = runningState.getComponent(SpeedComponent.class).maxSpeed;

		createNodes();
		setupNodeTypes();
		setupRunConnections();
		setupFallAndOverConnections();
		setupJumpAndOverConnections();
		setupJumpConnections();
	}

	public static NavMesh createNavMesh(Entity entity, Level level, EntityStates runningState, EntityStates jumpingState) {
		return new NavMesh(entity, level, runningState, jumpingState);
	}

	public void render(SpriteBatch batch) {
		sRender.setProjectionMatrix(batch.getProjectionMatrix());
		sRender.begin(ShapeType.Line);

		// Render NavLinks first
		for (Node node : nodes) {
			for (NavLink link : node.getLinks()) {
				switch (link.type) {
				case RUN:
//					if(true) break;
					sRender.setColor(Color.BLUE);
					sRender.line(node.col + 0.5f, node.row + 0.5f, link.toNode.col + 0.5f, link.toNode.row + 0.5f);
					break;
				case FALL:
//					if(true) break;
					sRender.setColor(Color.CHARTREUSE);
//					sRender.line(node.col + 0.5f, node.row + 0.5f, node.col + 1.5f, node.row + 0.5f);
					TrajectoryData fallData = (TrajectoryData) link.data;
					for (int i = 0; i < fallData.trajectory.size - 1; i++) {
						Point2f point1 = fallData.trajectory.get(i);
						Point2f point2 = fallData.trajectory.get(i + 1);
						sRender.line(point1.x, point1.y, point2.x, point2.y);
					}
					break;
				case FALL_OVER:
//					if(true) break;
					sRender.setColor(Color.GOLD);
					// sRender.line(node.col + 0.5f, node.row + 0.5f,
					// link.toNode.col + 0.5f, node.row + 0.5f);
					// sRender.line(link.toNode.col + 0.5f, node.row + 0.5f,
					// link.toNode.col + 0.5f, link.toNode.row + 0.5f);
					sRender.line(node.col + 0.5f, node.row + 0.5f, link.toNode.col + 0.5f, link.toNode.row + 0.5f);
					break;
				case JUMP:
//					if(true) break;
					TrajectoryData jumpData = (TrajectoryData) link.data;
					for (int i = 0; i < jumpData.trajectory.size - 1; i++) {
						Color color = i < jumpData.trajectory.size / 2 ? Color.SALMON : Color.BLACK;
						Point2f point1 = jumpData.trajectory.get(i);
						Point2f point2 = jumpData.trajectory.get(i + 1);
						sRender.setColor(color);
						sRender.line(point1.x, point1.y, point2.x, point2.y);
					}
					break;
				case JUMP_OVER:
//					if(true) break;
					sRender.setColor(Color.BROWN);
					sRender.line(node.col + 0.5f, node.row + 0.5f, node.col + 0.5f, link.toNode.row + 0.5f);
					sRender.line(node.col + 0.5f, link.toNode.row + 0.5f, link.toNode.col + 0.5f, link.toNode.row + 0.5f);
					break;
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
			}
			float x1 = node.col;
			float y1 = node.row;
			float width = 8.0f * PPM_INV;
			float height = width;
			sRender.rect(x1 + (0.5f - width * 0.5f), y1 + (0.5f - height * 0.5f), width * 0.5f, height * 0.5f, width, height, 1.0f, 1.0f, 45f);
		}
		sRender.end();
	}

	private void createNodes() {
		Rectangle boundingBox = PhysicsUtils.getAABB(runningState.getFixtures());
		for (int row = 0; row < level.getHeight(); row++) {
			for (int col = 0; col < level.getWidth(); col++) {
				if (isValidNode(row, col, level, boundingBox)) {
					Node node = new Node();
					node.row = row + 1;
					node.col = col;
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

			tileOnLeft = i - 1 >= 0 && (nodes.get(i - 1).row == node.row && nodes.get(i - 1).col + 1 == node.col);
			tileOnRight = i + 1 <= nodes.size - 1 && (nodes.get(i + 1).row == node.row && nodes.get(i + 1).col - 1 == node.col);

			if (tileOnLeft && tileOnRight)
				node.type = NodeType.MIDDLE;
			else if (tileOnLeft)
				node.type = NodeType.RIGHT_EDGE;
			else if (tileOnRight)
				node.type = NodeType.LEFT_EDGE;
			else
				node.type = NodeType.SOLO;

			if (node.type != NodeType.MIDDLE) edgeNodes.add(node);
		}
	}

	private void setupRunConnections() {
		for (int i = 0; i < nodes.size; i++) {
			Node node = nodes.get(i);
			if (node.type == NodeType.LEFT_EDGE || node.type == NodeType.MIDDLE) {
				node.addLink(new NavLink(NavLink.LinkType.RUN, null, node, nodes.get(i + 1), 1.0f / runSpeed));
			}
			if (node.type == NodeType.RIGHT_EDGE || node.type == NodeType.MIDDLE) {
				node.addLink(new NavLink(NavLink.LinkType.RUN, null, node, nodes.get(i - 1), 1.0f / runSpeed));
			}
		}
	}

	private void setupFallAndOverConnections() {
		ArrayMap<Integer, Array<Node>> nodeCols = new ArrayMap<Integer, Array<Node>>();
		for (Node node : nodes) {
			if (!nodeCols.containsKey(node.col)) nodeCols.put(node.col, new Array<Node>());
			nodeCols.get(node.col).add(node);
		}
		for (Node edgeNode : edgeNodes) {
			if (edgeNode.type == NodeType.LEFT_EDGE || edgeNode.type == NodeType.SOLO) {
				if (edgeNode.col - 1 < 0 || !level.isAir(edgeNode.row, edgeNode.col - 1)) {
					if (edgeNode.type != NodeType.SOLO) continue;
				} else {
					Array<Node> fallToNodes = nodeCols.get(edgeNode.col - 1);
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
			if (edgeNode.type == NodeType.RIGHT_EDGE || edgeNode.type == NodeType.SOLO) {
				if (edgeNode.col + 1 > level.getWidth() - 1 || !level.isAir(edgeNode.row, edgeNode.col + 1)) continue;
				Array<Node> fallToNodes = nodeCols.get(edgeNode.col + 1);
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
	
	private void setupJumpAndOverConnections(){
		float maxJumpHeight = getMaxJumpHeight();
		for(Node edgeNode : edgeNodes){
			if(!edgeNode.hasLinkType(LinkType.FALL_OVER)) continue;
			Array<NavLink> fallLinks = edgeNode.getLinks(LinkType.FALL_OVER);
			for(NavLink fallLink : fallLinks){
				Node jumpTo = fallLink.fromNode;
				Node jumpFrom = fallLink.toNode;
				
				int height = jumpTo.row - jumpFrom.row;
				float tx = 1.0f / maxSpeed;
				float totalHeight = height - (GameVars.GRAVITY * 0.5f * tx * tx);
				if(totalHeight > maxJumpHeight) continue;
				float jumpForce = (float)Math.sqrt(-2 * GameVars.GRAVITY * totalHeight);
				float ty = -jumpForce / GameVars.GRAVITY; // vf = v0 + at (vf = 0 b/c top of jump)
				jumpFrom.addLink(new NavLink(LinkType.JUMP_OVER, new JumpOverData(jumpForce / maxJumpForce), jumpFrom, jumpTo, tx + ty));
			}
		}
	}

	private void setupJumpConnections() {
		Rectangle boundingBox = PhysicsUtils.getAABB(jumpingState.getFixtures());
//		int divisions = 3;
//
		int linksCreated = 0;
//		for (Node edgeNode : edgeNodes) {
//			for (int i = 1; i <= divisions; i++) {
//				for (int j = 1; j <= divisions; j++) {
//					float speed = maxSpeed / i;
//					float jumpForce = maxJumpForce / j;
//					if (edgeNode.type == NodeType.SOLO) {
//						System.out.println();
//					}
//					if (edgeNode.type == NodeType.RIGHT_EDGE || edgeNode.type == NodeType.SOLO) {
//						JumpData rightJump = getTrajectory(edgeNode.row, edgeNode.col, speed, jumpForce, boundingBox, true, edgeNode.type);
//						if (rightJump != null) {
//							edgeNode.addLink(new JumpLink(edgeNode, rightJump.toNode, rightJump.time, rightJump.trajectory, 1.0f / i, 1.0f / j));
//							linksCreated++;
//						}
//					}
//					if (edgeNode.type == NodeType.LEFT_EDGE || edgeNode.type == NodeType.SOLO) {
//						JumpData leftJump = getTrajectory(edgeNode.row, edgeNode.col, speed, jumpForce, boundingBox, false, edgeNode.type);
//						if (leftJump != null) {
//							edgeNode.addLink(new JumpLink(edgeNode, leftJump.toNode, leftJump.time, leftJump.trajectory, 1.0f / i, 1.0f / j));
//							linksCreated++;
//						}
//					}
//				}
//			}
//		}
		
		for(Node edgeNode : edgeNodes){
			float fromX = edgeNode.col + 0.5f;
			float fromY = edgeNode.row + boundingBox.height * 0.5f;
			
			float totalTime = (-maxJumpForce - (float)Math.sqrt(maxJumpForce * maxJumpForce - 2 * GameVars.GRAVITY * fromY)) / GameVars.GRAVITY;
			float maxHeight = getMaxJumpHeight();
			float maxDistance = totalTime * maxSpeed;
			float vertexX = (-maxJumpForce / GameVars.GRAVITY) * maxSpeed; // x-coordinate of highest point along arc
			
			Array<Node> toNodes = new Array<Node>();
			for(Node node : nodes){
				float toX = node.col + 0.5f;
				float toY = node.row + boundingBox.height * 0.5f;
				
				if(Math.abs(toX - fromX) > maxDistance || toX == fromX) continue;
			
				if(toY - fromY > maxHeight) continue;
				float tx = Math.abs((toX - fromX) / maxSpeed); // The shortest time it takes to get to this x position
				float maxY = maxJumpForce * tx + 0.5f * GameVars.GRAVITY * tx * tx; // The maximum height you could be at a given time
				if(maxY + fromY < toY && toX > vertexX) continue;
				
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
				float maxFallDistance = maxSpeed * t1 + 1;
				if(maxFallDistance < Math.abs(toX - fallX) || toY > fromY || (fallX >= toX && fallRight) || (fallX <= toX && !fallRight)) canFall = false;
				boolean right = false;
				boolean left = false;
				if(canFall){
					float t2 = (float)Math.sqrt((2 * (toY - fromY)) / GameVars.GRAVITY);
					float speedNeeded = Math.abs((toX - fallX) / t2);
					if(speedNeeded < maxSpeed){
						float cost = 1.0f / maxSpeed + t2;
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
					if(speedNeeded > maxSpeed) continue;
					
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
		Node targetNode = nodeMap.get(new Point((int)toY, (int)toX));
		boolean finished = false;
		Array<Point2f> points = new Array<Point2f>();
		Node toNode = null;
		float time = 0;
		while (!finished) {
			Point2f point = new Point2f(fromX + speed * time * (right ? 1.0f : -1.0f), fromY + jumpForce * time + 0.5f * GameVars.GRAVITY * time * time);
			if (point.y < level.getHeight() && !level.inBounds(point.x, point.y)) return null;
			if (!isValidPoint(point.x, point.y, boundingBox)) {
				return null;
			}
			points.add(point);
			time += interval;
			float deriv = jumpForce + GameVars.GRAVITY * time;
			if (deriv >= 0) continue;
			Node node = nodeMap.get(new Point((int) point.y, (int) point.x));
			if (node != null && node.row == targetNode.row && node.col == targetNode.col) {
				toNode = node;
				finished = true;
			}
		}
		return new TrajectoryData(points, time, toNode, speed, jumpForce);
	}

	// -distance = (1/2) * gravity * t^2
	// -2distance / gravity = t ^ 2
	// sqrt(-2distance / gravity) = t
	private float getFallingCost(float distance) {
		float cost = 1.0f / runSpeed;
		return cost + (float) Math.sqrt(-2.0f * distance / GameVars.GRAVITY);
	}

	private boolean isValidPoint(float x, float y, Rectangle boundingBox) {
		float hw = boundingBox.width * 0.5f;
		float hh = boundingBox.height * 0.5f;
		float minX = x - hw;
		float minY = y - hh;
		float maxX = x + hw;
		float maxY = y + hh;

		for (int row = (int) minY; row <= (int) maxY; row++) {
			for (int col = (int) minX; col <= (int) maxX; col++) {
				if (!level.inBounds(row, col)) return true;
				if (!level.isAir(row, col)) {
					return false;
				}
			}
		}
		return true;
	}

	private static boolean isValidNode(int row, int col, Level level, Rectangle boundingBox) {
		if (row + 1 > level.getHeight() || !level.tileAt(row, col).isOpen(Side.NORTH)) return false;
		int tilesTall = (int) (boundingBox.height + 1.0f);
		for (int i = 1; i <= tilesTall; i++) {
			if (!level.inBounds(row + i, (int) col)) return true;
			Tile t = level.tileAt(row + i, col);
			if (!t.isAir()) return false;
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
		return nodeMap.get(new Point((int) y, (int) x));
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
		return getNearestNode(body, 0.0f, 0.0f);
	}
	
	public Node getNearestNode(Body body, float xOff, float yOff){
		if(body.getLinearVelocity().y != 0) return null;
		Vector2 position = body.getPosition();
		float x = position.x + xOff;
		float y = position.y + yOff;
		Node node = getNodeAt(x, y);
		if(node != null) return node;
		int closestCol = (int)(x + 0.5f);
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

	public Array<Node> getEdgeNodes() {
		return edgeNodes;
	}

	public Array<Node> getNodes() {
		return nodes;
	}

	public ArrayMap<Point, Node> getNodeMap() {
		return nodeMap;
	}

	public Entity getEntity() {
		return entity;
	}

	public Level getLevel() {
		return level;
	}
	
	public float getMaxJumpForce(){
		return maxJumpForce;
	}
	
	public float getMaxSpeed(){
		return maxSpeed;
	}
}
