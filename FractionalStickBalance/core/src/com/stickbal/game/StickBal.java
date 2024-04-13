package com.stickbal.game;

import com.badlogic.gdx.Game;
import screen.SetupSc;

public class StickBal extends Game {
	
	@Override
	public void create () {
		this.setScreen(new SetupSc(this));
	}


	
	@Override
	public void dispose () {
		super.dispose();
	}
}
