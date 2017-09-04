package com.cpubrew.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.cpubrew.entity.EntityIndex;
import com.cpubrew.gui.ActionEvent;
import com.cpubrew.gui.ActionListener;
import com.cpubrew.gui.Component;
import com.cpubrew.gui.Container;
import com.cpubrew.gui.TextField;

public class EnemyPanel extends Container {
	
	private boolean panelOpen;
	private Texture background;
	private ArrayMap<EntityIndex, EnemyIcon> iconMap;
	private Array<SelectListener> listeners;
	private TextField textField;
	
	private float scale = 3.0f;
	private float horizPadding = 16.0f;
	private float vertPadding = 8.0f;
	
	public EnemyPanel() {
		setSize(500, 500);
		iconMap = new ArrayMap<EntityIndex, EnemyIcon>();
		listeners = new Array<SelectListener>();
		
		textField = new TextField();
		textField.setBackgroundColor(Color.BLACK);
		textField.setSize(width, 30);
		textField.setPosition(0, y + height - textField.getHeight());
		
		add(textField);
		
		// Wont work yet...
//		textField.requestFocus();
		
		drawBackground();
		initButtons();
	}
	
	private void drawBackground() {
		Pixmap pix = new Pixmap(width, height, Format.RGBA8888);
		pix.setColor(new Color(Color.BLACK).mul(1.0f, 1.0f, 1.0f, 0.9f));
		pix.fill();
		
		background = new Texture(pix);
		pix.dispose();
	}
	
	private void initButtons() {
		int length = EntityIndex.values().length;
		
		for(int i = 0; i < length; i++) {
			final EntityIndex index = EntityIndex.values()[i];
			EnemyIcon icon = new EnemyIcon(index, scale);
			iconMap.put(index, icon);
			
			icon.addListener(new ActionListener() {
				@Override
				public void onAction(ActionEvent event) {
					for(SelectListener listener : listeners) {
						listener.onSelect(index);
					}
				}
			});
		}
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		updatePanel(textField.getText());
	}
	
	public void updatePanel(String filter) {
		filter = filter.toLowerCase().trim();
		
		for(int i = 0; i < getComponents().size; i++) {
			Component comp = getComponents().get(i);
			if(comp instanceof EnemyIcon) {
				remove(comp);
				i--;
			}
		}
		
		ArrayMap<EntityIndex, EnemyIcon> row = new ArrayMap<EntityIndex, EnemyIcon>();
		
		float xx = x + horizPadding * scale;
		float yy = y + height - textField.getHeight() - vertPadding * scale;
		float height = 0;
		
		for(EntityIndex index : iconMap.keys()) {
			EnemyIcon icon = iconMap.get(index);
			
			if(!index.name().toLowerCase().startsWith(filter)) continue;

			// Need to create a new row
			if(icon.getWidth() + xx > x + width) {
				for(EntityIndex idx : row.keys()) {
					EnemyIcon ei = row.get(idx);
					ei.setY((int)(yy - (height * 0.5f) - ei.getHeight() * 0.5f));
					add(ei);
				}
				row.clear();
				yy -= height + vertPadding * scale;
				xx = x + horizPadding * scale;
				height = 0;
			} 
			
			icon.setPosition((int)xx, (int)yy);
			height = Math.max(height, icon.getHeight());
			
			xx += icon.getWidth() + horizPadding * scale;

			row.put(index, icon);
		}
		
		for(EntityIndex idx : row.keys()) {
			EnemyIcon ei = row.get(idx);
			ei.setY((int)(yy - (height * 0.5f) - ei.getHeight() * 0.5f));
			add(ei);
		}
	}
	
	@Override
	public void render(SpriteBatch batch) {
		batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		batch.draw(background, x, y);

		super.render(batch);
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
	
	public void addListener(SelectListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(SelectListener listener) {
		listeners.removeIndex(listeners.indexOf(listener, false));
	}

}
