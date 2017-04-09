package com.fullspectrum.handlers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.ability.Ability;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.ability.BlacksmithAbility;
import com.fullspectrum.ability.ParryAbility;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.BarrierComponent;
import com.fullspectrum.component.BlacksmithComponent;
import com.fullspectrum.component.BlinkComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.EngineComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.InvincibilityComponent.InvincibilityType;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.MoneyComponent;
import com.fullspectrum.component.RenderComponent;
import com.fullspectrum.component.ShaderComponent;
import com.fullspectrum.component.TimeListener;
import com.fullspectrum.component.TimerComponent;
import com.fullspectrum.component.WorldComponent;
import com.fullspectrum.debug.DebugVars;
import com.fullspectrum.effects.Effects;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.factory.DropFactory;
import com.fullspectrum.factory.EntityFactory;
import com.fullspectrum.shader.HurtShader;
import com.fullspectrum.shader.Shader;
import com.fullspectrum.utils.Maths;

public class DamageHandler {
	
	private final static Shader hurtShader = new HurtShader();

	private DamageHandler() {
	}

	public static void dealDamage(Entity fromEntity, Entity toEntity, float amount, float knockBackDistance, float knockBackAngle) {
		EngineComponent engineComp = Mappers.engine.get(toEntity);
		WorldComponent worldComp = Mappers.world.get(toEntity);
		LevelComponent levelComp = Mappers.level.get(toEntity);
		HealthComponent healthComp = Mappers.heatlh.get(toEntity);
		BarrierComponent barrierComp = Mappers.barrier.get(toEntity);
		BlacksmithComponent blacksmithComp = Mappers.blacksmith.get(toEntity);
		BodyComponent bodyComp = Mappers.body.get(toEntity);
		Body body = bodyComp.body;

		if (MathUtils.isEqual(healthComp.health, 0.0f)) return;

		// CLEANUP Why is knockback being handled in the damage handler?
		if (knockBackDistance > 0) {
			Effects.giveKnockBack(toEntity, knockBackDistance, knockBackAngle);
		}
		
		if(amount < 1.0f) return;
		
		if (Mappers.inviciblity.get(toEntity) != null){
			if(Mappers.inviciblity.get(toEntity).isInvincible(toEntity, fromEntity)) return;
		}
		if (Mappers.player.get(toEntity) != null) {
			// Check for parry
			Ability ability = Mappers.ability.get(toEntity).getAbility(AbilityType.PARRY);
			if(ability != null && ability.inUse()){
				ParryAbility parryAbility = (ParryAbility)ability;
				if(parryAbility.readyToBlock()){
					parryAbility.parry(fromEntity);
					return;
				}
			}
			
			// Check for blacksmith
			ability = Mappers.ability.get(toEntity).getAbility(AbilityType.BLACKSMITH);
			if(ability != null && ability.inUse()){
				BlacksmithAbility blacksmithAbility = (BlacksmithAbility)ability;
				if(MathUtils.random() <= blacksmithAbility.getConversionChance()){
					// convert into shield
					blacksmithAbility.convertIntoShield(amount);
					return;
				}
			}
			
			if(DebugVars.PLAYER_INVINCIBILITY) return;
			float duration = 1.0f;
			Mappers.inviciblity.get(toEntity).add(InvincibilityType.ALL);
			toEntity.add(engineComp.engine.createComponent(BlinkComponent.class).addBlink(duration, 0.15f));
			TimerComponent timerComp = Mappers.timer.get(toEntity);
			timerComp.add("invincibility", duration, false, new TimeListener() {
				@Override
				public void onTime(Entity entity) {
					Mappers.inviciblity.get(entity).remove(InvincibilityType.ALL);
					entity.remove(BlinkComponent.class);
					if (entity.getComponent(RenderComponent.class) == null) entity.add(new RenderComponent());
				}
			});
		} else {
			ShaderComponent shaderComp = Mappers.shader.get(toEntity);
			if(shaderComp != null && shaderComp.shader == null){
				shaderComp.shader = hurtShader;
				Mappers.timer.get(toEntity).add("red_tint", 0.2f, false, new TimeListener() {
					@Override
					public void onTime(Entity entity) {
						ShaderComponent shaderComp = Mappers.shader.get(entity);
						if(shaderComp.shader != null && shaderComp.shader.equals(hurtShader)){
							shaderComp.shader = null;
						}
					}
				});
			}
		}

		float half = 0.25f * amount * 0.5f;
		amount += MathUtils.random(-half, half);
		float dealt = MathUtils.clamp(amount, 1.0f, healthComp.health + (barrierComp != null ? barrierComp.barrier : 0) + (blacksmithComp != null ? blacksmithComp.shield : 0));

		float healthDown = 0;
		float shieldDown = 0;
		
		// CLEANUP System can be made more robust (only considering three components now)
		if(blacksmithComp != null){
			float overflow = Maths.getOverflow(dealt, blacksmithComp.shield);
			float before = blacksmithComp.shield;
			if(overflow > 0){
				shieldDown += blacksmithComp.shield;
				blacksmithComp.shield = 0.0f;
				toEntity.remove(BlacksmithComponent.class);
			} else {
				shieldDown += dealt;
				blacksmithComp.shield -= dealt;
			}
			dealt -= before - blacksmithComp.shield;
		}
		
		if (barrierComp != null && dealt > 0) {
			float overflow = Maths.getOverflow(dealt, barrierComp.barrier);
			float before = barrierComp.barrier;
			if(overflow > 0){
				shieldDown += barrierComp.barrier;
				barrierComp.barrier = 0;
			} else {
				shieldDown += dealt;
				barrierComp.barrier -= dealt;
			}
			dealt -= before - barrierComp.barrier;
			barrierComp.timeElapsed = 0.0f;
		}
		healthDown = dealt;
		healthComp.health -= dealt;

		if (healthComp.health <= 0) {
			MoneyComponent moneyComp = Mappers.money.get(toEntity);
			if(moneyComp != null && moneyComp.money > 0){
				DropFactory.spawnCoins(toEntity);
			}
		}

		float x = body.getPosition().x;
		float y = body.getPosition().y + bodyComp.getAABB().height * 0.5f + 0.5f;
		BitmapFont font = AssetLoader.getInstance().getFont(AssetLoader.font18);
		
		int displayShield = (int) shieldDown;
		int displayHealth = (int) healthDown;
		if (displayShield > 0 && displayHealth > 0) {
			EntityManager.addEntity(EntityFactory.createDamageText(engineComp.engine, worldComp.world, levelComp.level, "-" + displayShield, Color.BLUE, font, x - 0.5f, y, 2.0f));
			EntityManager.addEntity(EntityFactory.createDamageText(engineComp.engine, worldComp.world, levelComp.level, "-" + displayHealth, Color.RED, font, x + 0.5f, y, 2.0f));
		}
		else if (displayShield > 0) {
			EntityManager.addEntity(EntityFactory.createDamageText(engineComp.engine, worldComp.world, levelComp.level, "-" + displayShield, Color.BLUE, font, x, y, 2.0f));
		}
		else {
			EntityManager.addEntity(EntityFactory.createDamageText(engineComp.engine, worldComp.world, levelComp.level, "-" + displayHealth, Color.RED, font, x, y, 2.0f));
		}
	}

	public static void dealDamage(Entity fromEntity, Entity toEntity, float amount) {
		DamageHandler.dealDamage(fromEntity, toEntity, amount, 0, 0);
	}

}
