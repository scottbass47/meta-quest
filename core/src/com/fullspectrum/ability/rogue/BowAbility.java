package com.fullspectrum.ability.rogue;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.ability.AnimationAbility;
import com.fullspectrum.ability.OnGroundConstraint;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TimeListener;
import com.fullspectrum.effects.EffectType;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.factory.EntityFactory;
import com.fullspectrum.factory.ProjectileFactory;
import com.fullspectrum.factory.ProjectileFactory.ProjectileData;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.input.Actions;

public class BowAbility extends AnimationAbility{

	private float damage;
	private float speed;
	private int shootFrame = 1;
	
	public BowAbility(float cooldown, Actions input, Animation animation, float damage, float speed){
		super(AbilityType.BOW, AssetLoader.getInstance().getRegion(Asset.BOW_ICON), cooldown, input, animation);
		this.damage = damage;
		this.speed = speed;
		addTemporaryImmunties(EffectType.values());
		setAbilityConstraints(new OnGroundConstraint());
	}

	@Override
	protected void init(Entity entity) {
		Mappers.esm.get(entity).get(EntityStates.BOW_ATTACK).changeState(EntityStates.BOW_ATTACK);
		Mappers.timer.get(entity).add("bow_shoot", shootFrame * GameVars.ANIM_FRAME, false, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				ProjectileData data = ProjectileFactory.initProjectile(entity, 8, -4, 0.0f);
				EntityManager.addEntity(EntityFactory.createArrow(data.x, data.y, speed, data.angle, damage, Mappers.status.get(entity).status));
			}
		});
	}
	
	@Override
	public void onUpdate(Entity entity, float delta) {
	}

	@Override
	protected void destroy(Entity entity) {
		Mappers.esm.get(entity).get(EntityStates.IDLING).changeState(EntityStates.IDLING);
	}
}
