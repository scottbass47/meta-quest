package com.fullspectrum.entity;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class EntityLoader {
	
	private static ArrayMap<EntityIndex, EntityStats> statsMap;
	
	static{
		statsMap = new ArrayMap<EntityIndex, EntityStats>();
		for(EntityIndex index : EntityIndex.values()){
			statsMap.put(index, load(index));
		}
	}
	
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
	
	public static EntityStats get(EntityIndex index){
		return statsMap.get(index);
	}
	
}
