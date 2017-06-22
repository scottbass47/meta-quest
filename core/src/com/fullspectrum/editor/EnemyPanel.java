package com.fullspectrum.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.editor.gui.Container;
import com.fullspectrum.entity.EntityIndex;

public class EnemyPanel extends Container {
	
	private boolean panelOpen;
	private float animTime = 0.0f;
	private Texture background;
	private ArrayMap<EntityIndex, Rectangle> icons;
	private Array<SelectListener> listeners;
	
	private float scale = 3.0f;
	private float padding = 8.0f;
	
	public EnemyPanel() {
		setSize(500, 500);
		icons = new ArrayMap<EntityIndex, Rectangle>();
		listeners = new Array<SelectListener>();
		
		drawBackground();
	}
	
	private void drawBackground() {
		Pixmap pix = new Pixmap(width, height, Format.RGBA8888);
		pix.setColor(new Color(Color.BLACK).mul(1.0f, 1.0f, 1.0f, 0.9f));
		pix.fill();
		
		background = new Texture(pix);
		pix.dispose();
	}

	@Override
	public void update(float delta) {
		animTime += delta;
		updatePanel("");
	}
	
	public void updatePanel(String filter) {
		filter = filter.toLowerCase().trim();
		
		icons.clear();
		ArrayMap<EntityIndex, Rectangle> row = new ArrayMap<EntityIndex, Rectangle>();
		
		float xx = x + padding * scale;
		float yy = y + height - padding * scale;
		float height = 0;
		
		int length = EntityIndex.values().length;
		for(int i = 0; i < length; i++) {
			EntityIndex index = EntityIndex.values()[i];
			if(index == EntityIndex.ALCHEMIST || index == EntityIndex.ROGUE || index == EntityIndex.KNIGHT) continue;
			
			Rectangle hitbox = index.getHitBox();
			
			if(!index.name().toLowerCase().startsWith(filter)) continue;

			// Need to create a new row
			if(hitbox.width * scale + xx > x + width) {
				for(EntityIndex idx : row.keys()) {
					Rectangle rect = row.get(idx);
					rect.y = yy - (height * 0.5f) - rect.height * 0.5f;
					icons.put(idx, rect);
				}
				row.clear();
				yy -= height + padding * scale;
				xx = x + padding * scale;
				height = 0;
			} 
			
			Rectangle placement = new Rectangle();
			placement.x = xx;
			placement.y = yy;
			placement.width = hitbox.width * scale;
			placement.height = hitbox.height * scale;
			height = Math.max(height, hitbox.height * scale);
			
			xx += placement.width + padding * scale;

			row.put(index, placement);
		}
		
		for(EntityIndex idx : row.keys()) {
			Rectangle rect = row.get(idx);
			rect.y = yy - (height * 0.5f) - rect.height * 0.5f;
			icons.put(idx, rect);
		}
	}
	
	@Override
	public void render(SpriteBatch batch) {
		batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		batch.draw(background, x, y);
		
		for(EntityIndex index : icons.keys()) {
			Animation animation = index.getIdleAnimation();
			TextureRegion region = animation.getKeyFrame(animTime);
			Rectangle rect = icons.get(index);
			
			float w = region.getRegionWidth();
			float h = region.getRegionHeight();
			float hw = w * 0.5f;
			float hh = h * 0.5f;
			
			batch.draw(region, rect.x + rect.width * 0.5f - hw, rect.y + rect.height * 0.5f - hh, hw, hh, w, h, scale, scale, 0.0f);
		}
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
	
	public EntityIndex getIndex(float x, float y) {
		for(EntityIndex index : icons.keys()) {
			Rectangle rect = icons.get(index);
			if(rect.contains(x, y)) return index;
		}
		return null;
	}
	
	public void addListener(SelectListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(SelectListener listener) {
		listeners.removeIndex(listeners.indexOf(listener, false));
	}

	// ---------
	// - INPUT -
	// ----------
	// NOTE: All coords are HUD coords (already converted by level editor)
	
	@Override
	public void onMouseUp(int x, int y, int button) {
		EntityIndex index = getIndex(x, y);
		if(index != null) {
			for(SelectListener listener : listeners) {
				listener.onSelect(index);
			}
		}
	}

}
