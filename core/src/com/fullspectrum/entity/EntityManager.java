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

public class EntityManager {

	private static Array<Entity> toDie = new Array<Entity>();
	
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
	
	public static void update(float delta){
		for(Iterator<Entity> iter = toDie.iterator(); iter.hasNext();){
			Entity entity = iter.next();
			DeathComponent deathComp = Mappers.death.get(entity);
			deathComp.triggerDeath();
			iter.remove();
		}
	}
	
	public static void sendToDie(Entity entity){
		toDie.add(entity);
	}

}
