package com.fullspectrum.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.Mappers;

public enum Sensors {

	FEET {
		@Override
		public void beginCollision(Fixture me, Fixture other) {
			CollisionComponent collisionComp = Mappers.collision.get((Entity)me.getBody().getUserData());
			collisionComp.bottomContacts++;			
		}

		@Override
		public void endCollision(Fixture me, Fixture other) {
			CollisionComponent collisionComp = Mappers.collision.get((Entity)me.getBody().getUserData());
			collisionComp.bottomContacts--;			
		}
	};
	
	public abstract void beginCollision(Fixture me, Fixture other);
	public abstract void endCollision(Fixture me, Fixture other);
	
	public static Sensors get(String name){
		for(Sensors sensor : Sensors.values()){
			if(sensor.name().equalsIgnoreCase(name)) return sensor;
		}
		return null;
	}
	
}
