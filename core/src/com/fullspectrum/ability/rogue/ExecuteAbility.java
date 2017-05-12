package com.fullspectrum.ability.rogue;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.ability.AnimationAbility;
import com.fullspectrum.ability.OnGroundConstraint;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TimeListener;
import com.fullspectrum.debug.DebugRender;
import com.fullspectrum.effects.EffectType;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.handlers.DamageHandler;
import com.fullspectrum.input.Actions;
import com.fullspectrum.utils.PhysicsUtils;

public class ExecuteAbility extends AnimationAbility{

	private int executeFrame = 4;
	
	public ExecuteAbility(float cooldown, Actions input, Animation animation) {
		super(AbilityType.EXECUTE, AssetLoader.getInstance().getRegion(Asset.EXECUTE_ICON), cooldown, input, animation);
		addTemporaryImmunties(EffectType.values());
		setAbilityConstraints(new OnGroundConstraint());
	}

	@Override
	protected void init(Entity entity) {
		Mappers.esm.get(entity).get(EntityStates.EXECUTE).changeState(EntityStates.EXECUTE);
		
		Mappers.timer.get(entity).add("execute_delay", executeFrame * GameVars.ANIM_FRAME, false, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				execute(entity);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void execute(Entity entity){
		boolean facingRight = Mappers.facing.get(entity).facingRight;
		Vector2 pos = PhysicsUtils.getPos(entity);
		
		float xRange = 1.0f;
		float yRange = 0.5f;
		Entity closest = null;
		float distance = Float.MAX_VALUE;
		
		Rectangle rect = new Rectangle(
				facingRight ? pos.x : pos.x - xRange,
				pos.y - yRange * 0.5f,
				xRange, 
				yRange);
		
		DebugRender.setColor(Color.RED);
		DebugRender.setType(ShapeType.Line);
		DebugRender.rect(rect.x, rect.y, rect.width, rect.height, 1.0f);
		
		for(Entity target : Mappers.engine.get(entity).engine.getEntitiesFor(Family.all(HealthComponent.class, BodyComponent.class).get())){
			if(!Mappers.status.get(target).status.equals(Mappers.status.get(entity).status.getOpposite())) continue;
			Vector2 targetPos = PhysicsUtils.getPos(target);
			Rectangle aabb = Mappers.body.get(target).getAABB();
			Rectangle hitBox = new Rectangle(aabb).setCenter(targetPos);
			if(rect.overlaps(hitBox)){
				float x1 = targetPos.x + aabb.width * 0.5f;
				float x2 = targetPos.x - aabb.width * 0.5f;
				float d = 0.0f;
				if(Math.abs(pos.x - x1) < Math.abs(pos.x - x2)){
					d = Math.abs(pos.x - x1);
				} else {
					d = Math.abs(pos.x - x2);
				}
				if(d < distance) {
					distance = d;
					closest = target;
				}
			}
		}
		
		// INCOMPLETE Doesn't handle bosses
		if(closest != null){
			DamageHandler.dealDamage(entity, closest, Float.MAX_VALUE);
		}
	}
	
	@Override
	public void onUpdate(Entity entity, float delta) {
	}

	@Override
	protected void destroy(Entity entity) {
		Mappers.esm.get(entity).get(EntityStates.IDLING).changeState(EntityStates.IDLING);
	}
}
