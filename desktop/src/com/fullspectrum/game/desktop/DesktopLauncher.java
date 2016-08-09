package com.fullspectrum.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fullspectrum.game.GdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
//		config.resizable = false;
//		config.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
//		config.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
		config.vSyncEnabled = false;
		config.foregroundFPS = 0;
		config.backgroundFPS = 0;
		config.resizable = true;
		config.width = 1600;
		config.height = 900;
		new LwjglApplication(new GdxGame(), config);
	}
}
