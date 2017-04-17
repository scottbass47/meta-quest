package com.fullspectrum.physics.collision.filter;

import com.badlogic.gdx.utils.ObjectSet;
import com.fullspectrum.entity.EntityStatus;
import com.fullspectrum.physics.collision.BodyInfo;
import com.fullspectrum.physics.collision.CollisionBodyType;

public class CollisionFilter {

	private ObjectSet<CollisionBodyType> collisionBodyTypes;
	private ObjectSet<EntityStatus> entityTypes;
	private ObjectSet<CustomFilter> customFilters;
	
	public CollisionFilter() {
		collisionBodyTypes = new ObjectSet<CollisionBodyType>();
		entityTypes = new ObjectSet<EntityStatus>();
		customFilters = new ObjectSet<CustomFilter>();
	}

	public boolean passesFilter(BodyInfo me, BodyInfo other){
		for(CustomFilter filter : customFilters){
			if(filter.passesFilter(me, other)) return true;
		}
		if(!collisionBodyTypes.contains(other.getBodyType())) return false;
		if(!entityTypes.contains(other.getEntityStatus())) return false;
		return true;
	}
	
	public static class Builder {
		
		private CollisionFilter filter;
		
		public Builder(){
			filter = new CollisionFilter();
		}
		
		public Builder addBodyTypes(CollisionBodyType... bodyTypes){
			filter.collisionBodyTypes.addAll(bodyTypes);
			return this;
		}
		
		public Builder addEntityTypes(EntityStatus... entityTypes){
			filter.entityTypes.addAll(entityTypes);
			return this;
		}
		
		public Builder addCustomFilter(CustomFilter customFilter){
			filter.customFilters.add(customFilter);
			return this;
		}
		
		public Builder allBodyTypes(){
			filter.collisionBodyTypes.addAll(CollisionBodyType.values());
			return this;
		}
		
		public Builder allEntityTypes(){
			filter.entityTypes.addAll(EntityStatus.values());
			return this;
		}
		
		public Builder removeBodyType(CollisionBodyType bodyType){
			filter.collisionBodyTypes.remove(bodyType);
			return this;
		}
		
		public Builder removeEntityType(EntityStatus entityType){
			filter.entityTypes.remove(entityType);
			return this;
		}
		
		public CollisionFilter build(){
			return filter;
		}
	}
	
}
