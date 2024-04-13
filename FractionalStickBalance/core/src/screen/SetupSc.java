package screen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import objects.InputHandler;

public class SetupSc implements Screen {
	private Game game;
	private Stage stage;
	private TextField[] txF = new TextField[15];
	private boolean control = false, anim = false, constLen = true, aspect = true, dpend = false, topdown = false, frac = false;
	private String fileID = "Settings.txt";
	private Label Lab, Lab2;


	public SetupSc(Game g) {

		game=g;
		stage=new Stage();
		Gdx.input.setInputProcessor(stage);
		Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
		Texture LogoT = new Texture(Gdx.files.internal("logo.png"));
		LogoT.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion Logo = new TextureRegion(LogoT,0,0,32,32);

		Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);

		String[] iText= new String[13];
		iText[0]="Rod length [m]:"; // initial rod length
		iText[1]="Added delay [s]:"; // added delay
		iText[2]="q [-]:"; // q | initial 2. rod length
		iText[3]="Rod step [m/s]:"; // step in rod length
		iText[4]="sec"; // end of test or time step for rod change
		iText[5]="Initial disp. [m]:"; // initial disp
		iText[6]="Manip.length [m]:"; // manipulation length
		iText[7]="Filtering :"; // filteri*ng
		iText[8]="Blank-out test [s]:"; // blankout length
		iText[9]="Proportional gain:"; // proportional gain
		iText[10]="Derivative gain:"; // derivative gain
		iText[11]="Subject name :"; // filename
		iText[12]="Port adress :"; // port adress

		String[] inputs= new String[19];
		
		Lab = new Label("",skin);
		Lab2 = new Label("",skin);

		File f=new File(fileID);

		if (f.exists()&&!f.isDirectory()) {

			inputs = loadSettings(fileID);

		}else {
			inputs[0]="5"; // initial rod length
			inputs[1]="0"; // added delay
			inputs[2]="1"; // q
			inputs[3]=".2"; // step in rod length
			inputs[4]="1"; // end test
			inputs[5]=".01"; // initial disp
			inputs[6]=".05"; // manipulation length
			inputs[7]="3"; // filtering
			inputs[8]="0.5"; // blankout length
			inputs[9]="50"; // kP
			inputs[10]="10"; // kD 
			inputs[11]="TEST"; // filanme
			inputs[12]="***"; // port adress
			inputs[13]=".34"; // sw
			inputs[14]=".27"; // sh
			inputs[15]="false"; // use acceleration sensor
			inputs[16]="false"; // use acceleration sensor
			inputs[17]="false"; // use acceleration sensor
			inputs[18]="false"; // use acceleration sensor
		}
		
		//int sWidth = Gdx.graphics.getWidth();
		//int sHeight = Gdx.graphics.getHeight();
		//System.out.printf("screen height:%d", sHeight);

		/*
		 * Buttons
		 */

		createButton("Balance \n Test", 400, 250, 115, 50, 1, skin,stage); // Start Simulation
		createButton("Blank-out \n Test", 400, 190, 115, 50, 5, skin,stage); // Start Simulation
		createButton("Calibrate \n Inputs", 525, 250, 115, 50, 2, skin,stage); // Start Calibration
		createButton("Exit", 400, 130, 240, 50, 3, skin,stage); // Exit
		createButton("Reaction \n Test", 525, 190, 115, 50, 4, skin,stage); // Reaction

		/*
		 * Text fields
		 */
		int col=0;
		for(int i=0;i<13;i++) {
			txF[i]=createTbox(iText[i],inputs[i],240+col*300,580-50*(i-7*col),100,40,skin,stage);
			if(i>5) {
				col=1;
			}
		}

		txF[13]=createTbox("Screen width [m]",inputs[13],850,250,100,40,skin,stage);
		txF[14]=createTbox("Screen height [m]",inputs[14],850,190,100,40,skin,stage);

		/*
		 * Misc.
		 */

		String txt1 = "Enhanced mouse precision should be\n"
				+ "turned off in system settings\n"
				+ "for precise measurment.\n"
				+ "Button commands during balance test:\n"
				+ "- 'S': Start test\n"
				//+ "- 'B': Start test with blank-out\n"
				//+ "- 'R': Reset test\n"
				+ "- 'N': Next/Reset test\n"
				+ "- 'K': Decrease length\n"
				+ "- 'I': Increase length\n"
				+ "- 'P': Perturb (kick at pivot point)\n"
				+ "- 'H': Help";
		Label helplbL = new Label(txt1, skin);
		helplbL.setPosition(700, 380);
		helplbL.setColor(0.1f,0.1f, 0.1f, 1);
		stage.addActor(helplbL);

		// checkboxes

		Label lbL = new Label("Options:", skin);
		lbL.setPosition(130, 230);
		lbL.setSize(100, 10);
		lbL.setColor(0.1f,0.1f, 0.1f, 1);
		stage.addActor(lbL);

		anim=false;
		txF[9].setVisible(anim);
		txF[10].setVisible(anim);
		final CheckBox checkBox1 = new CheckBox("Animate control (PD w. delay)", skin);
		checkBox1.setChecked(anim);
		checkBox1.setPosition(130, 195);
		checkBox1.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				anim = checkBox1.isChecked();
				txF[9].setVisible(anim);
				txF[10].setVisible(anim);
			}
		});
		stage.addActor(checkBox1);

		// Accelerometer control
		//control=Boolean.parseBoolean(inputs[16]);
		control=false;
		final CheckBox checkBox2 = new CheckBox(" Use accelerometer", skin);
		checkBox2.setChecked(control);
		checkBox2.setPosition(130, 175);
		checkBox2.setDisabled(true);
		checkBox2.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				control = checkBox2.isChecked();

			}
		});
		stage.addActor(checkBox2);

		constLen=Boolean.parseBoolean(inputs[15]);
		if(constLen) {
			Lab2.setText("Overall ST [mins]:");
		}else {

			Lab2.setText("Time [s]:");
		}
		final CheckBox checkBox3 = new CheckBox("Keep initial length", skin);
		checkBox3.setChecked(constLen);
		checkBox3.setPosition(130, 155);
		checkBox3.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				constLen = checkBox3.isChecked();
				if(constLen) {
					Lab2.setText("Overall ST [mins]:");
				}else {

					Lab2.setText("Time [s]:");
				}

			}
		});
		stage.addActor(checkBox3);

		aspect=Boolean.parseBoolean(inputs[16]);
		final CheckBox checkBox4 = new CheckBox("Fit height", skin);
		checkBox4.setChecked(aspect);
		checkBox4.setPosition(130, 135);
		checkBox4.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				aspect = checkBox4.isChecked();

			}
		});
		stage.addActor(checkBox4);
		
		dpend=false;//Boolean.parseBoolean(inputs[15]);
		final CheckBox checkBox5 = new CheckBox("Double Pendulum", skin);
		checkBox5.setChecked(dpend);
		checkBox5.setPosition(130, 115);
		checkBox5.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				dpend = checkBox5.isChecked();
				aspect = true;
				checkBox4.setChecked(aspect);
				constLen = true;
				checkBox3.setChecked(constLen);
				if(dpend) {
					Lab.setText("2. Rod length [m]:");
				}else {
					Lab.setText("q [-]:");
				}

			}
		});
		stage.addActor(checkBox5);
		
		topdown=Boolean.parseBoolean(inputs[17]);
		final CheckBox checkBox6 = new CheckBox("Top-down view", skin);
		checkBox6.setChecked(topdown);
		checkBox6.setPosition(130, 95);
		checkBox6.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				topdown = checkBox6.isChecked();

			}
		});
		stage.addActor(checkBox6);
		

		frac=Boolean.parseBoolean(inputs[18]);
		//frac=false;
		final CheckBox checkBox7 = new CheckBox("Modified 'q'", skin);
		checkBox7.setChecked(frac);
		checkBox7.setVisible(true);
		checkBox7.setPosition(130, 75);
		checkBox7.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				frac = checkBox7.isChecked();

			}
		});
		stage.addActor(checkBox7);

		// label && logo
		Label lbCr = new Label(
				"Credits: \n" + "KovB 2017 @ LibGDX \n" + "MTA-BME Lendulet \n" + "Human Balancing Research Group", skin);
		lbCr.setPosition(760, 20);
		lbCr.setSize(250, 40);
		lbCr.setColor(0.5f, 0.5f, 1f, 0.5f);
		stage.addActor(lbCr);

		Image imlogo = new Image(Logo);
		imlogo.setPosition(700, 5);
		imlogo.setSize(50, 50);
		imlogo.setColor(1, 1, 1, 1);
		stage.addActor(imlogo);

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.6f, 0.6f, 1f, 0.4f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {

	}

	/*
	 *  UI objects create
	 */

	private TextField createTbox(String label, String text, int x, int y, int w, int h, Skin skin,Stage stage) {
		TextField txfL = new TextField(text, skin);
		txfL.setPosition(x, y);
		txfL.setSize(w, h);
		stage.addActor(txfL);
		Label lbL = new Label(label, skin);
		lbL.setPosition(x - 140, y);
		lbL.setSize(w - 50, h);
		if(label.equals("q [-]:")) {
			Lab=lbL;
		}
		if(label.equals("sec")) {
			Lab2=lbL;
		}
		stage.addActor(lbL);
		return txfL;
	}


	private void createButton(String text, int x, int y, int w, int h, final int type, Skin skin,Stage stage) {

		TextButton btn = new TextButton(text, skin);
		btn.setPosition(x, y);
		btn.setSize(w, h);

		switch (type) {
		case 1: // Start Simulation
			btn.setColor(0f, 1f, 0f, 1f);
			btn.addListener(new ClickListener() {
				@Override
				public void touchUp(InputEvent e, float x, float y, int point, int button) {
					btnPressed(false);
				}
			});
			break;
		case 2: // Start Calibration
			btn.setColor(0f, 0f, 1f, 1f);
			btn.addListener(new ClickListener() {
				@Override
				public void touchUp(InputEvent e, float x, float y, int point, int button) {
					btnCalPressed();
				}
			});
			break;
		case 3: // Exit Program
			btn.setColor(1f, 0f, 0f, 1f);
			btn.addListener(new ClickListener() {
				@Override
				public void touchUp(InputEvent e, float x, float y, int point, int button) {

					Gdx.app.exit();
				}
			});
			break;
		case 4: // Reaction test
			btn.setColor(0f, 1f, .7f, 1f);
			btn.addListener(new ClickListener() {
				@Override
				public void touchUp(InputEvent e, float x, float y, int point, int button) {
					btnReactPressed();
				}
			});
			break;
		case 5: // Start Blank out
			btn.setColor(0f, 1f, .7f, 1f);
			btn.addListener(new ClickListener() {
				@Override
				public void touchUp(InputEvent e, float x, float y, int point, int button) {
					btnPressed(true);
				}
			});
			break;

		}
		stage.addActor(btn);
	}

	private void btnPressed(boolean blank) {
		int n=txF.length;
		double[] Data = new double[n];
		String[] inputData = new String[n];
		for(int i =0;i<n;i++) {
			if(i<n-4) {
				Data[i]= Double.parseDouble(txF[i].getText());				
			}
			inputData[i]=txF[i].getText();
		}
		Data[11]=Double.parseDouble(txF[13].getText());
		Data[12]=Double.parseDouble(txF[14].getText());

		InputHandler.setFilename(txF[11].getText());

		saveSettings(inputData, constLen, aspect, topdown, frac);

		game.setScreen(new SimulationSc(game, Data, anim, control,constLen, aspect, blank, txF[12].getText(),dpend, topdown, frac));
	}

	private void btnCalPressed() {
		int n=txF.length;
		double[] Data = new double[n];
		String[] inputData = new String[n];
		for(int i =0;i<n;i++) {
			if(i<n-4) {
				Data[i]= Double.parseDouble(txF[i].getText());				
			}
			inputData[i]=txF[i].getText();
		}
		Data[11]=Double.parseDouble(txF[13].getText());
		Data[12]=Double.parseDouble(txF[14].getText());

		InputHandler.setFilename(txF[11].getText());

		saveSettings(inputData, constLen, aspect, topdown, frac);

		game.setScreen(new CalibrationSc(game,Data,anim,control,txF[12].getText()));
	}

	private void btnReactPressed() {
		int n=txF.length;
		double[] Data = new double[n];
		String[] inputData = new String[n];
		for(int i =0;i<n;i++) {
			if(i<n-4) {
				Data[i]= Double.parseDouble(txF[i].getText());				
			}
			inputData[i]=txF[i].getText();
		}
		Data[11]=Double.parseDouble(txF[13].getText());
		Data[12]=Double.parseDouble(txF[14].getText());

		InputHandler.setFilename(txF[11].getText());

		saveSettings(inputData, constLen, aspect, topdown, frac);

		game.setScreen(new ReactionSc(game, Data, anim, control,constLen, aspect,txF[12].getText()));
	}

	/*
	 * File writer for calibration and test results 
	 * read/write
	 * ______________________________________________________________________________________________________
	 */
	private String[] loadSettings(String fn) {

		String[] datas=new String[20];

		BufferedReader reader;
		try{
			reader= new BufferedReader(new FileReader(fn));
			String line=reader.readLine();
			datas[0]=line;
			int jj=0;
			//Gdx.app.log(String.format("%d", jj), line);
			while(line!=null&&jj<datas.length) {
				line=reader.readLine();
				jj+=1;
				datas[jj]=line;

				//Gdx.app.log(String.format("%d", jj), line);
			}
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
		}

		return datas;
	}

	public void saveSettings(String[] idatas, boolean control2,boolean control3,boolean control4,boolean control5) {

		try {
			FileWriter fileWriter = new FileWriter(String.format("%s", fileID));
			// fileWriter.append(String.format("Balance %s", fileId));
			for (int ii = 0; ii < idatas.length; ii++) {
				fileWriter.append(idatas[ii]);
				fileWriter.append("\n");
			}

			fileWriter.append(String.format("%b", control2));
			fileWriter.append("\n");
			fileWriter.append(String.format("%b", control3));
			fileWriter.append("\n");
			fileWriter.append(String.format("%b", control4));
			fileWriter.append("\n");
			fileWriter.append(String.format("%b", control5));
			fileWriter.append("\n");

			fileWriter.close();

			System.out.println("Settings file created succesfully!");

		} catch (IOException e) {

			System.out.println("Settings file creation failed!");
			e.printStackTrace();
		}

	}
}
