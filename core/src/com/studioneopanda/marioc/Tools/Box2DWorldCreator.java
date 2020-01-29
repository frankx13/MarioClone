package com.studioneopanda.marioc.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.studioneopanda.marioc.MarioClone;
import com.studioneopanda.marioc.Screens.PlayScreen;
import com.studioneopanda.marioc.Sprites.Brick;
import com.studioneopanda.marioc.Sprites.Coin;
import com.studioneopanda.marioc.Sprites.Enemy;
import com.studioneopanda.marioc.Sprites.Goomba;
import com.studioneopanda.marioc.Sprites.Turtle;

public class Box2DWorldCreator {

    private Array<Goomba> goombas;
    private Array<Turtle> turtles;

    public Box2DWorldCreator(PlayScreen screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //0background 1graphics 2ground 3pipes 4coins 5bricks
        //parsing through all objects of type Rectangle created in Tiled
        //CREATE GROUND OBJECTS
        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioClone.PPM, (rect.getY() + rect.getHeight() / 2) / MarioClone.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioClone.PPM, rect.getHeight() / 2 / MarioClone.PPM);
            fdef.shape = shape;

            body.createFixture(fdef);
        }

        //PIPES OBJECTS
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioClone.PPM, (rect.getY() + rect.getHeight() / 2) / MarioClone.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioClone.PPM, rect.getHeight() / 2 / MarioClone.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MarioClone.OBJECT_BIT;

            body.createFixture(fdef);
        }

        //BRICKS OBJECTS
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            new Brick(screen, object);
        }

        //COINS OBJECTS
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            new Coin(screen, object);
        }

        //GOOMBAS OBJECTS
        goombas = new Array<Goomba>();
        for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            goombas.add(new Goomba(screen, rect.getX() / MarioClone.PPM, rect.getY() / MarioClone.PPM));
        }

        //TURTLES OBJECTS
        turtles = new Array<Turtle>();
        for (MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            turtles.add(new Turtle(screen, rect.getX() / MarioClone.PPM, rect.getY() / MarioClone.PPM));
        }
    }

    public Array<Enemy> getEnemies() {
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(goombas);
        enemies.addAll(turtles);
        return enemies;
    }

//    public static void removeTurtles(Turtle turtle){
//        turtles.removeValue(turtle);
//    }
}
