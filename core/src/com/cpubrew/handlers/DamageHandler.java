package com.cpubrew.handlers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.ArrayMap;
import com.cpubrew.ability.Ability;
import com.cpubrew.ability.AbilityType;
import com.cpubrew.ability.knight.BlacksmithAbility;
import com.cpubrew.ability.knight.ParryAbility;
import com.cpubrew.assets.AssetLoader;
import com.cpubrew.audio.AudioLocator;
import com.cpubrew.audio.Sounds;
import com.cpubrew.component.BarrierComponent;
import com.cpubrew.component.BlacksmithComponent;
import com.cpubrew.component.BlinkComponent;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.EngineComponent;
import com.cpubrew.component.HealthComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.MoneyComponent;
import com.cpubrew.component.RenderComponent;
import com.cpubrew.component.ShaderComponent;
import com.cpubrew.component.TextRenderComponent;
import com.cpubrew.component.TimeListener;
import com.cpubrew.component.TimerComponent;
import com.cpubrew.component.InvincibilityComponent.InvincibilityType;
import com.cpubrew.debug.DebugVars;
import com.cpubrew.effects.Effects;
import com.cpubrew.entity.EntityManager;
import com.cpubrew.entity.EntityType;
import com.cpubrew.factory.DropFactory;
import com.cpubrew.factory.EntityFactory;
import com.cpubrew.shader.HurtShader;
import com.cpubrew.shader.Shader;
import com.cpubrew.utils.EntityUtils;
import com.cpubrew.utils.Maths;
import com.cpubrew.utils.PhysicsUtils;

public class DamageHandler {
	
	private final static Shader hurtShader = new HurtShader();
	private static ArrayMap<Entity, Entity> damageMap = new ArrayMap<Entity, Entity>();
	private static Color damageColor = new Color(0xff2245ff);
	private static Color shieldColor = new Color(0x00b9ffff);
	
	private DamageHandler() {
	}

	public static void dealDamage(Entity fromEntity, Entity toEntity, float amount, float knockBackDistance, float knockBackAngle) { 
		EngineComponent engineComp = Mappers.engine.get(toEntity);
		HealthComponent healthComp = Mappers.heatlh.get(toEntity);
		BarrierComponent barrierComp = Mappers.barrier.get(toEntity);
		BlacksmithComponent blacksmithComp = Mappers.blacksmith.get(toEntity);
		BodyComponent bodyComp = Mappers.body.get(toEntity);
		
		if(healthComp == null || bodyComp == null) return;
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
			Mappers.immune.get(toEntity).addCompleteImmunity();
			toEntity.add(engineComp.engine.createComponent(BlinkComponent.class).addBlink(duration, 0.15f));
			TimerComponent timerComp = Mappers.timer.get(toEntity);
			timerComp.add("invincibility", duration, false, new TimeListener() {
				@Override
				public void onTime(Entity entity) {
					Mappers.inviciblity.get(entity).remove(InvincibilityType.ALL);
					Mappers.immune.get(entity).removeCompleteImmunity();
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

		float percent = 0.05f * amount;
		amount += MathUtils.random(-percent, percent);
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

		// HACK Damage dealing should fire an event that entities can listen for and decide what to do
		if(Mappers.entity.get(toEntity).type == EntityType.GRUNT_GREMLIN) {
			AudioLocator.getAudio().playSound(Sounds.DAMAGE, PhysicsUtils.getPos(toEntity));
		}
		
		float x = body.getPosition().x;
		float y = body.getPosition().y + bodyComp.getAABB().height * 0.5f + 0.5f;
		BitmapFont font = AssetLoader.getInstance().getFont(AssetLoader.font18);
		
		int displayShield = (int) shieldDown;
		int displayHealth = (int) healthDown;
		if (displayShield > 0 && displayHealth > 0) {
			EntityManager.addEntity(EntityFactory.createDamageText("-" + displayShield, shieldColor, font, x - 0.5f, y, 2.0f));
			EntityManager.addEntity(EntityFactory.createDamageText("-" + displayHealth, damageColor, font, x + 0.5f, y, 2.0f));
		}
		else if (displayShield > 0) {
			EntityManager.addEntity(EntityFactory.createDamageText("-" + displayShield, shieldColor, font, x, y, 2.0f));
		}
		else {
			// If there is damage text for this entity and it's a valid entity, then update it
			if(damageMap.containsKey(toEntity) && EntityUtils.isValid(damageMap.get(toEntity))) {
				Entity damageText = damageMap.get(toEntity);
				TextRenderComponent textComp = Mappers.textRender.get(damageText);
				int damage = Integer.parseInt(textComp.text.substring(1));
				damage += displayHealth;
				textComp.text = "-" + damage;
			}
			else {
				Entity damageText = EntityFactory.createDamageText("-" + displayHealth, damageColor, font, x, y, 2.0f);
				damageMap.put(toEntity, damageText);
				EntityUtils.setValid(damageText, true);
				EntityManager.addEntity(damageText);
			}
		}
	}

	public static void dealDamage(Entity fromEntity, Entity toEntity, float amount) {
		DamageHandler.dealDamage(fromEntity, toEntity, amount, 0, 0);
	}

}
