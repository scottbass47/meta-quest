package com.fullspectrum.entity;

import com.badlogic.gdx.utils.ArrayMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fullspectrum.utils.StringUtils;

public class EntityStats {

	private EntityIndex index;
	private ArrayMap<String, Float> statsMap;
	
	protected EntityStats(EntityIndex index, ArrayMap<String, Float> statsMap){
		if(index == null) throw new IllegalArgumentException("Entity index can't be null.");
		this.index = index;
		this.statsMap = statsMap;
	}
	
	protected EntityStats(EntityIndex index){
		this(index, new ArrayMap<String, Float>());
	}
	
	public void set(String attribute, float value){
		statsMap.put(attribute, value);
	}
	
	public float get(String attribute){
		return statsMap.get(attribute);
	}
	
	public EntityIndex getEntityIndex(){
		return index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((index == null) ? 0 : index.getName().hashCode());
		result = prime * result + ((statsMap == null) ? 0 : statsMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		EntityStats other = (EntityStats) obj;
		if (!index.getName().equals(other.index.getName())) return false;
		if (statsMap == null) {
			if (other.statsMap != null) return false;
		} else if (!statsMap.equals(other.statsMap)) return false;
		return true;
	}
	
	@Override
	public String toString() {
		String name = StringUtils.toTitleCase(index.getName());
		StringBuilder builder = new StringBuilder(name + "\n");
		for(String key : statsMap.keys()){
			builder.append(key + ": " + statsMap.get(key) + "\n");
		}
		return super.toString();
	}
	
	public static EntityStatsSerializer getSerializer(){
		return new EntityStatsSerializer();
	}
	
	public static class EntityStatsSerializer extends Serializer<EntityStats>{
		@Override
		public void write(Kryo kryo, Output output, EntityStats object) {
			output.writeShort(object.index.shortIndex());
			output.writeShort((short)object.statsMap.size);
			for(String key : object.statsMap.keys()){
				output.writeString(key);
				output.writeFloat(object.statsMap.get(key));
			}
		}

		@Override
		public EntityStats read(Kryo kryo, Input input, Class<EntityStats> type) {
			short entityIndex = input.readShort();
			short mapSize = input.readShort();
			ArrayMap<String, Float> statsMap = new ArrayMap<String, Float>();
			for(int i = 0; i < mapSize; i++){
				String attribute = input.readString();
				float value = input.readFloat();
				statsMap.put(attribute, value);
			}
			return new EntityStats(EntityIndex.values()[entityIndex], statsMap);
		}
	}
}