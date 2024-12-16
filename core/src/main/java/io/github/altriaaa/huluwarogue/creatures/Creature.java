package io.github.altriaaa.huluwarogue.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Timer;

public abstract class Creature extends Actor implements Json.Serializable
{
    protected float health;
    protected float damage;
    protected float healthLim;

    protected float stateTime;
    protected float speed;
    protected CharacterState state;
    protected Rectangle boundingBox;
    protected Rectangle atkBox;

    private final transient ShapeRenderer shapeRenderer;
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> walkAnimation;
    protected Animation<TextureRegion> deathAnimation;
    protected Animation<TextureRegion> attackAnimation;

    public Creature()
    {
        state = CharacterState.IDLE;
        stateTime = 0f;
        shapeRenderer = new ShapeRenderer();
        boundingBox = new Rectangle();
        atkBox = new Rectangle();
    }

    public enum CharacterState {
        IDLE,
        WALKING,
        DYING,
        ATTACK,
        FAR_ATTACK
    }

    @Override
    public void write(Json json)
    {
        json.writeValue("health", health);
        json.writeValue("damage", damage);
        json.writeValue("healthLim", healthLim);
        json.writeValue("speed", speed);
        json.writeValue("state", state);
        json.writeValue("stateTime", stateTime);
        json.writeValue("boundingBox", boundingBox);
        json.writeValue("atkBox", atkBox);
        json.writeValue("x", getX());
        json.writeValue("y", getY());
    }

    @Override
    public void read(Json json, JsonValue jsonData)
    {
        health = json.readValue("health", Float.class, jsonData);
        damage = json.readValue("damage", Float.class, jsonData);
        healthLim = json.readValue("healthLim", Float.class, jsonData);
        speed = json.readValue("speed", Float.class, jsonData);
        state = json.readValue("state", CharacterState.class, jsonData);
        stateTime = json.readValue("stateTime", Float.class, jsonData);
        boundingBox = json.readValue("boundingBox", Rectangle.class, jsonData);
        atkBox = json.readValue("atkBox", Rectangle.class, jsonData);
        setPosition(json.readValue("x", Float.class, jsonData), json.readValue("y", Float.class, jsonData));
    }

    public float getDamage(){
        return damage;
    }

    public void vulDamage(float d)
    {
        health -= d;
        if(health <= 0)
        {
            setState(CharacterState.DYING);
//            Json json = new Json();
//            String jsonData = json.toJson(this);
//            System.out.println("Serialized: " + jsonData);
//            Creature deserializedCreature = json.fromJson(Creature.class, jsonData);
//            System.out.println("Deserialized Health: " + deserializedCreature.getHealth());
            Timer.schedule(new Timer.Task() {
                @Override
                public void run()
                {
                    remove();
                }
            }, deathAnimation.getAnimationDuration());
        }
    }

    public float getHealth(){
        return health;
    }

    public Rectangle getBoundingBox(){
        return boundingBox;
    }

    public void setBox()
    {
        atkBox.set(getX(), getY(), getWidth(), getHeight());
        boundingBox.set(getX(), getY(), getWidth(), getHeight());
    }

    public CharacterState getState() {
        return state;
    }

    public void setState(CharacterState newState) {
        if (state != newState)
        {
            state = newState;
            stateTime = 0f; // Reset animation time on state change
//            System.out.println(state);
        }
    }

    public boolean isAttacking(Rectangle other)
    {
        return atkBox.overlaps(other);
    }

    public boolean isColliding(Rectangle other)
    {
        return boundingBox.overlaps(other);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setBox();
        stateTime += delta;
    }

    protected TextureRegion getCurFrame() {
        Animation<TextureRegion> currentAnimation;
        switch (state)
        {
            case IDLE:
                currentAnimation = idleAnimation;
                break;
            case WALKING:
                currentAnimation = walkAnimation;
                break;
            case DYING:
                currentAnimation = deathAnimation;
                break;
            case ATTACK:
                currentAnimation = attackAnimation;
                break;
            default:
                throw new IllegalStateException("Unexpected state: " + state);
        }
        return currentAnimation.getKeyFrame(stateTime, true);
    }

    public void drawRect(Batch batch)
    {
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 1); // 红色边界框
        shapeRenderer.rect(boundingBox.getX(), boundingBox.getY(), boundingBox.getWidth(), boundingBox.getHeight());
        shapeRenderer.setColor(0, 0, 1, 1); // 蓝色边界框
        shapeRenderer.rect(atkBox.getX(), atkBox.getY(), atkBox.getWidth(), atkBox.getHeight());
        shapeRenderer.end();
        batch.begin();
    }

    public void drawHealthBar(Batch batch) {

        float HEALTH_BAR_WIDTH = getWidth();
        float HEALTH_BAR_HEIGHT = 8;

        // 开始绘制形状
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // 绘制血条背景（灰色）
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);  // 设置为灰色
        shapeRenderer.rect(getX(), getY() + getHeight() + 10, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);

        // 绘制当前生命值（绿色）
        float healthBarWidth = (health / healthLim) * HEALTH_BAR_WIDTH;
        shapeRenderer.setColor(0.0f, 1.0f, 0.0f, 1f);  // 设置为绿色
        shapeRenderer.rect(getX(), getY() + getHeight() + 10, healthBarWidth, HEALTH_BAR_HEIGHT);

        // 结束绘制
        shapeRenderer.end();
        batch.begin();
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        TextureRegion curFrame = getCurFrame();
        batch.draw(
            curFrame,                  // 当前帧
            getX(), getY(),            // 位置
            getOriginX(), getOriginY(),// 缩放中心
            getWidth(), getHeight(),   // 宽度和高度
            getScaleX(), getScaleY(),  // 缩放比例
            getRotation()              // 旋转角度
        );
        drawRect(batch);
        drawHealthBar(batch);
    }
}
