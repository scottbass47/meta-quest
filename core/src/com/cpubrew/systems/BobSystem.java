package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.cpubrew.component.BobComponent;
import com.cpubrew.component.ForceComponent;
import com.cpubrew.component.Mappers;

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

		if(Mappers.force.get(entity) == null){
			entity.add(Mappers.engine.get(entity).engine.createComponent(ForceComponent.class));
		}
		ForceComponent forceComp = Mappers.force.get(entity);
		forceComp.fy += MathUtils.isEqual(bobComp.elapsed - deltaTime, 0.0f) ? ffy : ffy - f0y;
	}
	
	private float getFY(BobComponent bobComp, float elapsed){
		float freq = bobComp.bobSpeed * MathUtils.PI2;
		return bobComp.bobHeight * freq * MathUtils.cos(freq * bobComp.elapsed);
	}
	
}
