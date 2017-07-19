package com.fullspectrum.ability.knight;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.ability.AnimationAbility;
import com.fullspectrum.ability.OnGroundConstraint;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
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

	public OverheadSwingAbility(float cooldown, Actions input, Animation<TextureRegion> swingAnimation, SwingComponent swing) {
		super(AbilityType.OVERHEAD_SWING, AssetLoader.getInstance().getRegion(Asset.OVERHEAD_SWING_ICON), cooldown, input, swingAnimation);
		this.swing = swing;
		setAbilityConstraints(new OnGroundConstraint());
		addTemporaryImmunties(EffectType.KNOCKBACK, EffectType.STUN);
	}

	@Override
	public void init(Entity entity) {
		SwingComponent swingComp = Mappers.engine.get(entity).engine.createComponent(SwingComponent.class);
		swingComp.set(swing.rx, swing.ry, swing.startAngle, swing.endAngle, swing.delay, swing.damage, swing.knockback).setEffects(swing.effects);
		swingComp.shouldSwing = true;
		entity.add(swingComp);
		Mappers.esm.get(entity).get(EntityStates.OVERHEAD_SWING).changeState(EntityStates.OVERHEAD_SWING);
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
		Mappers.esm.get(entity).get(EntityStates.OVERHEAD_SWING).changeState(EntityStates.IDLING);
		Mappers.facing.get(entity).locked = false;
		entity.remove(SwingComponent.class);
		forceDown = false;
	}
}