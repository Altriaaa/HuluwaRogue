package io.github.altriaaa.huluwarogue.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import io.github.altriaaa.huluwarogue.GameScreen;
import io.github.altriaaa.huluwarogue.GameWorld;
import io.github.altriaaa.huluwarogue.Main;
import io.github.altriaaa.huluwarogue.ResourceManager;
import io.github.altriaaa.huluwarogue.tiles.Obstacle;

public class Knight extends Creature
{
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

    }

    @Override
    public void setBox()
    {
        boundingBox.set(getX()+getWidth()/3, getY()+getHeight()/3, getWidth()/3, getHeight()/3);
        atkBox.set(getX()+20, getY()+20, getWidth()-10, getHeight()-30);
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);
        float nextX = getX();
        float nextY = getY();
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            setState(CharacterState.WALKING);
            nextX += speed*delta; // Move right
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            setState(CharacterState.WALKING);
            nextX -= speed*delta; // Move left
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            setState(CharacterState.WALKING);
            nextY += speed*delta; // Move up
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            setState(CharacterState.WALKING);
            nextY -= speed*delta; // Move down
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.J)) {
            setState(CharacterState.ATTACK);
        }
        else
        {
            setState(CharacterState.IDLE);
        }
        Rectangle nextBoundingBox = new Rectangle(nextX+getWidth()/3, nextY+getHeight()/3, getWidth()/3, getHeight()/3);
        boolean canMove = true;
        for(Orc enemy : GameWorld.getInstance().getEnemies())
        {
            if(nextBoundingBox.overlaps(enemy.getBoundingBox()))
            {
                canMove = false;
                break;
            }
        }
        for(Obstacle o : GameWorld.getInstance().getObstacles())
        {
            if(nextBoundingBox.overlaps(o.getBoundingBox()))
            {
                canMove = false;
                break;
            }
        }
        if(canMove)
        {
            setPosition(nextX, nextY);
        }
    }
}
