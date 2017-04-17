package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TargetComponent;
import com.fullspectrum.debug.DebugVars;
import com.fullspectrum.level.LevelHelper;
import com.fullspectrum.utils.EntityUtils;

public class TargetingSystem extends IteratingSystem{
	
	public TargetingSystem(){
		super(Family.all(TargetComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if(DebugVars.AI_DISABLED) return;
		
		TargetComponent targetComp = Mappers.target.get(entity);
		
		// don't re-target if you already have a valid target
		if(EntityUtils.isValid(targetComp.target) && 
			EntityUtils.isTargetable(targetComp.target) && 
			targetComp.behavior.targetCost(entity, targetComp.target) < targetComp.behavior.maxLimit()) return;
	
		LevelComponent levelComp = Mappers.level.get(entity);
		LevelHelper helper = levelComp.levelHelper;
		
		Entity newTarget = null;
		Array<Entity> candidates = helper.getAliveEntities(Mappers.status.get(entity).status.getOpposite());;
		
		float cost = Float.MAX_VALUE;
		for(Entity e : candidates){
			if(!EntityUtils.isTargetable(e)) continue;
			float c = targetComp.behavior.targetCost(entity, e);
			if(c < cost && c < targetComp.behavior.maxLimit()){
				cost = c;
				newTarget = e;
			}
		}
		
		targetComp.target = newTarget;
	}
	
	
	
}
