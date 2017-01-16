package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.VelocityComponent;

public class PositioningSystem extends IteratingSystem{

	/**
	 * Uses velocity component and delta time to determine new position
	 */
	public PositioningSystem(){
		super(Family.all(PositionComponent.class, VelocityComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PositionComponent positionComp = Mappers.position.get(entity);
		VelocityComponent velocityComp = Mappers.velocity.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		
		if(bodyComp != null && bodyComp.body != null){
			positionComp.x = bodyComp.body.getPosition().x;
			positionComp.y = bodyComp.body.getPosition().y;
		}else{
			positionComp.x += velocityComp.dx * deltaTime;
			positionComp.y += velocityComp.dy * deltaTime;
		}
		
//		BodyComponent bodyComp = Mappers.body.get(entity);
//		PositionComponent posComp = Mappers.position.get(entity);
//		TextureComponent texComp = Mappers.texture.get(entity);
//		posComp.x = bodyComp.body.getPosition().x - (texComp.region.getRegionWidth() * 0.5f * PPM_INV);
//		posComp.y = bodyComp.body.getPosition().y - (texComp.region.getRegionHeight() * 0.5f * PPM_INV);
//		posComp.x = ((int)(posComp.x * GameVars.PPM)) / GameVars.PPM;
//		posComp.y = ((int)(posComp.y * GameVars.PPM)) / GameVars.PPM;
//		System.out.println((posComp.x * PPM) + ", " + (posComp.y * PPM));
	}
	
}
