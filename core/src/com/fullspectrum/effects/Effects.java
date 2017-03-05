package com.fullspectrum.effects;

import java.util.Iterator;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.component.EffectComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TimerComponent;
import com.fullspectrum.entity.EntityManager;

public class Effects {

	public static void giveKnockBack(Entity toEntity, float distance, float angle){
		EffectComponent effectComp = Mappers.effect.get(toEntity);
		Effect effect = new KnockBackEffect(toEntity, distance, angle);
		effect.apply();
		effectComp.add(effect);
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
		effect.apply();
		effectComp.add(effect);
	}
	
	// CLEANUP Values are hardcoded (see call hierarchy)
	public static void giveEase(Entity toEntity, float duration, float accel){
		EffectComponent effectComp = Mappers.effect.get(toEntity);
		Effect effect = new EaseEffect(toEntity, duration, accel);
		effect.apply();
		effectComp.add(effect);
	}
	
	public static void clearAll(Entity entity){
		if(Mappers.effect.get(entity) == null) return;
		TimerComponent timerComp = Mappers.timer.get(entity);
		for(Iterator<String> iter = timerComp.timers.keys().iterator(); iter.hasNext();){
			String name = iter.next();
			if(name.contains("_effect")) iter.remove();
		}
		for(Iterator<Effect> iter = Mappers.effect.get(entity).effects.iterator(); iter.hasNext();){
			Effect effect = iter.next();
			effect.cleanUp();
			iter.remove();
		}
	}
	
}
