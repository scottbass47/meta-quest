package com.cpubrew.systems;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.FacingComponent;
import com.cpubrew.component.HealthComponent;
import com.cpubrew.component.KnightComponent;
import com.cpubrew.component.LevelComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.PositionComponent;
import com.cpubrew.component.ProjectileComponent;
import com.cpubrew.component.StatusComponent;
import com.cpubrew.component.SwingComponent;
import com.cpubrew.debug.DebugRender;
import com.cpubrew.debug.DebugVars;
import com.cpubrew.effects.EffectDef;
import com.cpubrew.handlers.DamageHandler;
import com.cpubrew.level.EntityGrabber;

public class SwingingSystem extends IteratingSystem{

	// TODO Swings should allow for offsetting
	public SwingingSystem(){
		super(Family.all(SwingComponent.class, FacingComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		LevelComponent levelComp = Mappers.level.get(entity);
		final SwingComponent swingComp = Mappers.swing.get(entity);
		
		if(!swingComp.shouldSwing) return;
		
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
		
		// Arc2D needs to be reflected across the x-axis because Java's Geometry
		// package uses a top down coordinate system
		double extent = swingArc.getAngleExtent();
		double arcEnd = swingArc.getAngleStart() + extent;
		double newStart = 360 - arcEnd;
		
		swingArc.setAngleStart(newStart);
		
		Array<Entity> hitEntities = levelComp.levelHelper.getEntities(new EntityGrabber(){
			@SuppressWarnings("unchecked")
			@Override
			public Family componentsNeeded() {
				return Family.all(BodyComponent.class, StatusComponent.class).one(HealthComponent.class, ProjectileComponent.class).get();
			}

			@Override
			public boolean validEntity(Entity me, Entity other) {
				StatusComponent swordTypeComp = Mappers.status.get(me);
				StatusComponent otherTypeComp = Mappers.status.get(other);
				
				// Don't deal damage to entities that aren't compatible
				if(!swordTypeComp.shouldCollide(otherTypeComp)) return false;
				if(Mappers.projectile.get(other) != null && !swingComp.breaksProjectiles) return false;
				
				Body otherBody = Mappers.body.get(other).body;
				Rectangle aabb = Mappers.body.get(other).getAABB();
				
				// Bottom Left
				float x2 = otherBody.getPosition().x - aabb.width * 0.5f;
				float y2 = otherBody.getPosition().y - aabb.height * 0.5f;

				Shape shape = new Rectangle2D.Float(x2, y2, aabb.width, aabb.height);
				
				return swingArc.intersects((Rectangle2D)shape);
			}
		});
		
		if(DebugVars.SWING_ON){
			PositionComponent posComp = Mappers.position.get(entity);
			float duration = 1.0f;
			
			x1 = facingComp.facingRight ? swingComp.rx * MathUtils.cosDeg(swingComp.startAngle) : swingComp.rx * MathUtils.cosDeg(180 - swingComp.startAngle);
			y1 = swingComp.ry * MathUtils.sinDeg(swingComp.startAngle);
			float x2 = facingComp.facingRight ? swingComp.rx * MathUtils.cosDeg(swingComp.endAngle) : swingComp.rx * MathUtils.cosDeg(180 - swingComp.endAngle);
			float y2 = swingComp.ry * MathUtils.sinDeg(swingComp.endAngle);
			
			x1 += posComp.x;
			y1 += posComp.y;
			x2 += posComp.x;
			y2 += posComp.y;
			
			DebugRender.setType(ShapeType.Line);
			DebugRender.setColor(Color.PURPLE);
			DebugRender.line(posComp.x, posComp.y, x1, y1, duration);
			DebugRender.line(posComp.x, posComp.y, x2, y2, duration);
			
			// Render ellipse
			float stepSize = -0.1f;
			float prevX = Float.MAX_VALUE;
			float prevY = Float.MIN_VALUE;
			for(float t = swingComp.startAngle * MathUtils.degreesToRadians; ; t += stepSize){
				float cos = (float) Math.cos(facingComp.facingRight ? t : MathUtils.PI - t);
				float sin = (float) Math.sin(t);
				float xx = posComp.x + cos;
				float yy = posComp.y + sin;
				float angle = (float) (Math.toDegrees(Math.atan2(yy - posComp.y, facingComp.facingRight ? xx - posComp.x : posComp.x - xx)));
				
				float start = swingComp.startAngle;
				float end = swingComp.endAngle;

				if(angle - 0.1f > start || angle < end){
					break;
				}
				
				xx = posComp.x + swingComp.rx * cos;
				yy = posComp.y + swingComp.ry * sin;

				if(!MathUtils.isEqual(prevX, Float.MAX_VALUE)){
					DebugRender.line(prevX, prevY, xx, yy, duration);
				}
				prevX = xx;
				prevY = yy;
			}
		}
		
		for(Entity e : hitEntities){
			if(Mappers.projectile.get(e) != null) {
				Mappers.death.get(e).triggerDeath();
				continue;
			}
			DamageHandler.dealDamage(entity, e, swingComp.damage, swingComp.knockback, facingComp.facingRight ? 0.0f : 180.0f);
			for(EffectDef effect : swingComp.effects){
				effect.give(e);
			}
		}
		
		// Knight stuff
		KnightComponent knightComp = Mappers.knight.get(entity);
		if(knightComp != null){
			knightComp.hitAnEnemy = hitEntities.size > 0;
			knightComp.hitEnemies.addAll(hitEntities);
		}
		swingComp.timeElapsed = 0.0f;
		swingComp.shouldSwing = false;
	}
}
