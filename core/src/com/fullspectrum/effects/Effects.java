package com.fullspectrum.effects;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.entity.EntityManager;

public class Effects {

	public static void giveKnockBack(Entity toEntity, float distance, float angle){
		new KnockBackEffect(toEntity, distance, angle).apply();
	}
	
	public static void giveStun(Entity toEntity, float duration){
		EntityManager.addEffect(new StunEffect(toEntity, duration));
	}
	
	// CLEANUP Values are hardcoded (see call hierarchy)
	public static void giveEase(Entity toEntity, float duration, float accel){
		new EaseEffect(toEntity, duration, accel).apply();
	}
	
}
