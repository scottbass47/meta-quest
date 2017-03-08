package com.fullspectrum.debug;

public class DebugVars {

	// Show
	public static boolean NAVMESH_ON = false;
	public static boolean PATHS_ON = false;
	public static boolean RANGES_ON = false;
	public static boolean FLOW_FIELD_ON = false;
	public static boolean HITBOXES_ON = false;
	public static boolean HEALTH_ON = false;
	
	// Disable
	public static boolean AI_DISABLED = false;
	public static boolean SPAWNERS_DISABLED = false;
	
	// Invincible
	public static boolean PLAYER_INVINCIBILITY = false;
	
	// Slow
	public static int SLOW = 1;
	
	// FPS
	public static boolean FPS_ON = false;

	public static void resetAll() {
		NAVMESH_ON = false;
		PATHS_ON = false;
		RANGES_ON = false;
		FLOW_FIELD_ON = false;
		HITBOXES_ON = false;
		HEALTH_ON = false;
		
		AI_DISABLED = false;
		SPAWNERS_DISABLED = false;
        
		PLAYER_INVINCIBILITY = false;
		
		SLOW = 1;
		
		FPS_ON = false;
	}

	private DebugVars() {
	}
}
