package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.cpubrew.component.ASMComponent;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.InputComponent;
import com.cpubrew.component.LadderComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.fsm.AnimationStateMachine;
import com.cpubrew.input.Actions;

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
			ASMComponent asmComp = Mappers.asm.get(entity);
			if(asmComp == null || asmComp.size() == 0) return;
			for(AnimationStateMachine machine : asmComp.getMachines()) machine.addTime(-deltaTime);
		}
	}
	
}
