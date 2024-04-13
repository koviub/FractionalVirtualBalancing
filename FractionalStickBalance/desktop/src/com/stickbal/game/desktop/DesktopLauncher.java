package com.stickbal.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.stickbal.game.StickBal;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title="Virtual StickBalancing";
		config.useGL30=false;
		
		/*
		 * Screen size/resolution setup
		 */
		boolean fuls = true;
		config.setFromDisplayMode(LwjglApplicationConfiguration.getDesktopDisplayMode());
		if(!fuls) {
			config.fullscreen=false;
			config.width/=1.1f;
			config.height/=1.1f;
		}
		config.resizable=false;
		config.forceExit=false;
		/*
		 * fps rate settings: - mouse derivative noisy at higher fps - acceleration
		 * signal has to be syncronized
		 * 
		 */
		int FPS = 60;
		
		config.vSyncEnabled=true;
		config.foregroundFPS=FPS;
		config.backgroundFPS=FPS;	
		
		
		new LwjglApplication(new StickBal(), config);
	}
}
