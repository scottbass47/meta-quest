package com.fullspectrum.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.fullspectrum.game.GdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
//		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
////		System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
////		config.resizable = false;
////		config.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
////		config.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
//		config.vSyncEnabled = false;
//		config.foregroundFPS = 0;
//		config.backgroundFPS = 0;
//		config.resizable = true;
//		config.width = 1600;
//		config.height = 900;
//		new LwjglApplication(new GdxGame(), config);
		
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.useVsync(false);
		config.setWindowedMode(1280, 720);
		config.setResizable(true);
		config.setDecorated(true);
		new Lwjgl3Application(new GdxGame(), config);
	}
}


