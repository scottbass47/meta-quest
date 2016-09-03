package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.TextureComponent;
import com.fullspectrum.game.GameVars;

public class PositioningSystem extends IteratingSystem{

	@SuppressWarnings("unchecked")
	public PositioningSystem(){
		super(Family.all(PositionComponent.class, TextureComponent.class, BodyComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		BodyComponent bodyComp = Mappers.body.get(entity);
		PositionComponent posComp = Mappers.position.get(entity);
		TextureComponent texComp = Mappers.texture.get(entity);
		posComp.x = (float) (bodyComp.body.getPosition().x - ((texComp.region.getRegionWidth() * 0.5) / GameVars.PPM));
		posComp.y = (float) (bodyComp.body.getPosition().y - ((texComp.region.getRegionHeight() * 0.5) / GameVars.PPM));
	}
	
}
