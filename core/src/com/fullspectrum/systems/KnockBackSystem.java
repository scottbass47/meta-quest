package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.KnockBackComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TimerComponent.Timer;

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
		float knockUp = 5.0f + knockBackComp.speed / 4.0f; //+ 10.0f * MathUtils.sinDeg(knockBackComp.angle);
		
		float dx = MathUtils.cosDeg(knockBackComp.angle) * knockBackComp.speed * ((total - elapsed) / total);
		float dy = bodyComp.body.getLinearVelocity().y;
		
		if(Mappers.flying.get(entity) == null && !knockBackComp.knockedUp){
			// One time knock upwards
			dy = knockBackComp.angle <= 180 && knockBackComp.angle >= 0 ? knockUp : -knockUp;
			knockBackComp.knockedUp = true;
		} else if(Mappers.flying.get(entity) != null){
			dy = knockBackComp.angle <= 180 && knockBackComp.angle >= 0 ? knockUp * 0.5f : -knockUp * 0.5f;
		}
		bodyComp.body.setLinearVelocity(dx, dy);
	}
	
}
