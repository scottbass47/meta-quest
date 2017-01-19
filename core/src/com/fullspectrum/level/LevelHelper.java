package com.fullspectrum.level;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TypeComponent;
import com.fullspectrum.component.TypeComponent.EntityType;

public class LevelHelper {

	private final Level level;
	private Engine engine;
	
	public LevelHelper(Level level, Entity entity){
		this.level = level;
		this.engine = Mappers.engine.get(entity).engine;
	}
	
	public boolean isOpen(float x, float y){
		return !level.isSolid(x, y);
	}
	
	public boolean isOpen(int row, int col){
		return !level.isSolid(row, col);
	}
	
	@SuppressWarnings("unchecked")
	public Array<Entity> getEntities(EntityType type){
		Array<Entity> ret = new Array<Entity>();
		for(Entity entity : engine.getEntitiesFor(Family.all(LevelComponent.class, TypeComponent.class).get())){
			if(Mappers.type.get(entity).type.equals(type)){
				ret.add(entity);
			}
		}
		return ret;
	}
	
	public Level getLevel(){
		return this.level;
	}
	
}