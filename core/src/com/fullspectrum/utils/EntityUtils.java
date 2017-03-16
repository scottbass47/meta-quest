package com.fullspectrum.utils;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.Mappers;

public class EntityUtils {
	
	public static final int VALID = 0x1;
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
	
	public static <T extends Component> T lazyAdd(Entity entity, Class<T> component){
		if(entity.getComponent(component) == null){
			entity.add(Mappers.engine.get(entity).engine.createComponent(component));
		}
		return entity.getComponent(component);
	}
}
