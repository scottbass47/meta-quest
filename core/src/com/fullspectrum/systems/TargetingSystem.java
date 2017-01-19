package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.TargetComponent;
import com.fullspectrum.entity.EntityUtils;
import com.fullspectrum.level.LevelHelper;

public class TargetingSystem extends IteratingSystem{

	
	public TargetingSystem(){
		super(Family.all(TargetComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TargetComponent targetComp = Mappers.target.get(entity);
		
		// don't re-target if you already have a valid target
		if(targetComp.target != null && EntityUtils.isValid(targetComp.target)) return;
	
		PositionComponent positionComp = Mappers.position.get(entity);
		LevelComponent levelComp = Mappers.level.get(entity);
		LevelHelper helper = levelComp.levelHelper;
		
		Entity newTarget = null;
		float distance = Float.MAX_VALUE;
		for(Entity e : helper.getEntities(Mappers.type.get(entity).type.getOpposite())){
			PositionComponent posComp = Mappers.position.get(e);
			float d = (posComp.x - positionComp.x) * (posComp.x - positionComp.x) + (posComp.y - positionComp.y) * (posComp.y - positionComp.y);
			if(d < distance){
				distance = d;
				newTarget = e;
			}
		}
		
		targetComp.target = newTarget;
	}
	
	
	
}
