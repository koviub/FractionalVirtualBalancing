package screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;

import objects.InputHandler;
import world.Renderer;
import world.World;

public class SimulationSc implements Screen{

	private World world;
	private Renderer rend;
	private boolean cont, frac;
	private float runTime;

	public SimulationSc(Game game, double[] data, boolean anim, boolean control, boolean cLen, boolean aspect, boolean blank, String comPort, boolean dpend, boolean topdown, boolean fr) {

		int sWidth = Gdx.graphics.getWidth();
		int sHeight = Gdx.graphics.getHeight();

		cont = control;
		frac=fr;
		world = new World(sWidth,sHeight,data,anim, cont, cLen,false,blank,false,comPort,dpend,frac);
		rend = new Renderer(world,data[11],data[12],sWidth,sHeight,aspect,topdown,data[4]);

		Gdx.input.setInputProcessor(new InputHandler(game, world, sWidth, sHeight));
		Pixmap pm = new Pixmap(Gdx.files.internal("cc.png"));
		Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
		pm.dispose();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {

		if(cont) {
			Gdx.gl.glClearColor(1f, 0.6f, 0.6f, 0.4f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		}else if(!frac) {
			Gdx.gl.glClearColor(0.6f, 0.6f, 1f, 0.4f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			}else{
			Gdx.gl.glClearColor(0.5f, .5f, 1f, 0.4f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			}

		runTime += delta;

		if(Math.abs(delta-(float)(1/60.))>10) {
			delta = (float)(1/60.);
		}

		world.update((float)(1/60.),runTime);
		rend.rendSim(delta, runTime);
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
		// TODO Auto-generated method stub

	}

}
