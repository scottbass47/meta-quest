package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TargetComponent implements Component, Poolable{

	public Entity target;
	public TargetBehavior behavior;

	public TargetComponent(){
		this.behavior = new DefaultTargetBehavior();
	}
	
	@Override
	public void reset() {
		target = null;
		behavior = null;
	}
	
	public TargetComponent set(TargetBehavior behavior){
		this.behavior = behavior;
		return this;
	}
	
	public static interface TargetBehavior{
		public float targetCost(Entity me, Entity target);
		public float maxLimit();
	}
	
	public static class DefaultTargetBehavior implements TargetBehavior{
		@Override
		public float targetCost(Entity me, Entity target) {
			PositionComponent myPos = Mappers.position.get(me);
			PositionComponent targetPos = Mappers.position.get(target);
			return (float)(Math.pow(myPos.x - targetPos.x, 2) + Math.pow(myPos.y - targetPos.y, 2));
		}

		@Override
		public float maxLimit() {
			return Float.MAX_VALUE;
		} 
	}
	
	public static class PlayerTargetBehavior implements TargetBehavior{

		@Override
		public float targetCost(Entity me, Entity target) {
			if(Mappers.player.get(target) == null) return Float.MAX_VALUE;
			PositionComponent myPos = Mappers.position.get(me);
			PositionComponent targetPos = Mappers.position.get(target);
			return (float)(Math.pow(myPos.x - targetPos.x, 2) + Math.pow(myPos.y - targetPos.y, 2));
		}

		@Override
		public float maxLimit() {
			return Float.MAX_VALUE - 1;
		}
		
	}
	
}
