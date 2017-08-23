package com.fullspectrum.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.game.GameVars;

public class TextField extends Component implements KeyListener {

	private Color backgroundColor;
	private ShapeRenderer shape;
	private Texture cursor;
	private float padding = 3.0f;
	private float elapsed = 0.0f;
	private BitmapFont font;
	private String text = "";
	private float cursorX = padding;
	private GlyphLayout layout;
	
	public TextField() {
		shape = new ShapeRenderer();
		
		Pixmap pix = new Pixmap(4, 25, Format.RGBA8888);
		pix.setColor(Color.WHITE);
		pix.fill();
		
		cursor = new Texture(pix);
		pix.dispose();
		
		font = AssetLoader.getInstance().getFont(AssetLoader.font18);
		
		layout = new GlyphLayout();
	}
	
	@Override
	public void update(float delta) {
		elapsed += delta;
	}
	
	@Override
	public void render(SpriteBatch batch) {
		batch.end();
		
		shape.setProjectionMatrix(batch.getProjectionMatrix());
		shape.setColor(backgroundColor);
		shape.begin(ShapeType.Filled);
		shape.rect(x, y, width, height);
		shape.end();
		
		batch.begin();
		
		if((int)(elapsed * 0.05f * GameVars.UPS) % 2 == 0 && hasFocus()) {
			batch.draw(cursor, x + cursorX, y + padding);
		}
		
		font.setColor(Color.WHITE);
		font.getData().setScale(1.0f);
		font.draw(batch, text, x + padding, y + height - height * 0.5f + layout.height * 0.5f);
	}

	@Override
	public void onKeyPress(int keycode) {
	}

	@Override
	public void onKeyRelease(int keycode) {
		
	}

	@Override
	public void onKeyType(char character) {
		if(character == '\b') {
			if(text.length() > 0) {
				text = text.substring(0, text.length() - 1);
			}
		} else {
			text += character;
		}
		layout.setText(font, text);
		cursorX = layout.width + padding;
	}
	
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	
	public void setFont(BitmapFont font) {
		this.font = font;
	}
	
	public BitmapFont getFont() {
		return font;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
		layout.setText(font, text);
		cursorX = layout.width + padding;
	}

}
