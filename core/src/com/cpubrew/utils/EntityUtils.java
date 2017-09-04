package com.cpubrew.utils;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.PlayerComponent;
import com.cpubrew.factory.EntityFactory;

public class EntityUtils {
	
	public static final int VALID 	   = 1 << 0;
	public static final int TARGETABLE = 1 << 1;
	public static final int STUNNED    = 1 << 2;
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
		return isValid(entity) && (entity.flags & TARGETABLE) == TARGETABLE;
	}
	
	public static void setTargetable(Entity entity, boolean valid){
		if(valid){
			entity.flags |= TARGETABLE;
		}
		else{
			entity.flags &= ~TARGETABLE;
		}
	}
	
	public static boolean isStunned(Entity entity){
		return isValid(entity) && (entity.flags & STUNNED) == STUNNED;
	}
	
	public static void setStunned(Entity entity, boolean stunned){
		if(stunned){
			entity.flags |= STUNNED;
		}
		else{
			entity.flags &= ~STUNNED;
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
	public static Entity getPlayer(){
		ImmutableArray<Entity> players = EntityFactory.engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
		return players.size() == 0 ? null : players.first();
	}
	
	public static int getID(Entity entity){
		return Mappers.entity.get(entity).getID();
	}
	
	public static String asString(Entity entity) {
		return Mappers.entity.get(entity).toString();
	}
	
}