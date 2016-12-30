package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.ESMComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.LadderComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.input.Actions;

public class LadderMovementSystem extends IteratingSystem{

	public LadderMovementSystem(){
		super(Family.all(LadderComponent.class, InputComponent.class, BodyComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		LadderComponent ladderComp = Mappers.ladder.get(entity);
		InputComponent inputComp = Mappers.input.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		Body body = bodyComp.body;
		
		boolean right = inputComp.input.getValue(Actions.MOVE_RIGHT) > inputComp.input.getValue(Actions.MOVE_LEFT);
		boolean up = inputComp.input.getValue(Actions.MOVE_UP) > inputComp.input.getValue(Actions.MOVE_DOWN);
		
		float mx = Math.abs(inputComp.input.getValue(Actions.MOVE_RIGHT) - inputComp.input.getValue(Actions.MOVE_LEFT));
		float my = Math.abs(inputComp.input.getValue(Actions.MOVE_DOWN) - inputComp.input.getValue(Actions.MOVE_UP));
	
		float vx = ladderComp.speedX * mx * (right ? 1.0f : -1.0f);
		float vy = ladderComp.speedY * my * (up ? 1.0f : -1.0f);
		
		float cx = vx - body.getLinearVelocity().x;
		float cy = vy - body.getLinearVelocity().y;
		
		bodyComp.body.applyLinearImpulse(cx * body.getMass(), cy * body.getMass(), bodyComp.body.getWorldCenter().x, bodyComp.body.getWorldCenter().y, true);
	
		if(MathUtils.isEqual(vx, 0) && MathUtils.isEqual(vy, 0)){
			ESMComponent esmComp = Mappers.esm.get(entity);
			if(esmComp == null || esmComp.esm == null) return;
			esmComp.esm.addAnimationTime(-deltaTime);
		}
	}
	
}