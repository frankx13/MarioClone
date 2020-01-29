package com.studioneopanda.marioc.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.studioneopanda.marioc.Items.ItemDef;
import com.studioneopanda.marioc.Items.Mushroom;
import com.studioneopanda.marioc.MarioClone;
import com.studioneopanda.marioc.Scenes.Hud;
import com.studioneopanda.marioc.Screens.PlayScreen;

public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28; // = 27+1

    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);

        tileSet = map.getTileSets().getTileSet("NES - Super Mario Bros - Tileset");
        fixture.setUserData(this);
        setCategoryFilter(MarioClone.COIN_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        Gdx.app.log("Coin", "Collision");
        if (getCell().getTile().getId() == BLANK_COIN) {
            MarioClone.manager.get("sounds/bump.wav", Sound.class).play();
        } else {
            if (object.getProperties().containsKey("mushroom") && !mario.isBig()) {
                screen.spawnItems(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioClone.PPM),
                        Mushroom.class));
                MarioClone.manager.get("sounds/powerup_spawning.wav", Sound.class).play();
            } else {
                MarioClone.manager.get("sounds/coin.wav", Sound.class).play();
            }
        }
        getCell().setTile(tileSet.getTile(BLANK_COIN));
        Hud.addScore(100);
    }
}
