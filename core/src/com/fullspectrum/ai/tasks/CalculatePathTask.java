package com.fullspectrum.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.ai.PathFinder;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PathComponent;
import com.fullspectrum.component.TargetComponent;
import com.fullspectrum.level.NavMesh;
import com.fullspectrum.level.Node;
import com.fullspectrum.utils.EntityUtils;

public class CalculatePathTask extends LeafTask<Entity> {

	private boolean shadowPathing;
	
	public CalculatePathTask() {
		shadowPathing = false;
	}
	
	public CalculatePathTask(boolean shadowPathing) {
		this.shadowPathing = shadowPathing;
	}
	
	@Override
	public Status execute() {
		Entity entity = getObject();
		TargetComponent targetComp = Mappers.target.get(entity);
		if(targetComp == null || !EntityUtils.isTargetable(targetComp.target)) return Status.FAILED;
		
		PathComponent pathComp = Mappers.path.get(entity);
		if(pathComp == null || pathComp.pathFinder == null) return Status.FAILED;

		PathFinder pathFinder = pathComp.pathFinder;
		NavMesh mesh = pathFinder.getNavMesh();
		
		BodyComponent myBodyComp = Mappers.body.get(entity);
		Body myBody = myBodyComp.body;
		Rectangle myHitbox = myBodyComp.getAABB();
		
		BodyComponent targetBodyComp = Mappers.body.get(targetComp.target);
		Body targetBody = targetBodyComp.body;
		Rectangle targetHitbox = targetBodyComp.getAABB();
		
		Node targetNode = shadowPathing 
				? mesh.getShadowNode(targetBody, 0.0f, -targetHitbox.height * 0.5f) 
				: mesh.getNearestNode(targetBody, 0.0f, -targetHitbox.height * 0.5f, true);
				
		if(targetNode == null) return Status.FAILED;
		
		Node myNode = mesh.getNearestNode(myBody, 0.0f, -myHitbox.height * 0.5f, true);
		
		if(myNode == null) return Status.FAILED;
		
		if(pathFinder.onPath(myNode) && pathFinder.getGoal().equals(targetNode)) return Status.SUCCEEDED;
		
		if(!pathFinder.onPath(myNode)) pathFinder.setStart(myNode);
		if(pathFinder.getGoal() == null || !pathFinder.getGoal().equals(targetNode)) pathFinder.setGoal(targetNode);
		pathFinder.calculatePath();
		
		return pathFinder.getPath().size == 0 ? Status.FAILED : Status.SUCCEEDED;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		CalculatePathTask ptask = (CalculatePathTask) task;
		ptask.shadowPathing = shadowPathing;
		return ptask;
	}
	
	

}
