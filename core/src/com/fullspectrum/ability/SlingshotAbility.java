package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.fullspectrum.assets.Assets;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TypeComponent.EntityType;
import com.fullspectrum.entity.EntityAnim;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.factory.EntityFactory;
import com.fullspectrum.factory.ProjectileFactory;
import com.fullspectrum.factory.ProjectileFactory.ProjectileData;
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
		ProjectileData data = ProjectileFactory.initProjectile(entity, 5.0f, 0.0f, 0.0f);
		EntityManager.addEntity(EntityFactory.createSlingshotProjectile(data.engine, data.world, data.level, data.x, data.y, data.angle, damage, knockback, EntityType.FRIENDLY));
	}

	@Override
	public void onUpdate(Entity entity, float delta) {
	}

	@Override
	protected void destroy(Entity entity) {
		Mappers.asm.get(entity).get(EntityAnim.SLINGHOT_ARMS).changeState(EntityAnim.INIT);
	}
}
