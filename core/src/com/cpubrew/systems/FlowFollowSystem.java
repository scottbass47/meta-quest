package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.cpubrew.ai.AIController;
import com.cpubrew.component.AIControllerComponent;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.FlowFieldComponent;
import com.cpubrew.component.FlowFollowComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.debug.DebugVars;
import com.cpubrew.input.Actions;
import com.cpubrew.level.FlowField;
import com.cpubrew.level.FlowNode;

public class FlowFollowSystem extends IteratingSystem {

	public FlowFollowSystem() {
		super(Family.all(AIControllerComponent.class, FlowFieldComponent.class, FlowFollowComponent.class, BodyComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if(DebugVars.AI_DISABLED) return;

		FlowFieldComponent flowFieldComp = Mappers.flowField.get(entity);
		AIControllerComponent aiControllerComp = Mappers.aiController.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
//		LevelComponent levelComp = Mappers.level.get(entity);
//		Level level = levelComp.level;

		FlowField field = flowFieldComp.field;
		if (field == null) return;

		// INCOMPLETE Enemies can get stuck against walls (hitbox bigger than tile)
		AIController controller = aiControllerComp.controller;
		Body body = bodyComp.body;

		float myX = body.getPosition().x;
		float myY = body.getPosition().y;

		FlowNode node = field.getNode(myX, myY);
		if (node == null) return;
		float angle = node.getAngle() * MathUtils.radiansToDegrees;

//		// Perform Ray Casts
//		Rectangle myHitbox = bodyComp.getAABB();
//
//		float x1 = 0.0f;
//		float y1 = 0.0f;
//		float x2 = 0.0f;
//		float y2 = 0.0f;
//		float toX1 = 0.0f;
//		float toY1 = 0.0f;
//		float toX2 = 0.0f;
//		float toY2 = 0.0f;
//
//		boolean ray1 = false;
//		boolean ray2 = false;
//		
//		float interval = 10.0f;
//		float length = 1.5f;
//		float finalAngle = angle;
//		int counter = 0;
//		
//		do {
//			counter++;
//			finalAngle = angle;
//			// Quadrant 1 or 3
//			if ((angle >= 0 && angle <= 90) || (angle >= -180 && angle <= -90)) {
//				// Use upper left and lower right
//				x1 = myX - myHitbox.width * 0.5f;
//				y1 = myY + myHitbox.height * 0.5f;
//				x2 = myX + myHitbox.width * 0.5f;
//				y2 = myY - myHitbox.height * 0.5f;
//			}
//			// Quadrant 2 or 4
//			else {
//				// Use upper right and lower left
//				x1 = myX + myHitbox.width * 0.5f;
//				y1 = myY + myHitbox.height * 0.5f;
//				x2 = myX - myHitbox.width * 0.5f;
//				y2 = myY - myHitbox.height * 0.5f;
//			}
//			toX1 = x1 + length * MathUtils.cosDeg(angle);
//			toY1 = y1 + length * MathUtils.sinDeg(angle);
//			toX2 = x2 + length * MathUtils.cosDeg(angle);
//			toY2 = y2 + length * MathUtils.sinDeg(angle);
//			
//			ray1 = level.performRayTrace(x1, y1, toX1, toY1); 
//			ray2 = level.performRayTrace(x2, y2, toX2, toY2);
//			
//			// One of the ray traces failed
//			if(!ray1 || !ray2){
//				// Quadrant 1
//				if(angle >= 0 && angle < 90){
//					// Looking up, move down
//					if(!ray1) angle += interval;
//					else      angle -= interval;
//				}
//				// Quadrant 2
//				else if(angle <= 180 && angle >= 90){
//					// Looking up, move down
//					if(!ray1) angle -= interval;
//					else      angle += interval;
//				}
//				// Quadrant 3
//				else if(angle >= -180 && angle <= -90){
//					// Looking down, move up
//					if(!ray1) angle += interval;
//					else      angle -= interval;
//				}
//				// Quadrant 4
//				else{
//					// Looking down, move up
//					if(!ray1) angle -= interval;
//					else      angle += interval;
//				}
//				if(angle > 180) angle -= 360;
//				if(angle <= -180) angle += 360;
//			}
//			
//			System.out.println("Testing Angle: " + angle);
//			
//			if(counter >= 30) break;
//		}while(!ray1 || !ray2);
//		
//		System.out.println("Found Angle: " + finalAngle);
//
//		// Debug Render
//		DebugRender.setType(ShapeType.Line);
//		DebugRender.setColor(Color.RED);
//		DebugRender.line(x1, y1, toX1, toY1);
//		DebugRender.line(x2, y2, toX2, toY2);
//		DebugRender.setType(ShapeType.Filled);
//		DebugRender.rect(myX - 0.05f, myY - 0.05f, 0.1f, 0.1f, 5.0f);
		
		float xx = MathUtils.cosDeg(angle);
		float yy = MathUtils.sinDeg(angle);

		controller.releaseAll();
		if (xx < 0) {
			controller.press(Actions.MOVE_LEFT, Math.abs(xx));
		}
		else {
			controller.press(Actions.MOVE_RIGHT, Math.abs(xx));
		}
		if (yy < 0) {
			controller.press(Actions.MOVE_DOWN, Math.abs(yy));
		}
		else {
			controller.press(Actions.MOVE_UP, Math.abs(yy));
		}
	}

}
