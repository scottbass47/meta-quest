package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Pool.Poolable;

public class CameraComponent implements Component, Poolable {

	public float x = 0.0f;
	public float y = 0.0f;
	public float zoom = 0.0f;
	
	public float minX = 0.0f;
	public float minY = 0.0f;
	public float maxX = 0.0f;
	public float maxY = 0.0f;

	public float windowMinX = 0.0f;
	public float windowMinY = 0.0f;
	public float windowMaxX = 0.0f;
	public float windowMaxY = 0.0f;

	public float subpixelX = 0.0f;
	public float subpixelY = 0.0f;
	public float upscaleOffsetX = 0.0f;
	public float upscaleOffsetY = 0.0f;

	public OrthographicCamera camera;
	public Entity toFollow;

	@Override
	public void reset() {
		x = 0.0f;
		y = 0.0f;
		zoom = 0.0f;

		minX = 0.0f;
		minY = 0.0f;
		maxX = 0.0f;
		maxY = 0.0f;

		windowMinX = 0.0f;
		windowMinY = 0.0f;
		windowMaxX = 0.0f;
		windowMaxY = 0.0f;

		subpixelX = 0.0f;
		subpixelY = 0.0f;
		upscaleOffsetX = 0.0f;
		upscaleOffsetY = 0.0f;

		camera = null;
		toFollow = null;
	}

}
