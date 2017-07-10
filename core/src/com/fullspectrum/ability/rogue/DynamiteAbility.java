package com.fullspectrum.ability.rogue;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.ability.AnimationAbility;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TimeListener;
import com.fullspectrum.entity.EntityAnim;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.entity.EntityStatus;
import com.fullspectrum.factory.EntityFactory;
import com.fullspectrum.factory.ProjectileFactory;
import com.fullspectrum.factory.ProjectileFactory.ProjectileData;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.input.Actions;

public class DynamiteAbility extends AnimationAbility{

	private float knockback;
	private float damage;
	private float speed = 8.0f;
	private float explosionRadius;
	
	public DynamiteAbility(float cooldown, Actions input, Animation<TextureRegion> animation, float knockback, float damage, float explosionRadius) {	
		super(AbilityType.DYNAMITE, AssetLoader.getInstance().getRegion(Asset.DYNAMITE), cooldown, input, animation);
		this.knockback = knockback;
		this.damage = damage;
		this.explosionRadius = explosionRadius;
	}

	@Override
	protected void init(Entity entity) {
		Mappers.asm.get(entity).get(EntityAnim.DYNAMITE_ARMS).changeState(EntityAnim.DYNAMITE_ARMS);
		Mappers.timer.get(entity).add("dynamite_delay", GameVars.ANIM_FRAME * 2, false, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				ProjectileData data = ProjectileFactory.initProjectile(entity, 5.0f, 0.0f, 15f);
				EntityManager.addEntity(EntityFactory.createDynamiteProjectile(data.x, data.y, data.angle, speed, damage, knockback, explosionRadius, EntityStatus.FRIENDLY));
			}
		});
	}

	@Override
	public void onUpdate(Entity entity, float delta) {
	}

	@Override
	protected void destroy(Entity entity) {
		Mappers.asm.get(entity).get(EntityAnim.DYNAMITE_ARMS).changeState(EntityAnim.INIT);
	}
}
