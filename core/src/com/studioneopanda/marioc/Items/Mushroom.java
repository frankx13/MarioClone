package com.studioneopanda.marioc.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.studioneopanda.marioc.MarioClone;
import com.studioneopanda.marioc.Screens.PlayScreen;
import com.studioneopanda.marioc.Sprites.Mario;

public class Mushroom extends Item{

    public Mushroom(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("mushroom"), 0, 0, 16, 16);
        velocity = new Vector2(0.7f,0);
    }

    @Override
    public void definedItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioClone.PPM);
        fdef.filter.categoryBits = MarioClone.ITEM_BIT;
        fdef.filter.maskBits = MarioClone.MARIO_BIT |
                MarioClone.OBJECT_BIT |
                MarioClone.GROUND_BIT |
                MarioClone.COIN_BIT |
                MarioClone.BRICK_BIT |
                MarioClone.ENEMY_HEAD_BIT |
                MarioClone.ITEM_BIT;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void use(Mario mario) {
        destroy();
        mario.grow();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (!destroyed){
            setPosition(body.getPosition().x - getWidth() /2, body.getPosition().y - getHeight() /2);
            velocity.y = body.getLinearVelocity().y;
            body.setLinearVelocity(velocity);
        }
    }
}
