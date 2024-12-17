package io.github.altriaaa.huluwarogue.creatures;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import io.github.altriaaa.huluwarogue.GameScreen;
import io.github.altriaaa.huluwarogue.GameWorld;
import io.github.altriaaa.huluwarogue.Main;
import io.github.altriaaa.huluwarogue.ResourceManager;

import java.util.Random;

public class OrcFactory implements CreatureFactory<Orc>
{
    public OrcFactory()
    {
    }

    public Orc create()
    {
        Orc orc = new Orc();
        // Generate random position on the top, bottom, or right edge of the screen
        Random random = new Random();
        float worldWidth = GameWorld.getInstance().getStage().getWidth();
        float worldHeight = GameWorld.getInstance().getStage().getHeight();
        int edge = random.nextInt(3); // 0 for top, 1 for bottom, 2 for right

        switch (edge)
        {
            case 0: // Top edge
                orc.setPosition(random.nextFloat() * (worldWidth - orc.getWidth()), worldHeight - orc.getHeight());
                break;
            case 1: // Bottom edge
                orc.setPosition(random.nextFloat() * (worldWidth - orc.getWidth()), 0);
                break;
            case 2: // Right edge
                orc.setPosition(worldWidth - orc.getWidth(), random.nextFloat() * (worldHeight - orc.getHeight()));
                break;
        }
        return orc;
    }
}
