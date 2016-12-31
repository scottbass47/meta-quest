package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class BarrierComponent implements Component, Poolable {

	public float maxBarrier = 0.0f;
	public float barrier = 0.0f;
	public float rechargeRate = 0.0f;
	public float delay = 0.0f;
	public float timeElapsed = 0.0f;
	public boolean locked = false;

	public BarrierComponent set(float maxBarrier, float barrier, float rechargeRate, float delay) {
		this.maxBarrier = maxBarrier;
		this.barrier = barrier;
		this.rechargeRate = rechargeRate;
		this.delay = delay;
		return this;
	}

	@Override
	public void reset() {
		maxBarrier = 0.0f;
		barrier = 0.0f;
		rechargeRate = 0.0f;
		delay = 0.0f;
		timeElapsed = 0.0f;
		locked = false;
	}

}
