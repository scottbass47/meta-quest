package com.fullspectrum.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fullspectrum.component.CameraComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.TextRenderComponent;
import com.fullspectrum.game.GameVars;

public class TextRenderingSystem extends EntitySystem{

	private ImmutableArray<Entity> entities;

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(PositionComponent.class, TextRenderComponent.class).get());
	}

	@Override
	public void update(float deltaTime) {
		
	}
	
	public void render(SpriteBatch batch, Entity camera){
		CameraComponent cameraComp = Mappers.camera.get(camera);
		batch.begin();
		for (Entity e : entities) {
			PositionComponent positionComp = Mappers.position.get(e);
			TextRenderComponent textRenderComp = Mappers.textRender.get(e);
			
			float x = (positionComp.x - cameraComp.x + GameVars.R_WORLD_WIDTH * 1.0f / cameraComp.zoom * 0.5f) * GameVars.PPM * cameraComp.zoom;
			float y = (positionComp.y - cameraComp.y + GameVars.R_WORLD_HEIGHT * 1.0f / cameraComp.zoom * 0.5f) * GameVars.PPM * cameraComp.zoom;
			
			textRenderComp.font.setColor(textRenderComp.color);
			textRenderComp.font.draw(batch, textRenderComp.text, x, y);
		}
		batch.end();
	}
	
}
