package io.github.altriaaa.huluwarogue;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game
{
    public SpriteBatch batch;
    public FitViewport viewport;

    @Override
    public void create()
    {
        batch = new SpriteBatch();
        viewport = new FitViewport(1260, 910);
        this.setScreen(new FirstScreen(this));
    }

    public void render()
    {
        super.render();
    }

    public void dispose()
    {
        batch.dispose();
        ResourceManager.getInstance().dispose();
        this.getScreen().dispose();
    }
}
