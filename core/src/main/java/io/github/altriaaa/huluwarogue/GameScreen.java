package io.github.altriaaa.huluwarogue;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.*;
import io.github.altriaaa.huluwarogue.creatures.*;
import io.github.altriaaa.huluwarogue.network.*;
import io.github.altriaaa.huluwarogue.tiles.Obstacle;
import io.github.altriaaa.huluwarogue.tiles.Square;
// import sun.font.CreatedFontTracker;
// import sun.tools.jstat.Jstat;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GameScreen implements Screen, GameClient.MessageListener
{
    final Main game;
    GameWorld world;
    private String name;
    private GameClient client;
    private Control control;

    boolean isRecording;
    BufferedWriter recordWriter;
    private final String REC_FILE_PATH;
    private int recGap;
    private Stage stage;
    private Label recordingLabel;

    public GameScreen(final Main game, String serverAddress, String playerName) throws IOException
    {
        this.game = game;
        this.name = playerName;

        world = GameWorld.getInstance();
        world.assetInit();
        world.worldInit();

        REC_FILE_PATH = "saves/record_" + name + ".log";
        recGap = 0;
        isRecording = false;
        stage = new Stage();
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        BitmapFont font = new BitmapFont();
        font.setUseIntegerPositions(false);
        font.getData().setScale(world.viewport.getWorldHeight() / Gdx.graphics.getHeight());
        Label.LabelStyle style = new Label.LabelStyle(font, Color.RED);

        recordingLabel = new Label("Recording...", skin);
        recordingLabel.setPosition(10, Gdx.graphics.getHeight() - 50);
        recordingLabel.setFontScale(5.0f);
        recordingLabel.setVisible(false);
        recordingLabel.addAction(Actions.forever(Actions.sequence(
            Actions.color(Color.RED, 0.5f),
            Actions.color(Color.ORANGE, 0.5f)
        )));
        stage.addActor(recordingLabel);

        control = new Control();

        client = new GameClient(serverAddress, 12345);
        client.setMessageListener(this);
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
    }

    @Override
    public void onMessageReceived(String message)
    {
        Gdx.app.postRunnable(() ->
        {
//            System.out.println("Message from server: " + message);
            if(world.isPaused()) return;
            Json json = new Json();
            JsonValue jsonValue = new JsonReader().parse(message);
            String type = jsonValue.getString("type");
            if ("gameStat".equals(type))
            {
                GameStatMessage gameStatMessage = json.fromJson(GameStatMessage.class, message);
                world.buildFromGameStat(gameStatMessage.data);
            }
            else if ("mapStat".equals(type))
            {
                MapStatMessage mapStatMessage = json.fromJson(MapStatMessage.class, message);
                world.refreshMap(mapStatMessage.data);
            }
        });
    }

    @Override
    public void show()
    {
//        music.play();
        SetupMessage setupMessage = new SetupMessage();
        setupMessage.name = this.name;
        Json json = new Json();
        json.setUsePrototypes(false);
        String jsonData = json.toJson(setupMessage);
        try
        {
            client.send(jsonData);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resize(int width, int height)
    {
        GameWorld.getInstance().viewport.update(width, height, true);
    }

    public void recordGame()
    {
        if (isRecording)
        {
            recGap += 1;
            if (recGap >= 1)
            {
                recGap = 0;
                Json json = new Json();
                try
                {
                    recordWriter.write(json.toJson(world.getGameStat()));
                    recordWriter.newLine();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void recordMap()
    {
        Json json = new Json();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REC_FILE_PATH)))
        {
            writer.write("MAP:");
            writer.newLine();
            writer.write(json.toJson(world.getMap()));
            writer.newLine();
            writer.write("GAME:");
            writer.newLine();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void recordBegin()
    {
        recordMap();
        try
        {
            recordWriter = new BufferedWriter(new FileWriter(REC_FILE_PATH, true));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        isRecording = true;
        recordingLabel.setVisible(true);
        System.out.println("Record Begin...");
    }

    public void recordEnd()
    {
        isRecording = false;
        recordingLabel.setVisible(false);
        try
        {
            if (recordWriter != null)
            {
                recordWriter.close();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
//        recordGame();
        System.out.println("Record end. Data saved in " + REC_FILE_PATH);
    }

    @Override
    public void render(float delta)
    {
        ScreenUtils.clear(Color.BLACK);
        Json json = new Json();
        json.setUsePrototypes(false);
        // control
        control.set();
        ControlMessage controlMsg = new ControlMessage();
        controlMsg.control = this.control;
        // request
        RequestMessage requestMsg = new RequestMessage();
        if (Gdx.input.isKeyJustPressed(Input.Keys.P))
        {
            requestMsg.kind = RequestMessage.REQ_KIND.SAVE;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.L))
        {
            requestMsg.kind = RequestMessage.REQ_KIND.LOAD;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R))
        {
            if (isRecording)
            {
                recordEnd();
            }
            else
            {
                recordBegin();
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.K))
        {
            if(world.showBox)
            {
                world.showBox = false;
            }
            else
            {
                world.showBox = true;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
        {
            requestMsg.kind = RequestMessage.REQ_KIND.EXIT;
            dispose();
        }
        String jsonControlData = json.toJson(controlMsg);
        String jsonReqData = json.toJson(requestMsg);
        try
        {
            client.send(jsonControlData);
            if (requestMsg.kind != null)
                client.send(jsonReqData);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        recordGame();
        world.getStage().draw();
        stage.act(delta);
        stage.draw();
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
        stage.dispose();
        world.clear();
        game.setScreen(new FirstScreen(game));
    }
}
