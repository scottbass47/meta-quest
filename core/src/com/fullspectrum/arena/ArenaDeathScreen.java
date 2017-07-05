package com.fullspectrum.arena;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.gui.Label;
import com.fullspectrum.gui.Window;
import com.fullspectrum.utils.RenderUtils;

public class ArenaDeathScreen extends Window{

	private Arena arena;
	private Texture background;
	private Label title;
	private Label description;
	
	public ArenaDeathScreen(Arena arena, OrthographicCamera hudCamera) {
		this.arena = arena;
		
		setHudCamera(hudCamera);
		setSize(GameVars.SCREEN_WIDTH, GameVars.SCREEN_HEIGHT);
	
		title = new Label("Slaughtered!");
		title.setFont(AssetLoader.getInstance().getFont(AssetLoader.font72));
		title.autoSetSize();
		title.setPosition(GameVars.SCREEN_WIDTH / 2 - title.getWidth() / 2, 550);
		
		description = new Label("Press enter to restart");
		description.setFont(AssetLoader.getInstance().getFont(AssetLoader.font28));
		description.autoSetSize();
		description.setPosition(GameVars.SCREEN_WIDTH / 2 - description.getWidth() / 2, title.getY() - description.getHeight() - 20);
		
		background = RenderUtils.createBackground(width, height, new Color(0.0f, 0.0f, 0.0f, 0.9f));
	
		add(title);
		add(description);
	}
	
	@Override
	public void render(SpriteBatch batch) {
		batch.begin();
		batch.draw(background, x, y);
		batch.end();
		
		super.render(batch);
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.ENTER) {
			arena.switchState(ArenaState.PICKING_PLAYER);
		}
		
		return super.keyUp(keycode);
	}
	
}
