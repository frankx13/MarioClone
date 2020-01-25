package com.studioneopanda.marioc.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.studioneopanda.marioc.MarioClone;
import com.studioneopanda.marioc.Scenes.Hud;

import org.w3c.dom.Text;

public class PlayScreen implements Screen {

    private MarioClone game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    public PlayScreen(MarioClone game){
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(MarioClone.V_WIDTH, MarioClone.V_HEIGHT, gameCam);
        hud = new Hud(game.batch);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

    }
}
