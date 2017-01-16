package com.fullspectrum.systems;

import java.util.Iterator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.ForceComponent;
import com.fullspectrum.component.ForceComponent.CForce;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.VelocityComponent;

public class ForceSystem extends IteratingSystem{

//	public VelocitySystem(){
//		super(Family.all(DirectionComponent.class, SpeedComponent.class, VelocityComponent.class).get());
//	}

//	@Override
//	protected void processEntity(Entity entity, float deltaTime) {
//		DirectionComponent directionComp = Mappers.direction.get(entity);
//		SpeedComponent speedComp = Mappers.speed.get(entity);
//		VelocityComponent velocityComp = Mappers.velocity.get(entity);
//		InputComponent inputComp = Mappers.input.get(entity);
//		
//		if(inputComp != null){
//			speedComp.multiplier = Math.abs(inputComp.input.getValue(Actions.MOVE_LEFT) - inputComp.input.getValue(Actions.MOVE_RIGHT));
//		}
//		
//		velocityComp.dx = speedComp.maxSpeed * speedComp.multiplier * directionComp.direction.getDirection();
//	}
	
	/**
	 * Takes sum of forces and sets velocity component
	 */
	public ForceSystem(){
		super(Family.all(ForceComponent.class, VelocityComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ForceComponent forceComp = Mappers.force.get(entity);
		VelocityComponent velocityComp = Mappers.velocity.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		
		if(bodyComp != null && bodyComp.body != null){
			Body body = bodyComp.body;
			
			// Loop through continuous forces and reset inactive ones
			for(Iterator<CForce> iter = forceComp.cForceMap.keys().iterator(); iter.hasNext();){
				CForce cForce = iter.next();
				if(!cForce.isActive(entity)){
					forceComp.cForceMap.put(cForce, new Vector2());
				}
			}
			
			Vector2 cForceSum = forceComp.sumCForces();
			
			float regForceX = forceComp.fx - cForceSum.x;
			float regForceY = forceComp.fy - cForceSum.y;
			
			float dx = forceComp.fx + (velocityComp.ex - body.getLinearVelocity().x);
			float dy = forceComp.fy + (velocityComp.ey - body.getLinearVelocity().y);
			
			velocityComp.ex = body.getLinearVelocity().x + dx;
			velocityComp.ey = body.getLinearVelocity().y + dy;
			
			body.applyLinearImpulse(dx, dy, body.getWorldCenter().x, body.getWorldCenter().y, true);
		}else{
			velocityComp.dx += forceComp.fx;
			velocityComp.dy += forceComp.fy;
		}
		forceComp.set(0.0f, 0.0f);
	}
	
}
