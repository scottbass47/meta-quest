package com.fullspectrum.ability.knight;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.ability.AnimationAbility;
import com.fullspectrum.ability.OnGroundConstraint;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.InvincibilityComponent.InvincibilityType;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SwingComponent;
import com.fullspectrum.effects.EffectType;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.input.Actions;

public class SpinSliceAbility extends AnimationAbility{
	
	private SwingComponent swing;

	public SpinSliceAbility(float cooldown, Actions input, Animation swingAnimation, SwingComponent swing) {
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