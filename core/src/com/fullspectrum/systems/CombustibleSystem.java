package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.CombustibleComponent;
import com.fullspectrum.component.DeathComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.RemoveComponent;
import com.fullspectrum.component.TimeListener;
import com.fullspectrum.component.TimerComponent;
import com.fullspectrum.factory.ProjectileFactory;
import com.fullspectrum.game.GameVars;

public class CombustibleSystem extends IteratingSystem {

	public CombustibleSystem() {
		super(Family.all(CombustibleComponent.class, BodyComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		CombustibleComponent combustibleComp = Mappers.combustible.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		Body body = bodyComp.body;
		
		if(body.isActive() && combustibleComp.shouldExplode){
			body.setActive(false);
			combustibleComp.shouldExplode = false;
			DeathComponent deathComp = Mappers.death.get(entity);
			deathComp.triggerDeath();
			float radius = combustibleComp.radius;
			float r = 4 * GameVars.PPM_INV;
			int numParticles = (int)(MathUtils.PI * radius / (r + 8 * GameVars.PPM_INV));
			float degInc = 360.0f / numParticles;
			for(int i = 0; i < numParticles; i++){
				float deg = i * degInc;
				ProjectileFactory.spawnExplosiveParticle(entity, 2*MathUtils.cosDeg(deg), 2*MathUtils.sinDeg(deg), combustibleComp.speed, deg);
			}
		}
		
	}

}
