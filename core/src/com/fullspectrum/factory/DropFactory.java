package com.fullspectrum.factory;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.MoneyComponent;
import com.fullspectrum.entity.CoinType;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.level.Level;

public class DropFactory {

	private final static int MIN_COIN_DROPS = 3;
	
	public static void spawnCoin(Engine engine, World world, Level level, float x, float y, float fx, float fy, int amount){
		EntityManager.addEntity(EntityFactory.createCoin(engine, world, level, x, y, fx, fy, amount));
	}
	
	public static void spawnCoins(Entity entity){
		MoneyComponent moneyComp = Mappers.money.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		Body body = bodyComp.body;
		
		if(bodyComp == null || bodyComp.body == null || moneyComp == null || moneyComp.money <= 0) return;
		
		int amount = moneyComp.money;
		int min = CoinType.getLowestCoinValue();
		int max = CoinType.getHighestCoinValue();
		
		int numCoins = MIN_COIN_DROPS;
		Array<Integer> coins = new Array<Integer>();
		for(int i = 0; i < numCoins; i++){
			float averageCoinValue = (float)amount / (float)(numCoins - i);
			if(averageCoinValue > max) {
				numCoins++;
				i--;
				continue;
			}
			if(i == numCoins - 1){
				coins.add(amount);
				break;
			}
			int value = MathUtils.random(min, Math.min(max, amount - min * (numCoins - i)));
			coins.add(value);
			amount -= value;
		}
		float range = 15.0f;
		float interval = range / coins.size;
		for(int i = 0; i < coins.size; i++){
			float fx = MathUtils.random(i * interval, (i + 1) * interval) - range * 0.5f;
			spawnCoin(Mappers.engine.get(entity).engine, Mappers.world.get(entity).world, Mappers.level.get(entity).level, body.getPosition().x, body.getPosition().y, fx, MathUtils.random(2.5f, 7.5f), coins.get(i));
		}
	}
	
}
