package com.fullspectrum.arena;

import com.badlogic.gdx.utils.ArrayMap;

public class ArenaWave {

	private ArrayMap<Integer, ArenaGroup> groupMap;
	
	public ArenaWave() {
		groupMap = new ArrayMap<Integer, ArenaGroup>();
	}
	
	public void addGroup(Integer spawnID, ArenaGroup group) {
		groupMap.put(spawnID, group);
	}
	
	public ArenaGroup getGroup(Integer spawnID) {
		return groupMap.get(spawnID);
	}
	
	public int numGroups() {
		return groupMap.size;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for(Integer spawnID : groupMap.keys()) {
			builder.append("Spawnpoint: " + spawnID);
			builder.append("\n----------------\n");
			builder.append(groupMap.get(spawnID) + "\n\n");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}
	
}
