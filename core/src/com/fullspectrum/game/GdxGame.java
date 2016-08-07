package com.fullspectrum.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GdxGame extends Game {
	// Dimensions
	public static final int WORLD_WIDTH = 1280;
	public static final int WORLD_HEIGHT = 720;
	
	// Rendering
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Viewport viewport;
	private TextureAtlas knightAtlas;
	private Animation knightRunning;
	private Sprite knight;
	private BitmapFont font;
	private final static float SPEED = 200f;
	private final static float DURATION = 1.0f / 10;
	private float animationTime = 0;
	private boolean facingRight = true;
	private ArrayMap<ScreenState, Screen> screens;
	
	// FPS Logging
	private int fps = 0;
	private int drawFPS = fps;
	private long startTime = System.nanoTime();
	private boolean fpsOn = false;
	private FPSLogger fpsLogger;
	private boolean prevPressed = false;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
		font = new BitmapFont();
		fpsLogger = new FPSLogger();
		
		camera.position.x = WORLD_WIDTH * 0.5f;
		camera.position.y = WORLD_HEIGHT * 0.5f;
		
		// Init Screens
		screens = new ArrayMap<ScreenState, Screen>();
		screens.put(ScreenState.MENU, new MenuScreen(camera, this, screens));
		screens.put(ScreenState.GAME, new GameScreen(camera, this, screens));
		setScreen(screens.get(ScreenState.MENU));
		
// 		// Load Animation
//		knightAtlas = new TextureAtlas(Gdx.files.internal("sprites/knight_anim.atlas"));
//		knightRunning = new Animation(DURATION, knightAtlas.getRegions(), Animation.PlayMode.LOOP);
//		for (TextureRegion tr : knightRunning.getKeyFrames()){
//			tr.getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
//		}
//		
//		// Setup Knight Sprite
//		knight = new Sprite(knightRunning.getKeyFrames()[0]);
//		knight.setOrigin(knight.getWidth() * 0.5f, knight.getHeight() * 0.5f);
//		knight.setPosition(WORLD_WIDTH * 0.5f - knight.getWidth() * 0.5f, WORLD_HEIGHT * 0.5f - knight.getHeight() * 0.5f);
//		knight.setScale(6.0f);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.4f, 0.4f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
//		fps++;
//		Gdx.gl.glClearColor(0.4f, 0.4f, 0.8f, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		
//		camera.update();
//		batch.setProjectionMatrix(camera.combined);
//		
//		// Update and Draw Animation
//		animationTime += Gdx.graphics.getDeltaTime();
//		
//		batch.begin();
//		TextureRegion frame = knightRunning.getKeyFrame(animationTime);
//		knight.setRegion(frame);
//		if(facingRight) knight.setFlip(false, false);
//		else knight.setFlip(true, false);
//		knight.draw(batch);
//		if(fpsOn) {
//			font.draw(batch, "" + drawFPS, 10, 710);
//		}
//		batch.end();
//		
//		// Input
//		if(Gdx.input.isKeyPressed(Keys.RIGHT)){
//			knight.setX(knight.getX() + Gdx.graphics.getDeltaTime() * SPEED);
//			facingRight = true;
//		}
//		
//		if(Gdx.input.isKeyPressed(Keys.LEFT)){
//			knight.setX(knight.getX() - Gdx.graphics.getDeltaTime() * SPEED);
//			facingRight = false;
//		}
//		
//		// Setup P to Toggle FPS
//		if(Gdx.input.isKeyPressed(Keys.P)){
//			if(!prevPressed) fpsOn = !fpsOn;
//			prevPressed = false;
//			if(fpsOn){
//				startTime = System.nanoTime();
//				fps = 0;
//			}
//		}
//		prevPressed = Gdx.input.isKeyPressed(Keys.P);
//		
//		// FPS
//		if((System.nanoTime() - startTime) / 1000000 > 1000 && fpsOn){
//			drawFPS = fps;
//			startTime = System.nanoTime();
//			fps = 0;
//			fpsLogger.log();
//		}
	}
	
	@Override
	public void dispose () {
		super.dispose();
		batch.dispose();
		knightAtlas.dispose();
		font.dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		viewport.update(width, height);
	}
}
