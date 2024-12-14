package io.github.altriaaa.huluwarogue.creatures;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import io.github.altriaaa.huluwarogue.GameScreen;
import io.github.altriaaa.huluwarogue.Main;
import io.github.altriaaa.huluwarogue.ResourceManager;

import java.util.Random;

public class OrcFactory implements CreatureFactory<Orc>
{
    final Main game;
    GameScreen gameScreen;

    public OrcFactory(Main game, GameScreen gameScreen)
    {
        this.game = game;
        this.gameScreen = gameScreen;
    }

    public Orc create()
    {
        ResourceManager manager = game.resourceManager;
        // need one TextureRegion to determine width and height
        TextureAtlas atlas = manager.getAtlas("orc_idle");
        Array<TextureAtlas.AtlasRegion> regionArray = atlas.getRegions();
        float width = regionArray.get(0).getRegionWidth();
        float height = regionArray.get(0).getRegionHeight();
        Orc orc = new Orc(game, gameScreen);
        orc.setSize(width, height);
        orc.setOrigin(width/2,height/2);
        orc.setScale(3.0f,3.0f);     // scale is determined by specific image, this is a magic number

        // Generate random position on the top, bottom, or right edge of the screen
        Random random = new Random();
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();
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
