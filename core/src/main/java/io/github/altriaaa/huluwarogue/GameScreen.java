package io.github.altriaaa.huluwarogue;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.altriaaa.huluwarogue.creatures.*;
import io.github.altriaaa.huluwarogue.tiles.Obstacle;
import io.github.altriaaa.huluwarogue.tiles.Square;
// import sun.font.CreatedFontTracker;
// import sun.tools.jstat.Jstat;

import java.util.Random;

public class GameScreen implements Screen
{
    final Main game;

    // 舞台
    Stage stage;

    // 角色
    Knight knight;
    Array<Orc> enemies;
    Array<Obstacle> obstacles;

    float genEnemyTimer;
    float cldDtcTimer;

    public GameScreen(final Main game)
    {
        this.game = game;

        ResourceManager resourceManager = game.resourceManager;
        genEnemyTimer = 0;
        cldDtcTimer = 0;
        enemies = new Array<>();
        obstacles = new Array<>();

        // 纹理
        resourceManager.loadAtlas("ui", "ui/uiskin.atlas");
        resourceManager.loadAtlas("effect", "Effects/Effect.atlas");
        resourceManager.loadAtlas("knight_idle", "Heroes/Knight/Idle/Idle.atlas");
        resourceManager.loadAtlas("knight_run", "Heroes/Knight/Run/Run.atlas");
        resourceManager.loadAtlas("knight_attack", "Heroes/Knight/Attack/Attack.atlas");
        resourceManager.loadAtlas("knight_death", "Heroes/Knight/Death/Death.atlas");
        resourceManager.loadAtlas("orc_idle", "Enemy/Orc/Idle.atlas");
        resourceManager.loadAtlas("orc_run", "Enemy/Orc/Run.atlas");
        resourceManager.loadAtlas("orc_attack", "Enemy/Orc/Attack.atlas");
        resourceManager.loadAtlas("orc_death", "Enemy/Orc/Death.atlas");

        // 角色
        knight = (Knight) createCreature(new KnightFactory(game, this));

        // 舞台
        stage = new Stage(game.viewport);
        generateMap();
        stage.addActor(knight);

    }

    public void generateMap()
    {
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();
        float tileWidth = new Square().getWidth();
        float tileHeight = new Square().getHeight();
        int xNum = (int) (worldWidth / tileWidth);
        int yNum = (int) (worldHeight / tileHeight);

        Random random = new Random();
        for (int i = 0; i < xNum; i++)
        {
            for (int j = 0; j < yNum; j++)
            {
                if (random.nextFloat() < 0.1)
                {
                    Obstacle obstacle = new Obstacle(i * tileWidth, j * tileHeight);
                    stage.addActor(obstacle);
                    obstacles.add(obstacle);
                }
                else
                {
                    stage.addActor(new Square(i * tileWidth, j * tileHeight));
                }
            }
        }
    }

    public void createEnemy(float delta)
    {
        genEnemyTimer += delta;
        if(genEnemyTimer > 5.0f)
        {
            genEnemyTimer = 0;
            Orc orc = (Orc) createCreature(new OrcFactory(game, this));
            enemies.add(orc);
            stage.addActor(orc);
        }
    }

    public Knight getKnight()
    {
        return knight;
    }

    public Array<Orc> getEnemies()
    {
        return enemies;
    }

    public Array<Obstacle> getObstacles()
    {
        return obstacles;
    }

    public void atkDetect(float delta)
    {
        cldDtcTimer += delta;
        if(cldDtcTimer > 0.3f)
        {
            cldDtcTimer = 0;
            for (int i = enemies.size - 1; i >= 0; i--)
            {
                Orc curEnemy = enemies.get(i);
                if (knight.getState() == Creature.CharacterState.ATTACK && knight.isAttacking(curEnemy.getBoundingBox()))
                {
                    curEnemy.vulDamage(knight.getDamage());
                    if (curEnemy.getHealth() <= 0)
                        enemies.removeIndex(i);
                }
            }
        }
    }


    <T extends Creature> Creature createCreature(CreatureFactory<T> factory)
    {
        return factory.create();
    }

    @Override
    public void show()
    {
//        music.play();
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void render(float delta)
    {
        ScreenUtils.clear(Color.BLACK); //清屏
        createEnemy(delta);
        atkDetect(delta);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose()
    {
        for(int i = enemies.size-1; i >= 0; i--)
        {
            enemies.get(i).remove();
        }
        stage.dispose();
        game.resourceManager.dispose();
    }
}
