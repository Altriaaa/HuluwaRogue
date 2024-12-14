package io.github.altriaaa.huluwarogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public class ResourceManager {
    private static final ResourceManager instance = new ResourceManager();
    private final Map<String, TextureAtlas> atlases = new HashMap<>();

    private ResourceManager() {}

    public static ResourceManager getInstance() {
        return instance;
    }

    public void loadAtlas(String name, String path) {
        atlases.put(name, new TextureAtlas(Gdx.files.internal(path)));
    }

    public TextureAtlas getAtlas(String name) {
        return atlases.get(name);
    }

    public Animation<TextureRegion> createAnimation(String atlasName, float frameDuration, Animation.PlayMode playMode)
    {
        TextureAtlas atlas = getAtlas(atlasName);
        return new Animation<>(frameDuration, atlas.getRegions(), playMode);
    }

    public void dispose() {
        for (TextureAtlas atlas : atlases.values()) {
            atlas.dispose();
        }
    }
}
