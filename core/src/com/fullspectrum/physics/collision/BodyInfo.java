package com.fullspectrum.physics.collision;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.fullspectrum.entity.EntityStatus;
import com.fullspectrum.entity.EntityType;
import com.fullspectrum.physics.FixtureType;

public class BodyInfo {

	private Entity entity;
	private Body body;
	private Fixture fixture;
	private FixtureType fixtureType;
	private CollisionBodyType bodyType;
	private EntityStatus entityStatus;
	private EntityType entityType;
	private CollisionData data;
	
	public BodyInfo(Entity entity, Body body, Fixture fixture, FixtureType fixtureType, CollisionBodyType bodyType, EntityStatus entityStatus, EntityType entityType, CollisionData data) {
		this.entity = entity;
		this.body = body;
		this.fixture = fixture;
		this.fixtureType = fixtureType;
		this.bodyType = bodyType;
		this.entityStatus = entityStatus;
		this.setEntityType(entityType);
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

	public FixtureType getFixtureType() {
		return fixtureType;
	}

	public void setFixtureType(FixtureType fixtureType) {
		this.fixtureType = fixtureType;
	}

	public CollisionBodyType getBodyType() {
		return bodyType;
	}

	public void setBodyType(CollisionBodyType bodyType) {
		this.bodyType = bodyType;
	}

	public EntityStatus getEntityStatus() {
		return entityStatus;
	}

	public void setEntityStatus(EntityStatus entityStatus) {
		this.entityStatus = entityStatus;
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
