package com.neet.blockbunny.main;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = Application.TITLE;
		config.width = Application.V_WIDTH * Application.SCALE;
		config.height = Application.V_HEIGHT * Application.SCALE;

		new LwjglApplication(new Application(), config);
	}

}
