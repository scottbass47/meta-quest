package com.fullspectrum.game;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameVars {

	// Scale
	public static int UPSCALE = 4;
	public final static float PPM = 16;
	public final static float PPM_INV = 1.0f / PPM;

	// Frame Buffer
	public static int FRAMEBUFFER_WIDTH = 320 * (4 / UPSCALE);
	public static int FRAMEBUFFER_HEIGHT = 180 * (4 / UPSCALE);
	
	// Screen
	public final static int SCREEN_WIDTH = FRAMEBUFFER_WIDTH * UPSCALE;
	public final static int SCREEN_HEIGHT = FRAMEBUFFER_HEIGHT * UPSCALE;
	
	public static float R_WORLD_WIDTH = FRAMEBUFFER_WIDTH * PPM_INV;
	public static float R_WORLD_HEIGHT = FRAMEBUFFER_HEIGHT * PPM_INV;
	
	// Physics
	public final static float GRAVITY = -23.0f;
	
	// Collision Bits
	public static final short TILE = 1 << 0;
	public static final short ENTITY = 1 << 1;
	public static final short SENSOR = 1 << 2;
	
	// Change Scale
	public static void resize(int scale, OrthographicCamera worldCam){
		worldCam.zoom = 1.0f / scale;
		UPSCALE = scale;
		
		FRAMEBUFFER_WIDTH = 320 * (4 / UPSCALE);
		FRAMEBUFFER_HEIGHT = 180 * (4 / UPSCALE);
		
		R_WORLD_WIDTH = FRAMEBUFFER_WIDTH * PPM_INV;
		R_WORLD_HEIGHT = FRAMEBUFFER_HEIGHT * PPM_INV;
	}
	
}
