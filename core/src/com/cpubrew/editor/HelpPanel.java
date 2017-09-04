package com.cpubrew.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cpubrew.assets.AssetLoader;
import com.cpubrew.editor.action.EditorActions;
import com.cpubrew.gui.Container;
import com.cpubrew.gui.Label;

public class HelpPanel extends Container {
	
	private boolean panelOpen;
	private Texture background;
	
	private int padding = 10;
	
	public HelpPanel() {
		setSize(500, 500);
		initLabels();
		drawBackground();
	}
	
	private void initLabels() {
		BitmapFont font = AssetLoader.getInstance().getFont(AssetLoader.font18);
		
		int x = padding;
		int y = height - padding - 10;
		int maxWidth = 0;
		for(EditorActions action : EditorActions.values()){
			if(action.getShortcut() == null) continue;
			
			Label label = new Label(action.getDisplayName());
			label.setFont(font);
			label.autoSetSize();
			label.setPosition(x, y);
			
			maxWidth = Math.max(maxWidth, label.getWidth());
			
			y -= label.getHeight() + padding;
			add(label);
		}
		
		x += maxWidth + padding * 4;
		y = height - padding - 10;
		for(EditorActions action : EditorActions.values()){
			if(action.getShortcut() == null) continue;
			Label label = new Label(action.getShortcut());
			label.setFont(font);
			label.autoSetSize();
			label.setPosition(x, y);
			
			y -= label.getHeight() + padding;
			add(label);
		}
	}

	private void drawBackground() {
		Pixmap pix = new Pixmap(width, height, Format.RGBA8888);
		pix.setColor(new Color(Color.BLACK).mul(1.0f, 1.0f, 1.0f, 0.9f));
		pix.fill();
		
		background = new Texture(pix);
		pix.dispose();
	}
	
	@Override
	public void render(SpriteBatch batch) {
		batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		batch.draw(background, x, y);

		super.render(batch);
	}
	
	public void show() {
		panelOpen = true;
	}
	
	public void hide() {
		panelOpen = false;
	}
	
	public boolean isOpen() {
		return panelOpen;
	}

}
