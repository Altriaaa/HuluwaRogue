package io.github.altriaaa.huluwarogue;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.io.IOException;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends Game
{
//    public SpriteBatch batch;
    public FitViewport viewport;

    @Override
    public void create()
    {
//        batch = new SpriteBatch();
        viewport = new FitViewport(1260, 910);
        this.setScreen(new FirstScreen(this));
    }

    public void startGame(String serverAddress, String playerName)
    {
        System.out.println("Starting game with server: " + serverAddress + ", player: " + playerName);
        try
        {
            setScreen(new GameScreen(this, serverAddress, playerName));
        } catch (IOException e)
        {
            System.out.println("Failed to connect to server: " + e.getMessage());
        }
    }

    public void startReplay(String playerName)
    {
        System.out.println("Replaying...");
        try
        {
            setScreen(new ReplayScreen(this, playerName));
        } catch (Exception e)
        {
            System.out.println("no such record data");
        }
    }

    public void render()
    {
        super.render();
    }

    public void dispose()
    {
//        batch.dispose();
        ResourceManager.getInstance().dispose();
        this.getScreen().dispose();
    }
}
