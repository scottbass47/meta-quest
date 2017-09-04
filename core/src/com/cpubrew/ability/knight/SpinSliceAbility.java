package com.cpubrew.ability.knight;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cpubrew.ability.AbilityType;
import com.cpubrew.ability.AnimationAbility;
import com.cpubrew.ability.OnGroundConstraint;
import com.cpubrew.assets.Asset;
import com.cpubrew.assets.AssetLoader;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.SwingComponent;
import com.cpubrew.component.InvincibilityComponent.InvincibilityType;
import com.cpubrew.effects.EffectType;
import com.cpubrew.entity.EntityStates;
import com.cpubrew.input.Actions;

public class SpinSliceAbility extends AnimationAbility{
	
	private SwingComponent swing;

	public SpinSliceAbility(float cooldown, Actions input, Animation<TextureRegion> swingAnimation, SwingComponent swing) {
		super(AbilityType.SPIN_SLICE, AssetLoader.getInstance().getRegion(Asset.SPIN_SLICE_ICON), cooldown, input, swingAnimation);
		this.swing = swing;
		setAbilityConstraints(new OnGroundConstraint());
		addTemporaryImmunties(EffectType.KNOCKBACK, EffectType.STUN);
		addTemporaryInvincibilities(InvincibilityType.ALL);
	}

	@Override
	public void init(Entity entity) {
		SwingComponent swingComp = Mappers.engine.get(entity).engine.createComponent(SwingComponent.class);
		swingComp.set(swing.rx, swing.ry, swing.startAngle, swing.endAngle, swing.delay, swing.damage, swing.knockback).setEffects(swing.effects);
		swingComp.shouldSwing = true;
		entity.add(swingComp);
		Mappers.esm.get(entity).get(EntityStates.SPIN_SLICE).changeState(EntityStates.SPIN_SLICE);
		Mappers.facing.get(entity).locked = true;
	}

	@Override
	public void onUpdate(Entity entity, float delta) {
	}


	@Override
	public void destroy(Entity entity) {
		Mappers.esm.get(entity).get(EntityStates.SPIN_SLICE).changeState(EntityStates.IDLING);
		Mappers.facing.get(entity).locked = false;
		entity.remove(SwingComponent.class);
	}
}