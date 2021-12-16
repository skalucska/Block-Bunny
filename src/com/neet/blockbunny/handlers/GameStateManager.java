package com.neet.blockbunny.handlers;


import java.util.Stack;

import com.neet.blockbunny.main.Application;
import com.neet.blockbunny.states.GameState;
import com.neet.blockbunny.states.Play;

public class GameStateManager {

	private Application game;
    private Stack<GameState> gameStates;

    public static final int PLAY = 912837;

    public GameStateManager(Application game) {
        this.game = game;
        gameStates = new Stack<GameState>();
        pushState(PLAY);
    }

    public Application game() {
        return game;
    }

    public void update(float dt) {
        gameStates.peek().update(dt);
    }

    public void render() {
        gameStates.peek().render();
    }

    private GameState getState(int state) {
        if (state == PLAY)
            return new Play(this);
        return null;
    }

    public void setState(int state) {
        popState();
        pushState(state);
    }

    public void pushState(int state) {
        gameStates.push(getState(state));
    }

    public void popState() {
        GameState g = gameStates.pop();
        g.dispose();
    }
}
