package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.FlyingComponent;
import com.cpubrew.component.InputComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.SpeedComponent;
import com.cpubrew.input.Actions;
import com.cpubrew.input.Input;

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
		
		float xImp = dx * deltaTime * 4;
		float yImp = dy * deltaTime * 4;
		if(Math.abs(dx) < 0.05f){
			xImp = dx;
		}
		if(Math.abs(dy) < 0.05f){
			yImp = dy;
		}
		body.applyLinearImpulse(xImp, yImp, body.getWorldCenter().x, body.getWorldCenter().y, true);
	}
	
}
