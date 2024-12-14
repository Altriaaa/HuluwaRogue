package io.github.altriaaa.huluwarogue.tiles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.altriaaa.huluwarogue.ResourceManager;

public class Square extends Tile
{
    public Square()
    {
        super(ResourceManager.getInstance().getAtlas("effect").findRegion("square"));
    }

    public Square(float x, float y)
    {
        this();
        this.setPosition(x, y);
    }
}
