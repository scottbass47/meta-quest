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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.component.FSMComponent;
import com.fullspectrum.component.JumpComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.fsm.EntityState;
import com.fullspectrum.fsm.State;
import com.fullspectrum.game.GameVars;
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

	private NavMesh(Entity entity, Level level, State running, State jumping) {
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
		
		maxSpeed = jumpingState.getComponent(SpeedComponent.class).speed;
		maxJumpForce = jumpingState.getComponent(JumpComponent.class).force;
		runSpeed = runningState.getComponent(SpeedComponent.class).speed;
		
		createNodes();
		setupNodeTypes();
		setupRunConnections();
		setupFallConnections();
		setupJumpConnections();
	}

	public static NavMesh createNavMesh(Entity entity, Level level, State runningState, State jumpingState) {
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
					sRender.setColor(Color.BLUE);
					sRender.line(node.col + 0.5f, node.row + 0.5f, link.toNode.col + 0.5f, link.toNode.row + 0.5f);
					break;
				case FALL:
					sRender.setColor(Color.GOLD);
					// sRender.line(node.col + 0.5f, node.row + 0.5f,
					// link.toNode.col + 0.5f, node.row + 0.5f);
					// sRender.line(link.toNode.col + 0.5f, node.row + 0.5f,
					// link.toNode.col + 0.5f, link.toNode.row + 0.5f);
					sRender.line(node.col + 0.5f, node.row + 0.5f, link.toNode.col + 0.5f, link.toNode.row + 0.5f);
					break;
				case JUMP:
					JumpLink jumpLink = (JumpLink) link;
					for (int i = 0; i < jumpLink.trajectory.size - 1; i++) {
						Color color = i < jumpLink.trajectory.size / 2 ? Color.SALMON : Color.BLACK;
						Point2f point1 = jumpLink.trajectory.get(i);
						Point2f point2 = jumpLink.trajectory.get(i + 1);
						sRender.setColor(color);
						sRender.line(point1.x, point1.y, point2.x, point2.y);
					}
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
			
			if(node.type != NodeType.MIDDLE)
				edgeNodes.add(node);
		}
	}

	private void setupRunConnections() {
		for (int i = 0; i < nodes.size; i++) {
			Node node = nodes.get(i);
			if (node.type == NodeType.LEFT_EDGE || node.type == NodeType.MIDDLE) {
				node.addLink(new NavLink(NavLink.LinkType.RUN, node, nodes.get(i + 1), 1.0f / runSpeed));
			}
			if (node.type == NodeType.RIGHT_EDGE || node.type == NodeType.MIDDLE) {
				node.addLink(new NavLink(NavLink.LinkType.RUN, node, nodes.get(i - 1), 1.0f / runSpeed));
			}
		}
	}

	private void setupFallConnections() {
		ArrayMap<Integer, Array<Node>> nodeCols = new ArrayMap<Integer, Array<Node>>();
		for (Node node : nodes) {
			if (!nodeCols.containsKey(node.col)) nodeCols.put(node.col, new Array<Node>());
			nodeCols.get(node.col).add(node);
		}
		for (Node edgeNode : edgeNodes) {
			if (edgeNode.type == NodeType.LEFT_EDGE || edgeNode.type == NodeType.SOLO) {
				if (edgeNode.col - 1 < 0 || !level.isAir(edgeNode.row, edgeNode.col - 1)) {
					if (edgeNode.type != NodeType.SOLO) continue;
				}
				Array<Node> fallToNodes = nodeCols.get(edgeNode.col - 1);
				fallToNodes.sort(new Comparator<Node>() {
					@Override
					public int compare(Node o1, Node o2) {
						return o1.row < o2.row ? 1 : -1;
					}
				});
				for (Node fallNode : fallToNodes) {
					if (fallNode.row > edgeNode.row) continue;
					edgeNode.addLink(new NavLink(NavLink.LinkType.FALL, edgeNode, fallNode, getFallingCost(edgeNode.row - fallNode.row)));
					break;
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
					edgeNode.addLink(new NavLink(NavLink.LinkType.FALL, edgeNode, fallNode, getFallingCost(edgeNode.row - fallNode.row)));
					break;
				}
			}
		}
	}

	private void setupJumpConnections() {
		Rectangle boundingBox = PhysicsUtils.getAABB(jumpingState.getFixtures());
		int divisions = 3;

		int linksCreated = 0;
		for (Node edgeNode : edgeNodes) {
			for(int i = 1; i <= divisions; i++){
				for(int j = 1; j <= divisions; j++){
					float speed = maxSpeed / i;
					float jumpForce = maxJumpForce / j;
					if (edgeNode.type == NodeType.RIGHT_EDGE || edgeNode.type == NodeType.SOLO) {
						JumpData rightJump = getTrajectory(edgeNode.row, edgeNode.col, speed, jumpForce, boundingBox, true);
						if (rightJump != null) {
							edgeNode.addLink(new JumpLink(edgeNode, rightJump.toNode, rightJump.time, rightJump.trajectory, speed, jumpForce));
							linksCreated++;
						}
					}
					if (edgeNode.type == NodeType.LEFT_EDGE || edgeNode.type == NodeType.SOLO) {
						JumpData leftJump = getTrajectory(edgeNode.row, edgeNode.col, speed, jumpForce, boundingBox, false);
						if (leftJump != null) {
							edgeNode.addLink(new JumpLink(edgeNode, leftJump.toNode, leftJump.time, leftJump.trajectory, speed, jumpForce));
							linksCreated++;
						}
					}
				}
			}
		}
		Gdx.app.debug("NavMesh", "Links created - " + linksCreated);
	}

	private JumpData getTrajectory(int row, int col, float speed, float jumpForce, Rectangle boundingBox, boolean right) {
		float interval = 0.0015f;
		boolean finished = false;
		Array<Point2f> points = new Array<Point2f>();
		Node toNode = null;
		float time = 0;
		while (!finished) {
			Point2f point = new Point2f(col + 0.5f + speed * time * (right ? 1.0f : -1.0f), row + boundingBox.height * 0.5f + jumpForce * time + 0.5f * GameVars.GRAVITY * time * time);
			if (point.y < level.getHeight() && !level.inBounds(point.x, point.y)) return null;
			if(!isValidPoint(point.x, point.y, boundingBox)) return null;
			points.add(point);
			time += interval;
			float deriv = jumpForce + GameVars.GRAVITY * time;
			if (deriv >= 0) continue;
			Node node = nodeMap.get(new Point((int)point.y, (int)point.x));
			if (node != null) {
				toNode = node;
				finished = true;
			}
		}
		return new JumpData(points, time, toNode);
	}
	
	// -distance = (1/2) * gravity * t^2
	// -2distance / gravity = t ^ 2
	// sqrt(-2distance / gravity) = t
	private float getFallingCost(float distance){
		float cost = 1.0f / runSpeed;
		return cost + (float)Math.sqrt(-2.0f * distance / GameVars.GRAVITY);
	}
	
	private boolean isValidPoint(float x, float y, Rectangle boundingBox){
		float hw = boundingBox.width * 0.5f;
		float hh = boundingBox.height * 0.5f;
		float minX = x - hw;
		float minY = y - hh;
		float maxX = x + hw;
		float maxY = y + hh;
		
		for(float row = minY; row < maxY; row++){
			for(float col = minX; col < maxX; col++){
				if(!level.inBounds(col, row)) return true;
				if(!level.isAir((int)row, (int)col)){
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
			if (!level.inBounds(row + i, (int)col)) return true;
			Tile t = level.tileAt(row + i, col);
			if (!t.isAir()) return false;
		}
		return true;
	}
	
	public Node getNodeAt(int row, int col){
		return nodeMap.get(new Point(row, col));
	}
	
	public Node getNodeAt(float x, float y){
		return nodeMap.get(new Point((int)y, (int)x));
	}
	
	public Array<Node> getEdgeNodes(){
		return edgeNodes;
	}
	
	public Array<Node> getNodes(){
		return nodes;
	}
	
	public ArrayMap<Point, Node> getNodeMap(){
		return nodeMap;
	}
	
	public Entity getEntity(){
		return entity;
	}
	
	public Level getLevel(){
		return level;
	}
}
