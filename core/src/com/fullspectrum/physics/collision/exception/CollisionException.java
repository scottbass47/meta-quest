package com.fullspectrum.physics.collision.exception;

import com.fullspectrum.entity.EntityType;
import com.fullspectrum.physics.FixtureType;

public class CollisionException {

	private EntityType entityType;
	private FixtureType fixtureType;
	private EntityType collisionEntityType;

	public CollisionException(EntityType entityType, FixtureType fixtureType, EntityType collisionEntityType) {
		this.entityType = entityType;
		this.fixtureType = fixtureType;
		this.collisionEntityType = collisionEntityType;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public FixtureType getFixtureType() {
		return fixtureType;
	}

	public void setFixtureType(FixtureType fixtureType) {
		this.fixtureType = fixtureType;
	}

	public EntityType getCollisionEntityType() {
		return collisionEntityType;
	}

	public void setCollisionEntityType(EntityType collisionEntityType) {
		this.collisionEntityType = collisionEntityType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collisionEntityType == null) ? 0 : collisionEntityType.hashCode());
		result = prime * result + ((entityType == null) ? 0 : entityType.hashCode());
		result = prime * result + ((fixtureType == null) ? 0 : fixtureType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		CollisionException other = (CollisionException) obj;
		if (collisionEntityType != other.collisionEntityType) return false;
		if (entityType != other.entityType) return false;
		if (fixtureType != other.fixtureType) return false;
		return true;
	}
	
}
