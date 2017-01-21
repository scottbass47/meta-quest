package com.fullspectrum.entity;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class EntityLoader {
	
	// Player
	public static final EntityStats playerStats = load(EntityIndex.PLAYER);
	public static final EntityStats knightStats = load(EntityIndex.KNIGHT);
	public static final EntityStats rogueStats = load(EntityIndex.ROGUE);
	public static final EntityStats mageStats = load(EntityIndex.MAGE);

	// Enemies
	public static final EntityStats slimeStats = load(EntityIndex.SLIME);
	public static final EntityStats aiPlayerStats = load(EntityIndex.AI_PLAYER);
	public static final EntityStats spitterStats = load(EntityIndex.SPITTER);
	
	public static EntityStats load(EntityIndex index){
		EntityStats ret = new EntityStats(index);
		String file = Gdx.files.internal("config/" + index.getName() + ".json").readString();
		JsonReader reader = new JsonReader();
		JsonValue root = reader.parse(file);
		for(Iterator<JsonValue> iter = root.iterator(); iter.hasNext();){
			JsonValue child = iter.next();
			String attribute = child.name();
			float value = child.asFloat();
			ret.set(attribute, value);
		}
		return ret;
	}
	
}
