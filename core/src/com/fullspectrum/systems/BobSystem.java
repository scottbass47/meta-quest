package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.fullspectrum.component.BobComponent;
import com.fullspectrum.component.ForceComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.ForceComponent.CForce;

public class BobSystem extends IteratingSystem{

	public BobSystem() {
		super(Family.all(BobComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		BobComponent bobComp = Mappers.bob.get(entity);
		ForceComponent forceComp = Mappers.force.get(entity);

		bobComp.elapsed += deltaTime;
		forceComp.add(CForce.BOBBING, 0.0f, bobComp.bobHeight * MathUtils.cos(bobComp.elapsed * bobComp.bobSpeed * MathUtils.PI2));
	}
}
