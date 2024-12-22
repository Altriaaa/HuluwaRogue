package io.github.altriaaa.huluwarogue.tiles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import io.github.altriaaa.huluwarogue.ResourceManager;

public class Obstacle extends Tile
{
    private Rectangle boundingBox;
    private final ShapeRenderer shapeRenderer;

    public Obstacle()
    {
        super(ResourceManager.getInstance().getAtlas("effect").findRegion("obstacle"));
        shapeRenderer = new ShapeRenderer();
    }

    public Obstacle(float x, float y)
    {
        this();
        this.setPosition(x, y);
        boundingBox = new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public void drawRect(Batch batch)
    {
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 1, 0, 1); // 红色边界框
        shapeRenderer.rect(boundingBox.getX(), boundingBox.getY(), boundingBox.getWidth(), boundingBox.getHeight());
        shapeRenderer.end();
        batch.begin();
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        batch.draw(
            region,                  // 当前帧
            getX(), getY(),            // 位置
            getOriginX(), getOriginY(),// 缩放中心
            getWidth(), getHeight(),   // 宽度和高度
            getScaleX(), getScaleY(),  // 缩放比例
            getRotation()              // 旋转角度
        );
//        drawRect(batch);
    }

}
