package io.github.altriaaa.huluwarogue.creatures;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Json;

public class MyActor extends Actor
{
    protected float health;
    protected float damage;
    protected float healthLim;

    public MyActor()
    {
        healthLim = 20;
        health = healthLim;
        damage = 3;
    }

    public static void main(String[] args)
    {
        MyActor actor = new MyActor();
        actor.damage = 100;
//        Stage stage = new Stage();
//        stage.addActor(actor);
        Json json = new Json();
        System.out.println("wocaonima " + json.toJson(actor));
    }
}
