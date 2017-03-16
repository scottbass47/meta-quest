package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.ai.AIController;
import com.fullspectrum.component.AIControllerComponent;
import com.fullspectrum.component.AttackComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TargetComponent;
import com.fullspectrum.input.Actions;
import com.fullspectrum.utils.EntityUtils;

public class AttackingSystem extends IteratingSystem{

	public AttackingSystem(){
		super(Family.all(AttackComponent.class, TargetComponent.class, AIControllerComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TargetComponent targetComp = Mappers.target.get(entity);
		if(!EntityUtils.isValid(targetComp.target)) return;
		AIControllerComponent aiControllerComp = Mappers.aiController.get(entity);
		Entity target = targetComp.target;
		
		FacingComponent facingComp = Mappers.facing.get(entity);
		BodyComponent myBodyComp = Mappers.body.get(entity);
		BodyComponent otherBodyComp = Mappers.body.get(target);
		
		if(myBodyComp.body.getPosition().x < otherBodyComp.body.getPosition().x){
			facingComp.facingRight = true;
		}
		else{
			facingComp.facingRight = false;
		}
		
		AIController controller = aiControllerComp.controller;
		controller.release(Actions.MOVE_LEFT, Actions.MOVE_RIGHT);
		controller.justPress(Actions.ATTACK);
	}
	
}
