package com.fullspectrum.debug;

public class DebugVars {

	// Show
	public static boolean NAVMESH_ON = false;
	public static boolean PATHS_ON = false;
	public static boolean RANGES_ON = false;
	public static boolean FLOW_FIELD_ON = false;
	
	// Disable
	public static boolean AI_DISABLED = false;
	public static boolean SPAWNERS_DISABLED = false;
	
	// Invincible
	public static boolean PLAYER_INVINCIBILITY = false;

	public static void resetAll() {
		NAVMESH_ON = false;
		PATHS_ON = false;
		RANGES_ON = false;
		FLOW_FIELD_ON = false;
		
		AI_DISABLED = false;
		SPAWNERS_DISABLED = false;
        
		PLAYER_INVINCIBILITY = false;
	}

	private DebugVars() {
	}
}
