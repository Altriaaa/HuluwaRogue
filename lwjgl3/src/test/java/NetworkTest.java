import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.altriaaa.huluwarogue.GameScreen;
import io.github.altriaaa.huluwarogue.GameWorld;
import io.github.altriaaa.huluwarogue.Main;
import io.github.altriaaa.huluwarogue.ReplayScreen;
import io.github.altriaaa.huluwarogue.lwjgl3.Lwjgl3Launcher;
import io.github.altriaaa.huluwarogue.lwjgl3.StartupHelper;
import io.github.altriaaa.huluwarogue.lwjgl3.server.ServerGame;
import io.github.altriaaa.huluwarogue.lwjgl3.server.ServerGameScreen;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

public class NetworkTest
{
    private Runnable testBehavior;

    @BeforeEach
    public void setup()
    {
        testBehavior = () ->
        {

        };
    }

    @Test
    public void executeTestBehavior_Server()
    {
        if (StartupHelper.startNewJvmIfRequired()) return;
        Lwjgl3ApplicationConfiguration config = Lwjgl3Launcher.getDefaultConfiguration();
        new Lwjgl3Application(new ServerGame()
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
                } finally
                {
                    Gdx.app.exit();
                }
            }
        }, config);
    }

    @Test
    void testMethodShouldThrowException()
    {
        Exception exception = assertThrows(RuntimeException.class, this::executeTestBehavior_Game);

    }

    public void executeTestBehavior_Game()
    {
        if (StartupHelper.startNewJvmIfRequired()) return;
        Lwjgl3ApplicationConfiguration config = Lwjgl3Launcher.getDefaultConfiguration();
        new Lwjgl3Application(new Main()
        {
            @Override
            public void create()
            {
                try
                {
                    this.setScreen(new GameScreen(this, "localhost", "mockPlayer"));
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                } finally
                {
                    Gdx.app.exit();
                }
            }
        }, config);
    }

    @Test
    public void executeTestBehavior_Replay()
    {
        if (StartupHelper.startNewJvmIfRequired()) return;
        Lwjgl3ApplicationConfiguration config = Lwjgl3Launcher.getDefaultConfiguration();
        new Lwjgl3Application(new Main()
        {
            @Override
            public void create()
            {
                try
                {
                    this.setScreen(new ReplayScreen(this,  "mockPlayer"));
                } finally
                {
                    Gdx.app.exit();
                }
            }
        }, config);
    }

}
