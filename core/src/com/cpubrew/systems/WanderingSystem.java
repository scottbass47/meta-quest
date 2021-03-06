package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.cpubrew.ai.PathFinder;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.PathComponent;
import com.cpubrew.component.WanderingComponent;
import com.cpubrew.debug.DebugVars;
import com.cpubrew.level.NavMesh;
import com.cpubrew.level.Node;

public class WanderingSystem extends IteratingSystem{

	public WanderingSystem(){
		super(Family.all(BodyComponent.class, PathComponent.class, WanderingComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if(DebugVars.AI_DISABLED) return;
		
		WanderingComponent wanderingComp = Mappers.wandering.get(entity);
		PathComponent pathComp = Mappers.path.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		
		PathFinder path = pathComp.pathFinder;
		NavMesh navMesh = path.getNavMesh();
		Body body = bodyComp.body;
		Node currentNode = navMesh.getNearestNode(body, 0.0f, bodyComp.getAABB().y, true);
		if(currentNode == null) return;
		
		if(wanderingComp.wandering){
			if(path.getStart() == null || path.getGoal() == null){
				wanderingComp.wandering = false;
			}
			else if(path.atGoal(currentNode)){
				wanderingComp.wandering = false;
				return;
			}
		}else{
			wanderingComp.timeElapsed += deltaTime;
			if(wanderingComp.timeElapsed >= wanderingComp.idleTime){
				// Find a new path to "wander" to
				Node goal = navMesh.getRandomNode(currentNode, wanderingComp.radius);
				path.setStart(currentNode);
				path.setGoal(goal);
				path.calculatePath();
				wanderingComp.wandering = true;
				wanderingComp.timeElapsed = 0;
			}
		}
	}
	
}
