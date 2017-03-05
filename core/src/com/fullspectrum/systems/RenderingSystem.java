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
import com.fullspectrum.component.ShaderComponent;
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
		for (Entity entity : entities) {
			PositionComponent positionComp = Mappers.position.get(entity);
			TextureComponent textureComp = Mappers.texture.get(entity);
			FacingComponent facingComp = Mappers.facing.get(entity);
			TintComponent tintComp = Mappers.tint.get(entity);
			RenderComponent renderComp = Mappers.render.get(entity);
			ShaderComponent shaderComp = Mappers.shader.get(entity);
			if(shaderComp != null && shaderComp.shader != null){
				shaderComp.shader.setUniforms(entity);
				batch.setShader(shaderComp.shader.getProgram());
			} else{
				batch.setShader(null);
			}
			if(textureComp.getRegions() == null || textureComp.getRegions().size == 0) return;
			
			for(TextureRegion region : textureComp.getRegions()){
				if(region == null) continue;
				float width = region.getRegionWidth();
				float height = region.getRegionHeight();
				float x = positionComp.x - width * 0.5f;
				float y = positionComp.y - height * 0.5f;
				if(tintComp != null) batch.setColor(tintComp.tint);
				
				if(facingComp != null){
					// Rotation
					boolean quad23 = MathUtils.cosDeg(renderComp.rotation) < 0.0f;
					region.flip(!facingComp.facingRight, false);
					batch.draw(region, x, y, width * 0.5f, height * 0.5f, width, height, PPM_INV, PPM_INV, quad23 ? renderComp.rotation - 180: renderComp.rotation);
					region.flip(region.isFlipX(), false);
				}
				else{
					batch.draw(region, x, y, width * 0.5f, height * 0.5f, width, height, PPM_INV, PPM_INV, renderComp.rotation);
				}
				batch.setColor(Color.WHITE);
			}
		}
		batch.end();
		batch.setShader(null);
	}

}
