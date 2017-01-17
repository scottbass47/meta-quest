package com.fullspectrum.systems;

import static com.fullspectrum.game.GameVars.R_WORLD_HEIGHT;
import static com.fullspectrum.game.GameVars.R_WORLD_WIDTH;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.fullspectrum.component.CameraComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.entity.EntityUtils;

public class CameraSystem extends IteratingSystem{

	public CameraSystem(){
		super(Family.all(CameraComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		CameraComponent cameraComp = Mappers.camera.get(entity);
		if(!EntityUtils.isValid(cameraComp.toFollow) || Mappers.position.get(cameraComp.toFollow) == null) return;
		PositionComponent positionComp = Mappers.position.get(cameraComp.toFollow);
		
		float dx = positionComp.x - cameraComp.x;
		float dy = positionComp.y - cameraComp.y;

		if(dx < cameraComp.windowMinX || dx > cameraComp.windowMaxX){
			cameraComp.x = MathUtils.lerp(cameraComp.x, positionComp.x, deltaTime * 3f);
		}
		if(dy < cameraComp.windowMinY || dy > cameraComp.windowMaxY){
			cameraComp.y = MathUtils.lerp(cameraComp.y, positionComp.y, deltaTime * 3f);
		}
		if(!MathUtils.isEqual(cameraComp.zoom, cameraComp.camera.zoom)){
			cameraComp.camera.zoom = MathUtils.lerp(cameraComp.camera.zoom, 1.0f / cameraComp.zoom, deltaTime * 10f);
		}
		
		cameraComp.x = MathUtils.clamp(cameraComp.x, cameraComp.minX + R_WORLD_WIDTH * 0.5f * cameraComp.camera.zoom, cameraComp.maxX - R_WORLD_WIDTH * 0.5f * cameraComp.camera.zoom);
		cameraComp.y = MathUtils.clamp(cameraComp.y, cameraComp.minY + R_WORLD_HEIGHT * 0.5f * cameraComp.camera.zoom, cameraComp.maxY - R_WORLD_HEIGHT * 0.5f * cameraComp.camera.zoom);
		
		cameraComp.camera.position.x = cameraComp.x;
		cameraComp.camera.position.y = cameraComp.y;
	}
}
