package io.github.altriaaa.huluwarogue.lwjgl3.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.altriaaa.huluwarogue.Control;
import io.github.altriaaa.huluwarogue.GameStat;
import io.github.altriaaa.huluwarogue.GameWorld;
import io.github.altriaaa.huluwarogue.creatures.Knight;
import io.github.altriaaa.huluwarogue.network.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ServerGameScreen implements Screen, GameServer.MessageListener
{
    private GameWorld world;
    private GameServer server;
    private float sendGap;
    private Map<String, String> clientID;   // address => Id

    public ServerGameScreen() throws IOException
    {
        clientID = new HashMap<>();
        server = new GameServer(12345);
        server.setMessageListener(this);
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
        sendGap = 0;
    }

    @Override
    public void onClientConnected(String remoteAddr)
    {
        Gdx.app.postRunnable(() ->
            {
                System.out.println(remoteAddr + "connected.");
            }
        );
    }

    @Override
    public void onClientDisconnected(String remoteAddr)
    {
        Gdx.app.postRunnable(() ->
            {
                String Id = clientID.get(remoteAddr);
                world.removeKnightById(Id);
            }
        );
    }

    @Override
    public void onMessageReceived(String message, String remoteAddr)
    {
        Gdx.app.postRunnable(() ->
        {
//            System.out.println("Message from client: " + message);
            Json json = new Json();
            JsonValue jsonValue = new JsonReader().parse(message);
            String type = jsonValue.getString("type");

            if ("control".equals(type))
            {
                ControlMessage controlMsg = json.fromJson(ControlMessage.class, message);
                String Id = clientID.get(remoteAddr);
                Knight knight = world.getKnightById(Id);
                if (knight != null)
                {
                    knight.setControl(controlMsg.control);
                }
            }
            else if ("request".equals(type))
            {
                RequestMessage requestMsg = json.fromJson(RequestMessage.class, message);
                switch (requestMsg.kind)
                {
                    case LOAD:
                    {
                        world.load();
                        break;
                    }
                    case SAVE:
                    {
                        world.save();
                        break;
                    }
                    case EXIT:
                    {
                        world.removeKnightById(clientID.get(remoteAddr));
                    }
                }
            }
            else if("setup".equals(type))
            {
                SetupMessage setupMsg = json.fromJson(SetupMessage.class, message);
                if(world.isPaused()) world.resume();
                MapStatMessage mapStatMessage = new MapStatMessage();
                mapStatMessage.data = world.getMap();
//                Json json = new Json();
                json.setUsePrototypes(false);
                String jsondata = json.toJson(mapStatMessage);
                try
                {
                    server.broadcastMessage(jsondata);
//                System.out.println("Server sends: " + jsondata);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

//                String newId = UUID.randomUUID().toString();
                clientID.put(remoteAddr, setupMsg.name);
                world.addKnight(setupMsg.name);
            }
        });
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

        sendGap += 1;
        if (sendGap >= 5)
        {
            sendGap = 0;
            GameStat gameStat = world.getGameStat();
            GameStatMessage gameStatMessage = new GameStatMessage();
            gameStatMessage.data = gameStat;

            Json json = new Json();
            json.setUsePrototypes(false);
            String jsondata = json.toJson(gameStatMessage);
            try
            {
                server.broadcastMessage(jsondata);
//                System.out.println("Server sends: " + jsondata);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
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
