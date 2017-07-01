package com.fullspectrum.arena;

import java.util.Iterator;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.fullspectrum.entity.EntityIndex;

public class Arena {

	private ArrayMap<Integer, ArenaSpawn> spawnMap;
	private Array<ArenaWave> waves;
	
	public Arena() {
		spawnMap = new ArrayMap<Integer, ArenaSpawn>();
		waves = new Array<ArenaWave>();
	}
	
	public void load(FileHandle config) {
		JsonReader reader = new JsonReader();
		JsonValue root = reader.parse(config);

		JsonValue spawnpoints = root.get("spawnpoints");

		for(Iterator<JsonValue> iter = spawnpoints.iterator(); iter.hasNext(); ) {
			JsonValue value = iter.next();
			int id = value.getInt("id");
			float x = value.getFloat("x");
			float y = value.getFloat("y");
			ArenaSpawn spawn = new ArenaSpawn(id, new Vector2(x, y));
			spawnMap.put(id, spawn);
		}
		
		JsonValue waves = root.get("waves");
		
		for(Iterator<JsonValue> iter = waves.iterator(); iter.hasNext(); ) {
			JsonValue value = iter.next();
			
			ArenaWave wave = new ArenaWave();
			for(Iterator<JsonValue> i = value.iterator(); i.hasNext(); ) {
				JsonValue v = i.next();
				ArenaGroup group = parseGroup(v);
				wave.addGroup(Integer.parseInt(v.name), group);
			}
			this.waves.add(wave);
		}
	}
	
	private ArenaGroup parseGroup(JsonValue value) {
		ArenaGroup group = new ArenaGroup();
		for(Iterator<JsonValue> iter = value.iterator(); iter.hasNext(); ) {
			JsonValue v = iter.next();
			
			EntityIndex index = EntityIndex.get(v.name);
			int amount = v.asInt();
			group.addEnemy(index, amount);
		}
		return group;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("-------------\n");
		builder.append("-   ARENA   -\n");
		builder.append("-------------\n\n");
		builder.append("-- Spawnpoints --\n");
		
		for(ArenaSpawn spawn : spawnMap.values()) {
			builder.append(spawn + "\n");
		}
		
		builder.append("\n-- Waves --\n\n");
		
		int counter = 1;
		for(ArenaWave wave : waves) {
			builder.append("Wave: " + counter++ + "\n");
			builder.append(wave + "\n\n");
		}
		
		return builder.toString();
		
	}
	
}
