package com.cpubrew.ability.monk;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.cpubrew.ability.AbilityType;
import com.cpubrew.ability.TimedAbility;
import com.cpubrew.assets.Asset;
import com.cpubrew.assets.AssetLoader;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.MonkComponent;
import com.cpubrew.effects.PoisonDef;
import com.cpubrew.entity.EntityManager;
import com.cpubrew.factory.EntityFactory;
import com.cpubrew.input.Actions;

public class PoisonDebuffAbility extends TimedAbility {

	private float dps;
	private float decayRate;
	private float poisonDuration;
	private Entity poison;
	private float yOff = 0.75f;
	
	public PoisonDebuffAbility(float cooldown, Actions input, float duration, float dps, float decayRate, float poisonDuration) {
		super(AbilityType.POISON_DEBUFF, AssetLoader.getInstance().getRegion(Asset.POISON_DEBUFF_ICON), cooldown, input, duration, false);
		this.dps = dps;
		this.decayRate = decayRate;
		this.poisonDuration = poisonDuration;
	}

	@Override
	protected void init(Entity entity) {
		MonkComponent monkComp = Mappers.monk.get(entity);
		
		PoisonDef poisonDef = new PoisonDef(entity, poisonDuration, dps, decayRate);
		monkComp.activeEffect = poisonDef;
		
		Body body = Mappers.body.get(entity).body;
		
		poison = EntityFactory.createPoisonParticles(body.getPosition().x, body.getPosition().y + yOff);
		EntityManager.addEntity(poison);
	}

	@Override
	public void onUpdate(Entity entity, float delta) {
		Body body = Mappers.body.get(entity).body;
		Mappers.position.get(poison).set(body.getPosition().x, body.getPosition().y + yOff);
	}


	@Override
	protected void destroy(Entity entity) {
		EntityManager.cleanUp(poison);
		
		Mappers.monk.get(entity).activeEffect = null;
	}
	
}
