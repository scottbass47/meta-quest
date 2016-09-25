package com.fullspectrum.game;

public class GameVars {

	// Frame Buffer
	public final static int FRAMEBUFFER_WIDTH = 320;
	public final static int FRAMEBUFFER_HEIGHT = 180;
	
	// Scale
	public final static int UPSCALE = 4;
	public final static float PPM = 16;
	public final static float PPM_INV = 1.0f / PPM;
	
	// Screen
	public final static int SCREEN_WIDTH = FRAMEBUFFER_WIDTH * UPSCALE;
	public final static int SCREEN_HEIGHT = FRAMEBUFFER_HEIGHT * UPSCALE;
	
	public final static float R_WORLD_WIDTH = FRAMEBUFFER_WIDTH / PPM;
	public final static float R_WORLD_HEIGHT = FRAMEBUFFER_HEIGHT / PPM;
	
}
