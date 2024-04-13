package objects;

import world.World;

public class Reaction {

	private World myWorld;
	public int k;
	private double  time0, time_blink = 0.5, tstart, tref;
	public double av_time, std_time;
	public double[] react_time = new double[10];
	public boolean isBlink = false;


	public Reaction(World world) {

		k = 0;
		time0 = 0;
		tstart = 2*(1-Math.random()/2);
		myWorld = world;
	}

	public void update(float delta) {


		if(time0>=tstart&&time0<tstart+time_blink&&!isBlink) {
			tref = System.nanoTime()*Math.pow(10, -9);
			isBlink = true;
		}else {
			if(time0>3) {
				clicked();
			}
		}

		time0 += delta;

	}

	public void clicked() {

		react_time[k] = Math.pow(10,-9)*System.nanoTime()-tref;
		if(isBlink) {
			k++;
			time0 = 0;
			tstart = 2*(1-Math.random()/2);
			isBlink = false;
		}
		if(k>9) {
			restart();
			stats(react_time);
			myWorld.setReady();
		}

	}

	public void restart() {

		k = 0;
		time0 = 0;
		tstart = 2*(1-Math.random()/2);


	}

	public void stats(double[] data) {

		int N = data.length;
		double temp = 0;
		for (int i=0;i<N;i++) {
			temp+=data[i];
		}
		av_time = temp/N;
		temp = 0;
		for (int i=0;i<N;i++) {
			temp+=Math.pow(av_time-data[i],2);
		}
		std_time = temp/(N-1);



	}

}
