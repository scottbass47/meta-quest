package com.cpubrew.editor.mapobject;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public interface MapObjectRenderer {

	public void render(SpriteBatch batch, Vector2 position);
	public void setAnimTime(float time);
	public MapObjectRenderer createCopy();
}
