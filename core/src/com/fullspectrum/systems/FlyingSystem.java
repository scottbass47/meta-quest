package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.FlyingComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.Input;

public class FlyingSystem extends IteratingSystem{

	public FlyingSystem(){
		super(Family.all(FlyingComponent.class, BodyComponent.class, InputComponent.class, SpeedComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Input input = Mappers.input.get(entity).input;
		Body body = Mappers.body.get(entity).body;
		SpeedComponent speedComp = Mappers.speed.get(entity);
		
		float vx = speedComp.maxSpeed * (input.isPressed(Actions.MOVE_LEFT) ? -input.getValue(Actions.MOVE_LEFT) : input.getValue(Actions.MOVE_RIGHT));
		float vy = speedComp.maxSpeed * (input.isPressed(Actions.MOVE_DOWN) ? -input.getValue(Actions.MOVE_DOWN) : input.getValue(Actions.MOVE_UP));
		
		float dx = vx - body.getLinearVelocity().x;
		float dy = vy - body.getLinearVelocity().y;
		
		body.applyLinearImpulse(dx * deltaTime * 2, dy * deltaTime * 2, body.getWorldCenter().x, body.getWorldCenter().y, true);
	}
	
}
