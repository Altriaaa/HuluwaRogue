package io.github.altriaaa.huluwarogue;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.altriaaa.huluwarogue.creatures.*;
import io.github.altriaaa.huluwarogue.tiles.Obstacle;
import io.github.altriaaa.huluwarogue.tiles.Square;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

public class GameWorld
{
    private static final GameWorld world = new GameWorld();

    public FitViewport viewport;
    Stage stage;
    Array<Knight> knights;
    Array<Orc> enemies;
    Array<Obstacle> obstacles;

    float genEnemyTimer;
    float atkDtcTimer;

    ResourceManager manager = ResourceManager.getInstance();

    private GameWorld()
    {
    }

    public static GameWorld getInstance()
    {
        return world;
    }

    public void buildFromGameStat(GameStat gameStat)
    {
        setKnights(gameStat.knightsStat);
        setEnemies(gameStat.enemiesStat);

        // 更新其他全局状态（如时间、分数等）
        // ...
    }

    public GameStat getGameStat()
    {
        return new GameStat(this.knights, this.enemies);
    }


//    public class GameStat implements Json.Serializable
//    {
//        public Knight knightStat;
//        public Array<Orc> enemiesStat;
//
//        public GameStat()
//        {
//            knightStat = new Knight(); // 初始化为默认的 Knight 对象
//            enemiesStat = new Array<>(); // 初始化为空的 Array
//        }
//
//        @Override
//        public void write(Json json)
//        {
//            json.writeValue("knightStat", knightStat);
//            json.writeValue("enemiesStat", enemiesStat);
//        }
//
//        @Override
//        public void read(Json json, JsonValue jsonData)
//        {
//            knightStat = json.readValue("knightStat", Knight.class, jsonData);
//            enemiesStat = json.readValue("enemiesStat", Array.class, Orc.class, jsonData);
//        }
//
//    }

    public void worldInit()
    {
        genEnemyTimer = 0;
        atkDtcTimer = 0;
        knights = new Array<>();
        enemies = new Array<>();
        obstacles = new Array<>();
        viewport = new FitViewport(1260, 910);
        stage = new Stage(viewport);
        generateMap();
//        addKnight();
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
        if (genEnemyTimer > 15.0f)
        {
            genEnemyTimer = 0;
            Orc orc = (Orc) createCreature(new OrcFactory());
            enemies.add(orc);
            stage.addActor(orc);
            orc.startBehavior();
        }
    }

//    public void setStage(Stage s)
//    {
//        Array<Actor> actors = this.stage.getActors();
//        for (int i = 0; i < actors.size; i++)
//        {
//            s.addActor(actors.get(i));
//        }
//        this.stage.dispose();
//        this.stage = s;
//    }

    public Stage getStage()
    {
        return stage;
    }

    public void addKnight(String id)
    {
        Knight knight = (Knight) createCreature(new KnightFactory());
        knight.setId(id);
        knights.add(knight);
        stage.addActor(knight);
    }

    public void setKnights(Array<Knight> knights)
    {
//        this.knight.remove();
//        this.knight = knight;
//        stage.addActor(this.knight);
        // 首先，遍历knights中的每个骑士
        for (Knight newKnight : knights)
        {
            boolean found = false;
            for (Knight oldKnight : this.knights)
            {
                if (Objects.equals(oldKnight.getId(), newKnight.getId()))
                {
                    oldKnight.updateStat(newKnight);
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                this.knights.add(newKnight);
                stage.addActor(newKnight);
            }
        }
        Iterator<Knight> iterator = this.knights.iterator();
        while (iterator.hasNext())
        {
            Knight oldKnight = iterator.next();
            boolean foundInKnights = false;
            for (Knight newKnight : knights)
            {
                if (Objects.equals(oldKnight.getId(), newKnight.getId()))
                {
                    foundInKnights = true;
                    break; // 找到后跳出循环
                }
            }
            if (!foundInKnights)
            {
                oldKnight.remove();
                iterator.remove();
            }
        }
    }

    public Array<Knight> getKnights()
    {
        return knights;
    }

    public Knight getKnightById(String Id)
    {
        for (int i = knights.size - 1; i >= 0; i--)
        {
            if (Objects.equals(knights.get(i).getId(), Id))
            {
                return knights.get(i);
            }
        }
        return null;
    }

    public void removeKnightById(String Id)
    {
        Iterator<Knight> iterator = this.knights.iterator();
        while (iterator.hasNext())
        {
            Knight knight = iterator.next();
            if (Objects.equals(knight.getId(), Id))
            {
                knight.remove();
                iterator.remove();
                return;
            }
        }
    }

    public void setEnemies(Array<Orc> enemies)
    {
        for (Orc newOrc : enemies)
        {
            boolean found = false;
            for (Orc oldOrc : this.enemies)
            {
                if (Objects.equals(oldOrc.getId(), newOrc.getId()))
                {
                    oldOrc.updateStat(newOrc);
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                this.enemies.add(newOrc);
                stage.addActor(newOrc);
            }
        }
        Iterator<Orc> iterator = this.enemies.iterator();
        while (iterator.hasNext())
        {
            Orc oldOrc = iterator.next();
            boolean foundInEnemies = false;
            for (Orc newOrc : enemies)
            {
                if (Objects.equals(oldOrc.getId(), newOrc.getId()))
                {
                    foundInEnemies = true;
                    break; // 找到后跳出循环
                }
            }
            if (!foundInEnemies)
            {
                oldOrc.remove();
                iterator.remove();
            }
        }
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
                for (int j = knights.size - 1; j >= 0; j--)
                {
                    Knight knight = knights.get(j);
                    if (knight.getState() == Creature.CharacterState.ATTACK && knight.isAttacking(curEnemy.getBoundingBox()))
                    {
                        curEnemy.vulDamage(knight.getDamage());
                        if (curEnemy.getHealth() <= 0)
                            enemies.removeIndex(i);
                    }
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
        for (int i = enemies.size - 1; i >= 0; i--)
        {
            enemies.get(i).remove();
        }
        manager.dispose();
    }
}
