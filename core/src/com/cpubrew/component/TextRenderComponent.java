package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TextRenderComponent implements Poolable, Component {

	public BitmapFont font;
	public Color color = Color.WHITE;
	public String text = "";
	
	public TextRenderComponent set(BitmapFont font, Color color, String text){
		this.font = font;
		this.color = color;
		this.text = text;
		return this;
	}
	
	@Override
	public void reset() {
		font = null;
		color = Color.WHITE;
		text = "";
	}
}