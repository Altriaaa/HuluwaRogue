//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
//import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.glutils.ShaderProgram;
//import io.github.altriaaa.huluwarogue.Main;
////import org.mockito.Mockito;
//
////import static org.mockito.Mockito.mock;
//
//public class TestEnvironment
//{
//    public static void init()
//    {
//        if (!LibGDXInitialized.instance)
//        {
//            // 模拟 OpenGL 接口
//            HeadlessNativesLoader.load();
//            Gdx.graphics = new MockGraphics();
//            Gdx.files = new HeadlessFiles();
//            Gdx.gl = mock(GL20.class);
//            Gdx.gl20 = Gdx.gl;
//            ShaderProgram.pedantic = false;
//
//            // 初始化 HeadlessApplication
//            HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
//            new HeadlessApplication(new Main(), config);
//
//            System.out.println("HeadlessApplication initialized successfully!");
//
//            LibGDXInitialized.instance = true;
//        }
//    }
//
//    private static class DummyApplication extends com.badlogic.gdx.ApplicationAdapter
//    {
//    }
//
//    private static class LibGDXInitialized
//    {
//        static boolean instance = false;
//    }
//}
