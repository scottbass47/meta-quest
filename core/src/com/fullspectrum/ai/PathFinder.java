package com.fullspectrum.ai;

import static com.fullspectrum.game.GameVars.PPM_INV;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Queue;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.level.NavLink;
import com.fullspectrum.level.NavMesh;
import com.fullspectrum.level.Node;
import com.fullspectrum.level.TrajectoryData;
import com.fullspectrum.utils.RenderUtils;

public class PathFinder {
	
	// Debug Render
	private ShapeRenderer sRender;
	private Color debugColor;

	// Path
	private NavMesh navMesh;
	private Node start;
	private Node goal;
	private Node current;
	private Array<NavLink> path;
	private ArrayMap<Node, PathData> pathDataMap;
	private PathHeuristic heuristic;
	private Set<Node> visitedNodes;
	private TreeSet<NavLink> uncheckedLinks;
	private boolean calculating = false;
	
	// Queue
	private static Queue<PathFinder> pathQueue = new Queue<PathFinder>();
	private static final float MAX_ALLOTTED_TIME = GameVars.UPS_INV * 0.2f; // Path calculation cant take up more than X% of the update cycle
	
	public PathFinder(final NavMesh navMesh){
		this.navMesh = navMesh;
		sRender = new ShapeRenderer();
		path = new Array<NavLink>();
		pathDataMap = new ArrayMap<Node, PathData>();
		for(Node node : navMesh.getNodes()){
			pathDataMap.put(node, new PathData());
		}
		debugColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), 1.0f);
		heuristic = new DefaultHeuristic();
		
		visitedNodes = new HashSet<Node>();
		uncheckedLinks = new TreeSet<NavLink>(new Comparator<NavLink>() {
			@Override
			public int compare(NavLink linkOne, NavLink linkTwo) {
				return heuristic.cost(linkOne, navMesh, goal) > heuristic.cost(linkTwo, navMesh, goal) ? 1 : -1;
			}
		});	
	}
	
	// BUG Paths are looking kinda funny
	public void render(SpriteBatch batch){
		sRender.setProjectionMatrix(batch.getProjectionMatrix());
		sRender.begin(ShapeType.Line);
		sRender.setColor(debugColor);
		for(NavLink link : path){
			Node node = link.fromNode;
			switch (link.type) {
			case RUN:
			case CLIMB:
				sRender.line(node.getCol() + 0.5f, node.getRow() + 0.5f, link.toNode.getCol() + 0.5f, link.toNode.getRow() + 0.5f);
				break;
			case FALL:
				TrajectoryData fallData = (TrajectoryData) link.data;
				RenderUtils.renderTrajectory(sRender, node.getCol() + 0.5f, node.getRow() + 0.5f, link.isDirRight(), fallData.time, fallData.jumpForce, fallData.speed, 50);
				break;
			case FALL_OVER:
				sRender.line(node.getCol() + 0.5f, node.getRow() + 0.5f, link.toNode.getCol() + 0.5f, link.toNode.getRow() + 0.5f);
				break;
			case JUMP:
				TrajectoryData jumpData = (TrajectoryData) link.data;
				RenderUtils.renderTrajectory(sRender, node.getCol() + 0.5f, node.getRow() + 0.5f, link.isDirRight(), jumpData.time, jumpData.jumpForce, jumpData.speed, 50);
				break;
			case JUMP_OVER:
				sRender.line(node.getCol() + 0.5f, node.getRow() + 0.5f, node.getCol() + 0.5f, link.toNode.getRow() + 0.5f);
				sRender.line(node.getCol() + 0.5f, link.toNode.getRow() + 0.5f, link.toNode.getCol() + 0.5f, link.toNode.getRow() + 0.5f);
				break;
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
	
	public void calculatePath() {
		if(goal == null || start == null) return;
		addToQueue(this);
	}
	
	private float startPathCalc() {
		long time = System.nanoTime();
		calculating = true;
		path.clear();
		visitedNodes.clear();
		uncheckedLinks.clear();
		resetPath();
		visitedNodes.add(start);
		pathDataMap.get(start).setCost(0.0f);
		
		for(NavLink link : start.getLinks()){
			uncheckedLinks.add(link);
		}
		return elapsedSeconds(time, System.nanoTime());
	}
	
	private float elapsedSeconds(long start, long end) {
		return (end - start) / 1000000000f;
	}
	
	// Runs one cycle of the pathing calculating and returns the time it took
	private float runIteration() {
		long time = System.nanoTime();
		NavLink link = uncheckedLinks.pollFirst();
		if(link.toNode.equals(goal)){
			pathDataMap.put(goal, new PathData(link, link.cost));
			// Reached the goal, backtrack and create path
			Node node = goal;
			while(!node.equals(start)){
				path.add(pathDataMap.get(node).getFromLink());
				node = pathDataMap.get(node).getFromLink().fromNode;
			}
			path.reverse();
			calculating = false;
			return elapsedSeconds(time, System.nanoTime());
		}
		
		visitedNodes.add(link.fromNode);
		if(visitedNodes.contains(link.toNode)) return elapsedSeconds(time, System.nanoTime());

		if(link.cost < pathDataMap.get(link.toNode).getCost()){
			pathDataMap.get(link.toNode).setCost(link.cost);
			pathDataMap.get(link.toNode).setFromLink(link);
		}
		for(NavLink newLink : link.toNode.getLinks()){
			uncheckedLinks.add(newLink.increaseCost(link.cost));
		}
		return elapsedSeconds(time, System.nanoTime());
	}
	
	private static void addToQueue(PathFinder finder) {
		for(Iterator<PathFinder> iter = pathQueue.iterator(); iter.hasNext();) {
			// If there is already being a path calculated then
			// reset it and don't add it to the queue again
			if(iter.next() == finder) {
       			finder.reset();
				return;
			}
		}
		pathQueue.addLast(finder);
	}
	
	public static void update(float delta) {
		if(pathQueue.size == 0) return;
		
		float timeLeft = MAX_ALLOTTED_TIME;
		
		while(timeLeft > 0 && pathQueue.size != 0) {
			PathFinder finder = pathQueue.first();
			if(!finder.calculating) {
				timeLeft -= finder.startPathCalc();
			}
			
			if(timeLeft < 0) return;
			
			while(timeLeft > 0 && finder.calculating && finder.uncheckedLinks.size() > 0) {
				timeLeft -= finder.runIteration();
			}
			
			// If after running the calculation no path was found, set calculating to false
			if(finder.noPath()) finder.calculating = false; 
			
			pathQueue.removeFirst();
		}
		
	}
	
	public void reset() {
		calculating = false;
		path.clear();
		resetPath();
	}
	
	private void resetPath(){
		for(Entry<Node, PathData> node : pathDataMap.entries()){
			pathDataMap.get(node.key).reset();
		}
	}
	
	public void setHeuristic(PathHeuristic heuristic){
		this.heuristic = heuristic;
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
	
	public Node getStart(){
		return start;
	}
	
	public Node getGoal(){
		return goal;
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
	
	public boolean noPath(){
		return path.size == 0;
	}
	
	/**
	 * Returns the next link in the path and advances the current link to that link.
	 * 
	 * @param node
	 * @return
	 */
	public NavLink getNextLink(Node node){
		return getNextLink(node, true);
	}
	
	/**
	 * Returns the next link in the path and advances the current link to that link if advance is true.
	 * 
	 * @param node
	 * @return
	 */
	public NavLink getNextLink(Node node, boolean advance){
		if(node == null) return null;
		if(!node.equals(current) && advance) current = node;
		for(NavLink link : path){
			if(link.fromNode.equals(node)) return link;
		}
		return null;
	}
	
	public NavLink getCurrentLink(){
		return getNextLink(current);
	}
	
	public boolean atGoal(Node node){
		if(node == null || goal == null) return false;
		return goal.equals(node);
	}
}
