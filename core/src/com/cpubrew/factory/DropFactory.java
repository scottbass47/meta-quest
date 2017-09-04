package com.cpubrew.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.MoneyComponent;
import com.cpubrew.entity.CoinType;
import com.cpubrew.entity.EntityManager;

public class DropFactory {

	private final static int MIN_COIN_DROPS = 3;
	private static float variance = 0.25f;
	
	public static void spawnCoin(float x, float y, float fx, float fy, int amount){
		EntityManager.addEntity(EntityFactory.createCoin(x, y, fx, fy, amount));
	}
	
	public static void spawnCoins(Entity entity){
		MoneyComponent moneyComp = Mappers.money.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		
		if(bodyComp == null || bodyComp.body == null || moneyComp == null || moneyComp.money <= 0) return;
		Body body = bodyComp.body;
		
		int amount = (int)(moneyComp.money + MathUtils.random(2 * variance * moneyComp.money) - variance * moneyComp.money);
		int min = CoinType.getLowestCoinValue();
		int max = CoinType.getHighestCoinValue();
		
		int numCoins = MIN_COIN_DROPS;
		Array<Integer> coins = new Array<Integer>();
		for(int i = 0; i < numCoins; i++){
			if(amount < 1) break;
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
			int value = 0;
			try {
				value = MathUtils.random(min, Math.min(max, amount - min * (numCoins - i)));
			} catch (Exception e) {
				continue;
			}
			coins.add(value);
			amount -= value;
		}
		float range = 15.0f;
		float interval = range / coins.size;
		for(int i = 0; i < coins.size; i++){
			float fx = MathUtils.random(i * interval, (i + 1) * interval) - range * 0.5f;
			spawnCoin(body.getPosition().x, body.getPosition().y, fx, MathUtils.random(2.5f, 7.5f), coins.get(i));
		}
	}
	
}
