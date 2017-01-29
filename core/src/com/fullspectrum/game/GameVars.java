package com.fullspectrum.game;

public class GameVars {

	// Scale
	public final static float PPM = 16;
	public final static float PPM_INV = 1.0f / PPM;

	// Frame Buffer
	public static int FRAMEBUFFER_WIDTH = 1280;
	public static int FRAMEBUFFER_HEIGHT = 720;
	
	// Screen
	public final static int SCREEN_WIDTH = FRAMEBUFFER_WIDTH;
	public final static int SCREEN_HEIGHT = FRAMEBUFFER_HEIGHT;
	
	public static float R_WORLD_WIDTH = FRAMEBUFFER_WIDTH * PPM_INV;
	public static float R_WORLD_HEIGHT = FRAMEBUFFER_HEIGHT * PPM_INV;
	
	// Physics
	public final static float GRAVITY = -23f;
	
	// FPS
	public static final int UPS = 60;
	public static final float UPS_INV = 1.0f / UPS;
	
}
