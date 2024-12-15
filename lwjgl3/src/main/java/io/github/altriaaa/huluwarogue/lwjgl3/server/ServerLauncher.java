package io.github.altriaaa.huluwarogue.lwjgl3.server;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.altriaaa.huluwarogue.lwjgl3.Lwjgl3Launcher;
import io.github.altriaaa.huluwarogue.lwjgl3.StartupHelper;

/**
 * Launches the desktop (LWJGL3) application.
 */
public class ServerLauncher
{
    public static void main(String[] args)
    {
        if (StartupHelper.startNewJvmIfRequired()) return;
        new Lwjgl3Application(new ServerGame(), Lwjgl3Launcher.getDefaultConfiguration());
    }
}
