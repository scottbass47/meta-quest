package com.fullspectrum.game;

public class GameVars {

	// Dimensions
	public final static int V_WORLD_WIDTH = 640;
	public final static int V_WORLD_HEIGHT = 360;
	public final static float PPM = 16;
	public final static float R_WORLD_WIDTH = (float)V_WORLD_WIDTH / (float)PPM;
	public final static float R_WORLD_HEIGHT = (float)V_WORLD_HEIGHT / (float)PPM;
	
	// Collision Bits
	public final static short BIT_GROUND = 2;
	public final static short BIT_BOX = 4;
	public final static short BIT_BALL = 8;
}
