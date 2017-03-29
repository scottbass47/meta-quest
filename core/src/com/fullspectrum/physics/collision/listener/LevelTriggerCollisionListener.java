package com.fullspectrum.physics.collision.listener;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fullspectrum.component.LevelSwitchComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.physics.collision.CollisionInfo;
import com.fullspectrum.physics.collision.CollisionListener;

public class LevelTriggerCollisionListener implements CollisionListener{

	@Override
	public void beginCollision(CollisionInfo info) {
		Entity entity = info.getMe();
		Entity player = info.getOther();
		if(Mappers.player.get(player) == null) return;
		
		String levelSwitch = Mappers.levelSwitch.get(entity).data;
		player.add(Mappers.engine.get(player).engine.createComponent(LevelSwitchComponent.class).set(levelSwitch));
	}

	@Override
	public void endCollision(CollisionInfo info) {
		Entity player = info.getOther();
		if(Mappers.player.get(player) == null) return;
		if(Mappers.levelSwitch.get(player) != null) player.remove(LevelSwitchComponent.class);
	}

	@Override
	public void preSolveCollision(CollisionInfo info, Contact contact, Manifold manifold) {
		
	}

	@Override
	public void postSolveCollision(CollisionInfo info, Contact contact, ContactImpulse impulse) {
		
	}

}
