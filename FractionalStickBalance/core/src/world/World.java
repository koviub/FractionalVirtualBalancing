package world;

import objects.Pendulum;
import objects.Reaction;
import objects.Results;

public class World {

	public Pendulum pend;
	public Results res;
	public Reaction react;
	public boolean isGraph=false, isBlank = false, isFirst = false, isTest = false, isHelp = false, isReact = false;
	public static boolean dp=false, frac=false;
	public static double limitstep=0.0125, limittime = 10;
	public double eT = 10;
	public double modx, wx, gW;

	private GameState currentState;

	public enum GameState {
		READY, RUNNING, CALIBR, REACT
	}

	public World(int gWidth, int gHeight, double[] data, boolean anim, boolean control,boolean cLen, boolean calibr, boolean blank, boolean reaction, String comPort, boolean dpend, boolean ff) {


		isReact = reaction;
		isBlank = blank;
		dp = !dpend;
		frac = ff;
		if(isBlank) {
			cLen=true;
			//data[0] = 10; // legyen hard code-olva??
		}
		if(cLen) {
			eT = data[4];// overall survival Time
		}else {
			eT=5;
		}

		gW = gWidth;
		modx = wx/gW;
		
		pend = new Pendulum(data,gWidth,anim,control,cLen,comPort);
		react = new Reaction(this);
		res = new Results(this);

		if(!calibr) {
			setCurrentState(GameState.READY);
		}else {
			setCurrentState(GameState.CALIBR);
		}

	}

	/*
	 * Update methods
	 * ______________________________________________________________________________
	 */
	public void update(float delta, float runTime) {

		switch (getCurrentState()) {
		case READY:
			updateReady(delta);
			break;
		case RUNNING:
			updateRunning(delta);
			break;
		case CALIBR:
			updateCalib(delta, runTime);
			break;
		case REACT:
			updateReact(delta, runTime);
			break;
		default:
			break;
		}
	}

	private void updateReady(float delta) {

		if(!isReact) {
			res.recordData(pend.getInput());
			pend.update(delta, false);


		}else {

		}
	}

	private void updateRunning(float delta) {

		res.recordData(pend.getInput());
		res.ii=res.i;
		if( !(isBlank)&& pend.l_n>0&& res.i<40000&&Math.abs(pend.getSolu())<Math.PI/9) {//&& pend.overall_time+pend.time<eT*60 &&Math.abs(pend.getSolu())<Math.abs(Math.asin(.9*wx/pend.l_n)) 
			pend.update(delta, true);
		}else if((isBlank)&& Math.abs(pend.getSolu())<Math.PI/9 && pend.l_n>0 && pend.time < Pendulum.tstart+Pendulum.tlen+limittime/2 ) {
			pend.update(delta, true);
		}else{
			
			pend.critLen = pend.l_n;
			setCurrentState(GameState.READY);
			pend.overall_time += pend.end_time; 
			pend.end_time = 0;
		}

	}

	private void updateCalib(float delta, float runTime) {
		res.recordData(pend.getInput());
		res.ii=res.i;
		pend.update(delta, false);
	}

	private void updateReact(float delta, float runTime) {
		res.recordReact(react.react_time);
		react.update(delta);
	}

	public void restart() {
		if(isCalibr()) {
			setCurrentState(GameState.CALIBR);
		}else {
			setCurrentState(GameState.READY);
		}
		isFirst = false;
		isReact = false;
		res.restart();
	}

	public GameState getCurrentState() {
		return currentState;
	}

	public void setCurrentState(GameState currentState) {
		this.currentState = currentState;
	}

	public void setReady() {
		this.currentState = GameState.READY;
	}

	public boolean isReady() {
		return getCurrentState() == GameState.READY;
	}

	public boolean isRunning() {
		return getCurrentState() == GameState.RUNNING;
	}

	public boolean isCalibr() {
		return getCurrentState() == GameState.CALIBR;
	}

}
