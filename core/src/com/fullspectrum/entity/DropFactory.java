package com.fullspectrum.entity;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.MoneyComponent;

public class DropFactory {

	private final static int MIN_COIN_DROPS = 3;
	
	public static void spawnCoin(Engine engine, World world, float x, float y, float fx, float fy, int amount){
		engine.addEntity(EntityFactory.createCoin(engine, world, x, y, fx, fy, amount));
	}
	
	public static void spawnCoins(Entity entity){
		MoneyComponent moneyComp = Mappers.money.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		Body body = bodyComp.body;
		
		if(bodyComp == null || bodyComp.body == null || moneyComp == null) return;
		
		int amount = moneyComp.money;
		int min = CoinType.getLowestCoinValue();
		int max = CoinType.getHighestCoinValue();
		
		int[] coins = new int[MIN_COIN_DROPS];
		for(int i = 0; i < MIN_COIN_DROPS; i++){
			if(i == MIN_COIN_DROPS - 1){
				coins[i] = amount;
				break;
			}
			int value = MathUtils.random(min, Math.min(max, amount - min * (MIN_COIN_DROPS - i)));
			coins[i] = value;
			amount -= value;
		}
		
		float range = 15.0f;
		float interval = range / coins.length;
		for(int i = 0; i < coins.length; i++){
			float fx = MathUtils.random(i * interval, (i + 1) * interval) - range * 0.5f;
			spawnCoin(Mappers.engine.get(entity).engine, Mappers.world.get(entity).world, body.getPosition().x, body.getPosition().y, fx, MathUtils.random(2.5f, 7.5f), coins[i]);
		}
	}
	
}
