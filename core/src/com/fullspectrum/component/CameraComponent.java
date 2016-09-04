package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraComponent implements Component{

	public float minX = 0.0f;
	public float minY = 0.0f;
	public float maxX = 0.0f;
	public float maxY = 0.0f;
	public float windowMinX = 0.0f;
	public float windowMinY = 0.0f;
	public float windowMaxX = 0.0f;
	public float windowMaxY = 0.0f;
	
	public OrthographicCamera camera;
	public Entity toFollow;
	
	public CameraComponent(OrthographicCamera camera, Entity toFollow){
		this.camera = camera;
		this.toFollow = toFollow;
	}
	
}
