package io.github.altriaaa.huluwarogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.io.IOException;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class FirstScreen implements Screen
{
    final Main game;
    private Stage stage;
    private Skin skin;
    private TextField serverAddressField;
    private TextField playerNameField;

    public FirstScreen(final Main game)
    {
        this.game = game;
        stage = new Stage(new FitViewport(1440, 810));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Image background = new Image(new Texture(Gdx.files.internal("ui/background.jpg")));
        background.setFillParent(true);
        stage.addActor(background);

        Label serverAddressLabel = new Label("Server Address:", skin);
        serverAddressField = new TextField("", skin);
        serverAddressField.setMessageText("Enter server address");

        Label playerNameLabel = new Label("Player Name:", skin);
        playerNameField = new TextField("", skin);
        playerNameField.setMessageText("Enter your name");

        TextButton startButton = new TextButton("Start", skin);
        startButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                String serverAddress = serverAddressField.getText();
                String playerName = playerNameField.getText();
                game.startGame(serverAddress, playerName); // 启动游戏逻辑
            }
        });

        TextButton replayButton = new TextButton("Replay", skin);
        replayButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                String playerName = playerNameField.getText();
                game.startReplay(playerName);
            }
        });

        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Gdx.app.exit();
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(serverAddressLabel).pad(10);
        table.add(serverAddressField).width(300).pad(20).row();
        table.add(playerNameLabel).pad(20);
        table.add(playerNameField).width(300).pad(20).row();
        table.add(startButton).width(200).pad(20).row();
        table.add(replayButton).width(200).pad(20).row();
        table.add(exitButton).width(200).pad(20).row();

        stage.addActor(table);
    }

    @Override
    public void show()
    {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta)
    {
        ScreenUtils.clear(Color.BLACK);
        stage.act(delta);
        stage.draw();
//        game.viewport.apply();
//        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
//
//        game.batch.begin();
//        game.batch.end();
//
//        if (Gdx.input.isTouched())
//        {
//            try
//            {
//                game.setScreen(new GameScreen(game));
//            } catch (IOException e)
//            {
//                throw new RuntimeException(e);
//            }
//            dispose();
//        }
    }

    @Override
    public void resize(int width, int height)
    {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause()
    {
        // Invoked when your application is paused.
    }

    @Override
    public void resume()
    {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide()
    {
        // This method is called when another screen replaces this one.
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose()
    {
        // Destroy screen's assets here.
        stage.dispose();
        skin.dispose();
    }
}
