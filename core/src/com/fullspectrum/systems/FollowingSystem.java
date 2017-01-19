package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.ai.PathFinder;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.FollowComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PathComponent;
import com.fullspectrum.component.TargetComponent;
import com.fullspectrum.entity.EntityUtils;
import com.fullspectrum.level.NavMesh;
import com.fullspectrum.level.Node;

public class FollowingSystem extends IteratingSystem{

	public FollowingSystem(){
		super(Family.all(TargetComponent.class, FollowComponent.class, PathComponent.class, BodyComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PathComponent pathComp = Mappers.path.get(entity);
		TargetComponent targetComp = Mappers.target.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		if(!EntityUtils.isValid(targetComp.target)) return;
		BodyComponent otherBody = Mappers.body.get(targetComp.target);
		
		PathFinder pathFinder = pathComp.pathFinder;
		NavMesh navMesh = pathFinder.getNavMesh();
		
//		Node myNode = navMesh.getNodeAt(bodyComp.body.getPosition().x, bodyComp.body.getPosition().y + bodyComp.getAABB().y);
//		Node goalNode = navMesh.getNodeAt(otherBody.body.getPosition().x, otherBody.body.getPosition().y + bodyComp.getAABB().y);
		
		Node myNode = navMesh.getNearestNode(bodyComp.body, 0.0f, bodyComp.getAABB().y, true);
		Node goalNode = navMesh.getShadowNode(otherBody.body, 0.0f, /*bodyComp.getAABB().y*/ 0.0f);
		
		if(goalNode == null){
			System.out.println();
		}
		
		if(myNode == null){
			if(goalNode != null){
				pathFinder.setGoal(goalNode);
				pathFinder.calculatePath();
			}
			return;
		}
		
		if(pathFinder.atGoal(goalNode)) return;
		pathFinder.setGoal(goalNode);
		if(!pathFinder.onPath(myNode)){
			pathFinder.setStart(myNode);
		}
		pathFinder.calculatePath();
	}
	
}
