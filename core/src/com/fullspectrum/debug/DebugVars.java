package com.fullspectrum.debug;

import com.fullspectrum.entity.EntityIndex;

public class DebugVars {

	// Window
	public static boolean FULLSCREEN_MODE = true;
	
	// Show
	public static boolean NAVMESH_ON = false;
	public static boolean PATHS_ON = false;
	public static boolean RANGES_ON = false;
	public static boolean FLOW_FIELD_ON = false;
	public static boolean HITBOXES_ON = false;
	public static boolean HEALTH_ON = false;
	public static boolean MAP_COORDS_ON = false;
	public static boolean SWING_ON = false;
	
	// Spawn
	public static boolean SPAWN_ON_CLICK_ENABLED = false;
	public static EntityIndex SPAWN_TYPE = null;
	public static int SPAWN_AMOUNT = 1;
	
	// Disable
	public static boolean AI_DISABLED = false;
	public static boolean SPAWNERS_DISABLED = false;
	
	// Invincible
	public static boolean PLAYER_INVINCIBILITY = false;
	
	// Slow
	public static int SLOW = 1;
	
	// FPS
	public static boolean FPS_ON = false;

	public static boolean COMMANDS_ON = false;
	public static boolean SOUND_ON = false;

	public static void resetAll() {
		NAVMESH_ON = false;
		PATHS_ON = false;
		RANGES_ON = false;
		FLOW_FIELD_ON = false;
		HITBOXES_ON = false;
		HEALTH_ON = false;
		MAP_COORDS_ON = false;
		SWING_ON = false;
		
		SPAWN_ON_CLICK_ENABLED = false;
		SPAWN_TYPE = null;
		SPAWN_AMOUNT = 1;
		
		AI_DISABLED = false;
		SPAWNERS_DISABLED = false;
        
		PLAYER_INVINCIBILITY = false;
		
		SLOW = 1;
		
		FPS_ON = false;
		COMMANDS_ON = false;
	}

	private DebugVars() {
	}
}
