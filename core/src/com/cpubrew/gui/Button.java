package com.cpubrew.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.cpubrew.assets.AssetLoader;

public class Button extends Component implements MouseListener{

	private String text;
	private BitmapFont font;
	private Color noActionColor;
	private Color hoverColor;
	private Color pressColor;
	
	private GlyphLayout layout;
	private Array<ActionListener> listeners;
	
	public Button() {
		this("");
	}
	public Button(String text) {
		this.text = text;
		font = AssetLoader.getInstance().getFont(AssetLoader.font18);
		
		foregroundColor = Color.WHITE;
		noActionColor = Color.BLACK;
		hoverColor = Color.DARK_GRAY;
		pressColor = Color.LIGHT_GRAY;
		
		backgroundColor = noActionColor;
		layout = new GlyphLayout();
		
		listeners = new Array<ActionListener>();
		addMouseListener(this);
	}
	
	@Override
	public void update(float delta) {
	}

	@Override
	public void render(SpriteBatch batch) {
		layout.setText(font, text);
		
		Color oldColor = batch.getColor();
		batch.setColor(foregroundColor);
		font.draw(batch, text, x + width * 0.5f - layout.width * 0.5f, y + height * 0.5f + layout.height * 0.5f);
		batch.setColor(oldColor);
	}
	

	@Override
	public void onMouseMove(MouseEvent ev) {
		backgroundColor = hoverColor;
	}

	@Override
	public void onMouseDrag(MouseEvent ev) {
		backgroundColor = pressColor;
	}

	@Override
	public void onMouseUp(MouseEvent ev) {
		for(ActionListener listener : listeners) {
			listener.onAction(new ActionEvent(this));
		}
		backgroundColor = hoverColor;
	}

	@Override
	public void onMouseDown(MouseEvent ev) {
		backgroundColor = pressColor;
	}
	
	@Override
	public void onMouseEnter(MouseEvent ev) {
		backgroundColor = hoverColor;
	}

	@Override
	public void onMouseExit(MouseEvent ev) {
		backgroundColor = noActionColor;
	}
	
	public void addListener(ActionListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(ActionListener listener) {
		this.listeners.removeValue(listener, false);
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

	public Color getHoverColor() {
		return hoverColor;
	}

	public void setHoverColor(Color hoverColor) {
		this.hoverColor = hoverColor;
	}

	public Color getPressColor() {
		return pressColor;
	}

	public void setPressColor(Color pressColor) {
		this.pressColor = pressColor;
	}

}
