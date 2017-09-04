package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.KnockBackComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.TimerComponent.Timer;

public class KnockBackSystem extends IteratingSystem{

	public KnockBackSystem() {
		super(Family.all(KnockBackComponent.class, BodyComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		KnockBackComponent knockBackComp = Mappers.knockBack.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		
		Timer timer = Mappers.timer.get(entity).get("knockback_effect");
		float elapsed = timer.getElapsed();
		float total = timer.getTotalTime();
		float angle = knockBackComp.angle;
//		float angle = knockBackComp.angle + getAngleAdjustment(knockBackComp.angle);
		
		float dx = MathUtils.cosDeg(angle) * knockBackComp.speed * ((total - elapsed) / total);
		float dy = MathUtils.sinDeg(angle) * knockBackComp.speed * ((total - elapsed) / total);
		
		bodyComp.body.setLinearVelocity(dx, dy);
		
//		float dy = bodyComp.body.getLinearVelocity().y;
//		
//		if(Mappers.flying.get(entity) == null && !knockBackComp.knockedUp){
//			// One time knock upwards
//			dy = MathUtils.sinDeg(angle) * knockBackComp.speed;
//			knockBackComp.knockedUp = true;
//		} else if(Mappers.flying.get(entity) != null){
//			dy = 0.5f * MathUtils.sinDeg(angle) * knockBackComp.speed;
//		}
//		bodyComp.body.setLinearVelocity(dx, dy);
	}
	
//	private float getAngleAdjustment(float angle){
//		// Normalise angle between 0 and 360
//		while(angle < 0) angle += 360;
//		while(angle > 360) angle -= 360;
//		
//		float minAngle = 30.0f;
//		if(angle >= 0 && angle <= 90){
//			return (90.0f - angle) * (minAngle / 90.0f);
//		} else if(angle > 90 && angle <= 180){
//			angle = 180 - angle;
//			angle = (90.0f - angle) * (minAngle / 90.0f);
//			return -angle;
//		} else if(angle > 180 && angle <= 270){
//			angle = angle - 180;
//			angle = (90.0f - angle) * (minAngle / 90.0f);
//			return angle;
//		} else{
//			angle = 360 - angle;
//			angle = (90.0f - angle) * (minAngle / 90.0f);
//			return -angle;
//		}
//	}
}
