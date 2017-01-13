package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.fullspectrum.component.BobComponent;
import com.fullspectrum.component.ForceComponent;
import com.fullspectrum.component.Mappers;

public class BobSystem extends IteratingSystem{

	public BobSystem() {
		super(Family.all(BobComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		BobComponent bobComp = Mappers.bob.get(entity);
		
		float f0y = getFY(bobComp, bobComp.elapsed);
		bobComp.elapsed += deltaTime;
		float ffy = getFY(bobComp, bobComp.elapsed); // follows sin wave (cos is derivative of sin)

//		System.out.println("Position: " + Mappers.body.get(entity).body.getPosition().y);
//		System.out.println("Velocity: " + Mappers.body.get(entity).body.getLinearVelocity().y);

		if(Mappers.force.get(entity) == null){
			entity.add(Mappers.engine.get(entity).engine.createComponent(ForceComponent.class));
		}
		ForceComponent forceComp = Mappers.force.get(entity);
		forceComp.fy += MathUtils.isEqual(bobComp.elapsed - deltaTime, 0.0f) ? ffy : ffy - f0y;
	}
	
	private float getFY(BobComponent bobComp, float elapsed){
		return bobComp.bobHeight * MathUtils.cos(bobComp.elapsed * bobComp.bobSpeed * MathUtils.PI2);
	}
	
}
