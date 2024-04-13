package objects;

public class Filter {

	private double[] data = new double[60000],data1 = new double[61000],data2 = new double[61000],data3 = new double[61000];
	private int i,j;
	public double delta;
	public double dval;
	private int filter;
	private String type;
	private double[][] Ppred = new double[3][3], Pkor = new double[3][3], K = new double[3][2];
	private double[][] ypred = new double[3][1], ykor = new double[3][1];
	private double[][] Q = new double[3][3], R = new double[2][2];
	private double[][] F = new double[3][3];
	final double[][] H = { { 1, 0, 0 }, { 0, 0, 1 } };
	//-------------------------------------------------------
	//fractional derivative

	private int ii;
	private double [] data4 = new double[30000];

	//-------------------------------------------------------


	public Filter(int filt, String ftype) {
		//-------------------------------------------------------
		//fractional derivative		
		ii=0;
		//-------------------------------------------------------

		j=0;
		filter = filt;
		type = ftype;

		for (int i = 0; i < 1000; i++) {
			data1[i] = 0; // added delay buffer
			j=i;
		}

		if (type == "Kalman") {

			F = MxOper.eyeMx(3, 3);
			Q = MxOper.zeroMx(3, 3);
			R = MxOper.zeroMx(2, 2);

			//
			Pkor = MxOper.zeroMx(F.length, F[0].length);
			ykor = MxOper.zeroMx(F.length, 1);

		} else {
			for (int i = 0; i < filt; i++) {
				data[i] = 0; // averaging filter buffer
			}
			i = filt;
		}
	}

	public double[][] KalmanFilt(double[][] z, double dt,  double sdevp, double sdeva, double sdevm) {

		F[0][1] = dt;
		F[0][2] = Math.pow(dt, 2) / 2;
		F[1][2] = dt;

		R[0][0] = Math.pow(sdevp, 2);
		R[1][1] = Math.pow(sdeva, 2);

		Q[0][0] = Math.pow(dt, 4) / 4 * Math.pow(sdevm, 2);
		Q[0][1] = Math.pow(dt, 3) / 2 * Math.pow(sdevm, 2);
		Q[0][2] = Math.pow(dt, 2) / 2 * Math.pow(sdevm, 2);
		Q[1][1] = Math.pow(dt, 2) * Math.pow(sdevm, 2);
		Q[1][2] = dt * Math.pow(sdevm, 2);
		Q[2][2] = 1 * Math.pow(sdevm, 2);
		Q[2][0] = Q[0][2];
		Q[1][0] = Q[0][1];
		Q[2][1] = Q[1][2];

		Ppred = MxOper.addMx(MxOper.multMx(MxOper.multMx(F, Pkor), MxOper.transMx(F)), Q);

		ypred = MxOper.multMx(F, ykor);

		K = MxOper.multMx(MxOper.multMx(Ppred, MxOper.transMx(H)),
				MxOper.invMx(MxOper.addMx(MxOper.multMx(MxOper.multMx(H, Ppred), MxOper.transMx(H)), MxOper.multMx(R, MxOper.eyeMx(2, 2)))));

		Pkor = MxOper.subMx(Ppred, MxOper.multMx(K, MxOper.multMx(H, Ppred)));

		ykor = MxOper.addMx(ypred, MxOper.multMx(K, MxOper.subMx(z, MxOper.multMx(H, ypred))));

		return ykor;
	}

	public double AverageFilt(double cur) {

		data[i] = cur;
		delta = (cur - data[i - filter]) / filter;
		i++;

		return delta;
	}

	public double addDelay(double val,double del,double dt) {

		int k=(int)(del/dt);

		data1[j]=val;
		dval=data1[j-k];
		j++;

		return dval;

	}

	public double[] addDelay(double[] val,double del,double dt) {

		int k=(int)(del/dt);

		data2[j]=val[0];
		data3[j]=val[1];
		double[] dval1= {data2[j-k],data3[j-k]};
		j++;

		return dval1;

	}


	//-------------------------------------------------------
	//fractional derivative

	public double FractionalDerivativeSum(double[] coeff) {

		double sum = 0;

		for (int k = 0; k < i; k++) {
			sum += coeff[k]*data[i-1-k];
		}

		return sum;
	}


	public double FracSum(double[] coeff, double[] sol) {

		double sum1 = 0;
		double sum2 = 0;

		data4[ii] = sol[0];

		for (int k = 1; k < ii+1; k++) {
			sum1 += coeff[k]*data4[ii-k+1];
		}

		for (int k = 0; k < ii+1; k++) {
			sum2 += coeff[k];
		}

		ii++;

		return -sum1+data4[0]*sum2;
	}

	//-------------------------------------------------------

}
