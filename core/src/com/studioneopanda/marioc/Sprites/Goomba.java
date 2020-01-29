package com.studioneopanda.marioc.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.studioneopanda.marioc.MarioClone;
import com.studioneopanda.marioc.Screens.PlayScreen;

public class Goomba extends Enemy {

    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;

    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);

        frames = new Array<>();
        for (int i = 0; i < 2; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16));
        }
        walkAnimation = new Animation<>(0.4f, frames);
        stateTime = 0;

        setBounds(getX(), getY(), 16 / MarioClone.PPM, 16 / MarioClone.PPM);
        setToDestroy = false;
        destroyed = false;
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
        if (setToDestroy && !destroyed){
            world.destroyBody(box2DBody);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16,16));
            stateTime = 0;
        } else if (!destroyed){
            box2DBody.setLinearVelocity(velocity);
            setPosition(box2DBody.getPosition().x - getWidth() / 2, box2DBody.getPosition().y - getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(stateTime, true));
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;

        box2DBody = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioClone.PPM);
        fdef.filter.categoryBits = MarioClone.ENEMY_BIT; //filter of object
        fdef.filter.maskBits = MarioClone.GROUND_BIT |
                MarioClone.COIN_BIT |
                MarioClone.BRICK_BIT |
                MarioClone.ENEMY_BIT |
                MarioClone.OBJECT_BIT |
                MarioClone.MARIO_BIT; //filter of objects allowed to collide with this one

        fdef.shape = shape;
        box2DBody.createFixture(fdef).setUserData(this);

        //create the head
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5, 8).scl(1 / MarioClone.PPM);
        vertice[1] = new Vector2(5, 8).scl(1 / MarioClone.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1 / MarioClone.PPM);
        vertice[3] = new Vector2(3, 3).scl(1 / MarioClone.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = MarioClone.ENEMY_HEAD_BIT;

        box2DBody.createFixture(fdef).setUserData(this);
    }

    public void draw(Batch batch){
        if (!destroyed || stateTime < 1){
            super.draw(batch);
        }
    }

    @Override
    public void hitOnHead(Mario mario) {
        setToDestroy = true;
        MarioClone.manager.get("sounds/stomp.wav", Sound.class).play();
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Turtle && (((Turtle) enemy).currentState == Turtle.State.MOVING_SHELL)){
            setToDestroy = true;
        } else {
            reverseVelocity(true, false);
        }
    }
}
