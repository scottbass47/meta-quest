package com.cpubrew.effects;

import com.badlogic.ashley.core.Entity;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.TimeListener;
import com.cpubrew.entity.DelayedAction;
import com.cpubrew.entity.EntityManager;

public abstract class Effect {

	protected float duration;
	protected Entity toEntity;
	protected boolean delayed = false;
	protected boolean stackable;

	public Effect(Entity toEntity, float duration, boolean delayed) {
		this.toEntity = toEntity;
		this.duration = duration;
		this.delayed = delayed;
	}
	
	public boolean canApply() {
		if (Mappers.death.get(toEntity).shouldDie()) return false;
		if (Mappers.heatlh.get(toEntity) != null && Mappers.heatlh.get(toEntity).health <= 0.0f) return false;
		if (Mappers.timer.get(toEntity).timers.containsKey(getName() + "_effect") && !stackable) return false;
		if (Mappers.immune.get(toEntity) != null && Mappers.immune.get(toEntity).isImmuneTo(getType())) return false;
		return true;
	}
	
	public boolean apply() {
		// Don't apply the effect if its already applied
		if(!canApply()) return false;
		
		final Effect effect = this;
		if (delayed) {
			EntityManager.addDelayedAction(new DelayedAction(toEntity) {
				@Override
				public void onAction() {
					give();
					Mappers.timer.get(toEntity).add(getName() + "_effect", duration, false, new TimeListener() {
						@Override
						public void onTime(Entity entity) {
							EntityManager.addDelayedAction(new DelayedAction(getEntity()) {
								@Override
								public void onAction() {
									cleanUp();
									Mappers.effect.get(toEntity).remove(effect);
								}
							});
						}
					});
				}
			});
		} else {
			give();
			Mappers.timer.get(toEntity).add(getName() + "_effect", duration, false, new TimeListener() {
				@Override
				public void onTime(Entity entity) {
					cleanUp();
					Mappers.effect.get(toEntity).remove(effect);
				}
			});
		}
		return true;
	}

	protected abstract void give();
	protected abstract void cleanUp();
	public abstract EffectType getType();
	public abstract String getName();

	@Override
	public String toString() {
		return getName();
	}
	
	public Entity getEntity() {
		return toEntity;
	}
}
