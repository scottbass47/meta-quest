package com.fullspectrum.physics.collision;

import com.badlogic.ashley.core.Entity;
import com.fullspectrum.physics.FixtureType;
import static com.fullspectrum.utils.EntityUtils.*;

public class CollisionInstance {

	private Entity me;
	private FixtureType myFixtureType;
	private Entity other;

	public CollisionInstance(Entity me, FixtureType myFixtureType, Entity other) {
		this.me = me;
		this.myFixtureType = myFixtureType;
		this.other = other;
	}

	public Entity getMe() {
		return me;
	}

	public void setMe(Entity me) {
		this.me = me;
	}

	public FixtureType getMyFixtureType() {
		return myFixtureType;
	}

	public void setMyFixtureType(FixtureType myFixtureType) {
		this.myFixtureType = myFixtureType;
	}

	public Entity getOther() {
		return other;
	}

	public void setOther(Entity other) {
		this.other = other;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((me == null) ? 0 : getID(me));
		result = prime * result + ((myFixtureType == null) ? 0 : myFixtureType.hashCode());
		result = prime * result + ((other == null) ? 0 : getID(other));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		CollisionInstance other = (CollisionInstance) obj;
		if (me == null) {
			if (other.me != null) return false;
		}
		else if (getID(me) != getID(other.me)) return false;
		if (myFixtureType != other.myFixtureType) return false;
		if (this.other == null) {
			if (other.other != null) return false;
		}
		else if (getID(this.other) != getID(other.other)) return false;
		return true;
	}
	
	
}
