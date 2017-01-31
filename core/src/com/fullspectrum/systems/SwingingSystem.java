package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.RemoveComponent;
import com.fullspectrum.component.SwingComponent;
import com.fullspectrum.component.SwordComponent;
import com.fullspectrum.component.SwordStatsComponent;
import com.fullspectrum.component.TimeListener;
import com.fullspectrum.component.TimerComponent;
import com.fullspectrum.component.TypeComponent;
import com.fullspectrum.debug.DebugInput;
import com.fullspectrum.debug.DebugToggle;
import com.fullspectrum.entity.EntityFactory;
import com.fullspectrum.handlers.DamageHandler;
import com.fullspectrum.level.EntityGrabber;

public class SwingingSystem extends IteratingSystem{

	public SwingingSystem(){
		super(Family.all(SwingComponent.class, SwordComponent.class, FacingComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		// CLEANUP Should the sword control the swing or the sword wielder? Will swords exists without parents?
		LevelComponent levelComp = Mappers.level.get(entity);
		final SwordComponent swordComp = Mappers.sword.get(entity);
		
		if(!swordComp.shouldSwing || swordComp.sword == null) return;
		
		Array<Entity> hitEntities = levelComp.levelHelper.getEntities(new EntityGrabber(){
			@SuppressWarnings("unchecked")
			@Override
			public Family componentsNeeded() {
				return Family.all(BodyComponent.class, HealthComponent.class, TypeComponent.class).get();
			}

			@Override
			public boolean validEntity(Entity me, Entity other) {
				TypeComponent swordTypeComp = Mappers.type.get(me);
				TypeComponent otherTypeComp = Mappers.type.get(other);
				
				// Don't deal damage to entities that aren't compatible
				if(!swordTypeComp.shouldCollide(otherTypeComp)) return false;
				
				SwingComponent swingComp = Mappers.swing.get(me);
				Body myBody = Mappers.body.get(me).body;
				Body otherBody = Mappers.body.get(other).body;
				
				float x1 = myBody.getPosition().x;
				float y1 = myBody.getPosition().y;
				float x2 = otherBody.getPosition().x;
				float y2 = otherBody.getPosition().y;
				
				// Construct ellipse
				float rx2 = swingComp.rx * swingComp.rx;
				float ry2 = swingComp.ry * swingComp.ry;
				float xx = (x1 - x2) * (x1 - x2);
				float yy = (y1 - y2) * (y1 - y2);
				
				// Entity inside ellipse
				if(xx / rx2 + yy / ry2 <= 1.0){
					// Check to see if angle is valid
					FacingComponent facingComp = Mappers.facing.get(me);
					float angle = MathUtils.atan2(y2 - y1, facingComp.facingRight ? x2 - x1 : x1 - x2) * MathUtils.radiansToDegrees;
					angle = angle < 0 ? 360 + angle : angle; // angle is from 0 - 360
					
					float start = swingComp.startAngle;
					float end = swingComp.endAngle;
					
					if(angle <= start || angle - 360 >= end){
						return true;
					}
				}
				
				return false;
			}
		});
		
		if(DebugInput.isToggled(DebugToggle.SHOW_SWING)){
			Body body = Mappers.body.get(entity).body;
			SwingComponent swingComp = Mappers.swing.get(entity);
			// Create self-destructing swing entity
			// CLEANUP Move to entity factory
			Entity swing = new EntityFactory.EntityBuilder(getEngine(), Mappers.world.get(entity).world, levelComp.level).build();
			swing.add(getEngine().createComponent(PositionComponent.class).set(body.getPosition().x, body.getPosition().y));
			swing.add(getEngine().createComponent(FacingComponent.class).set(Mappers.facing.get(entity).facingRight));
			swing.add(getEngine().createComponent(SwingComponent.class).set(swingComp.rx, swingComp.ry, swingComp.startAngle, swingComp.endAngle));
			swing.getComponent(TimerComponent.class).add("self_destruct", 1.0f, false, new TimeListener() {
				@Override
				public void onTime(Entity entity) {
					entity.add(new RemoveComponent());
				}
			});
			getEngine().addEntity(swing);
		}
		
		SwordStatsComponent swordStats = Mappers.swordStats.get(swordComp.sword);
		for(Entity e : hitEntities){
			DamageHandler.dealDamage(e, swordStats.damage);
		}
		swordComp.shouldSwing = false;
	}
	
//	@Override
//	protected void processEntity(Entity entity, float deltaTime) {
//		SwordComponent swordComp = Mappers.sword.get(entity);
//		SwingComponent swingComp = Mappers.swing.get(entity);
//		
//		if(swordComp == null || !EntityUtils.isValid(swordComp.sword)){
//			Gdx.app.log("Swinging System", "sword isn't a valid entity.");
//			return;
//		}
//		
//		swingComp.time += deltaTime;
//		if(swingComp.time > swingComp.duration){
//			swingComp.time = 0;
//			return;
//		}
//		
//		BodyComponent swordBodyComp = Mappers.body.get(swordComp.sword);
//		
//		if(swordBodyComp == null || swordBodyComp.body == null){
//			Gdx.app.log("Swinging System", "sword doesn't have a valid physics body.");
//		}
//		
//		Body swordBody = swordBodyComp.body;
//		if(!swordBody.isActive()) {
//			swordBody.setActive(true);
//		}
//		FacingComponent facingComp = Mappers.facing.get(entity);
//		
//		float degrees = 0.0f;
//		if(facingComp.facingRight){
//			degrees = swingComp.startAngle;
//			degrees -= swingComp.time * (swingComp.rotationAmount / swingComp.duration);
//		}
//		else{
//			degrees = 180 - swingComp.startAngle;
//			degrees += swingComp.time * (swingComp.rotationAmount / swingComp.duration);
//		}
//		degrees *= MathUtils.degreesToRadians;
//		swordBody.setTransform(swordBody.getPosition(), degrees);
//		
//		float dx = facingComp.facingRight ? -20 * GameVars.PPM_INV * 0.5f : -(-20 * GameVars.PPM_INV * 0.5f);
//		float dy = 0;
//		float radius = (float)Math.sqrt(dx * dx + dy * dy);
//		swordBody.setTransform(swordBody.getPosition().x + dx + radius * MathUtils.cos(degrees), swordBody.getPosition().y + dy + radius * MathUtils.sin(degrees), degrees);
//		
//	}
	
}
