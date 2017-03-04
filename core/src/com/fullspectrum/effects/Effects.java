package com.fullspectrum.effects;

import com.badlogic.ashley.core.Entity;

public class Effects {

	public static void giveKnockBack(Entity toEntity, float distance, float angle){
		new KnockBackEffect(toEntity, distance, angle).apply();
	}
	
	public static void giveEase(Entity toEntity, float duration, float accel){
		new EaseEffect(toEntity, duration, accel).apply();
	}
	
}
