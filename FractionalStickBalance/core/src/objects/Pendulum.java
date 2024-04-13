package objects;

import world.World;

public class Pendulum {
	// control variables
	private static int f;
	private static double sp=.01, sa = .01, sm = .01, ro=25.12;

	private static int xCur, xPre, x1;
	private static double[] solu, solu_old;
	private static double[][] z = new double[2][1];
	private static double[][] y = new double[3][1];

	private Filter filterX, filterV, filterA, filterS, filterFP;

	public static double init, grav, len, len2, mr, mr2, tlen, tstart, tend, dt, del, parq, mw, magnif;
	public static boolean anim, control, constLen;
	public static double kP, kD;

	public int x1_del, numrestart;
	public double time, time0, end_time, overall_time, dx, dv, vCur, uAcc, fAcc, cAcc, cVel, l_n, stepl,stepl0, react = 0, critLen;
	public boolean isPerturb = false, isFailed = false;

	public ComHandler com;
	
	//-------------------------------------------------------
	//fractional derivative
	
	public double fractionalpos;
	public static double[] binomcoeff = new double[30000];
	public static double alpha;
	
	//-------------------------------------------------------
	

	public Pendulum(double[] data, int gWidth, boolean an, boolean con, boolean cLen, String comPort) {

		len = data[0];// initial rod length
		critLen = len;
		del = data[1];// added delay

		stepl0 = data[3];// step in rod length
		
		init = data[5];// initial disp
		mw = data[6];// manipulation length
		f = (int)data[7];// filtering
		tlen = data[8];// blankout length
		kP = data[9];// proportional gain
		kD = data[10];// derivative gain
		tstart = (int)(5+Math.random()*5);
		grav = 9.81;
		anim = an;
		control = con;
		constLen = cLen;
		if(World.dp) {
			parq = data[2];// q
		}else {
			len2 = data[2];// len2

			mr=ro*len;
			mr2=ro*len2;
		}
		if(!cLen) {
			tend = data[4];// overall survival Time
		}else {
			tend=0;
		}
		

		//-------------------------------------------------------
		//fractional derivative
		
		alpha = 1+parq;
		binomcoeff = binomialcoeff(alpha);
		
		//-------------------------------------------------------	
		
		numrestart=0;

		magnif=mw/gWidth; // Conversion between cart movement in pixel to meters, dimension: [m/px]

		filterX = new Filter(f,"");
		filterV = new Filter(f,"");
		filterA = new Filter(f,"Kalman");
		filterS = new Filter(f,"");
		filterFP = new Filter(f, "");

		//initial conditions
		stepl = stepl0;
		time = 0;time0=0;
		if(World.dp) {
			l_n = len;
			solu = new double[2];
			solu_old = new double[2];
			solu[0]=Math.asin(init/l_n);
			solu[1]=0;
		}else {
			l_n = len+len2;
			solu = new double[4];
			solu_old = new double[4];
			solu[0]=Math.asin(init/l_n);
			solu[1]=0;
			solu[2]=0;
			solu[3]=0;
		}
		solu_old = solu;
		x1=xCur=0;
		xPre = xCur;


		if(control) {
			/* Serial connection to acceleration sensor
			 * Still needs focus, not working properly!
			 * Starts up slow, losses connection eventually,
			 * Problem migth be on controller side
			 */
			com = new ComHandler();
			try {
				com.connect(comPort);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * Update/Restart method
	 */

	public void update(float dt, boolean simulate) {

		/*
		 * Check movement & Calculate control
		 */
		int dx1=xCur-xPre; // step on screen in pixels
		x1+=dx1; // position of the cart [px]
		x1_del = (int) filterX.addDelay(x1,del,dt); // Artificially delayed value of position to show

		dx=filterX.AverageFilt(xCur*magnif); // position change in meters (xCur-xPre)*magnif
		vCur = dx/dt; // velocity of the cart [m/s]
		cVel=filterV.addDelay(vCur,del,dt); // Artificially delayed velocity of the cart [m/s]
		dv=filterV.AverageFilt(vCur); // velocity change in meters (vCur-vPre)*magnif
		cAcc = filterA.addDelay(dv/dt,del,dt); // Artificially delayed calculated acceleration of the cart [m/s^2]

		//-------------------------------------------------------
		//fractional derivative
				
		fractionalpos=filterX.FractionalDerivativeSum(binomcoeff)/Math.pow(dt,alpha);
		fractionalpos=filterFP.addDelay(fractionalpos,del,dt); // filterS delay buffer used here, sum doesn't have to be delayed
		
		double sum=filterS.FracSum(binomcoeff,solu);
				
				
		//-------------------------------------------------------
				

		if(control) {
			ComHandler.writeData("l"); // "l" => left|right; "u" => up|down
			// filter measured data by Kalman filter
			z[0][0] = xCur*magnif;
			z[1][0] = com.data;
			y = filterA.KalmanFilt(z,dt,sp,sa,sm);
			fAcc = filterA.addDelay(y[2][0],del,dt); // Artificially delayed measured/filtered acceleration of the cart [m/s^2]
			uAcc = fAcc;
		}else{
			uAcc = cAcc;
		} // acceleration of the cart [m/s^2]

		// external force on cart when key.P is pressed down
		if(isPerturb) {
			uAcc += 2*grav;
		} 

		/*
		 * Calculate next state
		 */
		if(simulate) {

			if(!constLen) {
				stepLength("");
			} 
			
			//-------------------------------------------------------
			//fractional derivative
			if(!World.frac||parq==1||parq==0) {
				solu = solver(solu, solu_old, dt, "RK", sum);
			}else {
				solu = solver(solu, solu_old, dt, "Frac", sum);
			}							
			//-------------------------------------------------------

			//solu = solver(solu, solu_old, dt, "RK");
			
			time+=dt;
			time0+=dt;
			solu_old=filterS.addDelay(solu, del, dt);
			end_time = time;
		}
		xPre = xCur;
		// System.out.print(String.format("%d\n",x1));
		// vPre = vCur;

	}


	/* decrease pendulum length by XX cm in YY seconds
	 * or halving method on steps?
	 */
	private void stepLength(String type) {
		if(time0 > tend) {
			l_n = l_n - tend*stepl;
			switch (type) {
			case "half":
				stepl = stepl/2;
				break;
			default:
				break;
			}
			time0 = 0;
			//			System.out.print(String.format("%.2f\n", l_n));
		}
	}

	public void restart() {

		filterX = new Filter(f,"");
		filterV = new Filter(f,"");
		filterA = new Filter(f,"Kalman");
		filterS = new Filter(f,"");


		//		stepl = stepl0;
		//		len = l_n;
		time = 0;time0 = 0;
		if(World.dp) {
			l_n=len;
			solu[0]=Math.asin(init/l_n);
			solu[1]=0;
		}else {
			solu[0]=Math.asin(init/l_n);
			solu[1]=0;
			solu[2]=0;
			solu[3]=0;
		}
		solu_old = solu;
		xPre = xCur;

		numrestart++;

		tstart = (int)(5+Math.random()*5);

		dx=0;dv=0;vCur=0;
		uAcc=0;fAcc=0;cAcc=0;
		if(control) {
			ComHandler.writeData("o"); // offset sensor
		}
	}

	public void next() {

		filterX = new Filter(f,"");
		filterV = new Filter(f,"");
		filterA = new Filter(f,"Kalman");
		filterS = new Filter(f,"");
		critLen = l_n;
		if(!constLen) {
			if(time>World.limittime) {
				//l_n += (time-World.limittime)*stepl;

				l_n += (World.limittime)*stepl;
			}else {

				l_n += (World.limittime)*stepl;
			}
			stepl = stepl/2;
		}else {
			// if failed increase else decrease
			if (!isFailed) {
				l_n -= stepl;
			}else {
				l_n += stepl;
			}
			stepl = stepl/2;
		}
		time = 0;time0 = 0;
		if(World.dp) {
			len=l_n;
			solu[0]=Math.asin(init/l_n);
			solu[1]=0;
		}else {
			solu[0]=Math.asin(init/l_n);
			solu[1]=0;
			solu[2]=0;
			solu[3]=0;
		}
		solu_old = solu;
		xPre = xCur;

		numrestart = 0;

		dx=0;dv=0;vCur=0;
		uAcc=0;fAcc=0;cAcc=0;
		if(control) {
			ComHandler.writeData("o"); // offset sensor
		}
	}

	/*
	 * Equation of motion, Solvers
	 */

	public double[] dynamics(double[] y, double[] y_old) {

		int N=y.length;
		double[] dydt = new double[N];
		double cForce=0;
		if(anim) {
			cForce=-kP*y_old[0]-kD*y_old[1];
		}

		if(World.dp&&!World.frac) {

			double u = -3/(2*l_n)*Math.cos(y[0])*(parq*uAcc+(1-parq)*cVel)+cForce;
			
			if(parq!=0) {

				double qdd=(3*grav/(2*l_n)*Math.sin(y[0])-(1-parq)*y[1]+u)/parq;

				dydt[0]=y[1];
				dydt[1]=qdd;

			}else {

				double qd = 3*grav/(2*l_n)*Math.sin(y[0])+u;

				dydt[0]=qd;
				dydt[1]=1;

			}
//-------------------------------------------------------
//fractional derivative
		}else if(World.dp&&World.frac) {
			double u;
			if(parq == 0 || parq == 1) {
				u = -3/(2*l_n)*Math.cos(y[0])*(parq*uAcc+(1-parq)*cVel)+cForce;
			}else {
				u = -3/(2*l_n)*Math.cos(y[0])*fractionalpos+cForce;
			}
			
			if(parq==1) {

				double qdd=(3*grav/(2*l_n)*Math.sin(y[0])-(1-parq)*y[1]+u)/parq;

				dydt[0]=y[1];
				dydt[1]=qdd;

			}else if(parq==0){

				double qd = 3*grav/(2*l_n)*Math.sin(y[0])+u;

				dydt[0]=qd;
				dydt[1]=1;

			}else {

				double qfd = 3*grav/(2*l_n)*Math.sin(y[0])+u;

				dydt[0]=qfd;
				dydt[1]=0;

			}
//-------------------------------------------------------
		}else {

			double[] u = {(6*(len*Math.cos(y[0])*(4*mr+5*mr2)+mr2*(len2*Math.cos(y[0]+y[1])-3*len*Math.cos(y[0]+2*y[1]))))
					/(3*mr2*Math.pow(len2, 2)+2*Math.pow(len, 2)*(8*mr+15*mr2)+6*len*mr2*(2*len2*Math.cos(y[1])-3*len*Math.cos(2*y[1]))),
					(-6*len*(Math.cos(y[0])*(2*len*mr*Math.cos(y[1])+3*len2*(mr+mr2-mr2*Math.cos(2*y[1])))+2*(2*len*(mr+3*mr2)+3*len2*mr2*Math.cos(y[1]))*Math.sin(y[0])*Math.sin(y[1])))
					/(3*mr2*Math.pow(len2, 2)+2*Math.pow(len, 2)*(8*mr+15*mr2)+6*len*mr2*(2*len2*Math.cos(y[1])-3*len*Math.cos(2*y[1])))/len2};

			dydt[0]=y[2];
			dydt[1]=y[3];
			dydt[2]=(6*grav*(len*(4*mr+5*mr2)*Math.sin(y[0])+mr2*(len2*Math.sin(y[0]+y[1])-3*len*Math.sin(y[0]+2*y[1])))+6*len*mr2*Math.sin(y[1])*(4*len2*Math.pow(y[3],2)+8*len2*y[2]*y[3]+3*(len2+2*len*Math.cos(y[1]))*Math.pow(y[2], 2)))
					/(3*Math.pow(len2, 2)*mr2+2*Math.pow(len, 2)*8*mr+15*mr2+6*len*mr2*(2*len2*Math.cos(y[1])-3*len*Math.cos(2*y[1])))+u[0]*uAcc;
			dydt[3]=(6*len*(-grav*Math.sin(y[0])*(2*len*mr*Math.cos(y[1])+3*len2*(mr+mr2-mr2*Math.cos(2*y[1])))+2*grav*Math.cos(y[0])*Math.sin(y[1])*(2*len*(mr+3*mr2)+3*len2*mr2*Math.cos(y[1]))-Math.sin(y[1])*((3*mr2*Math.pow(len2, 2)+4*(mr+3*mr2)*Math.pow(len, 2)+12*len*len2*mr2*Math.cos(y[1]))*Math.pow(y[2], 2)+6*len2*mr2*(len2+2*len*Math.cos(y[1]))*y[2]*y[3]+3*len2*mr2*(len2+2*len*Math.cos(y[1]))*Math.pow(y[3], 2))))
					/(len2*(3*Math.pow(len2, 2)*mr2+2*Math.pow(len, 2)*(8*mr+15*mr2)+6*len*mr2*(2*len2*Math.cos(y[1])-3*len*Math.cos(2*y[1]))))+u[1]*uAcc;
			
		}

		return dydt;
	}

	public double[] solver(double[] y, double[] y_old, double dt, String method, double sum) {

		int N=y.length;
		double[] dydt=new double[N];
		double[] k1=new double[N];
		double[] k2=new double[N];
		double[] k3=new double[N];
		double[] k4=new double[N];

		switch (method) {
		case "RK":
			k1=dynamics(y,y_old);
			k2=dynamics(MxOper.addVec(y, MxOper.multVecwS(dt/2, k1)),y_old);
			k3=dynamics(MxOper.addVec(y, MxOper.multVecwS(dt/2, k2)),y_old);
			k4=dynamics(MxOper.addVec(y, MxOper.multVecwS(dt, k3)),y_old);

			for(int s=0;s<N;s++) {
				dydt[s] = (k1[s]+2*k2[s]+2*k3[s]+k4[s])/6.;
			}// dydt=(k1+2*(k2+k3)+k4)/6
			y=MxOper.addVec(y,MxOper.multVecwS(dt, dydt));
			break;
		case "EE":
			dydt=dynamics(y,y_old);
			y=MxOper.addVec(y,MxOper.multVecwS(dt, dydt));
			break;
		//-------------------------------------------------------
		//fractional derivative
		case "Frac":
			dydt=dynamics(y,y_old);
			y[0]=Math.pow(dt, alpha)*dydt[0]+sum;
			y[1]=y[1];
			break;
		//-------------------------------------------------------

		}

		return y;
		//y=y+dydt*dt

	}

	/*
	 * 
	 */

	public void exitCom() {
		if(control) {
			// close serial connection
			ComHandler.writeData("c");
			com.close();
		}
	}

	public void cartMove(int sX, int sY) {
		xCur = sX;
	}

	public double getSolu() {
		return solu[0];
	}
	
	public double[] getSoluDP() {
		double[] temp = {solu[0],solu[1]};
		return temp;
	}

	public double[] getInput() {
		// t, x1, fi xpp, acc, om/theta
		double[] temp =  {time,x1_del*magnif,l_n*Math.sin(solu[0]),uAcc,solu[0],solu[1],l_n};
		return temp;
	}


	// decrease/increase pendulum length by command

	public void increaseLength() {

		if(!constLen) {
			l_n +=stepl*tend;
		}else {
			l_n +=stepl;
		}
	}
	public void decreaseLength() {

		if(!constLen) {
			l_n -=stepl*tend;
		}else {
			l_n -=stepl;
		}
	}
	
	//-------------------------------------------------------
	//fractional derivative
	public static double[] binomialcoeff(double a) {

		double[] coeff = new double[60000];

		coeff[0]=1;
		
		for (int k = 1; k < 30000; k++) {
			coeff[k] = (1-(a+1)/k)*coeff[k-1];
		}
		
		return coeff;
	}
	//-------------------------------------------------------

}
