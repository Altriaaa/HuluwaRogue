package io.github.altriaaa.huluwarogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Control
{
    public boolean UP;
    public boolean DOWN;
    public boolean LEFT;
    public boolean RIGHT;
    public boolean ATTACK;

    public void set()
    {
        UP = Gdx.input.isKeyPressed(Input.Keys.W);
        DOWN = Gdx.input.isKeyPressed(Input.Keys.S);
        LEFT = Gdx.input.isKeyPressed(Input.Keys.A);
        RIGHT = Gdx.input.isKeyPressed(Input.Keys.D);
        ATTACK = Gdx.input.isKeyPressed(Input.Keys.J);
    }
}
