package com.fullspectrum.ai;

import static com.fullspectrum.game.GameVars.PPM_INV;

import java.awt.Point;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.level.JumpLink;
import com.fullspectrum.level.NavLink;
import com.fullspectrum.level.NavMesh;
import com.fullspectrum.level.Node;
import com.fullspectrum.level.Point2f;

public class PathFinder {
	
	// Debug Render
	private ShapeRenderer sRender;

	// Path
	private NavMesh navMesh;
	private Node start;
	private Node goal;
	private Node current;
	private Array<NavLink> path;
	
	public PathFinder(NavMesh navMesh, int startRow, int startCol, int goalRow, int goalCol){
		sRender = new ShapeRenderer();
		start = navMesh.getNodeMap().get(new Point(startRow, startCol));
		goal = navMesh.getNodeMap().get(new Point(goalRow, goalCol));
		if(start == null) throw new IllegalArgumentException("Start node doesn't exist!");
		if(goal == null) throw new IllegalArgumentException("Goal node doesn't exist!");
		current = start;
		path = new Array<NavLink>();
		this.navMesh = navMesh;
		
		calculatePath();
	}
	
	public PathFinder(NavMesh navMesh, Node start, Node goal){
		this(navMesh, start.getRow(), start.getCol(), goal.getRow(), goal.getCol());
	}
	
	
	public void render(SpriteBatch batch){
		sRender.setProjectionMatrix(batch.getProjectionMatrix());
		sRender.begin(ShapeType.Line);
		sRender.setColor(Color.WHITE);
		for(NavLink link : path){
			switch (link.type) {
			case RUN:
				sRender.line(link.fromNode.getCol() + 0.5f, link.fromNode.getRow() + 0.5f, link.toNode.getCol() + 0.5f, link.toNode.getRow() + 0.5f);
				break;
			case FALL:
				// sRender.line(node.col + 0.5f, node.row + 0.5f,
				// link.toNode.col + 0.5f, node.row + 0.5f);
				// sRender.line(link.toNode.col + 0.5f, node.row + 0.5f,
				// link.toNode.col + 0.5f, link.toNode.row + 0.5f);
				sRender.line(link.fromNode.getCol() + 0.5f, link.fromNode.getRow() + 0.5f, link.toNode.getCol() + 0.5f, link.toNode.getRow() + 0.5f);
				break;
			case JUMP:
				JumpLink jumpLink = (JumpLink) link;
				for (int i = 0; i < jumpLink.trajectory.size - 1; i++) {
					Point2f point1 = jumpLink.trajectory.get(i);
					Point2f point2 = jumpLink.trajectory.get(i + 1);
					sRender.line(point1.x, point1.y, point2.x, point2.y);
				}
			default:
				break;
			}
		}
		sRender.end();
		sRender.begin(ShapeType.Filled);
		for(NavLink link : path){
			float x1 = link.fromNode.getCol();
			float y1 = link.fromNode.getRow();
			float width = 8.0f * PPM_INV;
			float height = width;
			sRender.rect(x1 + (0.5f - width * 0.5f), y1 + (0.5f - height * 0.5f), width * 0.5f, height * 0.5f, width, height, 1.0f, 1.0f, 45f);
			
			x1 = link.toNode.getCol();
			y1 = link.toNode.getRow();
			sRender.rect(x1 + (0.5f - width * 0.5f), y1 + (0.5f - height * 0.5f), width * 0.5f, height * 0.5f, width, height, 1.0f, 1.0f, 45f);
		}
		sRender.end();
	}
	
	public void calculatePath(){
//		long startTime = System.nanoTime();
		path.clear();
		Set<Node> visitedNodes = new HashSet<Node>();
		TreeSet<NavLink> uncheckedLinks = new TreeSet<NavLink>(new Comparator<NavLink>() {
			@Override
			public int compare(NavLink linkOne, NavLink linkTwo) {
				return linkOne.cost > linkTwo.cost ? 1 : -1;
			}
		});
		for(NavLink link : start.getLinks()){
			uncheckedLinks.add(link);
		}
		while(uncheckedLinks.size() > 0){
//			System.out.println(uncheckedLinks);
			NavLink link = uncheckedLinks.pollFirst();
			if(link.toNode.equals(goal)){
				// Reached the goal, backtrack and create path
 				while(!link.fromNode.equals(start)){
//					System.out.println("From: " + link.fromNode + ", To: " + link.toNode);
					path.add(link);
					link = link.getParent();
				}
				path.add(link);
				path.reverse();
//				System.out.println("Time: " + ((System.nanoTime() - startTime) / 1000000000d));
				return;
			}
			visitedNodes.add(link.fromNode);
			for(NavLink newLink : link.toNode.getLinks()){
				if(visitedNodes.contains(newLink.toNode)) 
					continue;
				if(newLink.getParent() == null || newLink.cost < newLink.getParent().cost){
					newLink.setParent(link);
				}
				uncheckedLinks.add(newLink.increaseCost(link.cost));
			}
		}
		
	}
	
	public int getManhattanDistance(Node node1, Node node2){
		return Math.abs(node1.getRow() - node2.getRow()) + Math.abs(node1.getCol() - node2.getCol());
	}
	
	public void setStart(Node start){
		this.start = start;
	}
	
	public void setGoal(Node goal){
		this.goal = goal;
	}
	
	public Array<NavLink> getPath(){
		return path;
	}

	public NavMesh getNavMesh(){
		return navMesh;
	}
	
	public boolean onPath(Node node){
		for(NavLink link : path){
			if(link.toNode.equals(node) || link.fromNode.equals(node)) return true;
		}
		return false;
	}
	
	public NavLink getNextLink(Node node){
		if(node == null) return null;
		if(!node.equals(current)) current = node;
		for(NavLink link : path){
			if(link.fromNode.equals(node)) return link;
		}
		return null;
	}
	
	public NavLink getCurrentLink(){
		return getNextLink(current);
	}
	
	public boolean atGoal(Node node){
		return goal.equals(node);
	}
}
