package com.studioneopanda.marioc.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.studioneopanda.marioc.MarioClone;
import com.studioneopanda.marioc.Scenes.Hud;
import com.studioneopanda.marioc.Screens.PlayScreen;

public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object) {
        super(screen, object);

        fixture.setUserData(this);
        setCategoryFilter(MarioClone.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (mario.isBig()) {
            Gdx.app.log("Brick", "Collision");
            setCategoryFilter(MarioClone.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
            MarioClone.manager.get("sounds/breakblock.wav", Sound.class).play();
        } else {
            MarioClone.manager.get("sounds/bump.wav", Sound.class).play();
        }
    }
}
