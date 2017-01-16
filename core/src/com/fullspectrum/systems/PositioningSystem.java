package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.VelocityComponent;

public class PositioningSystem extends IteratingSystem{

	public PositioningSystem(){
		super(Family.all(PositionComponent.class).one(VelocityComponent.class, BodyComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		BodyComponent bodyComp = Mappers.body.get(entity);
		VelocityComponent velocityComp = Mappers.velocity.get(entity);
		PositionComponent posComp = Mappers.position.get(entity);
		
		if(bodyComp != null && bodyComp.body != null){
			posComp.x = bodyComp.body.getPosition().x;
			posComp.y = bodyComp.body.getPosition().y;
		}else{
			posComp.x += velocityComp.dx * deltaTime;
			posComp.y += velocityComp.dy * deltaTime;
		}
	}
	
}
