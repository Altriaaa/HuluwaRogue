package io.github.altriaaa.huluwarogue.lwjgl3.server;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.altriaaa.huluwarogue.GameWorld;
import io.github.altriaaa.huluwarogue.network.GameServer;

import java.io.IOException;

public class ServerGameScreen implements Screen
{
    private GameWorld world;
    private GameServer server;

    public ServerGameScreen() throws IOException
    {
        server = new GameServer(12345);
        // 启动 GameServer 在一个单独的线程中运行
        Thread serverThread = new Thread(() ->
        {
            try
            {
                server.start();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        });
        serverThread.setDaemon(true); // 设置为守护线程，以便在应用退出时自动关闭
        serverThread.start();

        world = GameWorld.getInstance();
        world.assetInit();
        world.worldInit();
    }

    @Override
    public void show()
    {
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
        world.update(delta);
        world.getStage().draw();
        Json json = new Json();
        String jsondata = json.toJson(world.getKnight());
        try
        {
            server.broadcastMessage(jsondata);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void hide()
    {
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
    public void dispose()
    {
        world.clear();
    }

}
