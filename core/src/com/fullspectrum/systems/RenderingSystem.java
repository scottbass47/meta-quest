package com.fullspectrum.systems;

import static com.fullspectrum.game.GameVars.PPM_INV;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.RenderComponent;
import com.fullspectrum.component.TextureComponent;
import com.fullspectrum.component.TintComponent;

public class RenderingSystem extends EntitySystem {

	private ImmutableArray<Entity> entities;

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(PositionComponent.class, RenderComponent.class, TextureComponent.class).get());
	}

	@Override
	public void update(float deltaTime) {
		
	}
	
	public void render(SpriteBatch batch){
		batch.begin();
		for (Entity e : entities) {
			PositionComponent position = Mappers.position.get(e);
			TextureComponent texture = Mappers.texture.get(e);
			FacingComponent facing = Mappers.facing.get(e);
			TintComponent tint = Mappers.tint.get(e);
			if (texture.region == null) return;
			float width = texture.region.getRegionWidth();
			float height = texture.region.getRegionHeight();
			if(tint != null) batch.setColor(tint.tint);
			if(facing != null){
				texture.region.flip(!facing.facingRight, false);
				batch.draw(texture.region, position.x, position.y, 0, 0, width, height, PPM_INV, PPM_INV, 0.0f);
				texture.region.flip(texture.region.isFlipX(), false);
			}
			else{
				batch.draw(texture.region, position.x, position.y, 0, 0, width, height, PPM_INV, PPM_INV, 0.0f);
			}
			batch.setColor(Color.WHITE);
		}
		batch.end();
	}

}
