package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraComponent implements Component{

	public float x = 0.0f;
	public float y = 0.0f;
	
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
	
	public CameraComponent(OrthographicCamera camera, Entity toFollow){
		camera.zoom = 1f;
		this.camera = camera;
		this.toFollow = toFollow;
	}
	
}
