package io.github.altriaaa.huluwarogue.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.utils.GdxRuntimeException;
import io.github.altriaaa.huluwarogue.Main;

/**
 * Launches the desktop (LWJGL3) application.
 */
public class Lwjgl3Launcher
{
    public static void main(String[] args)
    {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication()
    {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    public static Lwjgl3ApplicationConfiguration getDefaultConfiguration()
    {
        System.out.println("1");
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        System.out.println("2");
        configuration.setTitle("HuluwaRogue");
        System.out.println("3");
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);
        System.out.println("4");
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        try
        {
            // 正常环境下获取实际刷新率
            configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        } catch (NullPointerException | GdxRuntimeException e)
        {
            // 在无头环境中使用默认值
            System.err.println("Failed to get display mode. Using default refresh rate.");
            configuration.setForegroundFPS(60);
        }
        System.out.println("5");
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
        configuration.setWindowedMode(1440, 810);
        System.out.println("6");
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        System.out.println("7");
        return configuration;
    }
}
