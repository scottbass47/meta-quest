package com.cpubrew.ability.rogue;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.cpubrew.ability.Ability;
import com.cpubrew.ability.AbilityType;
import com.cpubrew.ability.OnGroundConstraint;
import com.cpubrew.assets.Asset;
import com.cpubrew.assets.AssetLoader;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.InvincibilityComponent.InvincibilityType;
import com.cpubrew.effects.EffectType;
import com.cpubrew.entity.EntityAnim;
import com.cpubrew.entity.EntityManager;
import com.cpubrew.factory.EntityFactory;
import com.cpubrew.fsm.AnimationStateMachine;
import com.cpubrew.game.GameVars;
import com.cpubrew.input.Actions;
import com.cpubrew.shader.VanishShader;
import com.cpubrew.utils.EntityUtils;
import com.cpubrew.utils.PhysicsUtils;

public class VanishAbility extends Ability {

	private final VanishShader vanishShader = new VanishShader();
	private int smokeBombFrame = 1;
	private boolean hasThrown = false;
	private float elapsed;
	private float duration;
	
	public VanishAbility(float cooldown, Actions input, float duration){
		super(AbilityType.VANISH, AssetLoader.getInstance().getRegion(Asset.VANISH_ICON), cooldown, input, true);
		this.duration = duration;
		setAbilityConstraints(new OnGroundConstraint());
		addTemporaryImmunties(EffectType.values());
		addTemporaryInvincibilities(InvincibilityType.ALL);
	}

	@Override
	protected void init(Entity entity) {
		Mappers.asm.get(entity).get(EntityAnim.SMOKE_BOMB_ARMS).changeState(EntityAnim.SMOKE_BOMB_ARMS);
		EntityUtils.setTargetable(entity, false);
	}

	@Override
	protected void update(Entity entity, float delta) {
		elapsed += delta;
		
		int frame = (int)(elapsed / GameVars.ANIM_FRAME);
		if(frame == smokeBombFrame && !hasThrown){
			hasThrown = true;
			Vector2 pos = PhysicsUtils.getPos(entity);
			EntityManager.addEntity(EntityFactory.createSmoke(pos.x, pos.y - 0.3f, AssetLoader.getInstance().getAnimation(Asset.SMOKE_BOMB), false));
			Mappers.shader.get(entity).shader = vanishShader;
			Mappers.ability.get(entity).unlockAllBlocking();
		}
		
		if(hasThrown){
			AnimationStateMachine upperBodyASM = Mappers.asm.get(entity).get(EntityAnim.IDLE_ARMS);
			if(upperBodyASM == null){
				setDone(true);
				return;
			}
			EntityAnim currentAnim = (EntityAnim) upperBodyASM.getCurrentState();
			if(currentAnim != EntityAnim.SMOKE_BOMB_ARMS && EntityFactory.isActiveRogueState(currentAnim)){
				setDone(true);
			}
		}
		
		if(elapsed >= duration) setDone(true);
	}

	@Override
	protected void destroy(Entity entity) {
		Mappers.shader.get(entity).shader = null;
		EntityUtils.setTargetable(entity, true);
		elapsed = 0.0f;
		hasThrown = false;
	}
	
	@Override
	public boolean unblockOnDestroy() {
		return false;
	}
	
}
