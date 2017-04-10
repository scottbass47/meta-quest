package com.fullspectrum.utils;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PlayerComponent;

public class EntityUtils {
	
	public static final int VALID = 0x1;
	public static final int TARGETABLE = 0x2;
	public static boolean engineUpdating = false;

	private EntityUtils(){}
	
	public static boolean isValid(Entity entity){
		if(entity == null) return false;
		return (entity.flags & VALID) == VALID;
	}
	
	public static void setValid(Entity entity, boolean valid){
		if(valid){
			entity.flags |= VALID;
		}
		else{
			entity.flags &= ~VALID;
		}
	}
	
	public static boolean isTargetable(Entity entity){
		return (entity.flags & TARGETABLE) == TARGETABLE;
	}
	
	public static void setTargetable(Entity entity, boolean valid){
		if(valid){
			entity.flags |= TARGETABLE;
		}
		else{
			entity.flags &= ~TARGETABLE;
		}
	}
	
	public static <T extends Component> T lazyAdd(Entity entity, Class<T> component){
		if(entity.getComponent(component) == null){
			entity.add(Mappers.engine.get(entity).engine.createComponent(component));
		}
		return entity.getComponent(component);
	}
	
	public static <T extends Component> T add(Entity entity, Class<T> component){
		T comp = Mappers.engine.get(entity).engine.createComponent(component);
		entity.add(comp);
		return comp;
	} 
	
	@SuppressWarnings("unchecked")
	public static Entity getPlayer(Engine engine){
		return engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
	}
	
}