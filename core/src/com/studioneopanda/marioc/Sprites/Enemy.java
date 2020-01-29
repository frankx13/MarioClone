package com.studioneopanda.marioc.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.studioneopanda.marioc.Screens.PlayScreen;

public abstract class Enemy extends Sprite {

    protected World world;
    protected PlayScreen screen;
    public Body box2DBody;
    public Vector2 velocity;

    public Enemy (PlayScreen screen, float x, float y){
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x,y);
        defineEnemy();
        velocity = new Vector2(-2, -1);
        box2DBody.setActive(false);
    }

    protected abstract void defineEnemy();
    public abstract void update(float deltaTime);
    public abstract void hitOnHead(Mario mario);
    public abstract  void onEnemyHit(Enemy enemy);

    public void reverseVelocity(boolean x, boolean y){
        if (x){
            velocity.x = -velocity.x;
        }
        if (y)
            velocity.y = -velocity.y;
    }
}
