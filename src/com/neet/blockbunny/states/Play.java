package com.neet.blockbunny.states;

import static com.neet.blockbunny.handlers.B2DVars.PPM;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.neet.blockbunny.entities.Crystal;
import com.neet.blockbunny.entities.HUD;
import com.neet.blockbunny.entities.Player;
import com.neet.blockbunny.handlers.B2DVars;
import com.neet.blockbunny.handlers.Background;
import com.neet.blockbunny.handlers.GameStateManager;
import com.neet.blockbunny.handlers.MyContactListerner;
import com.neet.blockbunny.handlers.MyInput;
import com.neet.blockbunny.main.Application;

public class Play extends GameState {

    private boolean debug = false;

    private World world;
    private Box2DDebugRenderer b2dr;

    private OrthographicCamera b2dCam;
    private MyContactListerner cl;

    private TiledMap tileMap;
    private float tileSize;
    private OrthogonalTiledMapRenderer tmr;

    private Player player;
    private Array<Crystal> crystals;
    
    private Background[] backgrounds;

    private HUD hud;

    public Play(GameStateManager gsm) {

        super(gsm);

        // set up box2d stuff
        world = new World(new Vector2(0, -9.81f), true);
        cl = new MyContactListerner();
        world.setContactListener(cl);
        b2dr = new Box2DDebugRenderer();

        // create player
        createPlayer();

        // create tiles
        createTiles();

        // create crystals
        createCrystals();
        
        // create backgrounds
        Texture bgs = Application.res.getTexture("bgs");
		TextureRegion sky = new TextureRegion(bgs, 0, 0, 320, 240);
		TextureRegion clouds = new TextureRegion(bgs, 0, 240, 320, 240);
		TextureRegion mountains = new TextureRegion(bgs, 0, 480, 320, 240);
		backgrounds = new Background[3];
		backgrounds[0] = new Background(sky, cam, 0f);
		backgrounds[1] = new Background(clouds, cam, 0.1f);
		backgrounds[2] = new Background(mountains, cam, 0.2f);
        /*
         * // create platform
         * BodyDef bdef = new BodyDef();
         * bdef.position.set(160 / PPM, 120 / PPM);
         * bdef.type = BodyType.StaticBody;
         * Body body = world.createBody(bdef);
         * 
         * PolygonShape shape = new PolygonShape();
         * shape.setAsBox(50 / PPM, 5 / PPM);
         * FixtureDef fdef = new FixtureDef();
         * fdef.shape = shape;
         * fdef.filter.categoryBits = B2DVars.BIT_GROUND;
         * fdef.filter.maskBits = B2DVars.BIT_PLAYER;
         * body.createFixture(fdef).setUserData("ground");
         */

        // // create ball
        // bdef.position.set(153 / PPM, 220 / PPM);
        // body = world.createBody(bdef);

        // CircleShape cshape = new CircleShape();
        // cshape.setRadius(5 / PPM);
        // fdef.shape = cshape;
        // // fdef.restitution = 0.2f;
        // fdef.filter.categoryBits = B2DVars.BIT_BALL;
        // fdef.filter.maskBits = B2DVars.BIT_GROUND;
        // body.createFixture(fdef).setUserData("ball");

        // set up box2d cam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, Application.V_WIDTH / PPM, Application.V_HEIGHT / PPM);

        // set up hud
        hud = new HUD(player);

    }

    public void handleInput() {

        // player jump
        if (MyInput.isPressed(MyInput.BUTTON1)) {
            if (cl.isPlayerOnGround()) {
                player.getBody().applyForceToCenter(0, 250, true);
            }
        }
        if (MyInput.isDown(MyInput.BUTTON2)) {
            System.out.println("hold x");
        }

        // switch block
        if (MyInput.isPressed(MyInput.BUTTON2)) {
            switchBlocks();
        }
    }

    public void update(float dt) {
        // check input
        handleInput();

        // update box2d
        world.step(dt, 6, 2);

        // remove crystals
        Array<Body> bodies = cl.getBodiesToRemove();
        for (int i = 0; i < bodies.size; i++) {
            Body b = bodies.get(i);
            crystals.removeValue((Crystal) b.getUserData(), true);
            world.destroyBody(b);
            player.collectCrystal();
        }
        bodies.clear();
        player.update(dt);

        for (int i = 0; i < crystals.size; i++) {
            crystals.get(i).update(dt);
        }

    }

    public void render() {

        // clear screen
        Gdx.gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // set camera to follow player
        cam.position.set(player.getPosition().x * PPM + Application.V_WIDTH / 4, Application.V_HEIGHT / 2, 0);
        cam.update();
        
        // draw bgs
        
        sb.setProjectionMatrix(hudCam.combined);
        for(int i = 0; i < backgrounds.length; i++) {
        	backgrounds[i].render(sb);
        }
        // draw tile map
        tmr.setView(cam);
        tmr.render();

        // draw player
        sb.setProjectionMatrix(cam.combined);
        player.render(sb);

        // draw crystals
        for (int i = 0; i < crystals.size; i++) {
            crystals.get(i).render(sb);
        }

        // draw hud
        sb.setProjectionMatrix(hudCam.combined);
        hud.render(sb);

        // draw box2d world
        if (debug) {
            b2dr.render(world, b2dCam.combined);
        }
    }

    public void dispose() {
    }

    private void createPlayer() {

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        // create player
        bdef.position.set(100 / PPM, 200 / PPM);
        bdef.type = BodyType.DynamicBody;
        bdef.linearVelocity.set(1f, 0);
        Body body = world.createBody(bdef);

        shape.setAsBox(13 / PPM, 13 / PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_RED | B2DVars.BIT_CRYSTAL;
        // fdef.restitution = 1f;
        body.createFixture(fdef).setUserData("player");

        // create foot sensor
        shape.setAsBox(13 / PPM, 2 / PPM, new Vector2(0, -13 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_RED;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("foot");

        // create player
        player = new Player(body);

        body.setUserData(player);
    }

    private void createTiles() {

        // load tile map
        tileMap = new TmxMapLoader().load("res/maps/level1.tmx");
        tmr = new OrthogonalTiledMapRenderer(tileMap);
        tileSize = (int) tileMap.getProperties().get("tilewidth");
        TiledMapTileLayer layer;
        layer = (TiledMapTileLayer) tileMap.getLayers().get("red");
        createLayer(layer, B2DVars.BIT_RED);

        layer = (TiledMapTileLayer) tileMap.getLayers().get("green");
        createLayer(layer, B2DVars.BIT_GREEN);

        layer = (TiledMapTileLayer) tileMap.getLayers().get("blue");
        createLayer(layer, B2DVars.BIT_BLUE);

    }

    private void createLayer(TiledMapTileLayer layer, short bits) {

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        // go through all the cells in the layer
        for (int row = 0; row < layer.getHeight(); row++) {
            for (int col = 0; col < layer.getWidth(); col++) {

                // get cell
                Cell cell = layer.getCell(col, row);

                // check if cell exists
                if (cell == null)
                    continue;
                if (cell.getTile() == null)
                    continue;

                // create a body + fixture
                bdef.type = BodyType.StaticBody;
                bdef.position.set(
                        (col + 0.5f) * tileSize / PPM,
                        (row + 0.5f) * tileSize / PPM);

                ChainShape cs = new ChainShape();
                Vector2[] v = new Vector2[3];
                v[0] = new Vector2(
                        -tileSize / 2 / PPM, -tileSize / 2 / PPM);
                v[1] = new Vector2(
                        -tileSize / 2 / PPM, tileSize / 2 / PPM);
                v[2] = new Vector2(
                        tileSize / 2 / PPM, tileSize / 2 / PPM);
                cs.createChain(v);
                fdef.friction = 0;
                fdef.shape = cs;
                fdef.filter.categoryBits = bits;
                fdef.filter.maskBits = B2DVars.BIT_PLAYER;
                fdef.isSensor = false;
                world.createBody(bdef).createFixture(fdef);

            }
        }
    }

    private void createCrystals() {

        crystals = new Array<Crystal>();

        MapLayer layer = tileMap.getLayers().get("crystals");

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        for (MapObject mo : layer.getObjects()) {
            bdef.type = BodyType.StaticBody;

            float x = (float) mo.getProperties().get("x") / PPM;
            float y = (float) mo.getProperties().get("y") / PPM;

            bdef.position.set(x, y);

            CircleShape cshape = new CircleShape();
            cshape.setRadius(8 / PPM);

            fdef.shape = cshape;
            fdef.isSensor = true;
            fdef.filter.categoryBits = B2DVars.BIT_CRYSTAL;
            fdef.filter.maskBits = B2DVars.BIT_PLAYER;

            Body body = world.createBody(bdef);
            body.createFixture(fdef).setUserData("crystal");

            Crystal c = new Crystal(body);
            crystals.add(c);

            body.setUserData(c);
        }
    }

    private void switchBlocks() {
    	
        Filter filter = player.getBody().getFixtureList().first()
        		.getFilterData();
        short bits = filter.maskBits;

        // switch to next color
        // red -> green -> blue -> red
        if ((bits & B2DVars.BIT_RED) != 0) {
            bits &= ~B2DVars.BIT_RED;
            bits |= B2DVars.BIT_GREEN;
        } else if ((bits & B2DVars.BIT_GREEN) != 0) {
            bits &= ~B2DVars.BIT_GREEN;
            bits |= B2DVars.BIT_BLUE;
        } else if ((bits & B2DVars.BIT_BLUE) != 0) {
            bits &= ~B2DVars.BIT_BLUE;
            bits |= B2DVars.BIT_RED;
        }

        // set new mask bits
        filter.maskBits = bits;
        player.getBody().getFixtureList().first().setFilterData(filter);

        // set new mask bits for foot
        filter = player.getBody().getFixtureList().get(1).getFilterData();
        bits &= ~B2DVars.BIT_CRYSTAL;
        filter.maskBits = bits;
        player.getBody().getFixtureList().get(1).setFilterData(filter);
    }
}
