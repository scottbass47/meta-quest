package com.fullspectrum.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.fullspectrum.component.CameraComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.TextRenderComponent;
import com.fullspectrum.game.GameVars;

public class TextRenderingSystem extends EntitySystem{

	private ImmutableArray<Entity> entities;
	private GlyphLayout layout;
	private OrthographicCamera hudCam;
	private Vector3 coords;
	
	public TextRenderingSystem(OrthographicCamera hudCam) {
		this.hudCam = hudCam;
		layout = new GlyphLayout();
		coords = new Vector3();
	}

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(PositionComponent.class, TextRenderComponent.class).get());
	}

	@Override
	public void update(float deltaTime) {
		
	}
	
	public void render(SpriteBatch batch, Entity camera){
		CameraComponent cameraComp = Mappers.camera.get(camera);
		batch.setProjectionMatrix(hudCam.combined);
		batch.begin();
		for (Entity e : entities) {
			PositionComponent positionComp = Mappers.position.get(e);
			TextRenderComponent textRenderComp = Mappers.textRender.get(e);
			coords = cameraComp.camera.project(coords.set(positionComp.x, positionComp.y, 0.0f)).scl(GameVars.SCREEN_WIDTH / (float)Gdx.graphics.getWidth());
			
			textRenderComp.font.setColor(textRenderComp.color);
			textRenderComp.font.getData().setScale(cameraComp.zoom);
			layout.setText(textRenderComp.font, textRenderComp.text);
			textRenderComp.font.draw(batch, textRenderComp.text, coords.x - layout.width * 0.5f, coords.y - layout.height * 0.5f);
		}
		batch.end();
		batch.setProjectionMatrix(cameraComp.camera.combined);
	}
	
}
