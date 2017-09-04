package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Rectangle;
import com.cpubrew.ai.AIController;
import com.cpubrew.ai.PathFinder;
import com.cpubrew.component.AIControllerComponent;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.CollisionComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.PathComponent;
import com.cpubrew.debug.DebugVars;
import com.cpubrew.input.Actions;
import com.cpubrew.level.JumpOverData;
import com.cpubrew.level.Level;
import com.cpubrew.level.NavLink;
import com.cpubrew.level.NavMesh;
import com.cpubrew.level.Node;
import com.cpubrew.level.TrajectoryData;
import com.cpubrew.level.NavLink.LinkType;

public class PathFollowingSystem extends IteratingSystem{

	public PathFollowingSystem(){
		super(Family.all(AIControllerComponent.class, PathComponent.class, BodyComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if(DebugVars.AI_DISABLED) return;
//		if(true) return;
		AIControllerComponent controllerComp = Mappers.aiController.get(entity);
		PathComponent pathComp = Mappers.path.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		CollisionComponent collisionComp = Mappers.collision.get(entity);
		
		if(!pathComp.shouldFollow) return;
		
		PathFinder pathFinder = pathComp.pathFinder;
		NavMesh navMesh = pathFinder.getNavMesh();
		
		// If you're at a node and the node is on the path, do the action required.
		// If you're at a node and the node isn't on the path, calculate a new path and do the action required.
		// If you're not at a node, do an action based off of the last node you were at.
		
		Rectangle aabb = bodyComp.getAABB();
		Node currentNode = navMesh.getNearestNode(bodyComp.body, 0.0f, aabb.y, true);
		NavLink link = null;
		if(currentNode == null){
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
		
		if(link == null){
			controller.releaseAll();
			return;
		}
		
		boolean right = link.toNode.getCol() > link.fromNode.getCol();
		boolean up = link.toNode.getRow() > link.fromNode.getRow();
		boolean landed = collisionComp.onGround();
		float x = bodyComp.body.getPosition().x;
		
		switch(link.type){
		case RUN:
			controller.releaseAll();
			if(!right){
				controller.press(Actions.MOVE_LEFT);
			}
			else{
				controller.press(Actions.MOVE_RIGHT);
			}
			break;
		case FALL_OVER:
			controller.releaseAll();
			// If you're not falling, then run
			if(landed){
				if(!right){
					controller.press(Actions.MOVE_LEFT);
				}
				else{
					controller.press(Actions.MOVE_RIGHT);
				}
			}
			break;
		case JUMP_OVER:
			controller.releaseAll();
			JumpOverData jData = (JumpOverData) link.data;
			// If you're now falling, then run
			if(bodyComp.body.getLinearVelocity().y < 0){
				if(!right){
					controller.press(Actions.MOVE_LEFT);
				}
				else{
					controller.press(Actions.MOVE_RIGHT);
				}
			}else{
				if(currentNode != null){
					if (right && x <= currentNode.getCol() + 0.52f || !right && x >= currentNode.getCol() + 0.48f) {
						controller.justPress(Actions.JUMP, jData.jumpForce / navMesh.getMaxJumpForce());
					}
					else{
						if(right){
							controller.press(Actions.MOVE_LEFT);
						}
						else{
							controller.press(Actions.MOVE_RIGHT);
						}
					}
				}
			}
			break;
		case JUMP:
			controller.releaseAll();
			TrajectoryData tData = (TrajectoryData) link.data;
			if(!right){
				controller.press(Actions.MOVE_LEFT, tData.speed / navMesh.getMaxSpeed());
			}
			else{
				controller.press(Actions.MOVE_RIGHT, tData.speed / navMesh.getMaxSpeed());
			}
			if(currentNode != null){
				if(right && x >= currentNode.getCol() + 0.5f || !right && x <= currentNode.getCol() + 0.5f){
					controller.justPress(Actions.JUMP, tData.jumpForce / navMesh.getMaxJumpForce());
				}
			}
			break;
		case FALL:
			controller.releaseAll();
			TrajectoryData fallData = (TrajectoryData)link.data;
			if(!landed){
				if(!right){
					controller.press(Actions.MOVE_LEFT, fallData.speed / navMesh.getMaxSpeed());
				}
				else{
					controller.press(Actions.MOVE_RIGHT, fallData.speed / navMesh.getMaxSpeed());
				}
			}else{
				if(!right){
					controller.press(Actions.MOVE_LEFT);
				}
				else{
					controller.press(Actions.MOVE_RIGHT);
				}
			}
			break;
		case CLIMB:
			controller.releaseAll();
			Node nextNode = link.fromNode;
			Level level = navMesh.getLevel();
			while(pathFinder.getNextLink(nextNode, false) != null && pathFinder.getNextLink(nextNode, false).type == LinkType.CLIMB){
				nextNode = pathFinder.getNextLink(nextNode, false).toNode;
				if(level.isSolid(nextNode.getRow(), nextNode.getCol() + 1) && x + bodyComp.getAABB().width * 0.5f > nextNode.getCol() + 1.0f){
					controller.press(Actions.MOVE_LEFT);
					break;
				}
				if(level.isSolid(nextNode.getRow(), nextNode.getCol() - 1) && x - bodyComp.getAABB().width * 0.5f < nextNode.getCol()){
					controller.press(Actions.MOVE_RIGHT);
					break;
				}
			}
			if(up){
				controller.press(Actions.MOVE_UP);
			}else{
				controller.press(Actions.MOVE_DOWN);
			}
			break;
		default:
			break;
		}
	}
	
}
