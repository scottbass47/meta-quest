package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.component.FlyingComponent;
import com.fullspectrum.component.ForceComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.component.ForceComponent.CForce;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.Input;

public class FlyingSystem extends IteratingSystem{

	public FlyingSystem(){
		super(Family.all(FlyingComponent.class, ForceComponent.class, InputComponent.class, SpeedComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Input input = Mappers.input.get(entity).input;
		SpeedComponent speedComp = Mappers.speed.get(entity);
		ForceComponent forceComp = Mappers.force.get(entity);
		
		float vx = speedComp.maxSpeed * (input.isPressed(Actions.MOVE_LEFT) ? -input.getValue(Actions.MOVE_LEFT) : input.getValue(Actions.MOVE_RIGHT));
		float vy = speedComp.maxSpeed * (input.isPressed(Actions.MOVE_DOWN) ? -input.getValue(Actions.MOVE_DOWN) : input.getValue(Actions.MOVE_UP));
		
		float dx = vx - forceComp.getFX(CForce.MOVEMENT);
		float dy = vy - forceComp.getFY(CForce.MOVEMENT);
		
		// acceleration
		float ax = dx >= 0 ? deltaTime : -deltaTime;
		float ay = dy >= 0 ? deltaTime : -deltaTime;
		
		float fx = MathUtils.clamp(forceComp.getFX(CForce.MOVEMENT) + ax, -Math.abs(vx), Math.abs(vx));
		float fy = MathUtils.clamp(forceComp.getFY(CForce.MOVEMENT) + ay, -Math.abs(vy), Math.abs(vy));
		
		forceComp.add(CForce.MOVEMENT, fx, fy);
		
	}
	
}
