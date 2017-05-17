package com.fullspectrum.physics.collision.behavior;

import com.badlogic.gdx.physics.box2d.Contact;
import com.fullspectrum.component.LevelSwitchComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.physics.collision.BodyInfo;
import com.fullspectrum.utils.EntityUtils;

public class LevelTriggerBehavior extends CollisionBehavior {

	@Override
	public void beginCollision(BodyInfo me, BodyInfo other, Contact contact) {
		String levelSwitch = Mappers.levelSwitch.get(me.getEntity()).data;
		EntityUtils.add(other.getEntity(), LevelSwitchComponent.class).set(levelSwitch);
	}

	@Override
	public void endCollision(BodyInfo me, BodyInfo other, Contact contact) {
		if (Mappers.levelSwitch.get(other.getEntity()) != null)
			other.getEntity().remove(LevelSwitchComponent.class);
	}

}
