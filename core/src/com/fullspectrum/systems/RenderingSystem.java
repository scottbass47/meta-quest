package com.fullspectrum.systems;

import static com.fullspectrum.game.GameVars.PPM_INV;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.RenderComponent;
import com.fullspectrum.component.RenderLevelComponent;
import com.fullspectrum.component.RotationComponent;
import com.fullspectrum.component.ShaderComponent;
import com.fullspectrum.component.TextureComponent;
import com.fullspectrum.component.TintComponent;

public class RenderingSystem extends EntitySystem {

	private Array<Entity> sorted;
	
	public RenderingSystem() {
		sorted = new Array<Entity>();
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		getEngine().addEntityListener(Family.all(PositionComponent.class, RenderComponent.class, TextureComponent.class, RenderLevelComponent.class).get(), new EntityListener() {
			@Override
			public void entityAdded(Entity entity) {
				sorted.add(entity);
			}

			@Override
			public void entityRemoved(Entity entity) {
				sorted.removeIndex(sorted.indexOf(entity, false));
			}
		});
	}
	
	@Override
	public void update(float deltaTime) {
		
	}
	
	public void render(SpriteBatch batch){
		// Resort list
		sortEntities();
		
		batch.begin();
		for (Entity entity : sorted) {
			PositionComponent positionComp = Mappers.position.get(entity);
			TextureComponent textureComp = Mappers.texture.get(entity);
			FacingComponent facingComp = Mappers.facing.get(entity);
			TintComponent tintComp = Mappers.tint.get(entity);
			
			if(textureComp.getRegions() == null || textureComp.getRegions().size == 0){
				continue;
			}

			// Setup Shader
			ShaderComponent shaderComp = Mappers.shader.get(entity);
			if(shaderComp != null && shaderComp.shader != null){
				batch.setShader(shaderComp.shader.getProgram());
				shaderComp.shader.setUniforms(entity);
			} else{
				batch.setShader(null);
			}
			
			// Get Rotation
			RotationComponent rotationComp = Mappers.rotation.get(entity);
			float rotation = rotationComp != null ? rotationComp.rotation : 0.0f;
			
			for(TextureRegion region : textureComp.getRegions()){
				if(region == null) continue;
				float width = region.getRegionWidth();
				float height = region.getRegionHeight();
				float x = positionComp.x - width * 0.5f;
				float y = positionComp.y - height * 0.5f;
				if(tintComp != null) batch.setColor(tintComp.tint);
				
				if(facingComp != null){
					// Rotation
					region.flip(!facingComp.facingRight, false);
					batch.draw(region, x, y, width * 0.5f, height * 0.5f, width, height, PPM_INV, PPM_INV, Mappers.facing.get(entity).facingRight ? rotation : -rotation);
					region.flip(region.isFlipX(), false);
				}
				else{
					batch.draw(region, x, y, width * 0.5f, height * 0.5f, width, height, PPM_INV, PPM_INV, rotation);
				}
				batch.setColor(Color.WHITE);
			}
		}
		batch.end();
		batch.setShader(null);
	}
	
	// PERFORMANCE Could be a more efficient algorithm
	private void sortEntities(){
        for (int i = 1; i < sorted.size; i++) {
            for(int j = i ; j > 0 ; j--){
            	Entity e1 = sorted.get(j);
            	Entity e2 = sorted.get(j-1);
                if(getRL(e1) < getRL(e2)){
                    sorted.set(j, e2);
                    sorted.set(j-1, e1);
                }
            }
        }
    }

	// Gets the render level of an entity
	private int getRL(Entity entity){
		return Mappers.renderLevel.get(entity).renderLevel;
	}
	
}
