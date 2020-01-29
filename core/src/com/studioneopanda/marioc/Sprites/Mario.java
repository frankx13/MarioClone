package com.studioneopanda.marioc.Sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.studioneopanda.marioc.MarioClone;
import com.studioneopanda.marioc.Screens.PlayScreen;

public class Mario extends Sprite {
    public State currentState;
    public State previousState;
    public World world;
    public Body box2DBody;
    public boolean marioIsBig;
    private TextureRegion marioStand;
    private com.badlogic.gdx.graphics.g2d.Animation<TextureRegion> marioRun;
    private TextureRegion marioJump;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private TextureRegion mariodead;
    private com.badlogic.gdx.graphics.g2d.Animation<TextureRegion> bigMarioRun;
    private com.badlogic.gdx.graphics.g2d.Animation<TextureRegion> growMario;
    private float stateTimer;
    private boolean runningRight;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean marioIsDead;
//    private boolean marioIsJumping;

    public Mario(PlayScreen screen) {
        //init default values
        this.world = screen.getWorld();

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<>();

        //get run animation frames and add them to marioRun Animation
        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        }
        marioRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        }
        bigMarioRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation<TextureRegion>(0.2f, frames);

        //get jump animation frames and add them to marioJump Animation
        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);

        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);

        mariodead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);

        defineMario();
        setBounds(0, 0, 16 / MarioClone.PPM, 16 / MarioClone.PPM);
        setRegion(marioStand);
    }

    public void update(float deltaTime) {
        //to make sprite correspond with the position of the Box2D body
        if (marioIsBig) {
            setPosition(box2DBody.getPosition().x - getWidth() / 2, box2DBody.getPosition().y - getHeight() / 2 - 6 / MarioClone.PPM);
        } else {
            setPosition(box2DBody.getPosition().x - getWidth() / 2, box2DBody.getPosition().y - getHeight() / 2);
        }
        //make sprite have correct frame depending on current mario action
        setRegion(getFrame(deltaTime));

        if (timeToDefineBigMario) {
            defineBigMario();
        }
        if (timeToRedefineMario) {
            redefineMario();
        }
    }

    public TextureRegion getFrame(float deltaTime) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case DEAD:
                region = mariodead;
                break;
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if (growMario.isAnimationFinished(stateTimer)) {
                    runGrowAnimation = false;
                }
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ? bigMarioRun.getKeyFrame(stateTimer, true) : marioRun.getKeyFrame(stateTimer, true); //true to get animation loop
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig ? bigMarioStand : marioStand;
                break;
        }

        if ((box2DBody.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } else if ((box2DBody.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
        previousState = currentState;
        return region;
    }

    public State getState() {
        if (marioIsDead) {
            return State.DEAD;
        } else if (runGrowAnimation) {
            return State.GROWING;
        } else if (box2DBody.getLinearVelocity().y > 0 || (box2DBody.getLinearVelocity().y < 0 && previousState == State.JUMPING)) {
            return State.JUMPING;
        } else if (box2DBody.getLinearVelocity().y < 0) {
            return State.FALLING;
        } else if (box2DBody.getLinearVelocity().x != 0) {
            return State.RUNNING;
        } else {
            return State.STANDING;
        }
    }

    public void grow() {
        runGrowAnimation = true;
        marioIsBig = true;
        timeToDefineBigMario = true;

        setBounds(getX(), getY(), getWidth(), getHeight() * 2);
        MarioClone.manager.get("sounds/powerup.wav", Sound.class).play();
    }

    public void defineBigMario() {
        //save current position of little mario
        Vector2 currentPosition = box2DBody.getPosition();
        //generate a big mario instead of the little
        world.destroyBody(box2DBody);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0, 10 / MarioClone.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        box2DBody = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioClone.PPM);
        fdef.filter.categoryBits = MarioClone.MARIO_BIT; //filter of object
        fdef.filter.maskBits = MarioClone.GROUND_BIT |
                MarioClone.COIN_BIT |
                MarioClone.BRICK_BIT |
                MarioClone.ENEMY_BIT |
                MarioClone.OBJECT_BIT |
                MarioClone.ENEMY_HEAD_BIT |
                MarioClone.ITEM_BIT;//filter of objects allowed to collide with this one

        fdef.shape = shape;
        box2DBody.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / MarioClone.PPM));
        box2DBody.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioClone.PPM, 6 / MarioClone.PPM), new Vector2(2 / MarioClone.PPM, 6 / MarioClone.PPM));
        fdef.filter.categoryBits = MarioClone.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        box2DBody.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
    }

    public void defineMario() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / MarioClone.PPM, 32 / MarioClone.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;

        box2DBody = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioClone.PPM);
        fdef.filter.categoryBits = MarioClone.MARIO_BIT; //filter of object
        fdef.filter.maskBits = MarioClone.GROUND_BIT |
                MarioClone.COIN_BIT |
                MarioClone.BRICK_BIT |
                MarioClone.ENEMY_BIT |
                MarioClone.OBJECT_BIT |
                MarioClone.ENEMY_HEAD_BIT |
                MarioClone.ITEM_BIT;//filter of objects allowed to collide with this one

        fdef.shape = shape;
        box2DBody.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioClone.PPM, 6 / MarioClone.PPM), new Vector2(2 / MarioClone.PPM, 6 / MarioClone.PPM));
        fdef.filter.categoryBits = MarioClone.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        box2DBody.createFixture(fdef).setUserData(this);
    }

    public void redefineMario() {
        Vector2 position = box2DBody.getPosition();
        world.destroyBody(box2DBody);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;

        box2DBody = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioClone.PPM);
        fdef.filter.categoryBits = MarioClone.MARIO_BIT; //filter of object
        fdef.filter.maskBits = MarioClone.GROUND_BIT |
                MarioClone.COIN_BIT |
                MarioClone.BRICK_BIT |
                MarioClone.ENEMY_BIT |
                MarioClone.OBJECT_BIT |
                MarioClone.ENEMY_HEAD_BIT |
                MarioClone.ITEM_BIT;//filter of objects allowed to collide with this one

        fdef.shape = shape;
        box2DBody.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioClone.PPM, 6 / MarioClone.PPM), new Vector2(2 / MarioClone.PPM, 6 / MarioClone.PPM));
        fdef.filter.categoryBits = MarioClone.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        box2DBody.createFixture(fdef).setUserData(this);

        timeToRedefineMario = false;
    }

    public boolean isBig() {
        return marioIsBig;
    }

    public boolean isDead() {
        return marioIsDead;
    }

    public float getStateTimer() {
        return stateTimer;
    }

    public void hit(Enemy enemy) {
        if (enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL) {
            ((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        } else {
            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                MarioClone.manager.get("sounds/powerdownandpipe.wav", Sound.class).play();
            } else {
                MarioClone.manager.get("music/mario_music.mp3", Music.class).stop();
                MarioClone.manager.get("sounds/mariodie.wav", Sound.class).play();
                marioIsDead = true;
                Filter filter = new Filter();
                filter.maskBits = MarioClone.NOTHING_BIT;
                for (Fixture fixture : box2DBody.getFixtureList()) {
                    fixture.setFilterData(filter);
                }
                box2DBody.applyLinearImpulse(new Vector2(0, 4f), box2DBody.getWorldCenter(), true);
            }
        }
    }

    public enum State {FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD}
}
