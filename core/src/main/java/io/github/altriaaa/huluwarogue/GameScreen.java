package io.github.altriaaa.huluwarogue;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.altriaaa.huluwarogue.creatures.*;
import io.github.altriaaa.huluwarogue.network.GameClient;
import io.github.altriaaa.huluwarogue.tiles.Obstacle;
import io.github.altriaaa.huluwarogue.tiles.Square;
// import sun.font.CreatedFontTracker;
// import sun.tools.jstat.Jstat;

import java.io.IOException;
import java.util.Random;

public class GameScreen implements Screen, GameClient.MessageListener
{
    final Main game;
    GameWorld world;
    private GameClient client;
    private Control control;

    public GameScreen(final Main game) throws IOException
    {
        this.game = game;
        control = new Control();

        world = GameWorld.getInstance();
        world.assetInit();
        world.worldInit();

        client = new GameClient("localhost", 12345);
        client.setMessageListener(this);
        // 启动 GameClient 在一个单独的线程中运行
        Thread clientThread = new Thread(() ->
        {
            try
            {
                client.start();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        });
        clientThread.setDaemon(true); // 设置为守护线程，以便在应用退出时自动关闭
        clientThread.start();
//        world.run();
    }

    @Override
    public void onMessageReceived(String message)
    {
        Gdx.app.postRunnable(()->
        {
//            System.out.println("Message from server: " + message);
            Json json = new Json();
            GameStat gameStat = json.fromJson(GameStat.class, message);
            world.buildFromGameStat(gameStat);
        });
    }

    @Override
    public void show()
    {
//        music.play();
    }

    @Override
    public void resize(int width, int height)
    {
        GameWorld.getInstance().viewport.update(width, height, true);
    }

    @Override
    public void render(float delta)
    {
        ScreenUtils.clear(Color.BLACK);
//        world.update(delta);
//        System.out.println(world.getKnight().getState());
        control.set();
        Json json = new Json();
        String jsonData = json.toJson(control);
        try
        {
            client.send(jsonData);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        world.getStage().draw();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose()
    {
        world.clear();
    }
}
