package com.studioneopanda.marioc.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.studioneopanda.marioc.MarioClone;
import com.studioneopanda.marioc.Scenes.Hud;
import com.studioneopanda.marioc.Sprites.Mario;
import com.studioneopanda.marioc.Tools.Box2DWorldCreator;

public class PlayScreen implements Screen {

    private MarioClone game;

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    //Tiled map
    private TmxMapLoader mapLoader; // will load the map into the game
    private TiledMap map; // reference to the map itself;
    private OrthogonalTiledMapRenderer renderer;

    //Box2D
    private World world;
    private Box2DDebugRenderer b2dr;

    private Mario player;

    public PlayScreen(MarioClone game) {
        this.game = game;

        //cam used to follow player through cam world
        gameCam = new OrthographicCamera();

        //FitViewPort maintain visual aspect ratio on any screen
        gamePort = new FitViewport(MarioClone.V_WIDTH / MarioClone.PPM, MarioClone.V_HEIGHT / MarioClone.PPM, gameCam);

        //HUD to manage the score/level etc.. all infos displayed
        hud = new Hud(game.batch);

        //Load map and setup the map renderer
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioClone.PPM);

        //Initially set the gameCam to be centered correctly at the beginning
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, -10), true);

        b2dr = new Box2DDebugRenderer();

        new Box2DWorldCreator(world, map);

        player = new Mario(world);
    }

    public void handleInput(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
            player.box2DBody.applyLinearImpulse(new Vector2(0, 4f), player.box2DBody.getWorldCenter(), true);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.box2DBody.getLinearVelocity().x >= -2)
            player.box2DBody.applyLinearImpulse(new Vector2(-0.1f, 0), player.box2DBody.getWorldCenter(), true);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.box2DBody.getLinearVelocity().x <= 2)
            player.box2DBody.applyLinearImpulse(new Vector2(0.1f, 0), player.box2DBody.getWorldCenter(), true);
    }

    public void update(float deltaTime) {
        //check for any user input
        handleInput(deltaTime);

        world.step(1 / 60f, 6, 2);

        gameCam.position.x = player.box2DBody.getPosition().x;

        //update cam pos
        gameCam.update();
        //tell renderer to only draw objects inside our camera vision field
        renderer.setView(gameCam);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //separate the update logic from render
        update(delta);

        //clear the game screen with Black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render the game map
        renderer.render();

        //render the Box2DDebugLines
        b2dr.render(world, gameCam.combined);

        //set the batch to now draw what the HUD camera sees
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
