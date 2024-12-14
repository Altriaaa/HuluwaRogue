package io.github.altriaaa.huluwarogue.creatures;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
// import com.sun.org.apache.xml.internal.utils.res.XResourceBundle;
import io.github.altriaaa.huluwarogue.Main;
import io.github.altriaaa.huluwarogue.ResourceManager;

import java.util.MissingFormatArgumentException;

public interface CreatureFactory<T extends Creature>
{
    public T create();
}
