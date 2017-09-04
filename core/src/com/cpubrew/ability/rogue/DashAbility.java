package com.cpubrew.ability.rogue;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.cpubrew.ability.AbilityType;
import com.cpubrew.ability.TimedAbility;
import com.cpubrew.assets.Asset;
import com.cpubrew.assets.AssetLoader;
import com.cpubrew.component.FacingComponent;
import com.cpubrew.component.ForceComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.InvincibilityComponent.InvincibilityType;
import com.cpubrew.effects.EffectType;
import com.cpubrew.entity.EntityStates;
import com.cpubrew.input.Actions;
import com.cpubrew.input.Input;
import com.cpubrew.utils.EntityUtils;

public class DashAbility extends TimedAbility{

	private float speed;
	
	public DashAbility(float cooldown, Actions input, float distance, float speed) {
		super(AbilityType.DASH, AssetLoader.getInstance().getRegion(Asset.DASH_ICON), cooldown, input, 0.0f, true);
		this.speed = speed;
		duration = distance / speed;
		addTemporaryImmunties(EffectType.values());
		addTemporaryInvincibilities(InvincibilityType.ALL);
		lockFacing();
	}

	@Override
	protected void init(Entity entity) {
		Body body = Mappers.body.get(entity).body;
		body.setGravityScale(0.0f);
		body.setLinearVelocity(body.getLinearVelocity().x, 0.0f);
		Mappers.esm.get(entity).get(EntityStates.DASH).changeState(EntityStates.DASH);
		
		FacingComponent facingComp = Mappers.facing.get(entity);
		Input input = Mappers.input.get(entity).input;
		boolean right = input.isPressed(Actions.MOVE_RIGHT);
		boolean left = input.isPressed(Actions.MOVE_LEFT);
		
		if(right == left){
			EntityUtils.add(entity, ForceComponent.class).set(facingComp.facingRight ? speed : -speed, 0.0f);
		} else {
			EntityUtils.add(entity, ForceComponent.class).set(right ? speed : -speed, 0.0f);
		}
	}

	@Override
	public void onUpdate(Entity entity, float delta) {
	}

	@Override
	protected void destroy(Entity entity) {
		Body body = Mappers.body.get(entity).body;
		body.setLinearVelocity(0.0f, 0.0f);
		body.setGravityScale(1.0f);
		Mappers.esm.get(entity).get(EntityStates.IDLING).changeState(EntityStates.IDLING);

	}
	
}
