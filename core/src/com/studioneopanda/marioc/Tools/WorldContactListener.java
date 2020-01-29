package com.studioneopanda.marioc.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.studioneopanda.marioc.Items.Item;
import com.studioneopanda.marioc.MarioClone;
import com.studioneopanda.marioc.Sprites.Enemy;
import com.studioneopanda.marioc.Sprites.InteractiveTileObject;
import com.studioneopanda.marioc.Sprites.Mario;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int colDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (colDef) {
            case MarioClone.MARIO_HEAD_BIT | MarioClone.BRICK_BIT:
            case MarioClone.MARIO_HEAD_BIT | MarioClone.COIN_BIT:
                if (fixA.getFilterData().categoryBits == MarioClone.MARIO_HEAD_BIT) {
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                } else {
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                }
                break;
            case MarioClone.ENEMY_HEAD_BIT | MarioClone.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == MarioClone.ENEMY_HEAD_BIT) {
                    ((Enemy) fixA.getUserData()).hitOnHead((Mario) fixB.getUserData());
                } else {
                    ((Enemy) fixB.getUserData()).hitOnHead((Mario) fixA.getUserData());
                }
                break;
            case MarioClone.ENEMY_BIT | MarioClone.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == MarioClone.ENEMY_BIT) {
                    ((Enemy) fixA.getUserData()).onEnemyHit((Enemy) fixB.getUserData());
                } else {
                    ((Enemy) fixB.getUserData()).onEnemyHit((Enemy) fixA.getUserData());
                }
                break;
            case MarioClone.MARIO_BIT | MarioClone.ENEMY_BIT:
                if (fixA.getFilterData().categoryBits == MarioClone.MARIO_BIT) {
                    ((Mario) fixA.getUserData()).hit((Enemy) fixB.getUserData());
                } else {
                    ((Mario) fixB.getUserData()).hit((Enemy) fixA.getUserData());
                }
                break;
            case MarioClone.ENEMY_BIT | MarioClone.ENEMY_BIT:
                ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                break;
            case MarioClone.ITEM_BIT | MarioClone.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == MarioClone.ITEM_BIT) {
                    ((Item) fixA.getUserData()).reverseVelocity(true, false);
                } else {
                    ((Item) fixB.getUserData()).reverseVelocity(true, false);
                }
                break;
            case MarioClone.ITEM_BIT | MarioClone.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == MarioClone.ITEM_BIT) {
                    ((Item) fixA.getUserData()).use((Mario) fixB.getUserData());
                } else {
                    ((Item) fixB.getUserData()).use((Mario) fixA.getUserData());
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
