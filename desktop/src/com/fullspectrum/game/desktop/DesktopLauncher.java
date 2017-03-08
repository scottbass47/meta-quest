package com.fullspectrum.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.fullspectrum.game.GdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.useVsync(false);
		config.setWindowedMode(1920, 1080);
		config.setResizable(false);
		config.setDecorated(false);
		new Lwjgl3Application(new GdxGame(), config);
	}
}