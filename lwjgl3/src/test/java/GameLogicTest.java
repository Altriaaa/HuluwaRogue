import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.altriaaa.huluwarogue.creatures.Knight;
import io.github.altriaaa.huluwarogue.creatures.Orc;
import io.github.altriaaa.huluwarogue.lwjgl3.Lwjgl3Launcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;


public class GameLogicTest
{
//    @BeforeAll
//    public static void setup() {
//        TestEnvironment.init();
//    }

    @Test
    public void setup()
    {
//        GLFWErrorCallback.createPrint(System.err).set();
//        if (!GLFW.glfwInit()) {
//            throw new IllegalStateException("Unable to initialize GLFW");
//        }
        System.out.println("GLFW initialized successfully.");
        // 初始化 LWJGL 应用程序
        Lwjgl3ApplicationConfiguration config = Lwjgl3Launcher.getDefaultConfiguration();
//        config.forceExit = false; // 测试结束时不退出 JVM

        new Lwjgl3Application(new TestApplication(), config);

//        System.out.println("dsa");
    }
}


