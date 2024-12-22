package io.github.altriaaa.huluwarogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.*;

public class ReplayScreen implements Screen
{
    private final Main game;
    private GameWorld world;
    private String recordName;
    int curFrame;
    private BufferedReader reader;
    private boolean finished;
    Json json;

    public ReplayScreen(final Main game, String playerName)
    {
        this.game = game;
        this.recordName = "saves/record_" + playerName + ".log";
        world = GameWorld.getInstance();
        world.assetInit();
        world.worldInit();

        json = new Json();
        try
        {
            reader = new BufferedReader(new FileReader(recordName));
            reader.readLine();
            String mapData = reader.readLine();
            world.refreshMap(json.fromJson(int[][].class, mapData));
            reader.readLine();
        } catch (IOException e)
        {
            e.printStackTrace();
            finished = true;
        } catch (NullPointerException e)
        {
            System.out.println("No record data.");
            finished = true;
        }

        curFrame = 0;
    }

    @Override
    public void show()
    {
    }

    @Override
    public void render(float delta)
    {
        if (finished) dispose();

        ScreenUtils.clear(Color.BLACK);
        try
        {
            String gameData = reader.readLine();
            if (gameData == null)
            {
                finished = true;
                return;
            }
            GameStat gameStat = json.fromJson(GameStat.class, gameData);
            world.buildFromGameStat(gameStat);
        } catch (IOException e)
        {
//            e.printStackTrace();
            finished = true;
        } catch (NullPointerException e)
        {
            System.out.println("error");
            finished = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
        {
            dispose();
        }

        world.getStage().draw();
    }

    @Override
    public void resize(int width, int height)
    {
        GameWorld.getInstance().viewport.update(width, height, true);
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void dispose()
    {
        try
        {
            if (reader != null)
            {
                reader.close();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        world.clear();
        game.setScreen(new FirstScreen(game));
    }
}
