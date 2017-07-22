package com.fullspectrum.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.ai.AIController;
import com.fullspectrum.ai.PathFinder;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PathComponent;
import com.fullspectrum.input.Actions;
import com.fullspectrum.level.JumpOverData;
import com.fullspectrum.level.NavLink;
import com.fullspectrum.level.NavMesh;
import com.fullspectrum.level.Node;
import com.fullspectrum.level.TrajectoryData;

public class FollowPathTask extends LeafTask<Entity> {

	@Override
	public Status execute() {
		Entity entity = getObject();
		PathComponent pathComp = Mappers.path.get(entity);

		if (pathComp == null || pathComp.pathFinder == null || pathComp.pathFinder.noPath())
			return Status.FAILED;

		PathFinder pathFinder = pathComp.pathFinder;
		AIController controller = Mappers.aiController.get(entity).controller;

		NavMesh navMesh = pathFinder.getNavMesh();

		BodyComponent myBodyComp = Mappers.body.get(entity);
		Body myBody = myBodyComp.body;
		Rectangle myHitbox = myBodyComp.getAABB();
		Node currentNode = navMesh.getNearestNode(myBody, 0.0f, -myHitbox.height * 0.5f, true);

		NavLink link = pathFinder.getCurrentLink();
		if (currentNode != null) {
			link = pathFinder.getNextLink(currentNode);
		}

		if (link == null)
			return Status.FAILED;

		boolean right = link.toNode.getCol() > link.fromNode.getCol();
		boolean landed = Mappers.collision.get(entity).onGround();
		float x = myBodyComp.body.getPosition().x;

		switch (link.type) {
		case RUN:
			controller.releaseAll();
			if (!right) {
				controller.press(Actions.MOVE_LEFT);
			} else {
				controller.press(Actions.MOVE_RIGHT);
			}
			break;
		case FALL_OVER:
			controller.releaseAll();
			// If you're not falling, then run
			if (landed) {
				if (!right) {
					controller.press(Actions.MOVE_LEFT);
				} else {
					controller.press(Actions.MOVE_RIGHT);
				}
			}
			break;
		case JUMP_OVER:
			controller.releaseAll();
			JumpOverData jData = (JumpOverData) link.data;
			// If you're now falling, then run
			if (myBodyComp.body.getLinearVelocity().y < 0) {
				if (!right) {
					controller.press(Actions.MOVE_LEFT);
				} else {
					controller.press(Actions.MOVE_RIGHT);
				}
			} else {
				if (currentNode != null) {
					if (right && x <= currentNode.getCol() + 0.52f || !right && x >= currentNode.getCol() + 0.48f) {
						controller.justPress(Actions.JUMP, jData.jumpForce / navMesh.getMaxJumpForce());
					} else {
						if (right) {
							controller.press(Actions.MOVE_LEFT);
						} else {
							controller.press(Actions.MOVE_RIGHT);
						}
					}
				}
			}
			break;
		case JUMP:
			controller.releaseAll();
			TrajectoryData tData = (TrajectoryData) link.data;
			if (!right) {
				controller.press(Actions.MOVE_LEFT, tData.speed / navMesh.getMaxSpeed());
			} else {
				controller.press(Actions.MOVE_RIGHT, tData.speed / navMesh.getMaxSpeed());
			}
			if (currentNode != null) {
				if (right && x >= currentNode.getCol() + 0.5f || !right && x <= currentNode.getCol() + 0.5f) {
					controller.justPress(Actions.JUMP, tData.jumpForce / navMesh.getMaxJumpForce());
				}
			}
			break;
		case FALL:
			controller.releaseAll();
			TrajectoryData fallData = (TrajectoryData) link.data;
			if (!landed) {
				if (!right) {
					controller.press(Actions.MOVE_LEFT, fallData.speed / navMesh.getMaxSpeed());
				} else {
					controller.press(Actions.MOVE_RIGHT, fallData.speed / navMesh.getMaxSpeed());
				}
			} else {
				if (!right) {
					controller.press(Actions.MOVE_LEFT);
				} else {
					controller.press(Actions.MOVE_RIGHT);
				}
			}
			break;
		default:
			break;
		}
		return Status.SUCCEEDED;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}

}
