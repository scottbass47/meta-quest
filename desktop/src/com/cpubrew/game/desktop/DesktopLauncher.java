package com.cpubrew.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.cpubrew.debug.DebugVars;
import com.cpubrew.game.GdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.useVsync(false);
		config.setWindowedMode(DebugVars.FULLSCREEN_MODE ? 1920 : 1280, DebugVars.FULLSCREEN_MODE ? 1080 : 720);
		config.setResizable(false);
		config.setDecorated(!DebugVars.FULLSCREEN_MODE);
		
		new Lwjgl3Application(new GdxGame(), config);
	}
}