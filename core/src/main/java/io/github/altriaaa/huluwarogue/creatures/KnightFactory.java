package io.github.altriaaa.huluwarogue.creatures;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import io.github.altriaaa.huluwarogue.GameScreen;
import io.github.altriaaa.huluwarogue.Main;
import io.github.altriaaa.huluwarogue.ResourceManager;

public class KnightFactory implements CreatureFactory<Knight>
{
    final Main game;
    final GameScreen gameScreen;

    public KnightFactory(Main game, GameScreen gameScreen)
    {
        this.game = game;
        this.gameScreen = gameScreen;
    }

    public Knight create()
    {
        ResourceManager manager = game.resourceManager;
        // need one TextureRegion to determine width and height
        TextureAtlas atlas = manager.getAtlas("knight_idle");
        Array<TextureAtlas.AtlasRegion> regionArray = atlas.getRegions();
        float width = regionArray.get(0).getRegionWidth();
        float height = regionArray.get(0).getRegionHeight();
        Knight knight = new Knight(game, gameScreen);
        knight.setSize(width, height);
        knight.setOrigin(width/2,height/2);
        knight.setScale(3.0f,3.0f);     // scale is determined by specific image, this is a magic number
        knight.setPosition(0, 0);
        return knight;
    }
}
