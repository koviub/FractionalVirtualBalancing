package screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import objects.InputHandler;
import world.Renderer;
import world.World;

public class ReactionSc implements Screen {
	private World world;
	private Renderer rend;
	private float runTime;

	public ReactionSc(Game game, double[] data, boolean anim, boolean control, boolean cLen, boolean aspect, String comPort) {

		int sWidth = Gdx.graphics.getWidth();
		int sHeight = Gdx.graphics.getHeight();

		world = new World(sWidth,sHeight,data,anim, false, cLen,false,false,true,comPort,false,false);
		rend = new Renderer(world,data[11],data[12],sWidth,sHeight,aspect,false,0);

		Gdx.input.setInputProcessor(new InputHandler(game, world, sWidth, sHeight));

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1f, 1f, 1f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		runTime += delta;

		world.update(delta,runTime);
		rend.rendReact(delta, runTime);
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
