package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.fullspectrum.assets.Assets;
import com.fullspectrum.component.InvincibilityComponent.InvincibilityType;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SwingComponent;
import com.fullspectrum.effects.EffectType;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.input.Actions;

public class OverheadSwingAbility extends AnimationAbility{
	
	private SwingComponent swing;

	public OverheadSwingAbility(float cooldown, Actions input, Animation swingAnimation, SwingComponent swing) {
		super(AbilityType.OVERHEAD_SWING, Assets.getInstance().getHUDElement(Assets.OVERHEAD_SWING_ICON), cooldown, input, swingAnimation);
		this.swing = swing;
	}

	@Override
	public void init(Entity entity) {
		SwingComponent swingComp = Mappers.engine.get(entity).engine.createComponent(SwingComponent.class);
		swingComp.set(swing.rx, swing.ry, swing.startAngle, swing.endAngle, swing.delay, swing.knockBackDistance);
		entity.add(swingComp);
		System.out.println(swingComp.delay);
		Mappers.immune.get(entity).add(EffectType.KNOCKBACK).add(EffectType.STUN);
		Mappers.esm.get(entity).get(EntityStates.OVERHEAD_SWING).changeState(EntityStates.OVERHEAD_SWING);
		Mappers.sword.get(entity).shouldSwing = true;
		Mappers.inviciblity.get(entity).add(InvincibilityType.ALL);
		Mappers.facing.get(entity).locked = true;
	}

	@Override
	public void onUpdate(Entity entity, float delta) {
		
	}


	@Override
	public void destroy(Entity entity) {
		Mappers.immune.get(entity).remove(EffectType.KNOCKBACK).remove(EffectType.STUN);
		Mappers.esm.get(entity).get(EntityStates.OVERHEAD_SWING).changeState(EntityStates.IDLING);
		Mappers.inviciblity.get(entity).remove(InvincibilityType.ALL);
		Mappers.facing.get(entity).locked = false;
	}
}