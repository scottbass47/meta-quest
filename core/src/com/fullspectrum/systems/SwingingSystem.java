package com.fullspectrum.systems;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Rectangle;
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
		
		SwingComponent swingComp = Mappers.swing.get(entity);
		swingComp.timeElapsed += deltaTime;
		if(swingComp.timeElapsed < swingComp.delay) return;
		
		final Body myBody = Mappers.body.get(entity).body;
		final FacingComponent facingComp = Mappers.facing.get(entity);
		
		// Center
		float x1 = myBody.getPosition().x;
		float y1 = myBody.getPosition().y;
		
		final Shape ellipse = new Rectangle2D.Float(x1 - swingComp.rx, y1 - swingComp.ry, swingComp.rx * 2.0f, swingComp.ry * 2.0f);
		final Arc2D swingArc = new Arc2D.Float(
				(Rectangle2D)ellipse, 
				facingComp.facingRight ? swingComp.endAngle: 180 - swingComp.startAngle, 
				swingComp.startAngle - swingComp.endAngle, 
				Arc2D.PIE);
		
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
				
				Body otherBody = Mappers.body.get(other).body;
				Rectangle aabb = Mappers.body.get(other).getAABB();
				
				// Bottom Left
				float x2 = otherBody.getPosition().x - aabb.width * 0.5f;
				float y2 = otherBody.getPosition().y - aabb.height * 0.5f;

				Shape shape = new Rectangle2D.Float(x2, y2, aabb.width, aabb.height);
				return swingArc.intersects((Rectangle2D)shape);
			}
		});
		
		if(DebugInput.isToggled(DebugToggle.SHOW_SWING)){
			Body body = Mappers.body.get(entity).body;
			// Create self-destructing swing entity
			// CLEANUP Move to entity factory
			Entity swing = new EntityFactory.EntityBuilder("swing_debug", getEngine(), Mappers.world.get(entity).world, levelComp.level).build();
			swing.add(getEngine().createComponent(PositionComponent.class).set(body.getPosition().x, body.getPosition().y));
			swing.add(getEngine().createComponent(FacingComponent.class).set(Mappers.facing.get(entity).facingRight));
			swing.add(getEngine().createComponent(SwingComponent.class).set(swingComp.rx, swingComp.ry, swingComp.startAngle, swingComp.endAngle, 0.0f));
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
		swingComp.timeElapsed = 0.0f;
		swordComp.shouldSwing = false;
	}
	
//	/**
//	 * X and Y are the coordinates being tested. EX and EY are the coordinates of the center of the 
//	 * ellipse. RX and RY are the radius values for the ellipse. Start and end angles are in range of
//	 * 180 to -180 where an arc is created from start to end, clockwise. 
//	 * 
//	 * @param x
//	 * @param y
//	 * @param ex
//	 * @param ey
//	 * @param rx
//	 * @param ry
//	 * @param startAngle
//	 * @param endAngle
//	 * @param facingRight
//	 * @return
//	 */
//	private boolean inEllipse(float x, float y, float ex, float ey, float rx, float ry, float startAngle, float endAngle, boolean facingRight){
//		// Construct ellipse
//		float rx2 = rx * rx;
//		float ry2 = ry * ry;
//		float xx = (x - ex) * (x - ex);
//		float yy = (y - ey) * (y - ey);
//		
//		// Entity inside ellipse
//		if(xx / rx2 + yy / ry2 <= 1.0){
//			// Check to see if angle is valid
//			float angle = MathUtils.atan2(y - ey, facingRight ? x - ex : x - ex) * MathUtils.radiansToDegrees;
//			
//			if(angle <= startAngle && angle >= endAngle){
//				return true;
//			}
//		}
//		return false;
//	}
}
