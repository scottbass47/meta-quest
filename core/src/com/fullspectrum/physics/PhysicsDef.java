package com.fullspectrum.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsDef {
	
	private Entity entity;
	private World world;
	private Vector2 position;
	private FileHandle file;
	private BodyProperties properties;
	
	public PhysicsDef(FileHandle file, World world, Vector2 position, Entity entity, BodyProperties properties){
		this.file = file;
		this.world = world;
		this.position = position;
		this.entity = entity;
		this.properties = properties;
	}

	public Entity getEntity() {
		return entity;
	}

	public World getWorld() {
		return world;
	}
	
	public Vector2 getPosition(){
		return position;
	}

	public FileHandle getFile() {
		return file;
	}

	public BodyProperties getProperties() {
		return properties;
	}
}
