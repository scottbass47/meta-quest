package com.fullspectrum.systems;

import static com.fullspectrum.game.GameVars.PPM_INV;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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
			RenderComponent render = Mappers.render.get(e);
			if(texture.getRegions() == null || texture.getRegions().size == 0) return;
			
			for(TextureRegion region : texture.getRegions()){
				if(region == null) continue;
				float width = region.getRegionWidth();
				float height = region.getRegionHeight();
				float x = position.x - width * 0.5f;
				float y = position.y - height * 0.5f;
				if(tint != null) batch.setColor(tint.tint);
				
				if(facing != null){
					// Rotation
					boolean quad23 = MathUtils.cosDeg(render.rotation) < 0.0f;
					region.flip(!facing.facingRight, false);
					batch.draw(region, x, y, width * 0.5f, height * 0.5f, width, height, PPM_INV, PPM_INV, quad23 ? render.rotation - 180: render.rotation);
					region.flip(region.isFlipX(), false);
				}
				else{
					batch.draw(region, x, y, width * 0.5f, height * 0.5f, width, height, PPM_INV, PPM_INV, render.rotation);
				}
				batch.setColor(Color.WHITE);
			}
		}
		batch.end();
	}

}
