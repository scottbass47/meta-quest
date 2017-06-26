package com.fullspectrum.ability.monk;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.ability.TimedAbility;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.MonkComponent;
import com.fullspectrum.effects.PoisonDef;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.factory.EntityFactory;
import com.fullspectrum.input.Actions;

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
