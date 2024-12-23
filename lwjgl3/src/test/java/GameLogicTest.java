import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.altriaaa.huluwarogue.creatures.Knight;
import io.github.altriaaa.huluwarogue.creatures.Orc;
import io.github.altriaaa.huluwarogue.lwjgl3.Lwjgl3Launcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class GameLogicTest
{
//    @BeforeAll
//    public static void setup() {
//        TestEnvironment.init();
//    }

    @Test
    public void setup()
    {
        // 初始化 LWJGL 应用程序
        Lwjgl3ApplicationConfiguration config = Lwjgl3Launcher.getDefaultConfiguration();
//        config.forceExit = false; // 测试结束时不退出 JVM

        new Lwjgl3Application(new TestApplication(), config);

//        System.out.println("dsa");
    }

//    @Test
//    public void testCollisionDetection()
//    {
//        Orc orc = new Orc();
//        Knight knight = new Knight();
//
//        orc.setPosition(0, 0);
//        knight.setPosition(0, 0);
//
//        boolean collision = orc.isAttacking(knight.getBoundingBox());
//        assertTrue(collision, "Orc should collide with Knight.");
////        assertTrue(true, "Orc should collide with Knight.");
//    }
}


