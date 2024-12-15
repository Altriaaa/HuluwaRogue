package io.github.altriaaa.huluwarogue;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.altriaaa.huluwarogue.creatures.*;
import io.github.altriaaa.huluwarogue.tiles.Obstacle;
import io.github.altriaaa.huluwarogue.tiles.Square;

import java.util.AbstractCollection;
import java.util.Random;

public class GameWorld
{
    private static final GameWorld world = new GameWorld();

    public FitViewport viewport;
    Stage stage;
    Knight knight;
    Array<Orc> enemies;
    Array<Obstacle> obstacles;

    private static final float FIXED_DELTA_TIME = 1 / 60f;
    //    float delta;
    float genEnemyTimer;
    float atkDtcTimer;
    private boolean running;

    ResourceManager manager = ResourceManager.getInstance();


    private GameWorld()
    {
    }

    public static GameWorld getInstance()
    {
        return world;
    }

    public void worldInit()
    {
        this.running = true;
        genEnemyTimer = 0;
        atkDtcTimer = 0;
        enemies = new Array<>();
        obstacles = new Array<>();
        knight = (Knight) createCreature(new KnightFactory());
        viewport = new FitViewport(1260, 910);
        stage = new Stage(viewport);
        generateMap();
        stage.addActor(knight);
    }

    public void generateMap()
    {
        float worldWidth = stage.getWidth();
        float worldHeight = stage.getHeight();
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

    public void assetInit()
    {
        manager.loadAtlas("ui", "ui/uiskin.atlas");
        manager.loadAtlas("effect", "Effects/Effect.atlas");
        manager.loadAtlas("knight_idle", "Heroes/Knight/Idle/Idle.atlas");
        manager.loadAtlas("knight_run", "Heroes/Knight/Run/Run.atlas");
        manager.loadAtlas("knight_attack", "Heroes/Knight/Attack/Attack.atlas");
        manager.loadAtlas("knight_death", "Heroes/Knight/Death/Death.atlas");
        manager.loadAtlas("orc_idle", "Enemy/Orc/Idle.atlas");
        manager.loadAtlas("orc_run", "Enemy/Orc/Run.atlas");
        manager.loadAtlas("orc_attack", "Enemy/Orc/Attack.atlas");
        manager.loadAtlas("orc_death", "Enemy/Orc/Death.atlas");
    }

    <T extends Creature> Creature createCreature(CreatureFactory<T> factory)
    {
        return factory.create();
    }

    public void createEnemy(float delta)
    {
        genEnemyTimer += delta;
        if (genEnemyTimer > 5.0f)
        {
            genEnemyTimer = 0;
            Orc orc = (Orc) createCreature(new OrcFactory());
            enemies.add(orc);
            stage.addActor(orc);
        }
    }

    public void setStage(Stage s)
    {
        Array<Actor> actors = this.stage.getActors();
        for (int i = 0; i < actors.size; i++)
        {
            s.addActor(actors.get(i));
        }
        this.stage.dispose();
        this.stage = s;
    }

    public Stage getStage()
    {
        return stage;
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
        atkDtcTimer += delta;
        if (atkDtcTimer > 0.3f)
        {
            atkDtcTimer = 0;
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

    public void update(float delta)
    {
        createEnemy(delta);
        atkDetect(delta);
        stage.act(delta);
    }

    public void clear()
    {
        running = false;
        for (int i = enemies.size - 1; i >= 0; i--)
        {
            enemies.get(i).remove();
        }
        manager.dispose();
    }

//    public static void main(String[] args)
//    {
//        GameWorld world = GameWorld.getInstance();
//        world.assetInit();
//        world.worldInit();
//        world.run();
//    }

}
