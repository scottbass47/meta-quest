package com.fullspectrum.systems;

import static com.fullspectrum.game.GameVars.*;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.component.CameraComponent;
import com.fullspectrum.component.Mappers;

public class CameraSystem extends IteratingSystem{

	@SuppressWarnings("unchecked")
	public CameraSystem(){
		super(Family.all(CameraComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		CameraComponent cameraComp = Mappers.camera.get(entity);
		Body body = Mappers.body.get(cameraComp.toFollow).body;
		assert(body != null);
		float dx = body.getPosition().x - cameraComp.x;
		float dy = body.getPosition().y - cameraComp.y;
//		System.out.printf("Camera X: %.2f, Player X: %.2f, DX: %.2f\n", cameraComp.camera.position.x, body.getPosition().x, dx);
//		System.out.printf("Camera Y: %.2f, Player Y: %.2f, DY: %.2f\n", cameraComp.camera.position.y, posComp.y, dy);
		if(dx < cameraComp.windowMinX || dx > cameraComp.windowMaxX){
			cameraComp.x = MathUtils.lerp(cameraComp.x, body.getPosition().x, deltaTime * 3f);
		}
		if(dy < cameraComp.windowMinY || dy > cameraComp.windowMaxY){
			cameraComp.y = MathUtils.lerp(cameraComp.y, body.getPosition().y, deltaTime * 3f);
		}
		
		cameraComp.x = MathUtils.clamp(cameraComp.x, cameraComp.minX + R_WORLD_WIDTH * 0.5f, cameraComp.maxX - R_WORLD_WIDTH * 0.5f);
		cameraComp.y = MathUtils.clamp(cameraComp.y, cameraComp.minY + R_WORLD_HEIGHT * 0.5f, cameraComp.maxY - R_WORLD_HEIGHT * 0.5f);
		
		cameraComp.camera.position.x = ((int)(cameraComp.x * PPM)) / PPM;
		cameraComp.camera.position.y = ((int)(cameraComp.y * PPM)) / PPM;
//		cameraComp.camera.position.x = (int)cameraComp.x;
//		cameraComp.camera.position.y = (int)cameraComp.y;
		
//		System.out.printf("X: %.3f, Y: %.3f\n", cameraComp.camera.position.x, cameraComp.camera.position.y);
//		cameraComp.camera.position.x = (int)(body.getPosition().x * PPM) / PPM;
//		cameraComp.camera.position.y = (int)(body.getPosition().y * PPM) / PPM;
	}
}
