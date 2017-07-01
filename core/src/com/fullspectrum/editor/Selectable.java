package com.fullspectrum.editor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public interface Selectable<T> {

	public void update(float delta, LevelEditor editor);
	public Vector2 getPosition(Vector2 offset);
	public void render(SpriteBatch batch, Vector2 worldPos, LevelEditor editor);
	public Selectable<T> copy(LevelEditor editor);
	public void remove(LevelEditor editor);
	public boolean contentsEqual(T value);
	public void move(Vector2 position, LevelEditor editor);
}
