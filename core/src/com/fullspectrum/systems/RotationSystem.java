package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.RotationComponent;
import com.fullspectrum.component.VelocityComponent;

public class RotationSystem extends IteratingSystem {

	public RotationSystem() {
		super(Family.all(RotationComponent.class, VelocityComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		RotationComponent rotationComp = Mappers.rotation.get(entity);
		
		// Automatic rotation uses the velocity component to determine direction
		if(rotationComp.automatic) {
			VelocityComponent velComp = Mappers.velocity.get(entity);
			rotationComp.rotation = MathUtils.radiansToDegrees * MathUtils.atan2(velComp.dy, velComp.dx);
		} else {
			rotationComp.rotation += rotationComp.angularVel * deltaTime;
		}
	}
	
}
