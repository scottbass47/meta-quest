package com.fullspectrum.debug;

import com.badlogic.gdx.utils.ArrayMap;

public class DebugInput {

	// Debug Options
	private static ArrayMap<DebugToggle, Boolean> toggles;
	private static ArrayMap<DebugCycle, Integer> cycles;
	private static ArrayMap<DebugKeys, Boolean> currentInput;
	private static ArrayMap<DebugKeys, Boolean> previousInput;
	
	static {
		toggles = new ArrayMap<DebugToggle, Boolean>();
		cycles = new ArrayMap<DebugCycle, Integer>();
		currentInput = new ArrayMap<DebugKeys, Boolean>();
		previousInput = new ArrayMap<DebugKeys, Boolean>();
		
		setupToggles();
		setupCycles();
		setupKeys();
	}
	
	public static boolean isPressed(DebugKeys key){
		return currentInput.get(key);
	}
	
	public static boolean isJustPressed(DebugKeys key){
		return currentInput.get(key) && !previousInput.get(key);
	}
	
	public static void update(){
		for(DebugKeys key : DebugKeys.values()){
			previousInput.put(key, currentInput.get(key));
		}
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
	
	private static void setupKeys(){
		for(DebugKeys key : DebugKeys.values()){
			currentInput.put(key, false);
			previousInput.put(key, false);
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
	
	public static void keyDown(int keycode){
		DebugKeys key = DebugKeys.getTrigger(keycode);
		if(key != null){
			currentInput.put(key, true);
		}
	}
	
	public static void keyUp(int keycode){
		DebugKeys key = DebugKeys.getTrigger(keycode);
		if(key != null){
			currentInput.put(key, false);
		}
	}
}
