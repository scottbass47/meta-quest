package com.fullspectrum.game;

public class GameVars {

	// Scale
	public final static int UPSCALE = 2;
	public final static float PPM = 16;
	public final static float PPM_INV = 1.0f / PPM;

	// Frame Buffer
	public final static int FRAMEBUFFER_WIDTH = 320 * (4 / UPSCALE);
	public final static int FRAMEBUFFER_HEIGHT = 180 * (4 / UPSCALE);
	
	// Screen
	public final static int SCREEN_WIDTH = FRAMEBUFFER_WIDTH * UPSCALE;
	public final static int SCREEN_HEIGHT = FRAMEBUFFER_HEIGHT * UPSCALE;
	
	public final static float R_WORLD_WIDTH = FRAMEBUFFER_WIDTH * PPM_INV;
	public final static float R_WORLD_HEIGHT = FRAMEBUFFER_HEIGHT * PPM_INV;
	
	// Physics
	public final static float GRAVITY = -23.0f;
	
}
