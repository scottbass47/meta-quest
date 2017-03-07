package com.fullspectrum.handlers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.assets.Assets;
import com.fullspectrum.component.BarrierComponent;
import com.fullspectrum.component.BlinkComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.EngineComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.InvincibilityComponent.InvincibilityType;
import com.fullspectrum.debug.DebugVars;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.MoneyComponent;
import com.fullspectrum.component.RenderComponent;
import com.fullspectrum.component.TimeListener;
import com.fullspectrum.component.TimerComponent;
import com.fullspectrum.component.TintComponent;
import com.fullspectrum.component.WorldComponent;
import com.fullspectrum.effects.Effects;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.factory.DropFactory;
import com.fullspectrum.factory.EntityFactory;

public class DamageHandler {
	
	private final static Color hitColor = new Color(210f / 255f, 20f / 255f, 60f / 255f, 1.0f);

	private DamageHandler() {
	}

	public static void dealDamage(Entity fromEntity, Entity toEntity, float amount, float knockBackDistance, float knockBackAngle) {
		EngineComponent engineComp = Mappers.engine.get(toEntity);
		WorldComponent worldComp = Mappers.world.get(toEntity);
		LevelComponent levelComp = Mappers.level.get(toEntity);
		HealthComponent healthComp = Mappers.heatlh.get(toEntity);
		BarrierComponent barrierComp = Mappers.barrier.get(toEntity);
		BodyComponent bodyComp = Mappers.body.get(toEntity);
		Body body = bodyComp.body;

		if (amount < 1.0f || (MathUtils.isEqual(healthComp.health, 0.0f))) return;

		if (Mappers.inviciblity.get(toEntity) != null){
			if(Mappers.inviciblity.get(toEntity).isInvincible(toEntity, fromEntity)) return;
		}
		if (Mappers.player.get(toEntity) != null) {
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
			toEntity.add(engineComp.engine.createComponent(TintComponent.class).set(hitColor));
			Mappers.timer.get(toEntity).add("red_tint", 0.15f, false, new TimeListener() {
				@Override
				public void onTime(Entity entity) {
					entity.remove(TintComponent.class);
				}
			});
		}

		float half = 0.25f * amount * 0.5f;
		amount += MathUtils.random(-half, half);
		int dealt = (int) MathUtils.clamp(amount, 1.0f, healthComp.health + (barrierComp != null ? barrierComp.barrier : 0));

		int healthDown = 0;
		int shieldDown = 0;
		
		if (barrierComp != null) {
			barrierComp.barrier = (int)(barrierComp.barrier - dealt);
			shieldDown = dealt;
			if (barrierComp.barrier < 0) {
				shieldDown = dealt - (int) Math.abs(barrierComp.barrier);
				dealt = (int) Math.abs(barrierComp.barrier);
				barrierComp.barrier = 0.0f;
			}
			else {
				dealt = 0;
			}
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

		if (knockBackDistance > 0 && Mappers.player.get(toEntity) == null) {
			Effects.giveKnockBack(toEntity, knockBackDistance, knockBackAngle);
			Effects.giveStun(toEntity, 5.0f);
		}

		float x = body.getPosition().x;
		float y = body.getPosition().y + bodyComp.getAABB().height * 0.5f + 0.5f;
		BitmapFont font = Assets.getInstance().getFont(Assets.font18);
		if (shieldDown > 0 && healthDown > 0) {
			EntityManager.addEntity(EntityFactory.createDamageText(engineComp.engine, worldComp.world, levelComp.level, "-" + shieldDown, Color.BLUE, font, x - 0.5f, y, 2.0f));
			EntityManager.addEntity(EntityFactory.createDamageText(engineComp.engine, worldComp.world, levelComp.level, "-" + healthDown, Color.RED, font, x + 0.5f, y, 2.0f));
		}
		else if (shieldDown > 0) {
			EntityManager.addEntity(EntityFactory.createDamageText(engineComp.engine, worldComp.world, levelComp.level, "-" + shieldDown, Color.BLUE, font, x, y, 2.0f));
		}
		else {
			EntityManager.addEntity(EntityFactory.createDamageText(engineComp.engine, worldComp.world, levelComp.level, "-" + healthDown, Color.RED, font, x, y, 2.0f));
		}
	}

	public static void dealDamage(Entity fromEntity, Entity toEntity, float amount) {
		DamageHandler.dealDamage(fromEntity, toEntity, amount, 0, 0);
	}

}
