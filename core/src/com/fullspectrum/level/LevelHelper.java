package com.fullspectrum.level;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.StatusComponent;
import com.fullspectrum.entity.EntityStatus;

public class LevelHelper {

	private final Level level;
	private Engine engine;
	private Entity entity;
	
	public LevelHelper(Level level, Entity entity){
		this.level = level;
		this.engine = Mappers.engine.get(entity).engine;
		this.entity = entity;
	}
	
	public boolean isOpen(float x, float y){
		return !level.isSolid(x, y);
	}
	
	public boolean isOpen(int row, int col){
		return !level.isSolid(row, col);
	}
	
	@SuppressWarnings("unchecked")
	public Array<Entity> getEntities(EntityStatus type){
		Array<Entity> ret = new Array<Entity>();
		for(Entity entity : engine.getEntitiesFor(Family.all(LevelComponent.class, StatusComponent.class).get())){
			if(Mappers.status.get(entity).status.equals(type)){
				ret.add(entity);
			}
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public Array<Entity> getAliveEntities(EntityStatus type){
		Array<Entity> ret = new Array<Entity>();
		for(Entity entity : engine.getEntitiesFor(Family.all(LevelComponent.class, StatusComponent.class, HealthComponent.class).get())){
			if(Mappers.status.get(entity).status.equals(type)){
				ret.add(entity);
			}
		}
		return ret;
	}
	
	public Array<Entity> getEntities(EntityGrabber grabber){
		Array<Entity> ret = new Array<Entity>();
		for(Entity entity : engine.getEntitiesFor(grabber.componentsNeeded())){
			if(grabber.validEntity(this.entity, entity)) ret.add(entity);
		}
		return ret;
	}
	
	public Level getLevel(){
		return this.level;
	}
}