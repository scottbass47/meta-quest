package com.cpubrew.component;

import static com.cpubrew.game.GameVars.R_WORLD_HEIGHT;
import static com.cpubrew.game.GameVars.R_WORLD_WIDTH;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool.Poolable;

public class CameraComponent implements Component, Poolable {

	public float x = 0.0f;
	public float y = 0.0f;
	public float zoom = 0.0f;
	public boolean locked = false;
	
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
	
	public void update(){
		camera.zoom = 1.0f / zoom;
		camera.position.x = MathUtils.clamp(x, minX + R_WORLD_WIDTH * 0.5f * camera.zoom, maxX - R_WORLD_WIDTH * 0.5f * camera.zoom);
		camera.position.y = MathUtils.clamp(y, minY + R_WORLD_HEIGHT * 0.5f * camera.zoom, maxY - R_WORLD_HEIGHT * 0.5f * camera.zoom);
		camera.update();
	}

	@Override
	public void reset() {
		x = 0.0f;
		y = 0.0f;
		zoom = 0.0f;
		locked = false;

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
