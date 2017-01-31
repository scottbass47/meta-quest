package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class SwingComponent implements Component, Poolable {

	public float rx;
	public float ry;
	public float startAngle = 0.0f;
	public float endAngle = 0.0f;

	public SwingComponent set(float rx, float ry, float startAngle, float endAngle) {
		this.rx = rx;
		this.ry = ry;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		return this;
	}

	@Override
	public void reset() {
		rx = 0.0f;
		ry = 0.0f;
		startAngle = 0.0f;
		endAngle = 0.0f;
	}

}
