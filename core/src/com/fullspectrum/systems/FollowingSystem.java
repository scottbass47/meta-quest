package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.ai.PathFinder;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.FollowComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PathComponent;
import com.fullspectrum.level.NavMesh;
import com.fullspectrum.level.Node;

public class FollowingSystem extends IteratingSystem{

	@SuppressWarnings("unchecked")
	public FollowingSystem(){
		super(Family.all(FollowComponent.class, PathComponent.class, BodyComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PathComponent pathComp = Mappers.path.get(entity);
		FollowComponent followComp = Mappers.follow.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		BodyComponent otherBody = Mappers.body.get(followComp.toFollow);
		
		PathFinder pathFinder = pathComp.pathFinder;
		NavMesh navMesh = pathFinder.getNavMesh();
		
		Node myNode = navMesh.getNodeAt(bodyComp.body.getPosition().x, bodyComp.body.getPosition().y + bodyComp.getAABB().y);
		Node goalNode = navMesh.getNodeAt(otherBody.body.getPosition().x, otherBody.body.getPosition().y + bodyComp.getAABB().y);
		if(myNode == null || goalNode == null) return;
		if(pathFinder.atGoal(goalNode)) return;
		pathFinder.setGoal(goalNode);
		if(!pathFinder.onPath(myNode)){
			pathFinder.setStart(myNode);
		}
		pathFinder.calculatePath();
	}
	
}
