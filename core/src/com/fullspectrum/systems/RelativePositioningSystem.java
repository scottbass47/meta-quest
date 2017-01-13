package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.OffsetComponent;
import com.fullspectrum.component.ParentComponent;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.entity.EntityUtils;

public class RelativePositioningSystem extends IteratingSystem{

	public RelativePositioningSystem(){
		super(Family.all(ParentComponent.class, OffsetComponent.class).one(BodyComponent.class, PositionComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ParentComponent parentComp = Mappers.parent.get(entity);
		OffsetComponent offsetComp = Mappers.offset.get(entity);
		
		if(parentComp.parent == null || !EntityUtils.isValid(parentComp.parent)) return;
		
		BodyComponent parentBodyComp = Mappers.body.get(parentComp.parent);
		FacingComponent facingComp = Mappers.facing.get(parentComp.parent);
		
		if(parentBodyComp == null || parentBodyComp.body == null) return;
		
		Body parentBody = parentBodyComp.body;
		float x = parentBody.getPosition().x;
		float y = parentBody.getPosition().y;
		
		if(offsetComp.canFlip && facingComp != null){
			x += (facingComp.facingRight ? offsetComp.xOff : -offsetComp.xOff);
		}
		else{
			x += offsetComp.xOff;
		}
		y += offsetComp.yOff;
		
		BodyComponent bodyComp = Mappers.body.get(entity);
		PositionComponent positionComp = Mappers.position.get(entity);
		if(bodyComp != null){
			bodyComp.body.setTransform(x, y, 0.0f);
			bodyComp.body.setActive(true);
		}else{
			positionComp.x = x;
			positionComp.y = y;
		}
	}
}
