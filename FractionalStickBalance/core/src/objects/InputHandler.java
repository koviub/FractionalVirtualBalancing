package objects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

import screen.SetupSc;
import world.World;
import world.World.GameState;

public class InputHandler implements InputProcessor{

	private World myWorld;
	private Game myGame;
	private Pendulum myPend;
	private Reaction myReact;
	private static String fn;


	public InputHandler(Game game, World world, int sW, int sH) {

		myWorld = world;
		myGame=game;
		myPend = world.pend;
		myReact = world.react;

	}

	@Override
	public boolean keyDown(int keycode) {
		switch(keycode) {
		case Keys.S: // start simulation
			break;
		case Keys.B: // start blank out simulation
			break;
		case Keys.ESCAPE: // kill
			break;
		case Keys.R: // restart
			break;
		case Keys.N: // next 
			break;
		case Keys.Q: // quit
			break;
		case Keys.G: // show graphs
			break;
		case Keys.P: // disturbance input during balancing
			if(!myPend.isPerturb) {
				myPend.isPerturb=true;
			}
			break;
		case Keys.H: // 
			break;
		}	
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {		
		switch(keycode) {
		case Keys.S: // start simulation
			if(myWorld.isReady()) {
				if (!myWorld.isReact) {
					myWorld.setCurrentState(GameState.RUNNING);
					myWorld.res.is=myWorld.res.i;
				}else {
					myWorld.setCurrentState(GameState.REACT);
				}
			}else {
				myWorld.setCurrentState(GameState.READY);
			}
			break;
		case Keys.B: // start blank out simulation
			if(myWorld.isReady()) {
				myWorld.setCurrentState(GameState.RUNNING);
				//myWorld.isBlank = true;
				myWorld.isFirst = false;
				//Pendulum.constLen = true;
				//myPend.l_n=10;
			}
			break;
		case Keys.ESCAPE: // kill
			if(!(myWorld.isBlank&&myPend.time<Pendulum.tstart + Pendulum.tlen+World.limittime/2)){

				try {
					myWorld.res.saveResults(fn);
					myPend.exitCom();
				} catch (Exception e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
			}
			myWorld.restart();
			Gdx.app.exit();
			break;
		case Keys.R: // restart
			if (!myWorld.isReact) {
				if(!(myWorld.isBlank&&myPend.time<Pendulum.tstart + Pendulum.tlen+World.limittime/3)){

					try {
						myWorld.res.saveResults(fn);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				myReact.restart();
				myPend.restart();
				myWorld.restart();
			}
			break;
		case Keys.N: // next
			if (!myWorld.isReact) {
				if(!(myWorld.isBlank&&myPend.time<Pendulum.tstart + Pendulum.tlen+World.limittime/2)){

					try {
						myWorld.res.saveResults(fn);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if ((myPend.time>10||myPend.numrestart>3)&&!myWorld.isBlank&&myPend.stepl/2>=World.limitstep) {
					if(Pendulum.constLen) {
						myPend.restart();
					}else {
						myPend.next();
					}
					myWorld.restart();
				}else {
					myPend.restart();
					myWorld.restart();
				}
			}
			break;
		case Keys.Q: // quit
			if(!(myWorld.isBlank&&myPend.time<Pendulum.tstart + Pendulum.tlen+World.limittime/2)){

				try {
					myWorld.res.saveResults(fn);
					myPend.exitCom();
				} catch (Exception e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
			}
			myWorld.restart();
			myGame.setScreen(new SetupSc(myGame));
			break;
		case Keys.G: // show graphs
			if (!myWorld.isGraph){
				myWorld.isGraph = true;
			}else {
				myWorld.isGraph = false;
			}
			break;
		case Keys.P: // disturbance input during balancing
			if(myPend.isPerturb) {
				myPend.isPerturb=false;
			}
			break;
		case Keys.I: // screnTest
			myPend.increaseLength();
			break;
		case Keys.K: // screnTest
			myPend.decreaseLength();
			break;
		case Keys.H: // screnTest
			if (!myWorld.isHelp){
				myWorld.isHelp = true;
			}else {
				myWorld.isHelp = false;
			}
			break;
		}	
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		myPend.cartMove(screenX, screenY);

		if(!myWorld.isFirst && Math.abs(myPend.cAcc)>.3 && myWorld.isBlank && Pendulum.tstart + Pendulum.tlen <= myPend.time && (myPend.time < Pendulum.tstart + Pendulum.tlen + 1)) { // scan for 1 sec only after screen returns
			myPend.react = myPend.time-(Pendulum.tstart+Pendulum.tlen);
			myWorld.isFirst = true;
		}
		/* rogzitsd a blankout alatt a gyorsulast.
		 * ahol nagy a valtoz�s rogzitsd az idot es az a reakcio
		 */

		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(!myWorld.isTest) {
			myWorld.isTest=true;
		}
		if(myWorld.isReact) {
			myWorld.react.clicked();
		}

		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(myWorld.isTest) {
			myWorld.isTest=false;
		}

		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		// TODO Auto-generated method stub
		return false;
	}

	public static void setFilename(String text) {
		fn = text;

	}
}