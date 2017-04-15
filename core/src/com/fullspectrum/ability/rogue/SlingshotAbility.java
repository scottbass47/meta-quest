package com.fullspectrum.ability.rogue;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.ability.AnimationAbility;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TimeListener;
import com.fullspectrum.component.TypeComponent.EntityType;
import com.fullspectrum.entity.EntityAnim;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.factory.EntityFactory;
import com.fullspectrum.factory.ProjectileFactory;
import com.fullspectrum.factory.ProjectileFactory.ProjectileData;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.input.Actions;

public class SlingshotAbility extends AnimationAbility{

	private float knockback;
	private float damage;
	
	public SlingshotAbility(float cooldown, Actions input, Animation animation, float knockback, float damage) {	
		super(AbilityType.SLINGSHOT, AssetLoader.getInstance().getRegion(Asset.SLINGSHOT_ICON), cooldown, input, animation);
		this.knockback = knockback;
		this.damage = damage;
	}

	@Override
	protected void init(Entity entity) {
		Mappers.asm.get(entity).get(EntityAnim.SLINGHOT_ARMS).changeState(EntityAnim.SLINGHOT_ARMS);
		Mappers.timer.get(entity).add("slingshot_delay", GameVars.ANIM_FRAME * 2, false, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				ProjectileData data = ProjectileFactory.initProjectile(entity, 5.0f, 0.0f, 0.0f);
				EntityManager.addEntity(EntityFactory.createSlingshotProjectile(data.x, data.y, data.angle, damage, knockback, EntityType.FRIENDLY));
			}
		});
	}

	@Override
	public void onUpdate(Entity entity, float delta) {
	}

	@Override
	protected void destroy(Entity entity) {
		Mappers.asm.get(entity).get(EntityAnim.SLINGHOT_ARMS).changeState(EntityAnim.INIT);
	}
}
