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
    private ExecutorService executorService; // 线程池，用于管理每个 Orc 的线程

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
        executorService = Executors.newSingleThreadExecutor();

        TextureAtlas atlas = resourceManager.getAtlas("orc_idle");
        Array<TextureAtlas.AtlasRegion> regionArray = atlas.getRegions();
        float width = regionArray.get(0).getRegionWidth();
        float height = regionArray.get(0).getRegionHeight();

        setSize(width, height);
        setOrigin(width / 2, height / 2);
        setScale(3.0f, 3.0f);
//        startBehavior();
    }

    @Override
    public void write(Json json)
    {
        super.write(json);
//        json.writeValue("targetX", targetX);
//        json.writeValue("targetY", targetY);
//        json.writeValue("executorService", executorService);
    }

    @Override
    public void read(Json json, JsonValue jsonData)
    {
        super.read(json, jsonData);
//        targetX = json.readValue("targetX", Float.class, jsonData);
//        targetY = json.readValue("targetY", Float.class, jsonData);
    }

//    @Override
//    public void vulDamage(float d)
//    {
//        health -= d;
//        if (health <= 0)
//        {
//            setState(CharacterState.DYING);
//            Timer.schedule(new Timer.Task()
//            {
//                @Override
//                public void run()
//                {
//                    remove();
//                }
//            }, deathAnimation.getAnimationDuration());
//        }
//    }

    public void startBehavior()
    {
        executorService.submit(() ->
        {
            while (health > 0)
            {
                try
                {
                    Array<Knight> knights = GameWorld.getInstance().getKnights();
                    float newX = knights.size == 0 ? 0 : knights.get(0).getX();
                    float newY = knights.size == 0 ? 0 : knights.get(0).getY();
                    // 将计算结果提交到主线程更新
                    Gdx.app.postRunnable(() ->
                    {
                        targetX = newX;
                        targetY = newY;
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
        // 在主线程中处理移动逻辑
        if (targetX != getX() || targetY != getY())
        {
            float deltaX = targetX - getX();
            float deltaY = targetY - getY();
            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            if (distance > speed * delta)
            {
                float ratio = speed * delta / distance;
                moveBy(deltaX * ratio, deltaY * ratio);
            }
            else
            {
                setPosition(targetX, targetY);
            }
        }
//        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
//            setState(CharacterState.WALKING);
//            move(6); // 右移
//        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
//            setState(CharacterState.WALKING);
//            move(4); // 左移
//        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
//            setState(CharacterState.WALKING);
//            move(8); // 上移
//        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
//            setState(CharacterState.WALKING);
//            move(2); // 下移
//        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
//            setState(CharacterState.ATTACK);
//        } else {
//            setState(CharacterState.IDLE);
//        }
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
        drawRect(batch);
        drawHealthBar(batch);
    }

    @Override
    public boolean remove()
    {
        if (executorService != null && !executorService.isShutdown())
        {
            executorService.shutdownNow();
        }
        return super.remove();
    }
}

