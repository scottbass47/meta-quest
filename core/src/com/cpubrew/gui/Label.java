package com.cpubrew.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cpubrew.assets.AssetLoader;

public class Label extends Component {

	private String text;
	private GlyphLayout layout;
	private BitmapFont font;
	
	public Label() {
		this("");
	}
	
	public Label(String text) {
		this.text = text;
		font = AssetLoader.getInstance().getFont(AssetLoader.font18);
		layout = new GlyphLayout();
		backgroundColor = Color.BLACK;
		foregroundColor = Color.WHITE;
		setRenderBackground(false);
	}
	
	@Override
	public void update(float delta) {
	}

	@Override
	public void render(SpriteBatch batch) {
		layout.setText(font, text);
		
		font.setColor(foregroundColor);
		font.draw(batch, text, x + width * 0.5f - layout.width * 0.5f, y + height * 0.5f + layout.height * 0.5f);
		font.setColor(Color.WHITE);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public BitmapFont getFont() {
		return font;
	}

	public void setFont(BitmapFont font) {
		this.font = font;
	}

	public void autoSetSize() {
		layout.setText(font, text);
		setSize((int)layout.width, (int)layout.height);
	}
	
}
