package com.fullspectrum.game;

public class GameVars {

	// Dimensions
	public final static int V_WORLD_WIDTH = 1280;
	public final static int V_WORLD_HEIGHT = 720;
	public final static float TILE_SCALE = 2;
	public final static float PPM = 16 * TILE_SCALE;
	public final static float R_WORLD_WIDTH = (float)V_WORLD_WIDTH / (float)PPM;
	public final static float R_WORLD_HEIGHT = (float)V_WORLD_HEIGHT / (float)PPM;
	
	// Collision Bits
	public final static short BIT_GROUND = 2;
	public final static short BIT_BOX = 4;
	public final static short BIT_BALL = 8;
}
