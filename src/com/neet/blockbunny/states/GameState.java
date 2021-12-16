package com.neet.blockbunny.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.neet.blockbunny.handlers.GameStateManager;
import com.neet.blockbunny.main.Application;

public abstract class GameState {

	protected GameStateManager gsm;
    protected Application application;

    protected SpriteBatch sb;
    protected OrthographicCamera cam;
    protected OrthographicCamera hudCam;

    protected GameState(GameStateManager gsm) {
        this.gsm = gsm;
        application = gsm.game();
        sb = application.getSpriteBatch();
        cam = application.getCamera();
        hudCam = application.getHUDCamera();
    }

    public abstract void handleInput();

    public abstract void update(float dt);

    public abstract void render();

    public abstract void dispose();
}
