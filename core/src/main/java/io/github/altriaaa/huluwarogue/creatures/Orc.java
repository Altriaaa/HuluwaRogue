package io.github.altriaaa.huluwarogue.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Timer;
import io.github.altriaaa.huluwarogue.GameScreen;
import io.github.altriaaa.huluwarogue.Main;
import io.github.altriaaa.huluwarogue.ResourceManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Orc extends Creature
{
    GameScreen gameScreen;
    private float targetX;
    private float targetY;
    private ExecutorService executorService; // 线程池，用于管理每个 Orc 的线程

    public Orc(final Main game, GameScreen gameScreen)
    {
        this.gameScreen = gameScreen;

        healthLim = 20;
        health = healthLim;
        damage = 3;

        ResourceManager resourceManager = game.resourceManager;
        idleAnimation = resourceManager.createAnimation("orc_idle", 0.1f, Animation.PlayMode.LOOP);
        walkAnimation = resourceManager.createAnimation("orc_run", 0.1f, Animation.PlayMode.LOOP);
        attackAnimation = resourceManager.createAnimation("orc_attack", 0.1f, Animation.PlayMode.LOOP);
        deathAnimation = resourceManager.createAnimation("orc_death", 0.1f, Animation.PlayMode.LOOP);

        speed = 64;
        boundingBox = new Rectangle();
        atkBox = new Rectangle();
        targetX = 0;
        targetY = 0;
        executorService = Executors.newSingleThreadExecutor();
        startBehavior();
    }

    private void startBehavior()
    {
        executorService.submit(() ->
        {
            while(health > 0)
            {
                try
                {
                    float newX = gameScreen.getKnight().getX();
                    float newY = gameScreen.getKnight().getY();
                    // 将计算结果提交到主线程更新
                    Gdx.app.postRunnable(() -> {
                        targetX = newX;
                        targetY = newY;
                    });
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
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
        boundingBox.set(getX()+getWidth()/3, getY()+getHeight()/3, getWidth()/3, getHeight()/3);
        atkBox.set(getX()-10, getY()+20, getWidth()-20, getHeight()-20);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(state == CharacterState.DYING) return;
        // 在主线程中处理移动逻辑
        if (targetX != getX() || targetY != getY())
        {
//            System.out.println(targetX + "  " + targetY);
            float deltaX = targetX - getX();
            float deltaY = targetY - getY();
            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            if (distance > speed * delta) {
                float ratio = speed * delta / distance;
                moveBy(deltaX * ratio, deltaY * ratio);
            } else {
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
            getX()+3*getWidth(), getY(),
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
//        System.out.println("into");
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        return super.remove();
    }
}

