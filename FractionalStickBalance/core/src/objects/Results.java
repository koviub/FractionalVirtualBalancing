package objects;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import world.World;

public class Results {

	private static int N;
	private int j,l;
	private double[][] record;
	private double[] bData, react;

	private World myWorld;
	private FileWriter fW;
	private String fId,stringDate;
	private Date date;
	private SimpleDateFormat formatter = new SimpleDateFormat("MMddHHmmss");

	public static int k;
	public int i, ii, is=0;

	public Results(World world) {
		i=0;j=0;l=0;k=0;ii=0;
		N=61000;
		myWorld = world;
		record = new double[N][7];
		bData = new double[2];
		date = new Date();

		stringDate = formatter.format(date);

		bData[0] = Pendulum.tstart; // blank  start
		bData[1] = Pendulum.tlen; // blank length
	}

	/*
	 * Record values
	 */
	public void recordData(double[] input) {

		for(int j=0;j<input.length;j++) {
			record[i][j]=input[j]; // t, x1, fi, xpp, acc
		}

		i++;
	}

	public void recordDist(double t) {
		record[i][6] = 1;
	}

	public void recordReact(double[] array) {
		react = array;
	}

	/*
	 * Plotter
	 */

	public void plotter(ShapeRenderer shaper,int sW, int sH, int sx, int sy) {
		int scale, offset, x1, x2, y1, y2, y0;
		if(sy!=0) {
			scale=3;
		}else {
			scale=20;
		}

		if(myWorld.isGraph) {
			shaper.begin(ShapeType.Filled);
			shaper.setColor(1, 1, 1, 0.f);
			shaper.rect(sx, sy, sW, sH);
			shaper.end();

			for (int i = -sH/2; i <= sH/2; i = i + sH/10) {
				shaper.begin(ShapeType.Line);
				shaper.setColor(0.9f, 0.9f, 0.9f, .1f);
				shaper.line(sx, sy+sH / 2 -  i, sx+sW, sy+sH / 2 -  i);
				shaper.end();
			}
		}

		for(int s=1;s<j;s++) {

			x1 = sx+(s-1);
			x2 = sx+s;

			y0 = (int) (sy+sH/2);
			offset = l*sW;

			// x1 
			y1 = y0 - (int)((record[x1+offset][1]-sW/2)/sW*sH);
			y2 = y0 - (int)((record[x2+offset][1]-sW/2)/sW*sH);
			y1=constrain(y1,sy,sy+sH);
			y2=constrain(y2,sy,sy+sH);
			if(myWorld.isGraph) {
				shaper.begin(ShapeType.Line);
				shaper.setColor(1, 0, 1, 1);
				shaper.line(x1, y1, x2, y2);
			}

			// fi 
			y1 = y0 - (int)(scale*record[x1+offset][2]);
			y2 = y0 - (int)(scale*record[x2+offset][2]);
			y1=constrain(y1,sy+10,sy+sH-10);
			y2=constrain(y2,sy+10,sy+sH-10);
			if(myWorld.isGraph) {
				shaper.setColor(1, 0, 0, 1);
				shaper.line(x1, y1, x2, y2);
			}

			// cAcc 
			y1 = y0 - (int)(scale*record[x1+offset][3]);
			y2 = y0 - (int)(scale*record[x2+offset][3]);
			y1=constrain(y1,sy+10,sy+sH-10);
			y2=constrain(y2,sy+10,sy+sH-10);

			if(myWorld.isGraph) {
				shaper.setColor(0, 0, 1, 1);
				shaper.line(x1, y1, x2, y2);
			}

			// mAcc 
			y1 = y0 - (int)(scale*record[x1+offset][4]);
			y2 = y0 - (int)(scale*record[x2+offset][4]);
			y1=constrain(y1,sy+10,sy+sH-10);
			y2=constrain(y2,sy+10,sy+sH-10);
			if(myWorld.isGraph) {
				shaper.setColor(0, 1, 0, 1);
				shaper.line(x1, y1, x2, y2);
				shaper.end();
			}


		}

		if(sW<j) {
			j=0;
			l++;
		}
		j++;
	}

	public void restart() {

		bData[0] = Pendulum.tstart; // blank  start
		bData[1] = Pendulum.tlen; // blank length
	}
	/*
	 * File writer for calibration and test results
	 */
	public void saveResults(String filename) {

		if(!myWorld.isReact) {
			if(myWorld.isBlank) {
				fId = filename + "_" + "B_" + String.format(stringDate)  + "_" + String.format("%.2f",Pendulum.parq) + "_" + String.format("%.2f",Pendulum.del) + "_" +  String.format("%02d", k) + "_" + String.format("%.2f", myWorld.pend.l_n);
			}else {
				fId = filename + "_" + String.format(stringDate) + "_" + String.format("%.2f",Pendulum.parq) + "_" + String.format("%.2f",Pendulum.del) + "_" +  String.format("%02d", k) + "_" + String.format("%.2f", myWorld.pend.l_n);
			}
		}else {
			fId = filename  + "_Reaction_" + String.format(stringDate)  + "_" + String.format("%d", k);
		}

		k++;

		try {
			fW = new FileWriter(String.format("%s.csv", fId));

			if(!myWorld.isReact) {
				fW.append(String.format("Balance results for %s", fId));
				fW.append("\n");
				fW.append("rod_len" + ";" + "q" + ";" + "tau"  );
				fW.append("\n");
				fW.append(myWorld.pend.l_n + ";" + Pendulum.parq + ";" + Pendulum.del);
				fW.append("\n");
//
				fW.append("t_start" + ";" + "t_off");
				fW.append("\n");
				fW.append(bData[0] + ";" + bData[1]);
				fW.append("\n");

				fW.append("t [s]; x1_delayed [m]; L*sin(fi) [m] ; input [m/s^2]; fi [rad]; om [rad/s]; len [m]; disturb");
				fW.append("\n");

				for (int s = is; s < ii; s++) {
					fW.append(record[s][0] + ";" + record[s][1] + ";" + record[s][2] + ";" + record[s][3] + ";"
							+ record[s][4] + ";" + record[s][5]+ ";" + record[s][6]);
					fW.append("\n");
				}
			} else {

				fW.append(String.format("Reaction results for %s", fId));
				fW.append("\n");
				for (int s = 0; s < react.length; s++) {
					fW.append(String.format(" %f;",react[s]));
					fW.append("\n");
				}

			}
			fW.close();

			System.out.println("Results file created succesfully!");

		} catch (IOException e) {

			System.out.println("Results file creation failed!");
			e.printStackTrace();
		}
		
		i = 0;
		record = new double[N][7];
		bData = new double[2];
		j = 0;
		l = 0;

	}

	private int constrain(int y1, int low, int up) {
		if (y1<low) {
			y1=low;
		}
		if(y1>up) {
			y1=up;
		}

		return y1;
	}

}
