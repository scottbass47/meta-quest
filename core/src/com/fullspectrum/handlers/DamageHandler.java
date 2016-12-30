package com.fullspectrum.handlers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.assets.Assets;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.DropSpawnComponent;
import com.fullspectrum.component.EngineComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.KnockBackComponent;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TimerComponent;
import com.fullspectrum.component.TimerComponent.Timer;
import com.fullspectrum.component.WorldComponent;
import com.fullspectrum.entity.DropType;
import com.fullspectrum.entity.EntityFactory;
import com.fullspectrum.utils.PhysicsUtils;

public class DamageHandler {

	private DamageHandler(){}
	
	public static void dealDamage(Entity toEntity, float amount, float knockBackDistance, float knockBackSpeed, float knockBackAngle){
		EngineComponent engineComp = Mappers.engine.get(toEntity);
		WorldComponent worldComp = Mappers.world.get(toEntity);
		LevelComponent levelComp = Mappers.level.get(toEntity);
		HealthComponent healthComp = Mappers.heatlh.get(toEntity);
		BodyComponent bodyComp = Mappers.body.get(toEntity);
		Body body = bodyComp.body;
		
		float half = 0.25f * amount * 0.5f;
		amount += MathUtils.random(-half, half);
		amount = MathUtils.clamp(amount, 1.0f, healthComp.health);
		
		healthComp.health -= (int)amount;
		
		if(healthComp.health <= 0){
			toEntity.add(engineComp.engine.createComponent(DropSpawnComponent.class).set(DropType.COIN));
		}
		
		if(knockBackDistance > 0 && knockBackSpeed > 0){
			if(toEntity.getComponent(KnockBackComponent.class) != null){
				TimerComponent timerComp = Mappers.timer.get(toEntity);
				Timer timer = timerComp.get("knockBack_life");
				timer.resetElapsed();
			}else{
				toEntity.add(engineComp.engine.createComponent(KnockBackComponent.class).set(knockBackDistance, knockBackSpeed, knockBackAngle));
			}
		}
		
		float x = body.getPosition().x;
		float y = body.getPosition().y + PhysicsUtils.getAABB(body).height * 0.5f + 0.5f;
		BitmapFont font = Assets.getInstance().getFont(Assets.font28);
		engineComp.engine.addEntity(EntityFactory.createDamageText(engineComp.engine, worldComp.world, levelComp.level, "-" + ((int)amount), Color.RED, font, x, y, 2.0f));
	}
	
	public static void dealDamage(Entity toEntity, float amount){
		DamageHandler.dealDamage(toEntity, amount, 0, 0, 0);
	}
	
}
