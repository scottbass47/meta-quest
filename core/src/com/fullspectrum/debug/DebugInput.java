package com.fullspectrum.debug;

import com.badlogic.gdx.utils.ArrayMap;

public class DebugInput {

	// Debug Options
	private static ArrayMap<DebugToggle, Boolean> toggles;
	private static ArrayMap<DebugCycle, Integer> cycles;
	
	static {
		toggles = new ArrayMap<DebugToggle, Boolean>();
		cycles = new ArrayMap<DebugCycle, Integer>();
		
		setupToggles();
		setupCycles();
	}
	
	public static boolean isToggled(DebugToggle toggle){
		return toggles.get(toggle);
	}
	
	public static int getCycle(DebugCycle cycle){
		return cycles.get(cycle);
	}
	
	private static void setupToggles(){
		for(DebugToggle toggle : DebugToggle.values()){
			toggles.put(toggle, false);
		}
	}
	
	private static void setupCycles(){
		for(DebugCycle cycle : DebugCycle.values()){
			cycles.put(cycle, 0);
		}
	}

	public static void keyTyped(char character) {
		DebugToggle toggle = DebugToggle.getToggle(character);
		if(toggle != null){
			toggles.put(toggle, !toggles.get(toggle));
			return;
		}
		DebugCycle cycle = DebugCycle.getCycle(character);
		if(cycle != null){
			if(cycles.get(cycle) >= cycle.getNumCycles() - 1) cycles.put(cycle, 0);
			else cycles.put(cycle, cycles.get(cycle) + 1);
			return;
		}
		
	}
}
