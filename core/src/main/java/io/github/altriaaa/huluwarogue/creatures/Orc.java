package io.github.altriaaa.huluwarogue.creatures;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Timer;
import io.github.altriaaa.huluwarogue.GameScreen;
import io.github.altriaaa.huluwarogue.GameWorld;
import io.github.altriaaa.huluwarogue.Main;
import io.github.altriaaa.huluwarogue.ResourceManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Orc extends Creature
{
    private float targetX;
    private float targetY;
    private float targetD;
    //    private ExecutorService executorService; // 线程池，用于管理每个 Orc 的线程
    private static final ExecutorService globalExecutorService = Executors.newCachedThreadPool();
    private boolean isStarted;


    public Orc()
    {
        healthLim = 20;
        health = healthLim;
        damage = 3;

        ResourceManager resourceManager = ResourceManager.getInstance();
        idleAnimation = resourceManager.createAnimation("orc_idle", 0.1f, Animation.PlayMode.LOOP);
        walkAnimation = resourceManager.createAnimation("orc_run", 0.1f, Animation.PlayMode.LOOP);
        attackAnimation = resourceManager.createAnimation("orc_attack", 0.1f, Animation.PlayMode.LOOP);
        deathAnimation = resourceManager.createAnimation("orc_death", 0.2f, Animation.PlayMode.LOOP);

        speed = 64;
        targetX = 0;
        targetY = 0;
        targetD = 10000;
        isStarted = false;
//        executorService = Executors.newSingleThreadExecutor();

        TextureAtlas atlas = resourceManager.getAtlas("orc_idle");
        Array<TextureAtlas.AtlasRegion> regionArray = atlas.getRegions();
        float width = regionArray.get(0).getRegionWidth();
        float height = regionArray.get(0).getRegionHeight();

        setSize(width, height);
        setOrigin(width / 2, height / 2);
        setScale(3.0f, 3.0f);
    }

    @Override
    public void write(Json json)
    {
        super.write(json);
    }

    @Override
    public void read(Json json, JsonValue jsonData)
    {
        super.read(json, jsonData);
    }

    public static void shutdownGlobalExecutorService()
    {
        globalExecutorService.shutdownNow();
    }

    public void startBehavior()
    {
        if(isStarted) return;
        isStarted = true;
        globalExecutorService.submit(() ->
        {
            while (health > 0 && this.state != null)
            {
                try
                {
//                    System.out.println("orc acting");
                    Array<Knight> knights = GameWorld.getInstance().getKnights();
                    float distance = 10000;
                    float newX = 0;
                    float newY = 0;
                    CharacterState newState = CharacterState.IDLE;
                    for (int i = knights.size - 1; i >= 0; i--)
                    {
                        Knight knight = knights.get(i);
                        float deltaX = (knight.getX()) - getX();
                        float deltaY = (knight.getY()) - getY();
                        float new_distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                        if (distance > new_distance)
                        {
                            distance = new_distance;
                            newX = knight.getX();
                            newY = knight.getY();
                        }
                        if (distance < 100)
                        {
                            newState = CharacterState.ATTACK;
                        }
                        else
                        {
                            newState = CharacterState.WALKING;
                        }

                    }

                    // 将计算结果提交到主线程更新
                    float finalNewX = newX;
                    float finalNewY = newY;
                    CharacterState finalNewState = newState;
                    float finalDistance = distance;
                    Gdx.app.postRunnable(() ->
                    {
                        synchronized (this)
                        {
                            targetX = finalNewX;
                            targetY = finalNewY;
                            targetD = finalDistance;
                            setState(finalNewState);
                        }
                    });
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();  // 恢复中断状态
                    break;  // 中断时退出线程
                }
            }
        });
    }

    @Override
    public void setBox()
    {
        boundingBox.set(getX() + getWidth() / 3, getY() + getHeight() / 3, getWidth() / 3, getHeight() / 3);
        atkBox.set(getX() - 10, getY() + 20, getWidth() - 20, getHeight() - 20);
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);
        if (state == CharacterState.DYING) return;
        if (state == CharacterState.WALKING)
        {
            if (targetX != getX() || targetY != getY())
            {
                float deltaX = targetX - getX();
                float deltaY = targetY - getY();
                if (targetD > speed * delta)
                {
                    float ratio = speed * delta / targetD;
                    moveBy(deltaX * ratio, deltaY * ratio);
                }
                else
                {
                    setPosition(targetX, targetY);
                }
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        TextureRegion curFrame = getCurFrame();
        batch.draw(
            curFrame,
            getX() + 3 * getWidth(), getY(),
            getOriginX(), getOriginY(),
            -getWidth(), getHeight(),
            getScaleX(), getScaleY(),
            getRotation()
        );
        if(GameWorld.getInstance().getShowBox())
            drawRect(batch);
        drawHealthBar(batch);
    }

    @Override
    public boolean remove()
    {
        return super.remove();
    }
}

