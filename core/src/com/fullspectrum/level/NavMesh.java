package com.fullspectrum.level;

import static com.fullspectrum.game.GameVars.PPM_INV;

import java.util.Comparator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.component.FSMComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.fsm.EntityState;
import com.fullspectrum.fsm.State;
import com.fullspectrum.level.Node.Type;
import com.fullspectrum.level.Tile.Side;
import com.fullspectrum.utils.PhysicsUtils;

public class NavMesh {

	private Array<Node> nodes;
	private ShapeRenderer sRender;

	private NavMesh(Array<Node> nodes) {
		this.nodes = nodes;
		sRender = new ShapeRenderer();
	}

	public static NavMesh createNavMesh(Entity entity, Level level, State runningState, State jumpingState) {
		FSMComponent fsmComp = Mappers.fsm.get(entity);

		Array<Node> nodes = getValidNodes(level, fsmComp.fsm.getState(runningState));
		setupNodeTypes(nodes);
		setupRunConnections(nodes);
		setupFallConnections(nodes, level);

		return new NavMesh(nodes);
	}

	public void render(SpriteBatch batch) {
		sRender.setProjectionMatrix(batch.getProjectionMatrix());
		sRender.begin(ShapeType.Line);
		
		// Render NavLinks first
		for(Node node : nodes){
			for(NavLink link : node.getLinks()){
				switch(link.type){
				case RUN:
					sRender.setColor(Color.MAGENTA);
					sRender.line(node.col + 0.5f, node.row + 0.5f, link.toNode.col + 0.5f, link.toNode.row + 0.5f);
					break;
				case FALL:
					sRender.setColor(Color.GOLD);
//					sRender.line(node.col + 0.5f, node.row + 0.5f, link.toNode.col + 0.5f, node.row + 0.5f);
//					sRender.line(link.toNode.col + 0.5f, node.row + 0.5f, link.toNode.col + 0.5f, link.toNode.row + 0.5f);
					sRender.line(node.col + 0.5f, node.row + 0.5f, link.toNode.col + 0.5f, link.toNode.row + 0.5f);
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
			switch(node.type){
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

	private static Array<Node> getValidNodes(Level level, EntityState runningState) {
		Array<Node> ret = new Array<Node>();
		Rectangle boundingBox = PhysicsUtils.getAABB(runningState.getFixtures());

		for (int row = 0; row < level.getHeight(); row++) {
			for (int col = 0; col < level.getWidth(); col++) {
				if (isValidNode(row, col, level, boundingBox)) {
					Tile tile = level.tileAt(row + 1, col);
					Node node = new Node();
					node.row = tile.getRow();
					node.col = tile.getCol();
					ret.add(node);
				}
			}
		}
		return ret;
	}

	private static void setupNodeTypes(Array<Node> nodes) {
		for (int i = 0; i < nodes.size; i++) {
			Node node = nodes.get(i);
			boolean tileOnRight = false;
			boolean tileOnLeft = false;

			tileOnLeft = i - 1 >= 0 && (nodes.get(i - 1).row == node.row && nodes.get(i - 1).col + 1 == node.col);
			tileOnRight = i + 1 <= nodes.size - 1 && (nodes.get(i + 1).row == node.row && nodes.get(i + 1).col - 1 == node.col);
			
			if(tileOnLeft && tileOnRight) node.type = Type.MIDDLE;
			else if(tileOnLeft) node.type = Type.RIGHT_EDGE;
			else if(tileOnRight) node.type = Type.LEFT_EDGE;
			else node.type = Type.SOLO;
		}
	}
	
	private static void setupRunConnections(Array<Node> nodes){
		for(int i = 0; i < nodes.size; i++){
			Node node = nodes.get(i);
			if(node.type == Type.LEFT_EDGE || node.type == Type.MIDDLE){
				node.addLink(new NavLink(NavLink.Type.RUN, nodes.get(i+1)));
			}
			if(node.type == Type.RIGHT_EDGE || node.type == Type.MIDDLE){
				node.addLink(new NavLink(NavLink.Type.RUN, nodes.get(i-1)));
			}
		}
	}
	
	private static void setupFallConnections(Array<Node> nodes, Level level){
		ArrayMap<Integer, Array<Node>> nodeCols = new ArrayMap<Integer, Array<Node>>();
		Array<Node> edges = new Array<Node>();
		for(Node node : nodes){
			if(!nodeCols.containsKey(node.col)) nodeCols.put(node.col, new Array<Node>());
			nodeCols.get(node.col).add(node);
			if(node.type != Type.MIDDLE) edges.add(node);
		}
		for(Node edgeNode : edges){
			if(edgeNode.type == Type.LEFT_EDGE || edgeNode.type == Type.SOLO){
				if(edgeNode.col - 1 < 0 || !level.isAir(edgeNode.row, edgeNode.col - 1)){
					if(edgeNode.type != Type.SOLO) continue;
				}
				Array<Node> fallToNodes = nodeCols.get(edgeNode.col - 1);
				fallToNodes.sort(new Comparator<Node>(){
					@Override
					public int compare(Node o1, Node o2) {
						return o1.row < o2.row ? 1 : -1;
					}
				});
				for(Node fallNode : fallToNodes){
					if(fallNode.row > edgeNode.row) continue;
					edgeNode.addLink(new NavLink(NavLink.Type.FALL, fallNode));
					break;
				}
			}
			if(edgeNode.type == Type.RIGHT_EDGE || edgeNode.type == Type.SOLO){
				if(edgeNode.col + 1 > level.getWidth() - 1 || !level.isAir(edgeNode.row, edgeNode.col + 1)) continue;
				Array<Node> fallToNodes = nodeCols.get(edgeNode.col + 1);
				fallToNodes.sort(new Comparator<Node>(){
					@Override
					public int compare(Node o1, Node o2) {
						return o1.row < o2.row ? 1 : -1;
					}
				});
				for(Node fallNode : fallToNodes){
					if(fallNode.row > edgeNode.row) continue;
					edgeNode.addLink(new NavLink(NavLink.Type.FALL, fallNode));
					break;
				}
			}
		}
	}

	private static boolean isValidNode(int row, int col, Level level, Rectangle boundingBox) {
		if (row + 1 > level.getHeight() || !level.tileAt(row, col).isOpen(Side.NORTH)) return false;
		int tilesTall = (int) (boundingBox.height + 1.0f);
		for (int i = 1; i <= tilesTall; i++) {
			if (row + i >= level.getHeight()) return true;
			Tile t = level.tileAt(row + i, col);
			if (!t.isAir()) return false;
		}
		return true;
	}
}
