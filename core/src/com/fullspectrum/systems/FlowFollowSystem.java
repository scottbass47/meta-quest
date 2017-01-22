package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.ai.AIController;
import com.fullspectrum.component.AIControllerComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.FlowFieldComponent;
import com.fullspectrum.component.FlowFollowComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.input.Actions;
import com.fullspectrum.level.FlowField;
import com.fullspectrum.level.FlowNode;

public class FlowFollowSystem extends IteratingSystem{
	
	public FlowFollowSystem() {
		super(Family.all(AIControllerComponent.class, FlowFieldComponent.class, FlowFollowComponent.class, BodyComponent.class).get());
	}
		
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		FlowFieldComponent flowFieldComp = Mappers.flowField.get(entity);
		AIControllerComponent aiControllerComp = Mappers.aiController.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		
		FlowField field = flowFieldComp.field;
		if(field == null) return;
		
		AIController controller = aiControllerComp.controller;
		Body body = bodyComp.body;
		
		float x = body.getPosition().x;
		float y = body.getPosition().y;
		
		FlowNode node = field.getNode(x, y);
		if(node == null) return;
		float angle = node.getAngle();
		
		float xx = MathUtils.cos(angle);
		float yy = MathUtils.sin(angle);
		
		controller.releaseAll();
		if(xx < 0){
			controller.press(Actions.MOVE_LEFT, Math.abs(xx));
		}else{
			controller.press(Actions.MOVE_RIGHT, Math.abs(xx));
		}
		if(yy < 0){
			controller.press(Actions.MOVE_DOWN, Math.abs(yy));
		}else{
			controller.press(Actions.MOVE_UP, Math.abs(yy));
		}
	}
	
}
