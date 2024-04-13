package world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;


import objects.Pendulum;
import objects.Results;

public class Renderer {

	private World myWorld;
	private int gWidth, gHeight;
	private OrthographicCamera cam;
	private ShapeRenderer shaper;
	private SpriteBatch batcher;
	private static BitmapFont font;
	public double wx=0.34, wy=0.27, eT;
	private float[] black= {0,0,0}, white= {1,1,1}, red = {1,0,0}, green= {0,1,0};
	private boolean aspect, td;

	public Renderer(World world, int gameW, int gameH) {
		myWorld = world;
		this.gWidth=gameW;
		this.gHeight=gameH;

		cam = new OrthographicCamera();
		cam.setToOrtho(true,gameW,gameH);
		batcher = new SpriteBatch();
		batcher.setProjectionMatrix(cam.combined);
		shaper = new ShapeRenderer();
		shaper.setProjectionMatrix(cam.combined);
		font = new BitmapFont(Gdx.files.internal("default.fnt"),true);


	}

	public Renderer(World world, double rWidth, double rHeight, int gameW, int gameH, boolean asp, boolean topdown, double endTest) {
		myWorld = world;
		this.gWidth=gameW;
		this.gHeight=gameH;
		aspect = asp;
		td = topdown;
		wx=rWidth;
		wy=rHeight;
		eT = endTest;
		myWorld.wx=wx;

		cam = new OrthographicCamera();
		cam.setToOrtho(true,gameW,gameH);
		batcher = new SpriteBatch();
		batcher.setProjectionMatrix(cam.combined);
		shaper = new ShapeRenderer();
		shaper.setProjectionMatrix(cam.combined);
		font = new BitmapFont(Gdx.files.internal("default.fnt"),true);


	}

	public void rendReact(float delta,float runTime) {
		if(myWorld.isReady()) {
			drawText(" \"Press 'S' to start test, Press 'Q' to exit... ",black);
		}
		drawFps(delta, runTime,black);
		drawReact2();
		shaper.begin(ShapeType.Line);
		shaper.setColor(0, 0, 0, 1);
		shaper.rect(gWidth/2, gHeight/2, 150, 150);
		shaper.end();
		shaper.begin(ShapeType.Filled);
		if(myWorld.react.isBlink) {
			shaper.setColor(1f, 0, 0, 1);
		}else {
			shaper.setColor(.8f, .8f, .8f, 1);
		}
		shaper.rect(gWidth/2+1, gHeight/2+1, 148, 148);
		shaper.end();
	}

	public void rendSim(float delta, float runTime) {

		drawGraph();
		drawFps(delta, runTime,white);
		drawHelp();
		drawReact();
		if(World.dp) {
			drawScale();
			if(td) {
				drawPend_TopDown(runTime);
			}else {
				drawPend(runTime);
			}

		}else {

			drawDPend(runTime);
		}

		if(myWorld.isReady()) {
			if(myWorld.pend.l_n<=0){
				drawText(" \n \n Zero|Negative pendulum length! Press 'I' to increase value...",red);
			}
			if(myWorld.pend.time==0) {
				drawText("Press 'S' to start...",white);
			}else {
				if(myWorld.isBlank) {
					if(Results.k<9) {
						drawText(" Press 'N' to progress, Press 'Q' to exit...\n"+String.format("%d tries left", 9-Results.k),white);
					}else {
						drawText(" End of test, Press 'Q' to exit...",white);
					}
				}else {

					if((!Pendulum.constLen&&myWorld.pend.time<World.limittime&&myWorld.pend.stepl>World.limitstep)||(Pendulum.constLen&&myWorld.pend.overall_time/60<eT)) {
						if(Pendulum.constLen) {
							drawText(" Press 'N' to progress, Press 'Q' to exit...\n" +String.format("%.3f seconds of balance time left", eT*60-myWorld.pend.overall_time),white);
						}else {
							drawText(" Press 'N' to progress, Press 'Q' to exit...\n" ,white);
						}

					}else {
						if (myWorld.pend.stepl>World.limitstep) {
							drawText(" Press 'N' to progress, Press 'Q' to exit...\n" ,white);
						}else {
							if (!(myWorld.pend.time>World.limittime)) {
								drawText(" Press 'N' to progress, Press 'Q' to exit...\n" ,white);
							}else {
								
								drawText(" End of test, Press 'Q' to exit...",white);
							}
						}
					}

				}
				if (!myWorld.isBlank) {
				boolean cond1=(myWorld.pend.l_n<=0)&&myWorld.pend.stepl>World.limitstep||myWorld.pend.time<World.limittime; 
				boolean cond2=myWorld.pend.overall_time/60<eT;
				if((!Pendulum.constLen&&cond1)||(Pendulum.constLen&&cond2)) {
					drawText(" \n \n Failed!",red);
					myWorld.pend.isFailed = true;
				}else{
					drawText("\n \n Success!",green);
					myWorld.pend.isFailed = false;
				}
				}
			}
		}

	}

	public void rendCal(float delta, float runTime) {
		drawGraph();
		drawFps(delta, runTime, black);
		drawHelp();
	}

	private void drawGraph() {
		if (myWorld.isCalibr()) {
			myWorld.isGraph = true;
			myWorld.res.plotter(shaper, gWidth, gHeight,0,0);
			batcher.begin();
			batcher.enableBlending();
			font.setColor(1, 0, 1, 1);
			font.draw(batcher, "position:" + String.format("%d", myWorld.pend.x1_del), (int)(gWidth*.8), (int)(gHeight*5/7));
			font.setColor(0, 1, 0, 1);
			font.draw(batcher, "measured acc.:" + String.format("%f", myWorld.pend.fAcc), (int)(gWidth*.8), (int)(gHeight*5/7)+20);
			font.setColor(0, 0, 1, 1);
			font.draw(batcher, "calculated acc.:" + String.format("%f", myWorld.pend.cAcc), (int)(gWidth*.8), (int)(gHeight*5/7)+40);
			batcher.end();
			if (myWorld.isTest) {
				shaper.begin(ShapeType.Filled);
				shaper.setColor(0.0f / 255.0f, 0 / 255.0f, 0 / 255.0f, 0);
				shaper.rect(5*gWidth /6, 5*gHeight/6, gWidth/6, gHeight/6);
				shaper.end();
			}
		} else {
			if(myWorld.isGraph) {
				batcher.begin();
				batcher.enableBlending();
				font.setColor(1, 0, 1, 1);
				font.draw(batcher, "position:" + String.format("%d", myWorld.pend.x1_del), (int)(gWidth*.7),(int)(gHeight*4/7));
				font.setColor(1, 0, 0, 1);
				font.draw(batcher, "pendulum angle:" + String.format("%f", myWorld.pend.getSolu()), (int)(gWidth*.7),(int)(gHeight*4/7)+20);
				font.setColor(0, 1, 0, 1);
				font.draw(batcher, "measured acc.:" + String.format("%f", myWorld.pend.fAcc), (int)(gWidth*.7), (int)(gHeight*4/7)+40);
				font.setColor(0, 0, 1, 1);
				font.draw(batcher, "calculated acc.:" + String.format("%f", myWorld.pend.cAcc), (int)(gWidth*.7), (int)(gHeight*4/7)+60);
				batcher.end();
				myWorld.res.plotter(shaper, (int)(gWidth*.3), (int)(gHeight*3/7)-80,(int)(gWidth*.7),(int)(gHeight*4/7)+80);
			}
		}
	}

	private void drawScale() {
		if(!Pendulum.constLen) {
			shaper.begin(ShapeType.Line);
			shaper.setColor(0f, 0f, 0f, 1);
			shaper.rect(50, gHeight/2-200, 20, 400);
			shaper.line(50, gHeight/2, 65, gHeight/2);
			shaper.line(50, gHeight/2-100, 60, gHeight/2-100);
			shaper.line(50, gHeight/2+100, 60, gHeight/2+100);
			shaper.end();
			shaper.begin(ShapeType.Filled);
			shaper.setColor(0.2f, 0.2f, 0.2f, 1);
			shaper.rect(50,(int)(gHeight/2-200+(Pendulum.len-myWorld.pend.l_n)*400/Pendulum.len), 20, (int)(400-(Pendulum.len-myWorld.pend.l_n)*400/Pendulum.len));
			shaper.end();

			batcher.begin();
			batcher.enableBlending();
			font.setColor(0,0,0, 1);
			font.draw(batcher, String.format("%.2f [m]", Pendulum.len), 75, gHeight/2-200);
			font.draw(batcher, "0 [m]", 75, gHeight/2+185);
			batcher.end();
		}
		if(Pendulum.anim) {
			batcher.begin();
			batcher.enableBlending();
			font.setColor(0,0,0, 1);
			font.draw(batcher, String.format("PD controller \n Gains:\n kP=%1$.1f, kD=%2$.1f", Pendulum.kP,Pendulum.kD), 45, (int)(gHeight*6/7+30));
			batcher.end();
		}
	}

	private void drawPend(float runTime) {
		// Calculate points
		double modx =  wx/gWidth;
		double mody = wy/gHeight;
		if(aspect) {
			// modx = myWorld.pend.l_n/(18*gHeight/20);
			mody = myWorld.pend.l_n/(18*gHeight/20);
		}

		int x1 = myWorld.pend.x1_del;
		int x2 = x1+(int)(myWorld.pend.l_n/modx*Math.sin(myWorld.pend.getSolu()));
		int y1 = 19*gHeight/20;
		int y2 = y1-(int)(myWorld.pend.l_n/mody*Math.cos(myWorld.pend.getSolu()));

		// draw cart
		shaper.begin(ShapeType.Filled);
		shaper.setColor(255.0f / 255.0f, 255 / 255.0f, 10 / 255.0f, 1);
		shaper.rect(x1 - 25, y1 - 10, 50, 20);
		shaper.end();

		// draw rod
		if (Pendulum.constLen && myWorld.isBlank && Pendulum.tstart <= myWorld.pend.time && (myWorld.pend.time < Pendulum.tstart + Pendulum.tlen)
				) {
			/*
			 *  blank screen 
			 */		
		} else {
			shaper.begin(ShapeType.Filled);
			shaper.setColor(0, 0, 0, 1);
			shaper.rectLine(x1, y1, x2, y2, 5f);
			shaper.end();

			shaper.begin(ShapeType.Filled);
			shaper.setColor(0, 0, 0, 1);
			shaper.circle(x2, y2, 10);
			shaper.end();
		}

	}

	private void drawPend_TopDown(float runTime) {
		// Calculate points
		double modx =  wx/gWidth;
		if(aspect) {
			// modx = myWorld.pend.l_n/(18*gHeight/20);
		}

		int x1 = myWorld.pend.x1_del;
		int x2 = x1+(int)(myWorld.pend.l_n/modx*Math.sin(myWorld.pend.getSolu()));
		int y1 = 10*gHeight/20;
		int y2 = 10*gHeight/20;;

		// draw cart
		shaper.begin(ShapeType.Filled);
		shaper.setColor(255.0f / 255.0f, 255 / 255.0f, 10 / 255.0f, 1);
		shaper.rect(x1 - 25, y1 - 10, 50, 20);
		shaper.end();

		// draw rod
		if (Pendulum.constLen && myWorld.isBlank && Pendulum.tstart <= myWorld.pend.time && (myWorld.pend.time < Pendulum.tstart + Pendulum.tlen)
				) {
			/*
			 *  blank screen 
			 */		
		} else {
			shaper.begin(ShapeType.Filled);
			shaper.setColor(0, 0, 0, 1);
			shaper.rectLine(x1, y1, x2, y2, 5f);
			shaper.end();

			shaper.begin(ShapeType.Filled);
			shaper.setColor(1, 0, 0, 1);
			shaper.circle(x2, y2, 10);
			shaper.end();
		}

	}

	private void drawDPend(float runTime) {
		// Calculate points
		double modx =  wx/gWidth;
		double mody = wy/gHeight;
		if(aspect) {
			//modx = myWorld.pend.l_n/(18*gHeight/20);
			mody = myWorld.pend.l_n/(18*gHeight/20);
		}

		int x1 = myWorld.pend.x1_del;
		int x2 = x1 + (int)(Pendulum.len/modx*Math.sin(myWorld.pend.getSoluDP()[0]));
		int y1 = 19*gHeight/20;
		int y2 = y1 - (int)(Pendulum.len/mody*Math.cos(myWorld.pend.getSoluDP()[0]));
		int x3 = x2 + (int)(Pendulum.len2/modx*Math.sin(myWorld.pend.getSoluDP()[0]+myWorld.pend.getSoluDP()[1])); 
		int y3 = y2 - (int)(Pendulum.len2/mody*Math.cos(myWorld.pend.getSoluDP()[0]+myWorld.pend.getSoluDP()[1]));


		// draw cart
		shaper.begin(ShapeType.Filled);
		shaper.setColor(255.0f / 255.0f, 255 / 255.0f, 10 / 255.0f, 1);
		shaper.rect(x1 - 25, y1 - 10, 50, 20);
		shaper.end();

		// draw rod
		if (Pendulum.constLen && myWorld.isBlank && Pendulum.tstart <= myWorld.pend.time && (myWorld.pend.time < Pendulum.tstart + Pendulum.tlen)
				) {
			/*
			 *  blank screen 
			 */		
		} else {
			shaper.begin(ShapeType.Filled);
			shaper.setColor(0, 0, 0, 1);
			shaper.rectLine(x1, y1, x2, y2, 5f);
			shaper.end();

			shaper.begin(ShapeType.Filled);
			shaper.setColor(0, 0, 0, 1);
			shaper.rectLine(x2, y2, x3, y3, 5f);
			shaper.end();

			shaper.begin(ShapeType.Filled);
			shaper.setColor(0, 0, 0, 1);
			shaper.circle(x2, y2, 10);
			shaper.end();

			shaper.begin(ShapeType.Filled);
			shaper.setColor(0, 0, 0, 1);
			shaper.circle(x3, y3, 10);
			shaper.end();
		}

	}

	private void drawText(String txt,float[] color) {
		batcher.begin();
		batcher.enableBlending();
		font.setColor(color[0], color[1], color[2], 1);
		font.draw(batcher, txt, (int)(gWidth*.35), (int)(gHeight*6/7));
		batcher.end();
	}

	public void drawHelp() {

		if(myWorld.isHelp) {
			String txt1 = "Button commands:\n"
					+ "'S' - Start test\n"
					//+ "'B' - Start test with blank-out\n"
					//+ "'R' - Reset test\n"
					+ "'N' - Next/Reset test\n"
					+ "'K' - Decrease length\n"
					+ "'I' - Increase length\n"
					+ "'P' - Perturb (kick at pivot point)\n"
					+ "'H' - Help";
			batcher.begin();
			batcher.enableBlending();
			font.setColor(.1f,.1f,.1f, 1);
			font.draw(batcher, txt1, 3*gWidth/4, gHeight/2+200);
			batcher.end();
		}
	}

	public void drawReact2() {

		if(myWorld.isReact) {
			String txt1 = String.format("Reaction time:\n %d : %d ms",(int)(1000*myWorld.react.av_time),(int)(1000*myWorld.react.std_time));
			String txt2 = String.format("\n \n %d",myWorld.react.k+1);
			batcher.begin();
			batcher.enableBlending();
			font.setColor(.0f,.1f,.6f, 1);
			font.draw(batcher, txt1, 3*gWidth/4, gHeight/2+200);
			font.draw(batcher, txt2, 3*gWidth/4, gHeight/2+200);
			batcher.end();
		}
	}

	public void drawReact() {

		if(myWorld.isFirst) {
			String txt1 = String.format("Estimated reaction time:\n %d ms",(int)(1000*myWorld.pend.react));
			batcher.begin();
			batcher.enableBlending();
			font.setColor(.0f,.1f,.6f, 1);
			font.draw(batcher, txt1, 3*gWidth/4, gHeight/2+200);
			batcher.end();
		}
	}

	private void drawFps(float delta, float runTime,float[] color) {
		int fps = (int) (1 / delta);
		batcher.begin();
		batcher.enableBlending();
		font.setColor(color[0], color[1], color[2], 1);
		font.draw(batcher, String.format("%d", fps) + "FPS", 50, (int)(gHeight*6/7));
		if (!myWorld.isCalibr()) {
			if(!myWorld.isReact) {
				font.setColor(1,1,.5f, 1);
				font.draw(batcher, String.format("%d [ms]",(int) (1000*Pendulum.del)), 45, gHeight/2+205);

				font.draw(batcher, String.format("Last round shortest length:\n%.2f [m]",myWorld.pend.critLen), gWidth-205, gHeight/2-250);

				if(World.dp) {
					if(myWorld.isBlank) {
						font.draw(batcher, String.format("\n\n  %d",Results.k+1), 45, gHeight/2+225);
					}else {
						font.draw(batcher, String.format("\n\n  %d",myWorld.pend.numrestart+1), 45, gHeight/2+225);
					}
					if(!Pendulum.constLen) {
						font.draw(batcher, String.format("\n / %d [mm/s]",(int)(1000*myWorld.pend.stepl)), 45, gHeight/2+225);
					}
					font.draw(batcher, String.format("%.2f [m]",myWorld.pend.l_n), 45, gHeight/2+225);
				}

			}
		}
		batcher.end();

		if (myWorld.isCalibr()) {
			batcher.begin();
			batcher.enableBlending();
			font.setColor(color[0], color[1], color[2], 1);
			font.draw(batcher, String.format("t: %f [s]", runTime), 50, 50);
			batcher.end();
		} else {
			batcher.begin();
			batcher.enableBlending();
			font.setColor(color[0], color[1], color[2], 1);

			font.draw(batcher, String.format("t: %f [s]", myWorld.pend.time), 50, 50);

			batcher.end();
		}

	}

}
