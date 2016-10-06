package com.fullspectrum.level;

import static com.fullspectrum.game.GameVars.PPM_INV;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
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

		Array<Node> tiles = getValidNodes(level, fsmComp.fsm.getState(runningState));
		setupNodeTypes(tiles);

		return new NavMesh(tiles);
	}

	public void render(SpriteBatch batch) {
		sRender.setProjectionMatrix(batch.getProjectionMatrix());
		sRender.begin(ShapeType.Filled);
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
			sRender.rect(x1 - (PPM_INV * 0.5f - width * 0.5f), y1 - (PPM_INV * 0.5f - height * 0.5f), width * 0.5f, height * 0.5f, width, height, 1.0f, 1.0f, 45f);
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

	private static void setupNodeTypes(Array<Node> tiles) {
		for (int i = 0; i < tiles.size; i++) {
			Node node = tiles.get(i);
			boolean tileOnRight = false;
			boolean tileOnLeft = false;

			tileOnLeft = i - 1 >= 0 && (tiles.get(i - 1).row == node.row && tiles.get(i - 1).col + 1 == node.col);
			tileOnRight = i + 1 <= tiles.size - 1 && (tiles.get(i + 1).row == node.row && tiles.get(i + 1).col - 1 == node.col);
			
			if(tileOnLeft && tileOnRight) node.type = Type.MIDDLE;
			else if(tileOnLeft) node.type = Type.LEFT_EDGE;
			else if(tileOnRight) node.type = Type.RIGHT_EDGE;
			else node.type = Type.SOLO;
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
