package com.studioneopanda.marioc.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.studioneopanda.marioc.MarioClone;
import com.studioneopanda.marioc.Screens.PlayScreen;

public class Turtle extends Enemy {
    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;

    public State currentState;
    public State previousState;
    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;
    private TextureRegion shell;
    private float deadRotationDegrees;

    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);

        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 0, 0, 16, 24));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 16, 0, 16, 24));
        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"), 64, 0, 16, 24);

        walkAnimation = new Animation<TextureRegion>(0.2f, frames);
        currentState = previousState = State.WALKING;
        deadRotationDegrees = 0;

        setBounds(getX(), getY(), 16 / MarioClone.PPM, 24 / MarioClone.PPM);
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
        fdef.restitution = 1.5f;
        fdef.filter.categoryBits = MarioClone.ENEMY_HEAD_BIT;

        box2DBody.createFixture(fdef).setUserData(this);
    }

    public TextureRegion getFrame(float deltaTime) {
        TextureRegion region;

        switch (currentState) {
            case STANDING_SHELL:
            case MOVING_SHELL:
                region = shell;
                break;
            case WALKING:
            default:
                region = walkAnimation.getKeyFrame(stateTime, true);
                break;
        }

        if (velocity.x > 0 && region.isFlipX() == false) {
            region.flip(true, false);
        }
        if (velocity.x < 0 && region.isFlipX() == true) {
            region.flip(true, false);
        }

        stateTime = currentState == previousState ? stateTime + deltaTime : 0;
        previousState = currentState;
        return region;
    }

    @Override
    public void update(float deltaTime) {
        setRegion(getFrame(deltaTime));
        if (currentState == State.STANDING_SHELL && stateTime > 5) {
            currentState = State.WALKING;
            velocity.x = 10;
        }

        setPosition(box2DBody.getPosition().x - getWidth() / 2, box2DBody.getPosition().y - 8 / MarioClone.PPM);

        if (currentState == State.DEAD) {
            deadRotationDegrees += 3;
            rotate(deadRotationDegrees);
            if (stateTime > 5 && !destroyed) {
                world.destroyBody(box2DBody);
                destroyed = true;
            }
        } else {
            box2DBody.setLinearVelocity(velocity);
        }
    }

    @Override
    public void hitOnHead(Mario mario) {
        if (currentState != State.STANDING_SHELL) {
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        } else {
            kick(mario.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
        }
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Turtle) {
            if (((Turtle) enemy).currentState == State.MOVING_SHELL && currentState != State.MOVING_SHELL) {
                killed();
            } else if (currentState == State.MOVING_SHELL && ((Turtle) enemy).currentState == State.WALKING) {
                return;
            } else {
                reverseVelocity(true,false);
            }
        } else if (currentState != State.MOVING_SHELL) {
            reverseVelocity(true, false);
        }
    }

    public void kick(int speed) {
        velocity.x = speed;
        currentState = State.MOVING_SHELL;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void killed() {
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = MarioClone.NOTHING_BIT;

        for (Fixture fixture : box2DBody.getFixtureList()) {
            fixture.setFilterData(filter);
        }

        box2DBody.applyLinearImpulse(new Vector2(0, 5f), box2DBody.getWorldCenter(), true);
    }

    public void draw(Batch batch){
        if (!destroyed){
            super.draw(batch);
        }
    }

    public enum State {WALKING, STANDING_SHELL, MOVING_SHELL, DEAD}
}
