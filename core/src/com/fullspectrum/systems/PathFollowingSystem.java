package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Rectangle;
import com.fullspectrum.ai.AIController;
import com.fullspectrum.ai.PathFinder;
import com.fullspectrum.component.AIControllerComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PathComponent;
import com.fullspectrum.input.Actions;
import com.fullspectrum.level.NavLink;
import com.fullspectrum.level.NavMesh;
import com.fullspectrum.level.Node;

public class PathFollowingSystem extends IteratingSystem{

	@SuppressWarnings("unchecked")
	public PathFollowingSystem(){
		super(Family.all(AIControllerComponent.class, PathComponent.class, BodyComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
//		if(true) return;
		AIControllerComponent controllerComp = Mappers.aiController.get(entity);
		PathComponent pathComp = Mappers.path.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		
		PathFinder pathFinder = pathComp.pathFinder;
		NavMesh navMesh = pathFinder.getNavMesh();
		
		// If you're at a node and the node is on the path, do the action required.
		// If you're at a node and the node isn't on the path, calculate a new path and do the action required.
		// If you're not at a node, do an action based off of the last node you were at.
		
		Rectangle aabb = bodyComp.getAABB();
		Node currentNode = navMesh.getNodeAt(bodyComp.body.getPosition().x, bodyComp.body.getPosition().y + aabb.y);
		NavLink link = null;
		if(currentNode == null || bodyComp.body.getLinearVelocity().y != 0){
			link = pathFinder.getCurrentLink();
		}
		else{
			if(!pathFinder.onPath(currentNode)){
				pathFinder.setStart(currentNode);
				pathFinder.calculatePath();
			}
			link = pathFinder.getNextLink(currentNode);
		}
		
		AIController controller = controllerComp.controller;
		// TEMPORARY
		if(link == null || !Mappers.input.get(entity).enabled){
			controller.releaseAll();
			return;
		}
		
		switch(link.type){
		case RUN:
			controller.releaseAll();
			if(link.toNode.getCol() < link.fromNode.getCol()){
				controller.press(Actions.MOVE_LEFT);
			}
			else{
				controller.press(Actions.MOVE_RIGHT);
			}
			break;
		case FALL:
			controller.releaseAll();
			// If you're not falling, then run
			if(bodyComp.body.getLinearVelocity().y >= 0){
				if(link.toNode.getCol() < link.fromNode.getCol()){
					controller.press(Actions.MOVE_LEFT);
				}
				else{
					controller.press(Actions.MOVE_RIGHT);
				}
			}
			break;
		case JUMP:
			break;
//			controller.releaseAll();
//			JumpLink jLink = (JumpLink) link;
//			boolean right = link.toNode.getCol() > link.fromNode.getCol();
//			if(!right){
//				controller.press(Actions.MOVE_LEFT, jLink.runMultiplier);
//			}
//			else{
//				controller.press(Actions.MOVE_RIGHT, jLink.runMultiplier);
//			}
//			float x = bodyComp.body.getPosition().x;
//			if(currentNode != null){
////				boolean shouldJump = false;
////				if(right){
////					if(bodyComp.body.getPosition().x >= currentNode.getCol() + 0.5f && bodyComp.body.getPosition().x < currentNode.getCol() + 0.6f){
////						shouldJump = true;
////					}
////					else if(bodyComp.body.getPosition().x >= currentNode.getCol() + 0.6f){
////						controller.releaseAll();
////						controller.press(Actions.MOVE_LEFT);
////					}
////				}
////				else{
////					if(bodyComp.body.getPosition().x <= currentNode.getCol() + 0.5f && bodyComp.body.getPosition().x > currentNode.getCol() + 0.4f){
////						shouldJump = true;
////					}
////					else if(bodyComp.body.getPosition().x <= currentNode.getCol() + 0.4f){
////						controller.releaseAll();
////						controller.press(Actions.MOVE_RIGHT);
////					}
////				}
////				if(shouldJump){
////					controller.justPress(Actions.JUMP, jLink.jumpMultiplier);
////				}
//				if(right && x >= currentNode.getCol() + 0.5f || !right && x <= currentNode.getCol() + 0.5f){
//					controller.justPress(Actions.JUMP, jLink.jumpMultiplier);
//				}
//			}
		}
	}
	
}
