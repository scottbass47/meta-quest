package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class StaminaComponent implements Component, Poolable {

	public float maxStamina = 0.0f;
	public float stamina = 0.0f;
	public float rechargeRate = 0.0f;
	public float delay = 0.0f;
	public float timeElapsed = 0.0f;
	public boolean locked = false;

	public StaminaComponent set(float maxStamina, float stamina, float rechargeRate, float delay) {
		this.maxStamina = maxStamina;
		this.stamina = stamina;
		this.rechargeRate = rechargeRate;
		this.delay = delay;
		return this;
	}

	@Override
	public void reset() {
		maxStamina = 0.0f;
		stamina = 0.0f;
		rechargeRate = 0.0f;
		delay = 0.0f;
		timeElapsed = 0.0f;
		locked = false;
	}

}
