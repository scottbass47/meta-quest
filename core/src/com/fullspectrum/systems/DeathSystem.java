package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.DeathComponent;
import com.fullspectrum.component.DeathComponent.DeathBehavior;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.RemoveComponent;
import com.fullspectrum.effects.Effects;

public class DeathSystem extends IteratingSystem {

	public DeathSystem() {
		super(Family.all(DeathComponent.class).get());
	}

	protected void processEntity(Entity entity, float deltaTime) {
		HealthComponent healthComp = Mappers.heatlh.get(entity);
		DeathComponent deathComp = Mappers.death.get(entity);

		if (healthComp != null && healthComp.health <= 0.0f) {
			deathComp.triggerDeath();
		}

		if (deathComp.shouldDie()) {
			Effects.clearAll(entity);
			if (deathComp.deathBehaviors.size == 0) {
				entity.add(getEngine().createComponent(RemoveComponent.class));
			}
			else {
				for (DeathBehavior behavior : deathComp.deathBehaviors) {
					behavior.onDeath(entity);
				}
			}
			deathComp.makeDead();
		}
	}
}