package io.github.altriaaa.huluwarogue;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.altriaaa.huluwarogue.creatures.*;
import io.github.altriaaa.huluwarogue.network.GameStatMessage;
import io.github.altriaaa.huluwarogue.tiles.Obstacle;
import io.github.altriaaa.huluwarogue.tiles.Square;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;

public class GameWorld
{
    private static final GameWorld world = new GameWorld();

    public FitViewport viewport;
    public boolean showBox;
    private boolean isPaused;
    Stage stage;
    Group characterGroup;
    Group mapGroup;
    Array<Knight> knights;
    Array<Orc> enemies;
    Array<Obstacle> obstacles;
    int xNum;
    int yNum;
    int[][] map;

    float genEnemyTimer;
    float atkDtcTimer;

    ResourceManager manager = ResourceManager.getInstance();
    private static final String SAVE_FILE_PATH = "saves/save.json";
    private static final String MAP_FILE_PATH = "saves/map.json";

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
    }

    public GameStat getGameStat()
    {
        return new GameStat(this.knights, this.enemies);
    }

    public void save()
    {
        Json json = new Json();
        String gameStat = json.toJson(this.getGameStat());
        String mapStat = json.toJson(this.getMap());
//        File saveDir = new File("saves");
//        if (!saveDir.exists())
//        {
//            saveDir.mkdirs(); // 创建目录
//        }
        try (FileWriter gameWriter = new FileWriter(SAVE_FILE_PATH); FileWriter mapWriter = new FileWriter(MAP_FILE_PATH))
        {
            gameWriter.write(gameStat);
            mapWriter.write(mapStat);
            System.out.println("Game saved");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void load()
    {
        Json json = new Json();
        try (BufferedReader gameReader = new BufferedReader(new FileReader(SAVE_FILE_PATH));
             BufferedReader mapReader = new BufferedReader(new FileReader(MAP_FILE_PATH)))
        {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = gameReader.readLine()) != null)
            {
                sb.append(line);
            }
            GameStat gameStat = json.fromJson(GameStat.class, sb.toString());
            sb.clear();
            while ((line = mapReader.readLine()) != null)
            {
                sb.append(line);
            }
            int[][] map = json.fromJson(int[][].class, sb.toString());
            this.refreshMap(map);
            this.buildFromGameStat(gameStat);
            for(int i = enemies.size-1; i >= 0; i--)
            {
                enemies.get(i).startBehavior();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void worldInit()
    {
        showBox = false;
        isPaused = false;
        genEnemyTimer = 0;
        atkDtcTimer = 0;
        knights = new Array<>();
        enemies = new Array<>();
        obstacles = new Array<>();
        viewport = new FitViewport(1260, 910);
        stage = new Stage(viewport);
        characterGroup = new Group();
        mapGroup = new Group();
        stage.addActor(mapGroup);
        stage.addActor(characterGroup);
        setWorldScale();
        map = new int[xNum][yNum];
        generateMap();
        refreshMap(map);
    }

    public void pause()
    {
        isPaused = true;
    }

    public void resume()
    {
        isPaused = false;
    }

    public boolean isPaused()
    {
        return isPaused;
    }

    public void setWorldScale()
    {
        float worldWidth = stage.getWidth();
        float worldHeight = stage.getHeight();
        float tileWidth = new Square().getWidth();
        float tileHeight = new Square().getHeight();
        xNum = (int) (worldWidth / tileWidth);
        yNum = (int) (worldHeight / tileHeight);
    }

    public void generateMap()
    {
        Random random = new Random();
        for (int i = 0; i < xNum; i++)
        {
            for (int j = 0; j < yNum; j++)
            {
                if (random.nextFloat() < 0.1 && i != 0)
                {
                    map[i][j] = 0;
                }
                else
                {
                    map[i][j] = 1;
                }
            }
        }
    }

    public void refreshMap(int[][] map)
    {
        this.map = map;
        float tileWidth = new Square().getWidth();
        float tileHeight = new Square().getHeight();
        for (int i = 0; i < xNum; i++)
        {
            for (int j = 0; j < yNum; j++)
            {
                if (map[i][j] == 0)
                {
                    Obstacle obstacle = new Obstacle(i * tileWidth, j * tileHeight);
                    mapGroup.addActor(obstacle);
                    obstacles.add(obstacle);
                }
                else
                {
                    mapGroup.addActor(new Square(i * tileWidth, j * tileHeight));
                }
            }
        }
    }

    public int[][] getMap()
    {
        return map;
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
        if (genEnemyTimer > 15.0f && enemies.size < 9)
        {
            genEnemyTimer = 0;
            Orc orc = (Orc) createCreature(new OrcFactory());
            enemies.add(orc);
            characterGroup.addActor(orc);
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
        characterGroup.addActor(knight);
    }

    public synchronized void setKnights(Array<Knight> knights)
    {
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
                characterGroup.addActor(newKnight);
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

    public synchronized Array<Knight> getKnights()
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

    public synchronized void setEnemies(Array<Orc> enemies)
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
                characterGroup.addActor(newOrc);
//                newOrc.startBehavior();
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

    public synchronized Array<Orc> getEnemies()
    {
        return enemies;
    }

    public synchronized Array<Obstacle> getObstacles()
    {
        return obstacles;
    }

    public void changeBoxShow()
    {
        this.showBox = !this.showBox;
    }

    public boolean getShowBox()
    {
        return showBox;
    }

    public synchronized void atkDetect(float delta)
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
                    if (curEnemy.getState() == Creature.CharacterState.ATTACK && curEnemy.isAttacking(knight.getBoundingBox()))
                    {
                        knight.vulDamage(curEnemy.getDamage());
                        if (knight.getHealth() <= 0)
                            knights.removeIndex(j);
                    }
                }
            }
        }
    }

    public void update(float delta)
    {
        if (isPaused)
        {
            return;
        }
        createEnemy(delta);
        atkDetect(delta);
        stage.act(delta);
    }

    public void clear()
    {
//        for (int i = enemies.size - 1; i >= 0; i--)
//        {
//            enemies.get(i).vulDamage(enemies.get(i).getHealth());
//            enemies.get(i).remove();
//        }
//        for (int i = knights.size - 1; i >= 0; i--)
//        {
//            knights.get(i).vulDamage(knights.get(i).getHealth());
//            knights.get(i).remove();
//        }
        Orc.shutdownGlobalExecutorService();
        Timer.instance().clear();
        if (stage != null) stage.dispose();
        manager.dispose();
        this.pause();
    }
}
