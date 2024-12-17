package io.github.altriaaa.huluwarogue.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.altriaaa.huluwarogue.*;
import io.github.altriaaa.huluwarogue.tiles.Obstacle;

public class Knight extends Creature
{
    Control control;

    public Knight()
    {
        healthLim = 100;
        health = healthLim;
        damage = 5;

        ResourceManager resourceManager = ResourceManager.getInstance();
        idleAnimation = resourceManager.createAnimation("knight_idle", 0.1f, Animation.PlayMode.LOOP);
        walkAnimation = resourceManager.createAnimation("knight_run", 0.07f, Animation.PlayMode.LOOP);
        attackAnimation = resourceManager.createAnimation("knight_attack", 0.05f, Animation.PlayMode.LOOP);
        deathAnimation = resourceManager.createAnimation("knight_death", 0.1f, Animation.PlayMode.NORMAL);

        speed = 128f;

        ResourceManager manager = ResourceManager.getInstance();
        TextureAtlas atlas = manager.getAtlas("knight_idle");
        Array<TextureAtlas.AtlasRegion> regionArray = atlas.getRegions();
        float width = regionArray.get(0).getRegionWidth();
        float height = regionArray.get(0).getRegionHeight();

        setSize(width, height);
        setOrigin(width / 2, height / 2);
        setScale(3.0f, 3.0f);

        control = new Control();
    }

    @Override
    public void setBox()
    {
        boundingBox.set(getX() + getWidth() / 3, getY() + getHeight() / 3, getWidth() / 3, getHeight() / 3);
        atkBox.set(getX() + 20, getY() + 20, getWidth() - 10, getHeight() - 30);
    }

    public void setControl(Control control)
    {
        this.control = control;
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);
        float nextX = getX();
        float nextY = getY();
        if (this.control.RIGHT)
        {
            setState(CharacterState.WALKING);
            nextX += speed * delta; // Move right
        }
        else if (this.control.LEFT)
        {
            setState(CharacterState.WALKING);
            nextX -= speed * delta; // Move left
        }
        else if (this.control.UP)
        {
            setState(CharacterState.WALKING);
            nextY += speed * delta; // Move up
        }
        else if (this.control.DOWN)
        {
            setState(CharacterState.WALKING);
            nextY -= speed * delta; // Move down
        }
        else if (this.control.ATTACK)
        {
            setState(CharacterState.ATTACK);
        }
        else
        {
            setState(CharacterState.IDLE);
        }
        Rectangle nextBoundingBox = new Rectangle(nextX + getWidth() / 3, nextY + getHeight() / 3, getWidth() / 3, getHeight() / 3);
        boolean canMove = true;
        for (Orc enemy : GameWorld.getInstance().getEnemies())
        {
            if (nextBoundingBox.overlaps(enemy.getBoundingBox()))
            {
                canMove = false;
                break;
            }
        }
        for (Obstacle o : GameWorld.getInstance().getObstacles())
        {
            if (nextBoundingBox.overlaps(o.getBoundingBox()))
            {
                canMove = false;
                break;
            }
        }
        if (canMove)
        {
            setPosition(nextX, nextY);
        }
    }
}
