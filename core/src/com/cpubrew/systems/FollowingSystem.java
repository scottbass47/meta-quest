package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.cpubrew.ai.PathFinder;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.FollowComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.PathComponent;
import com.cpubrew.component.TargetComponent;
import com.cpubrew.debug.DebugVars;
import com.cpubrew.level.NavMesh;
import com.cpubrew.level.Node;
import com.cpubrew.utils.EntityUtils;

public class FollowingSystem extends IteratingSystem{

	public FollowingSystem(){
		super(Family.all(TargetComponent.class, FollowComponent.class, PathComponent.class, BodyComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if(DebugVars.AI_DISABLED) return;
		
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
		
		if(myNode == null){
			if(goalNode != null){
				pathFinder.setGoal(goalNode);
				pathFinder.calculatePath();
			}
			return;
		}else{
			if(pathFinder.getStart() == null){
				pathFinder.setStart(myNode);
			}
		}
			
		if(pathFinder.atGoal(goalNode)) return;
		pathFinder.setGoal(goalNode);
		if(!pathFinder.onPath(myNode)){
			pathFinder.setStart(myNode);
		}
		pathFinder.calculatePath();
	}
	
}
