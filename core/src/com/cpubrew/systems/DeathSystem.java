package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.cpubrew.component.DeathComponent;
import com.cpubrew.component.HealthComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.RemoveComponent;
import com.cpubrew.component.DeathComponent.DeathBehavior;
import com.cpubrew.effects.Effects;

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