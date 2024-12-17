package io.github.altriaaa.huluwarogue;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import io.github.altriaaa.huluwarogue.creatures.Knight;
import io.github.altriaaa.huluwarogue.creatures.Orc;

public class GameStat
{
    public Array<Knight> knightsStat;
    public Array<Orc> enemiesStat;

    public GameStat()
    {
        knightsStat = new Array<>();
        enemiesStat = new Array<>();
    }

    public GameStat(Array<Knight> k, Array<Orc> e)
    {
        knightsStat = k;
        enemiesStat = e;
//        enemiesStat = new Array<>();
    }
}
