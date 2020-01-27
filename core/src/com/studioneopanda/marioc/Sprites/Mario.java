package com.studioneopanda.marioc.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.studioneopanda.marioc.MarioClone;

public class Mario extends Sprite {
    public World world;
    public Body box2DBody;

    public Mario(World world){
        this.world = world;
        defineMario();
    }

    public void defineMario() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / MarioClone.PPM, 32 / MarioClone.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;

        box2DBody = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5 / MarioClone.PPM);

        fdef.shape = shape;
        box2DBody.createFixture(fdef);
    }
}
