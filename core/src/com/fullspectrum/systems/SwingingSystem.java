package com.fullspectrum.systems;

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
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.KnightComponent;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.SwingComponent;
import com.fullspectrum.component.TypeComponent;
import com.fullspectrum.debug.DebugRender;
import com.fullspectrum.debug.DebugVars;
import com.fullspectrum.effects.EffectDef;
import com.fullspectrum.handlers.DamageHandler;
import com.fullspectrum.level.EntityGrabber;

public class SwingingSystem extends IteratingSystem{

	// TODO Swings should allow for offsetting
	public SwingingSystem(){
		super(Family.all(SwingComponent.class, FacingComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		// CLEANUP Should the sword control the swing or the sword wielder? Will swords exists without parents?
		LevelComponent levelComp = Mappers.level.get(entity);
		
		SwingComponent swingComp = Mappers.swing.get(entity);
		swingComp.timeElapsed += deltaTime;
		
		if(!swingComp.shouldSwing || swingComp.timeElapsed < swingComp.delay) return;
		
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
			DamageHandler.dealDamage(entity, e, swingComp.damage, swingComp.knockback, facingComp.facingRight ? 0.0f : 180);
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
