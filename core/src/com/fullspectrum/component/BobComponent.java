package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class BobComponent implements Component, Poolable {

	public float bobSpeed;
	public float bobHeight;
	public float elapsed = 0.0f;

	public BobComponent set(float bobSpeed, float bobHeight) {
		this.bobSpeed = bobSpeed;
		this.bobHeight = bobHeight;
		return this;
	}

	@Override
	public void reset() {
		bobSpeed = 0.0f;
		bobHeight = 0.0f;
		elapsed = 0.0f;
	}

}
