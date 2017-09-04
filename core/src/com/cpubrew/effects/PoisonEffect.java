package com.cpubrew.effects;

import com.badlogic.ashley.core.Entity;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.TimeListener;
import com.cpubrew.entity.DelayedAction;
import com.cpubrew.entity.EntityManager;
import com.cpubrew.handlers.DamageHandler;
import com.cpubrew.shader.PoisonShader;

public class PoisonEffect extends Effect {

	private int stacks = 1;
	private float dps;
	private float tick = 0.2f;
	private Entity fromEntity;
	private static PoisonShader shader = new PoisonShader();
	
	public PoisonEffect(Entity fromEntity, Entity toEntity, float duration, float dps) {
		super(toEntity, duration, false);
		this.dps = dps;
		this.fromEntity = fromEntity;
		stackable = true;
	}

	@Override
	protected void give() {
		addShader(toEntity);

		if(Mappers.children.get(toEntity) != null) {
			for(Entity child : Mappers.children.get(toEntity).children) {
				addShader(child);
			}
		}
			
		Mappers.timer.get(toEntity).add("poison_damage", tick, true, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				DamageHandler.dealDamage(fromEntity, toEntity, tick * dps);
			}
		});
	}

	@Override
	protected void cleanUp() {
		removeShader(toEntity);
		
		if(Mappers.children.get(toEntity) != null) {
			for(Entity child : Mappers.children.get(toEntity).children) {
				removeShader(child);
			}
		}
		
		// Deferred removal of poison_damage because you can't remove from timer comp during iteration
		EntityManager.addDelayedAction(new DelayedAction(toEntity) {
			@Override
			public void onAction() {
				Mappers.timer.get(toEntity).remove("poison_damage");
			}
		});
	}
	
	private void addShader(Entity toEntity) {
		if(Mappers.shader.get(toEntity) != null) {
			Mappers.shader.get(toEntity).shader = shader;
		}
	}
	
	private void removeShader(Entity toEntity) {
		if(Mappers.shader.get(toEntity) != null) {
			Mappers.shader.get(toEntity).shader = null;
		}
	}

	@Override
	public EffectType getType() {
		return EffectType.POISON;
	}

	@Override
	public String getName() {
		return "poison";
	}
	
	public float getDps() {
		return dps;
	}
	
	public void setDps(float dps) {
		this.dps = dps;
	}
	
	public void resetTime() {
		Mappers.timer.get(toEntity).get(getName() + "_effect").setElapsed(0.0f);;
	}
	
	public void addStack() {
		stacks++;
	}
	
	public int getStacks() {
		return stacks;
	}

}
