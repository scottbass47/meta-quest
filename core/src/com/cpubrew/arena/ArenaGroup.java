package com.cpubrew.arena;

import com.badlogic.gdx.utils.ArrayMap;
import com.cpubrew.entity.EntityIndex;

public class ArenaGroup {

	private ArrayMap<EntityIndex, Integer> spawnAmount;
	
	public ArenaGroup() {
		spawnAmount = new ArrayMap<EntityIndex, Integer>();
	}
	
	public void addEnemy(EntityIndex index, int amount) {
		spawnAmount.put(index, amount);
	}
	
	public int getAmount(EntityIndex index) {
		return spawnAmount.get(index);
	}
	
	public boolean hasEnemy(EntityIndex index) {
		return spawnAmount.containsKey(index);
	}
	
	public ArrayMap<EntityIndex, Integer> getSpawnAmount() {
		return spawnAmount;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for(EntityIndex index : spawnAmount.keys()) {
			builder.append(index + " x " + spawnAmount.get(index) + "\n");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}
		
}
