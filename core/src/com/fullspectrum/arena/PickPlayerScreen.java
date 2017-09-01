package com.fullspectrum.arena;

import java.awt.Point;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fullspectrum.gui.AnimatedLabel;
import com.fullspectrum.gui.Container;
import com.fullspectrum.gui.ImageLabel;
import com.fullspectrum.gui.KeyAdapter;
import com.fullspectrum.gui.Label;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.game.GameVars;

public class PickPlayerScreen extends Container {

	private Texture background;
	private Label pickPlayerLabel;
	private ImageLabel arrowLabel;
	private AnimatedLabel knightLabel;
	private AnimatedLabel rogueLabel;
	private AnimatedLabel monkLabel;
	private int labelScale = 4;
	private AnimatedLabel[] characters;
	private int index = 0;
	private EntityIndex selected;
	
	public PickPlayerScreen(final Arena arena, OrthographicCamera hudCamera) {
		createBackground();
		setSize(GameVars.SCREEN_WIDTH, GameVars.SCREEN_WIDTH);
		setFocusable(true);

		pickPlayerLabel = new Label("Choose Your Character");
		pickPlayerLabel.setFont(AssetLoader.getInstance().getFont(AssetLoader.font36));
		pickPlayerLabel.autoSetSize();
		pickPlayerLabel.setPosition(GameVars.SCREEN_WIDTH / 2 - pickPlayerLabel.getWidth() / 2, 600);
		
		knightLabel = new AnimatedLabel(EntityIndex.KNIGHT.getIdleAnimation(), labelScale);
		rogueLabel = new AnimatedLabel(EntityIndex.ROGUE.getIdleAnimation(), labelScale);
		monkLabel = new AnimatedLabel(EntityIndex.MONK.getIdleAnimation(), labelScale);
		
		Point knightOff = new Point();
		Point rogueOff = new Point(-30, -54);
		Point monkOff = new Point(-50, 16);

		int spacing = 20;
		int totalWidth = knightLabel.getWidth() + knightOff.x + spacing + rogueLabel.getWidth() + rogueOff.x + spacing + monkLabel.getWidth() + monkOff.x;
		
		int x = GameVars.SCREEN_WIDTH / 2 - totalWidth / 2;
		int y = 300;
		
		knightLabel.setPosition(x + knightOff.x, y + knightOff.y);
		x += knightLabel.getWidth() + spacing;
		
		rogueLabel.setPosition(x + rogueOff.x, y + rogueOff.y);
		x += rogueLabel.getWidth() + spacing;
		
		monkLabel.setPosition(x + monkOff.x, y + monkOff.y);
		
		arrowLabel = new ImageLabel(AssetLoader.getInstance().getRegion(Asset.VERTICAL_ARROW), labelScale);
		setArrowLabelPos(knightLabel);
		
		characters = new AnimatedLabel[]{ knightLabel, rogueLabel, monkLabel };

		add(pickPlayerLabel);
		add(knightLabel);
		add(rogueLabel);
		add(monkLabel);
		add(arrowLabel);
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void onKeyRelease(int keycode) {
				if(keycode == Keys.RIGHT) {
					index++;
				} else if(keycode == Keys.LEFT) {
					index--;
				} else if(keycode == Keys.ENTER) {
					selected = index == 0 ? EntityIndex.KNIGHT : (index == 1 ? EntityIndex.ROGUE : EntityIndex.MONK);
					arena.switchState(ArenaState.PLAYING);
				}
				
				if(index < 0) index = characters.length - 1;
				if(index >= characters.length) index = 0;
				
				setArrowLabelPos(characters[index]);
			}
		});
	}
	
	private void createBackground() {
		Pixmap pix = new Pixmap(GameVars.SCREEN_WIDTH, GameVars.SCREEN_HEIGHT, Format.RGBA8888);
		Color col = new Color(Color.BLACK);
		pix.setColor(col.mul(1.0f, 1.0f, 1.0f, 0.9f));
		pix.fill();
		
		background = new Texture(pix);
		pix.dispose();
	}
	
	private void setArrowLabelPos(AnimatedLabel characterLabel) {
		arrowLabel.setPosition(characterLabel.getX() + characterLabel.getWidth() / 2 - arrowLabel.getWidth() / 2, knightLabel.getY() - arrowLabel.getHeight() - 10);
	}
	
	@Override
	public void render(SpriteBatch batch) {
		batch.draw(background, 0, 0);
		super.render(batch);
	}
	
	public EntityIndex getSelectedPlayer() {
		return selected;
	}

}
