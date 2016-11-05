package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class SwingComponent implements Component, Poolable {

	public float startAngle = 0.0f;
	public float rotationAmount = 0.0f;
	public float duration = 0.0f;
	public float time = 0.0f;

	public SwingComponent set(float startAngle, float rotationAmount, float duration) {
		this.startAngle = startAngle;
		this.rotationAmount = rotationAmount;
		this.duration = duration;
		return this;
	}

	@Override
	public void reset() {
		startAngle = 0.0f;
		rotationAmount = 0.0f;
		duration = 0.0f;
		time = 0.0f;
	}

}
