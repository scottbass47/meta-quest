package com.fullspectrum.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.RenderComponent;
import com.fullspectrum.component.TextureComponent;

import static com.fullspectrum.game.GameVars.*;

public class RenderingSystem extends EntitySystem {

	private SpriteBatch batch;
	private ImmutableArray<Entity> entities;

	public RenderingSystem(SpriteBatch batch) {
		this.batch = batch;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(PositionComponent.class, RenderComponent.class, TextureComponent.class).get());
	}

	@Override
	public void update(float deltaTime) {
		
	}
	
	public void render(){
		batch.begin();
		for (Entity e : entities) {
			PositionComponent position = Mappers.position.get(e);
			TextureComponent texture = Mappers.texture.get(e);
			if (texture.region == null) return;
			float width = texture.region.getRegionWidth();
			float height = texture.region.getRegionHeight();
			batch.draw(texture.region, position.x, position.y, 0, 0, width, height, PLAYER_SCALE / PPM, PLAYER_SCALE / PPM, 0.0f);
		}
		batch.end();
	}

}
