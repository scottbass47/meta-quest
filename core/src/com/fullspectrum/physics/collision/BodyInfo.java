package com.fullspectrum.physics.collision;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.fullspectrum.component.TypeComponent.EntityType;
import com.fullspectrum.physics.FixtureType;

public class BodyInfo {

	private Entity entity;
	private Body body;
	private Fixture fixture;
	private FixtureType type;
	private CollisionBodyType bodyType;
	private EntityType entityType;
	private CollisionData data;
	
	public BodyInfo(Entity entity, Body body, Fixture fixture, FixtureType type, CollisionBodyType bodyType, EntityType entityType, CollisionData data) {
		this.entity = entity;
		this.body = body;
		this.fixture = fixture;
		this.type = type;
		this.bodyType = bodyType;
		this.entityType = entityType;
		this.data = data;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public Fixture getFixture() {
		return fixture;
	}

	public void setFixture(Fixture fixture) {
		this.fixture = fixture;
	}

	public FixtureType getType() {
		return type;
	}

	public void setType(FixtureType type) {
		this.type = type;
	}

	public CollisionBodyType getBodyType() {
		return bodyType;
	}

	public void setBodyType(CollisionBodyType bodyType) {
		this.bodyType = bodyType;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}
	
	public CollisionData getData() {
		return data;
	}
	
	public void setData(CollisionData data) {
		this.data = data;
	}
}
