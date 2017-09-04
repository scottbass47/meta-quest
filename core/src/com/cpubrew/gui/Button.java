package com.cpubrew.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.cpubrew.assets.AssetLoader;

public class Button extends Component implements MouseListener{

	private String text;
	private BitmapFont font;
	private Color backgroundColor;
	private Color fontColor;
	private Color hoverColor;
	private Color pressColor;
	private Color activeColor;
	
	private ShapeRenderer shape;
	private GlyphLayout layout;
	private Array<ActionListener> listeners;
	
	public Button() {
		this("");
	}
	public Button(String text) {
		this.text = text;
		font = AssetLoader.getInstance().getFont(AssetLoader.font18);
		
		backgroundColor = Color.BLACK;
		fontColor = Color.WHITE;
		hoverColor = Color.DARK_GRAY;
		pressColor = Color.LIGHT_GRAY;
		
		activeColor = backgroundColor;
		layout = new GlyphLayout();
		
		shape = new ShapeRenderer();
		listeners = new Array<ActionListener>();
		addMouseListener(this);
	}
	
	@Override
	public void update(float delta) {
		
	}

	@Override
	public void render(SpriteBatch batch) {
		batch.end();
		
		shape.setProjectionMatrix(batch.getProjectionMatrix());
		shape.setColor(activeColor);
		shape.begin(ShapeType.Filled);
		shape.rect(x, y, width, height);
		shape.end();
		
		batch.begin();

		layout.setText(font, text);
		
		Color oldColor = batch.getColor();
		batch.setColor(fontColor);
		font.draw(batch, text, x + width * 0.5f - layout.width * 0.5f, y + height * 0.5f + layout.height * 0.5f);
		batch.setColor(oldColor);
	}
	

	@Override
	public void onMouseMove(int x, int y) {
		activeColor = hoverColor;
	}

	@Override
	public void onMouseDrag(int x, int y) {
		activeColor = pressColor;
	}

	@Override
	public void onMouseUp(int x, int y, int button) {
		for(ActionListener listener : listeners) {
			listener.onAction(new ActionEvent(this));
		}
		activeColor = hoverColor;
	}

	@Override
	public void onMouseDown(int x, int y, int button) {
		activeColor = pressColor;
	}
	
	@Override
	public void onMouseEnter(int x, int y) {
		activeColor = hoverColor;
	}

	@Override
	public void onMouseExit(int x, int y) {
		activeColor = backgroundColor;
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

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		activeColor = backgroundColor;
	}

	public Color getFontColor() {
		return fontColor;
	}

	public void setFontColor(Color fontColor) {
		this.fontColor = fontColor;
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
