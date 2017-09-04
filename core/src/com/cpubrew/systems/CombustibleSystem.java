package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.cpubrew.component.CombustibleComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.PositionComponent;
import com.cpubrew.factory.ProjectileFactory;
import com.cpubrew.game.GameVars;

public class CombustibleSystem extends IteratingSystem {

	public CombustibleSystem() {
		super(Family.all(CombustibleComponent.class, PositionComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		CombustibleComponent combustibleComp = Mappers.combustible.get(entity);
		
		if(combustibleComp.shouldExplode){
			combustibleComp.shouldExplode = false;
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
