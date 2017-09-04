package com.cpubrew.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ImageLabel extends Component {

	private TextureRegion region;
	private float scale;
	
	public ImageLabel(TextureRegion region) {
		this(region, 1.0f);
	}
	
	public ImageLabel(TextureRegion region, float scale) {
		if(region == null) throw new IllegalArgumentException("Texture Region must be a non-null value.");
		this.region = region;
		this.scale = scale;

		setScale(scale);
	}
	
	
	@Override
	public void update(float delta) {
	}

	@Override
	public void render(SpriteBatch batch) {
		batch.draw(region, x, y, 0.0f, 0.0f, region.getRegionWidth(), region.getRegionHeight(), scale, scale, 0.0f);
	}

	public void setScale(float scale) {
		this.scale = scale;
		setSize((int)(region.getRegionWidth() * scale), (int)(region.getRegionHeight() * scale));
	}
	
	public float getScale() {
		return scale;
	}
	
	public void setRegion(TextureRegion region) {
		this.region = region;
	}
	
	public TextureRegion getRegion() {
		return region;
	}
}
