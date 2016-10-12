package com.fullspectrum.input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


public class Mouse {

	private static int x;
	private static int y;
	
	private static int button;
	private static boolean pressed;
	private static boolean wasPressed;
	
	public static Vector2 getScreenPosition(){
		return new Vector2(x, y);
	}
	
	public static Vector2 getWorldPosition(OrthographicCamera cam){
		Vector3 v3 = cam.unproject(new Vector3(x, y, 0));
		return new Vector2(v3.x, v3.y);
	}
	
	public static int getButton(){
		return button;
	}
	
	public static boolean isPressed(){
		return pressed;
	}
	
	public static boolean isJustPressed(){
		return pressed && !wasPressed;
	}
	
	public static void update(){
		wasPressed = pressed;
	}
	
	public static void touchDown(int screenX, int screenY,int button) {
		x = screenX;
		y = screenY;
		Mouse.button = button;
		pressed = true;
	}

	public static void touchUp(int screenX, int screenY, int button) {
		x = screenX;
		y = screenY;
		Mouse.button = button;
		pressed = false;
	}

	public static void touchDragged(int screenX, int screenY) {
		x = screenX;
		y = screenY;
	}

	public static void mouseMoved(int screenX, int screenY) {
		x = screenX;
		y = screenY;
	}
	
}
