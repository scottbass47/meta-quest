package com.cpubrew.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.FlowFieldComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.TargetComponent;
import com.cpubrew.level.FlowFieldManager;
import com.cpubrew.utils.EntityUtils;

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
