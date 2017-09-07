package com.cpubrew.arena;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cpubrew.assets.AssetLoader;
import com.cpubrew.game.GameVars;
import com.cpubrew.gui.Container;
import com.cpubrew.gui.KeyAdapter;
import com.cpubrew.gui.KeyEvent;
import com.cpubrew.gui.Label;
import com.cpubrew.utils.RenderUtils;

public class ArenaDeathScreen extends Container {

	private Texture background;
	private Label title;
	private Label description;
	
	public ArenaDeathScreen(final Arena arena, OrthographicCamera hudCamera) {
		setSize(GameVars.SCREEN_WIDTH, GameVars.SCREEN_HEIGHT);
		setFocusable(true);
		
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
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void onKeyRelease(KeyEvent ev) {
				if(ev.getKey() == Keys.ENTER) {
					arena.switchState(ArenaState.PICKING_PLAYER);
				}
			}
		});
	}
	
	@Override
	public void render(SpriteBatch batch) {
		batch.draw(background, x, y);
		super.render(batch);
	}
	
}
