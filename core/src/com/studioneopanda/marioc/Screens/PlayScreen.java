package com.studioneopanda.marioc.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.studioneopanda.marioc.Items.Item;
import com.studioneopanda.marioc.Items.ItemDef;
import com.studioneopanda.marioc.Items.Mushroom;
import com.studioneopanda.marioc.MarioClone;
import com.studioneopanda.marioc.Scenes.Hud;
import com.studioneopanda.marioc.Sprites.Enemy;
import com.studioneopanda.marioc.Sprites.Mario;
import com.studioneopanda.marioc.Tools.Box2DWorldCreator;
import com.studioneopanda.marioc.Tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {
    //reference to our Game used to set screens
    private MarioClone game;
    private TextureAtlas atlas;

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
    private Box2DWorldCreator creator;

    private Mario player;

    private Music music;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    public PlayScreen(MarioClone game) {
        atlas = new TextureAtlas("Mario_and_Enemies.pack");

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

        //create the box2d world with no x gravity and -10 for y with bodies allowed to sleep
        world = new World(new Vector2(0, -10), true);

        //render with debuglines
        b2dr = new Box2DDebugRenderer();

        creator = new Box2DWorldCreator(this);

        //create mario in the game world
        player = new Mario(this);

        world.setContactListener(new WorldContactListener());

        music = MarioClone.manager.get("music/mario_music.mp3", Music.class);
        music.setLooping(true);
        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

    public void spawnItems(ItemDef idef) {
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll();
            if (idef.type == Mushroom.class) {
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }

    public void handleInput(float deltaTime) {
        if (player.currentState != Mario.State.DEAD) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
                player.box2DBody.applyLinearImpulse(new Vector2(0, 4f), player.box2DBody.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.box2DBody.getLinearVelocity().x >= -2)
                player.box2DBody.applyLinearImpulse(new Vector2(-0.1f, 0), player.box2DBody.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.box2DBody.getLinearVelocity().x <= 2)
                player.box2DBody.applyLinearImpulse(new Vector2(0.1f, 0), player.box2DBody.getWorldCenter(), true);
        }
    }

    public void update(float deltaTime) {
        //check for any user input
        handleInput(deltaTime);
        handleSpawningItems();

        world.step(1 / 60f, 6, 2);

        player.update(deltaTime);

        for (Enemy enemy : creator.getEnemies()) {
            enemy.update(deltaTime);
            if (enemy.getX() < player.getX() + 224 / MarioClone.PPM) {
                enemy.box2DBody.setActive(true);
            }
        }

        for (Item item : items) {
            item.update(deltaTime);
        }

        hud.update(deltaTime);

        //attach camera to player.x coords
        if (player.currentState != Mario.State.DEAD){
            gameCam.position.x = player.box2DBody.getPosition().x;
        }

        //update cam pos
        gameCam.update();
        //tell renderer to only draw objects inside our camera vision field
        renderer.setView(gameCam);
    }

    public TextureAtlas getAtlas() {
        return atlas;
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

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy : creator.getEnemies()) {
            enemy.draw(game.batch);
        }
        for (Item item : items) {
            item.draw(game.batch);
        }
        game.batch.end();

        //set the batch to now draw what the HUD camera sees
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if (gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    public boolean gameOver(){
        if (player.currentState == Mario.State.DEAD && player.getStateTimer() > 3){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
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
