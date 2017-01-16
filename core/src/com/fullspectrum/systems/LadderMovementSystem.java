package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.fullspectrum.component.ESMComponent;
import com.fullspectrum.component.ForceComponent;
import com.fullspectrum.component.ForceComponent.CForce;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.LadderComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.input.Actions;

public class LadderMovementSystem extends IteratingSystem{

	public LadderMovementSystem(){
		super(Family.all(LadderComponent.class, InputComponent.class, ForceComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		LadderComponent ladderComp = Mappers.ladder.get(entity);
		InputComponent inputComp = Mappers.input.get(entity);
		ForceComponent forceComp = Mappers.force.get(entity);
		
		boolean right = inputComp.input.getValue(Actions.MOVE_RIGHT) > inputComp.input.getValue(Actions.MOVE_LEFT);
		boolean up = inputComp.input.getValue(Actions.MOVE_UP) > inputComp.input.getValue(Actions.MOVE_DOWN);
		
		float mx = Math.abs(inputComp.input.getValue(Actions.MOVE_RIGHT) - inputComp.input.getValue(Actions.MOVE_LEFT));
		float my = Math.abs(inputComp.input.getValue(Actions.MOVE_DOWN) - inputComp.input.getValue(Actions.MOVE_UP));
	
		float vx = ladderComp.speedX * mx * (right ? 1.0f : -1.0f);
		float vy = ladderComp.speedY * my * (up ? 1.0f : -1.0f);
		
		forceComp.add(CForce.MOVEMENT, vx, vy);
		
		if(MathUtils.isEqual(vx, 0) && MathUtils.isEqual(vy, 0)){
			ESMComponent esmComp = Mappers.esm.get(entity);
			if(esmComp == null || esmComp.esm == null) return;
			esmComp.esm.addAnimationTime(-deltaTime);
		}
	}
	
}
