package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.fullspectrum.assets.Assets;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.entity.EntityAnim;
import com.fullspectrum.factory.ProjectileFactory;
import com.fullspectrum.input.Actions;

public class SlingshotAbility extends AnimationAbility{

	private float knockback;
	private float damage;
	
	public SlingshotAbility(float cooldown, Actions input, Animation animation, float knockback, float damage) {	
		super(AbilityType.SLINGSHOT, Assets.getInstance().getHUDElement(Assets.SLINGSHOT_ICON), cooldown, input, animation);
		this.knockback = knockback;
		this.damage = damage;
	}

	@Override
	protected void init(Entity entity) {
		Mappers.asm.get(entity).get(EntityAnim.SLINGHOT_ARMS).changeState(EntityAnim.SLINGHOT_ARMS);
		ProjectileFactory.spawnExplosiveProjectile(entity, 5.0f, 0.0f, 10.0f, damage, 0.0f, 4.0f, 1.0f);
	}

	@Override
	public void onUpdate(Entity entity, float delta) {
		
	}

	@Override
	protected void destroy(Entity entity) {
		Mappers.asm.get(entity).get(EntityAnim.SLINGHOT_ARMS).changeState(EntityAnim.INIT);
	}
}
