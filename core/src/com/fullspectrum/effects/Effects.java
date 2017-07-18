package com.fullspectrum.effects;

import java.util.Iterator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.component.EffectComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TimerComponent;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.utils.PhysicsUtils;

public class Effects {

	public static void giveKnockBack(Entity toEntity, float distance, float angle){
		EffectComponent effectComp = Mappers.effect.get(toEntity);
		Effect effect = new KnockBackEffect(toEntity, distance, angle);
		if(effect.apply()){
			effectComp.add(effect);
		}
	}
	
	/**
	 * Applies knockback and automatically adjusts the angle based on the relative positioning between the two entities.
	 * @param fromEntity
	 * @param toEntity
	 * @param distance
	 * @param angle
	 */
	public static void giveKnockBackWithFlip(Entity fromEntity, Entity toEntity, float distance, float angle){
		Vector2 fromPos = PhysicsUtils.getPos(fromEntity);
		Vector2 toPos = PhysicsUtils.getPos(toEntity);
		giveKnockBack(toEntity, distance, fromPos.x < toPos.x ? angle : 180 - angle);
	}
	
	public static void giveStun(Entity toEntity, float duration){
		EntityManager.addEffect(new StunEffect(toEntity, duration));
	}
	
	/**
	 * WARNING - DON'T USE WHEN ENGINE IS UPDATING
	 * @param toEntity
	 * @param duration
	 */
	public static void giveImmediateStun(Entity toEntity, float duration){
		EffectComponent effectComp = Mappers.effect.get(toEntity);
		Effect effect = new StunEffect(toEntity, duration, true);
		if(effect.apply()){
			effectComp.add(effect);
		}
	}
	
	// CLEANUP Values are hardcoded (see call hierarchy)
	public static void giveEase(Entity toEntity, float duration, float accel){
		EffectComponent effectComp = Mappers.effect.get(toEntity);
		Effect effect = new EaseEffect(toEntity, duration, accel);
		if(effect.apply()){
			effectComp.add(effect);
		}
	}
	
	public static void givePoison(Entity fromEntity, Entity toEntity, float duration, float dps, float decayRate) {
		EffectComponent effectComp = Mappers.effect.get(toEntity);
		Effect effect = new PoisonEffect(fromEntity, toEntity, duration, dps);

		if(effect.canApply()) {
			if(effectComp.hasEffect(EffectType.POISON)) {
				PoisonEffect poison = (PoisonEffect) effectComp.getEffect(EffectType.POISON);
				poison.addStack();
				poison.setDps(poison.getDps() + dps * (float)Math.pow(decayRate, poison.getStacks() - 1));
				poison.resetTime();
			} else {
				if(effect.apply()) {
					effectComp.add(effect);
				}
			}
		}
	}
	
	public static void clearAll(Entity entity){
		if(Mappers.effect.get(entity) == null) return;
		TimerComponent timerComp = Mappers.timer.get(entity);
		for(Iterator<String> iter = timerComp.timers.keys().iterator(); iter.hasNext();){
			String name = iter.next();
			if(name.contains("_effect")) iter.remove();
		}
		Array<Effect> effects = Mappers.effect.get(entity).effects;
		for(Iterator<Effect> iter = effects.iterator(); iter.hasNext();){
			Effect effect = iter.next();
			effect.cleanUp();
			iter.remove();
		}
	}
	
}
