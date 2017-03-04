package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.KnockBackComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TimerComponent.Timer;
import com.fullspectrum.effects.KnockBackEffect;
import com.fullspectrum.game.GameVars;

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
		float knockUp = 5.0f;
		
		float dx = MathUtils.cosDeg(knockBackComp.angle) * KnockBackEffect.SPEED * ((total - elapsed) / total);
		float dy = knockBackComp.angle <= 180 && knockBackComp.angle >= 0 ? knockUp : -knockUp;
		
		if(Mappers.groundMovement.get(entity) != null){
			// One time knock upwards
			if(elapsed > GameVars.PPM_INV){
				dy = bodyComp.body.getLinearVelocity().y;
			}
		}
		bodyComp.body.setLinearVelocity(dx, dy);
	}
	
}
