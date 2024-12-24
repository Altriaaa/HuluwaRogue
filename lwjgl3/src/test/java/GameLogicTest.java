import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import io.github.altriaaa.huluwarogue.Control;
import io.github.altriaaa.huluwarogue.GameWorld;
import io.github.altriaaa.huluwarogue.creatures.Creature;
import io.github.altriaaa.huluwarogue.creatures.Knight;
import io.github.altriaaa.huluwarogue.creatures.Orc;
import io.github.altriaaa.huluwarogue.creatures.OrcFactory;
import io.github.altriaaa.huluwarogue.lwjgl3.Lwjgl3Launcher;
import io.github.altriaaa.huluwarogue.lwjgl3.StartupHelper;
import io.github.altriaaa.huluwarogue.lwjgl3.server.ServerGame;
import io.github.altriaaa.huluwarogue.lwjgl3.server.ServerGameScreen;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.ATITextureCompression3DC;

import java.io.IOException;
import java.util.function.Consumer;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class GameLogicTest
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
    public void testSaveLoad()
    {
        testBehavior = () ->
        {
            GameWorld.getInstance().addKnight("mockKnight");
            float X = GameWorld.getInstance().getKnightById("mockKnight").getX();
            GameWorld.getInstance().save();
            GameWorld.getInstance().removeKnightById("mockKnight");
            GameWorld.getInstance().load();
            assertTrue(GameWorld.getInstance().getKnights().size > 0);
            assertTrue(GameWorld.getInstance().getKnightById("mockKnight").getX() == X);
        };
    }

    @Test
    public void testAtk()
    {
        testBehavior = () ->
        {
            GameWorld.getInstance().addKnight("mockKnight");
            GameWorld.getInstance().getKnightById("mockKnight").setState(Creature.CharacterState.ATTACK);
            GameWorld.getInstance().getKnightById("mockKnight").setBox();
            Orc orc = new Orc();
            orc.setPosition(0, 0);
            orc.setBox();
            float oldHealth = orc.getHealth();
            Array<Orc> enemies = new Array<>();
            enemies.add(orc);
            GameWorld.getInstance().setEnemies(enemies);
            GameWorld.getInstance().update(0.4f);
            assertTrue(GameWorld.getInstance().getEnemies().get(0).getHealth() < oldHealth);
        };
    }

    @Test
    public void testEnemyGen()
    {
        testBehavior = () ->
        {
            Array<Orc> enemies = new Array<>();
            for (int i = 0; i < 10; i++)
            {
                enemies.add((Orc) GameWorld.getInstance().createCreature(new OrcFactory()));
            }
            GameWorld.getInstance().setEnemies(enemies);
            GameWorld.getInstance().createEnemy(100);
            assertTrue(GameWorld.getInstance().getEnemies().size == 10);
//            Orc.shutdownGlobalExecutorService();
        };
    }

    @Test
    public void testSetGet()
    {
        testBehavior = () ->
        {
            GameWorld.getInstance().addKnight("mockKnight");
            Knight knight1 = GameWorld.getInstance().getKnightById("mockKnight");
            float h1 = knight1.getHealth();
            Knight knight2 = new Knight();
            knight2.setId("vulKnight");
            knight2.vulDamage(1);
            Array<Knight> newKnights = new Array<>();
            newKnights.add(knight2);
            GameWorld.getInstance().setKnights(newKnights);
            float h2 = GameWorld.getInstance().getKnightById("vulKnight").getHealth();
            assertTrue(h2 == h1 - 1);
        };
    }

    @Test
    public void testSerialize()
    {
        testBehavior = () ->
        {
            Stage stage = new Stage();
            Knight knight = new Knight();
            float oldHealth = knight.getHealth();
            knight.vulDamage(1);

            Json json = new Json();
            String jsonData = json.toJson(knight);
            Knight knight1 = json.fromJson(Knight.class, jsonData);
            assertTrue(knight1.getHealth() == oldHealth - 1);
        };
    }

    @Test
    public void testDraw()
    {
        testBehavior = () ->
        {
            Stage stage = new Stage();

            Orc orc = new Orc();
            Knight knight = new Knight();

            stage.addActor(knight);
            stage.addActor(orc);

            knight.setPosition(0, 0);
            knight.setBox();
            orc.setPosition(0, 0);
            orc.setBox();

            GameWorld.getInstance().showBox = true;

            stage.draw();
        };
    }


    @Test
    public void testHealth()
    {
        testBehavior = () ->
        {
            Stage stage = new Stage();

            Orc orc = new Orc();
            Knight knight = new Knight();

            stage.addActor(knight);
            stage.addActor(orc);

            knight.vulDamage(knight.getHealth() + 1);
            assertTrue(knight.getHealth() < 0 && knight.getState() == Creature.CharacterState.DYING, "Knight should be dead.");
        };
    }

    @Test
    public void testAct()
    {
        testBehavior = () ->
        {
            Stage stage = new Stage();
            Knight knight = new Knight();

            float delta = 0.1F;

            stage.addActor(knight);
            knight.setPosition(0, 0);
            Control control = new Control();
            control.set();
            control.UP = true;
            knight.setControl(control);
            float oldY = knight.getY();
            knight.act(delta);
            assertTrue(knight.getY() != oldY);
            control.DOWN = true;
            oldY = knight.getY();
            knight.act(delta);
            assertTrue(knight.getY() != oldY);
        };
    }

    @Test
    public void testKnightCollidesWithOrc()
    {
        testBehavior = () ->
        {
            Stage stage = new Stage();

            Orc orc = new Orc();
            Knight knight = new Knight();

            stage.addActor(knight);
            stage.addActor(orc);

            knight.setPosition(0, 0);
            knight.setBox();
            orc.setPosition(0, 0);
            orc.setBox();

            float old_health = knight.getHealth();

            knight.vulDamage(1);
            orc.vulDamage(1);

            orc.remove();

            boolean collision = knight.isAttacking(orc.getBoundingBox());
            assertTrue(knight.getHealth() < old_health, "Knight get damaged.");
            assertTrue(collision, "Knight should detect collision with Orc.");
        };
    }

    @Test
    public void testOrcCollidesWithKnight()
    {
        testBehavior = () ->
        {
            Stage stage = new Stage();

            Orc orc = new Orc();
            Knight knight = new Knight();

            stage.addActor(orc);
            stage.addActor(knight);

            orc.setPosition(0, 0);
            orc.setBox();
            knight.setPosition(0, 0);
            knight.setBox();

            knight.vulDamage(1);
            orc.vulDamage(1);

            knight.remove();

            boolean collision = orc.isAttacking(knight.getBoundingBox());
            assertTrue(collision, "Orc should collide with Knight.");
        };
    }

    @AfterEach
    public void executeTestBehavior()
    {
        if (StartupHelper.startNewJvmIfRequired()) return;
        Lwjgl3ApplicationConfiguration config = Lwjgl3Launcher.getDefaultConfiguration();
        new Lwjgl3Application(new ApplicationAdapter()
        {
            @Override
            public void create()
            {
                try
                {
                    GameWorld.getInstance().assetInit();
                    GameWorld.getInstance().worldInit();
                    testBehavior.run();
                } finally
                {
                    GameWorld.getInstance().clear();
                    Gdx.app.exit();
                }
            }
        }, config);
    }
}


