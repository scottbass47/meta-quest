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
import com.fullspectrum.component.InvincibilityComponent;
import com.fullspectrum.component.KnockBackComponent;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.MoneyComponent;
import com.fullspectrum.component.RenderComponent;
import com.fullspectrum.component.TimeListener;
import com.fullspectrum.component.TimerComponent;
import com.fullspectrum.component.TimerComponent.Timer;
import com.fullspectrum.component.WorldComponent;
import com.fullspectrum.entity.DropFactory;
import com.fullspectrum.entity.EntityFactory;

public class DamageHandler {

	private DamageHandler() {
	}

	public static void dealDamage(Entity toEntity, float amount, float knockBackDistance, float knockBackSpeed, float knockBackAngle) {
		EngineComponent engineComp = Mappers.engine.get(toEntity);
		WorldComponent worldComp = Mappers.world.get(toEntity);
		LevelComponent levelComp = Mappers.level.get(toEntity);
		HealthComponent healthComp = Mappers.heatlh.get(toEntity);
		BarrierComponent barrierComp = Mappers.barrier.get(toEntity);
		BodyComponent bodyComp = Mappers.body.get(toEntity);
		Body body = bodyComp.body;

		if (amount < 1.0f || (MathUtils.isEqual(healthComp.health, 0.0f))) return;

		if (Mappers.inviciblity.get(toEntity) != null) return;
		if (Mappers.player.get(toEntity) != null) {
			float duration = 1.0f;
			toEntity.add(engineComp.engine.createComponent(InvincibilityComponent.class));
			toEntity.add(engineComp.engine.createComponent(BlinkComponent.class).addBlink(duration, 0.15f));
			TimerComponent timerComp = Mappers.timer.get(toEntity);
			timerComp.add("invincibility", duration, false, new TimeListener() {
				@Override
				public void onTime(Entity entity) {
					entity.remove(InvincibilityComponent.class);
					entity.remove(BlinkComponent.class);
					if (entity.getComponent(RenderComponent.class) == null) entity.add(new RenderComponent());
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

		if (knockBackDistance > 0 && knockBackSpeed > 0) {
			// CLEANUP Entities should be checked for whether or not they accept knockback
			if (Mappers.spawnerPool.get(toEntity) == null) {
				if (toEntity.getComponent(KnockBackComponent.class) != null) {
					TimerComponent timerComp = Mappers.timer.get(toEntity);
					Timer timer = timerComp.get("knockBack_life");
					timer.resetElapsed();
				}
				else {
					toEntity.add(engineComp.engine.createComponent(KnockBackComponent.class).set(knockBackDistance, knockBackSpeed, knockBackAngle));
				}
			}
		}

		float x = body.getPosition().x;
		float y = body.getPosition().y + bodyComp.getAABB().height * 0.5f + 0.5f;
		BitmapFont font = Assets.getInstance().getFont(Assets.font18);
		if (shieldDown > 0 && healthDown > 0) {
			engineComp.engine.addEntity(EntityFactory.createDamageText(engineComp.engine, worldComp.world, levelComp.level, "-" + shieldDown, Color.BLUE, font, x - 0.5f, y, 2.0f));
			engineComp.engine.addEntity(EntityFactory.createDamageText(engineComp.engine, worldComp.world, levelComp.level, "-" + healthDown, Color.RED, font, x + 0.5f, y, 2.0f));
		}
		else if (shieldDown > 0) {
			engineComp.engine.addEntity(EntityFactory.createDamageText(engineComp.engine, worldComp.world, levelComp.level, "-" + shieldDown, Color.BLUE, font, x, y, 2.0f));
		}
		else {
			engineComp.engine.addEntity(EntityFactory.createDamageText(engineComp.engine, worldComp.world, levelComp.level, "-" + healthDown, Color.RED, font, x, y, 2.0f));
		}
	}

	public static void dealDamage(Entity toEntity, float amount) {
		DamageHandler.dealDamage(toEntity, amount, 0, 0, 0);
	}

}
