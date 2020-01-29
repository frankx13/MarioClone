package com.studioneopanda.marioc;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.studioneopanda.marioc.Screens.PlayScreen;

public class MarioClone extends Game {
    public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 208;
    public static final float PPM = 100; //Pixels Per Meter

    public static final short NOTHING_BIT = 1; //Default value for all objects
    public static final short GROUND_BIT = 1; //Default value for all objects
    public static final short MARIO_BIT = 2;
    public static final short BRICK_BIT = 4;
    public static final short COIN_BIT = 8;
    public static final short DESTROYED_BIT = 16;
    public static final short OBJECT_BIT = 32;
    public static final short ENEMY_BIT = 64;
    public static final short ENEMY_HEAD_BIT = 128;
    public static final short ITEM_BIT = 256;
    public static final short MARIO_HEAD_BIT = 512;

    public SpriteBatch batch;
    public static AssetManager manager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        manager = new AssetManager();
        manager.load("music/mario_music.mp3", Music.class);
        manager.load("sounds/coin.wav", Sound.class);
        manager.load("sounds/bump.wav", Sound.class);
        manager.load("sounds/breakblock.wav", Sound.class);
        manager.load("sounds/powerup_spawning.wav", Sound.class);
        manager.load("sounds/powerup.wav", Sound.class);
        manager.load("sounds/stomp.wav", Sound.class);
        manager.load("sounds/powerdownandpipe.wav", Sound.class);
        manager.load("sounds/mariodie.wav", Sound.class);
        manager.finishLoading();

        setScreen(new PlayScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();

        manager.dispose();
        batch.dispose();
    }
}
