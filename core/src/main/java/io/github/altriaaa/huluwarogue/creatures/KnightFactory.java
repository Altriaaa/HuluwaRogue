package io.github.altriaaa.huluwarogue.creatures;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import io.github.altriaaa.huluwarogue.GameScreen;
import io.github.altriaaa.huluwarogue.Main;
import io.github.altriaaa.huluwarogue.ResourceManager;

public class KnightFactory implements CreatureFactory<Knight>
{
    public KnightFactory()
    {}

    public Knight create()
    {
        Knight knight = new Knight();
        knight.setPosition(0, 0);
        return knight;
    }
}
