package com.cpubrew.physics.collision.behavior;

import com.badlogic.gdx.physics.box2d.Contact;
import com.cpubrew.component.LevelSwitchComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.physics.collision.BodyInfo;
import com.cpubrew.utils.EntityUtils;

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
