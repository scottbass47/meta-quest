package com.fullspectrum.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Queue;

public class FlowField {

	private int width;
	private int height;
	private ArrayMap<GridPoint2, FlowNode> nodeMap;
	private Array<FlowNode> nodes;
	private Level level;
	private int goalRow;
	private int goalCol;
	
	// Debug Renderer
	private ShapeRenderer sRender;
	
	public FlowField(Level level){
		this.level = level;
		height = level.getHeight();
		width = level.getWidth();
		nodeMap = new ArrayMap<GridPoint2, FlowNode>();
		nodes = new Array<FlowNode>();
		
		sRender = new ShapeRenderer();
		initNodes();
	}
	
	public void render(SpriteBatch batch){
		sRender.setProjectionMatrix(batch.getProjectionMatrix());
		sRender.begin(ShapeType.Line);
		for(FlowNode node : nodes){
			sRender.setColor(Color.WHITE);
			float fromX = node.col + 0.5f;
			float fromY = node.row + 0.5f;
			float angle = 0.0f;
			if(node.row == goalRow && node.col == goalCol){
				sRender.rect(fromX - 0.05f, fromY - 0.05f, 0.1f, 0.1f);
				continue;
			}
			if(node.vec.x == 0){
				angle = node.vec.y < 0 ? MathUtils.PI * 3 * 0.5f : MathUtils.PI * 0.5f;
			}else{
				angle = MathUtils.atan2(node.vec.y, node.vec.x);
			}
			float length = 0.5f;
			float toX = fromX + MathUtils.cos(angle) * length;
			float toY = fromY + MathUtils.sin(angle) * length;
			sRender.line(fromX, fromY, toX, toY);
			
			// Triangular Point
			sRender.setColor(Color.RED);
			length = 0.1f;
			
			angle += MathUtils.PI * 0.5f;
			float x1 = toX + MathUtils.cos(angle) * length;
			float y1 = toY + MathUtils.sin(angle) * length;
			
			angle -= MathUtils.PI;
			float x2 = toX + MathUtils.cos(angle) * length;
			float y2 = toY + MathUtils.sin(angle) * length;
			
			angle += MathUtils.PI * 0.5f;
			float x3 = toX + MathUtils.cos(angle) * length;
			float y3 = toY + MathUtils.sin(angle) * length;
			
			sRender.triangle(x1, y1, x2, y2, x3, y3);
		}
		sRender.end();
	}
	
	private void initNodes(){
		for(int row = 0; row < height; row++){
			for(int col = 0; col < width; col++){
				if(!level.isSolid(row, col)){
					FlowNode node = new FlowNode(row, col);
					nodeMap.put(new GridPoint2(col, row), node);
					nodes.add(node);
				}
			}
		}
		for(FlowNode node : nodes){
			GridPoint2 point = new GridPoint2();
			
			point.x = node.col - 1;
			point.y = node.row;
			if(nodeMap.containsKey(point)){
				node.adjacentNodes.add(nodeMap.get(point));
			}
			
			point.x = node.col + 1;
			point.y = node.row;
			if(nodeMap.containsKey(point)){
				node.adjacentNodes.add(nodeMap.get(point));
			}
			
			point.x = node.col;
			point.y = node.row - 1;
			if(nodeMap.containsKey(point)){
				node.adjacentNodes.add(nodeMap.get(point));
			}
			
			point.x = node.col;
			point.y = node.row + 1;
			if(nodeMap.containsKey(point)){
				node.adjacentNodes.add(nodeMap.get(point));
			}
		}
	}
	
	private void updateField(){
//		long startTime = System.nanoTime();
		// Brushfire
		boolean success = brushFire(goalRow, goalCol);
		if(!success) return;
		
		// Vec field
		vecField();
		
//		double elapsed = (System.nanoTime() - startTime) / 1000000000d;
//		System.out.println("Elapsed: " + elapsed);
	}
	
	private boolean brushFire(int row, int col){
		ObjectSet<FlowNode> visited = new ObjectSet<FlowNode>();
		Queue<FlowNode> queue = new Queue<FlowNode>();
		FlowNode node = nodeMap.get(new GridPoint2(col, row));
		
		if(node == null) return false;
		node.distance = 0;
		visited.add(node);
		
		for(FlowNode adj : node.adjacentNodes){
			adj.parent = node;
			queue.addLast(adj);
			visited.add(adj);
		}
		
		while(queue.size > 0){
			FlowNode next = queue.removeFirst();
			next.distance = next.parent.distance + 1;
			for(FlowNode adj : next.adjacentNodes){
				if(visited.contains(adj)) continue;
				adj.parent = next;
				queue.addLast(adj);
				visited.add(adj);
			}
		}
		return true;
	}
	
	private void vecField(){
		for(FlowNode node : nodes){
			FlowNode right = getAdjacentNode(node, Direction.RIGHT);
			FlowNode left = getAdjacentNode(node, Direction.LEFT);
			FlowNode up = getAdjacentNode(node, Direction.UP);
			FlowNode down = getAdjacentNode(node, Direction.DOWN);
			
			int wallCost = 4;
			
			node.vec.x = (left == null ? node.distance + wallCost : left.distance) - (right == null ? node.distance + wallCost : right.distance);
			node.vec.y = (down == null ? node.distance + wallCost : down.distance) - (up == null ? node.distance + wallCost : up.distance);
		}
	}
	
	public void setGoal(int row, int col){
		if(row == goalRow && col == goalCol) return;
		this.goalRow = row;
		this.goalCol = col;
		updateField();
	}
	
	public FlowNode getNode(float x, float y){
		return getNode((int)y, (int)x);
	}
	
	public FlowNode getNode(int row, int col){
		GridPoint2 point = new GridPoint2(col, row);
		return nodeMap.get(point);
	}

	public FlowNode getAdjacentNode(FlowNode node, Direction direction){
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
		return nodeMap.get(new GridPoint2(col, row));
	}
	
	public enum Direction{
		RIGHT,
		LEFT,
		UP,
		DOWN
	}
	
}
