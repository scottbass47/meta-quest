package com.cpubrew.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cpubrew.assets.AssetLoader;

public class TextField extends Component implements KeyListener {

	private Texture cursor;
	private float padding = 3.0f;
	private float elapsed = 0.0f;
	private BitmapFont font;
	private String text = "";
	private float cursorX = padding;
	private GlyphLayout layout;
	private boolean hasFocus = false;
	
	public TextField() {
		Pixmap pix = new Pixmap(4, 25, Format.RGBA8888);
		pix.setColor(Color.WHITE);
		pix.fill();
		
		cursor = new Texture(pix);
		pix.dispose();
		
		backgroundColor = Color.BLACK;
		
		font = AssetLoader.getInstance().getFont(AssetLoader.font18);
		layout = new GlyphLayout();
		
		setBackgroundColor(Color.BLACK);
		setFocusable(true);
		addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent ev) {
				hasFocus = false;
			}
			
			@Override
			public void focusGained(FocusEvent ev) {
				hasFocus = true;
			}
		});
		addKeyListener(this);
	}
	
	@Override
	public void update(float delta) {
		elapsed += delta;
	}
	
	@Override
	public void render(SpriteBatch batch) {
		if((int)(elapsed / 0.5f) % 2 == 0 && hasFocus) {
			batch.draw(cursor, x + cursorX, y + height / 2 - cursor.getHeight() / 2);
		}
		
		font.setColor(Color.WHITE);
		font.draw(batch, text, x + padding, y + height - height * 0.5f + layout.height * 0.5f);
	}

	@Override
	public void onKeyPress(KeyEvent ev) {
	}

	@Override
	public void onKeyRelease(KeyEvent ev) {
		
	}

	@Override
	public void onKeyType(KeyEvent ev) {
		char character = ev.getCharacter();
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
