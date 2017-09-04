package com.cpubrew.ability.rogue;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cpubrew.ability.AbilityType;
import com.cpubrew.ability.AnimationAbility;
import com.cpubrew.ability.OnGroundConstraint;
import com.cpubrew.assets.Asset;
import com.cpubrew.assets.AssetLoader;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.TimeListener;
import com.cpubrew.effects.EffectType;
import com.cpubrew.entity.EntityManager;
import com.cpubrew.entity.EntityStates;
import com.cpubrew.factory.EntityFactory;
import com.cpubrew.factory.ProjectileFactory;
import com.cpubrew.factory.ProjectileFactory.ProjectileData;
import com.cpubrew.game.GameVars;
import com.cpubrew.input.Actions;

public class BowAbility extends AnimationAbility{

	private float damage;
	private float speed;
	private int shootFrame = 4;
	
	public BowAbility(float cooldown, Actions input, Animation<TextureRegion> animation, float damage, float speed){
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
				ProjectileData data = ProjectileFactory.initProjectile(entity, 8, 4, 0.0f);
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
