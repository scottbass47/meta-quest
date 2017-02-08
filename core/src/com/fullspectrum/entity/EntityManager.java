package com.fullspectrum.entity;

import java.util.Iterator;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.DeathComponent;
import com.fullspectrum.component.EngineComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.WorldComponent;
import com.fullspectrum.physics.PhysicsDef;
import com.fullspectrum.utils.PhysicsUtils;

public class EntityManager {

	private static Array<Entity> toDie = new Array<Entity>();
	private static Array<Entity> toAdd = new Array<Entity>();
	private static Array<PhysicsDef> toLoadPhysics = new Array<PhysicsDef>();
	
	public static void cleanUp(Entity entity) {
		EngineComponent engineComp = Mappers.engine.get(entity);
		WorldComponent worldComp = Mappers.world.get(entity);
		Engine engine = engineComp.engine;

		BodyComponent bodyComp = Mappers.body.get(entity);

		if (bodyComp != null && bodyComp.body != null && worldComp != null && worldComp.world != null) {
			worldComp.world.destroyBody(bodyComp.body);
		}
		for (Component c : entity.getComponents()) {
			if (c instanceof Poolable) {
				((Poolable) c).reset();
			}
		}
		if (engine != null) {
			engine.removeEntity(entity);
		}
	}
	
	public static void addEntity(Entity entity){
		toAdd.add(entity);
	}
	
	public static void addPhysicsLoad(PhysicsDef def){
		toLoadPhysics.add(def);
	}
	
	public static void update(float delta){
		// Delayed death
		for(Iterator<Entity> iter = toDie.iterator(); iter.hasNext();){
			Entity entity = iter.next();
			DeathComponent deathComp = Mappers.death.get(entity);
			deathComp.triggerDeath();
			iter.remove();
		}
		
		// Delayed physics loading
		for(Iterator<PhysicsDef> iter = toLoadPhysics.iterator(); iter.hasNext();){
			PhysicsDef def = iter.next();
			Entity entity = def.getEntity();
			entity.add(Mappers.engine.get(entity).engine.createComponent(BodyComponent.class).set(PhysicsUtils.createPhysicsBody(def)));
			iter.remove();
		}
		
		// Delayed adding
		for(Iterator<Entity> iter = toAdd.iterator(); iter.hasNext();){
			Entity entity = iter.next();
			Mappers.engine.get(entity).engine.addEntity(entity);
			iter.remove();
		}
	}
	
	public static void sendToDie(Entity entity){
		toDie.add(entity);
	}

}
