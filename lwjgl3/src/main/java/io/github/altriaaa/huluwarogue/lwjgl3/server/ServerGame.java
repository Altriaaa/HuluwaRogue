package io.github.altriaaa.huluwarogue.lwjgl3.server;

import com.badlogic.gdx.Game;
import io.github.altriaaa.huluwarogue.ResourceManager;

import java.io.IOException;

public class ServerGame extends Game
{
    @Override
    public void create()
    {
        try
        {
            this.setScreen(new ServerGameScreen());
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void dispose()
    {
        ResourceManager.getInstance().dispose();
        this.getScreen().dispose();
    }
}
