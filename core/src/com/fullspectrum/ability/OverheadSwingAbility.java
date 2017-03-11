package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.fullspectrum.assets.Assets;
import com.fullspectrum.component.ForceComponent;
import com.fullspectrum.component.InvincibilityComponent.InvincibilityType;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SwingComponent;
import com.fullspectrum.component.TimeListener;
import com.fullspectrum.effects.EffectType;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.input.Actions;

public class OverheadSwingAbility extends AnimationAbility{
	
	private SwingComponent swing;
	private boolean forceDown = false;

	public OverheadSwingAbility(float cooldown, Actions input, Animation swingAnimation, SwingComponent swing) {
		super(AbilityType.OVERHEAD_SWING, Assets.getInstance().getHUDElement(Assets.OVERHEAD_SWING_ICON), cooldown, input, swingAnimation, true);
		setAbilityConstraints(new AbilityConstraints() {
			@Override
			public boolean canUse(Ability ability, Entity entity) {
				return Mappers.collision.get(entity).onGround();
			}
		});
		this.swing = swing;
	}

	@Override
	public void init(Entity entity) {
		SwingComponent swingComp = Mappers.engine.get(entity).engine.createComponent(SwingComponent.class);
		swingComp.set(swing.rx, swing.ry, swing.startAngle, swing.endAngle, swing.delay, swing.knockBackDistance);
		entity.add(swingComp);
		Mappers.immune.get(entity).add(EffectType.KNOCKBACK).add(EffectType.STUN);
		Mappers.esm.get(entity).get(EntityStates.OVERHEAD_SWING).changeState(EntityStates.OVERHEAD_SWING);
		Mappers.sword.get(entity).shouldSwing = true;
		Mappers.inviciblity.get(entity).add(InvincibilityType.ALL);
		Mappers.facing.get(entity).locked = true;
	}

	@Override
	public void onUpdate(Entity entity, float delta) {
		// If after 9 frames you're not on the ground, then start falling
		// HACK All values are hardcoded in
		if(elapsed >= 9 * GameVars.ANIM_FRAME && !forceDown){
			if(!Mappers.collision.get(entity).onGround()){
				elapsed = duration;
				Mappers.timer.get(entity).add("down_force_delay", 2 * GameVars.UPS_INV, false, new TimeListener() {
					@Override
					public void onTime(Entity entity) {
						entity.add(Mappers.engine.get(entity).engine.createComponent(ForceComponent.class).set(0.0f, -15.0f));
					}
				});
			}
			forceDown = true;
		}
	}


	@Override
	public void destroy(Entity entity) {
		Mappers.immune.get(entity).remove(EffectType.KNOCKBACK).remove(EffectType.STUN);
		Mappers.esm.get(entity).get(EntityStates.OVERHEAD_SWING).changeState(EntityStates.IDLING);
		Mappers.inviciblity.get(entity).remove(InvincibilityType.ALL);
		Mappers.facing.get(entity).locked = false;
		forceDown = false;
	}
}