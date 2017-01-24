package com.fullspectrum.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.FlowFieldComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TargetComponent;
import com.fullspectrum.entity.EntityUtils;
import com.fullspectrum.level.FlowFieldManager;

public class FlowFieldSystem extends EntitySystem{

	private FlowFieldManager manager;
	private ImmutableArray<Entity> entities;
	
	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(FlowFieldComponent.class, TargetComponent.class).get());
	}
	
	@Override
	public void update(float deltaTime) {
		for(Entity entity : entities){
			TargetComponent targetComp = Mappers.target.get(entity);
			FlowFieldComponent flowFieldComp = Mappers.flowField.get(entity);
			
			if(!EntityUtils.isValid(targetComp.target)) continue;
			
			Entity target = targetComp.target;
			BodyComponent bodyComp = Mappers.body.get(target);
			Vector2 position = bodyComp.body.getPosition();
			
			flowFieldComp.field = manager.getField(position.x, position.y);
		}
		if(manager != null) manager.update(deltaTime);
	}
	
	public void setFlowManager(FlowFieldManager manager){
		this.manager = manager;
	}
}
