package com.fullspectrum.ability.knight;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.ability.TimedAbility;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.InvincibilityComponent.InvincibilityType;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.ProjectileComponent;
import com.fullspectrum.component.TypeComponent;
import com.fullspectrum.component.TypeComponent.EntityType;
import com.fullspectrum.component.VelocityComponent;
import com.fullspectrum.input.Actions;
import com.fullspectrum.level.EntityGrabber;
import com.fullspectrum.level.LevelHelper;
import com.fullspectrum.utils.PhysicsUtils;

// BUG Some deflected projectiles move very slowly after being deflected
public class AntiMagneticAbility extends TimedAbility{

	private float radius = 1.0f;
	private ObjectSet<Entity> deflected;
	
	public AntiMagneticAbility(float cooldown, Actions input, float radius, float time) {
		super(AbilityType.ANTI_MAGNETIC_ARMOR, AssetLoader.getInstance().getRegion(Asset.ANTI_MAGNETIC_ARMOR_ICON), cooldown, input, time);
		this.radius = radius;
		deflected = new ObjectSet<Entity>();
		addTemporaryInvincibilities(InvincibilityType.PROJECTILE);
	}

	@Override
	public void init(Entity entity) {
	}

	@Override
	public void onUpdate(Entity entity, float delta) {
		LevelComponent levelComp = Mappers.level.get(entity);
		LevelHelper helper = levelComp.levelHelper;
		
		Body myBody = Mappers.body.get(entity).body;
		final float r = radius;

//		DebugRender.setColor(Color.RED);
//		DebugRender.circle(myBody.getPosition().x, myBody.getPosition().y, r);
		
		Array<Entity> projectiles = helper.getEntities(new EntityGrabber() {
			@Override
			public boolean validEntity(Entity me, Entity other) {
				if(!Mappers.type.get(me).shouldCollide(Mappers.type.get(other))) return false;
				
				Body myBody = Mappers.body.get(me).body;
				Body otherBody = Mappers.body.get(other).body;
				Rectangle aabb = PhysicsUtils.getAABB(otherBody, true); // CLEANUP Cache?
				
				float l = Math.max(aabb.width, aabb.height) * 0.5f + 0.1f;
				
				if(PhysicsUtils.getDistanceSqr(myBody, otherBody) > (r * r + l * l + 2 * l)) return false;
				
//				DebugRender.setColor(Color.PURPLE);
//				DebugRender.circle(otherBody.getPosition().x, otherBody.getPosition().y, l, 1.0f);
				return true;
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public Family componentsNeeded() {
				return Family.all(ProjectileComponent.class, TypeComponent.class, VelocityComponent.class).get();
			}
		});
		
		for(Entity projectile : projectiles){
			Body projBody = Mappers.body.get(projectile).body;
				
			if(!deflected.contains(projectile)){
				// Vectors
				Vector2 projVel = projBody.getLinearVelocity();
				
				float speed = projVel.len();
				if(speed == 0.0f) continue;
				float velAngle = projVel.angle(); // angle that the projectile is pointed in
				float posAngle = MathUtils.atan2(projBody.getPosition().y - myBody.getPosition().y, projBody.getPosition().x - myBody.getPosition().x);
				posAngle *= MathUtils.radiansToDegrees;
				
				float normal = posAngle + 90;
				float reflection = 2 * normal - velAngle;
				projBody.setLinearVelocity(speed * MathUtils.cosDeg(reflection), speed * MathUtils.sinDeg(reflection));
				
				Mappers.rotation.get(projectile).rotation = reflection;
				
				// Render tangent line
//				float tanX = myBody.getPosition().x + r * MathUtils.cosDeg(posAngle);
//				float tanY = myBody.getPosition().y + r * MathUtils.sinDeg(posAngle);
//				
//				float l = 2.0f;
//				float x1 = tanX + l * MathUtils.cosDeg(normal);
//				float y1 = tanY + l * MathUtils.sinDeg(normal);
//				float x2 = tanX - l * MathUtils.cosDeg(normal);
//				float y2 = tanY - l * MathUtils.sinDeg(normal);
//				
//				DebugRender.setColor(Color.GREEN);
//				DebugRender.line(x1, y1, x2, y2, 1.0f);
				
				Mappers.type.get(projectile).set(EntityType.FRIENDLY);
				Mappers.type.get(projectile).setCollideWith(EntityType.ENEMY);
				
				// BUG Deflected projectiles don't collide with enemies that are too close to the player
				deflected.add(projectile);
			}else{
				// Check to see if the projectile is still within range,
				// if it is, speed it up so it can get out
				if(PhysicsUtils.getDistanceSqr(entity, projectile) <= r * r){
					// double the projectile's speed
					projBody.setLinearVelocity(projBody.getLinearVelocity().x * 2, projBody.getLinearVelocity().y * 2);
				}
			}			
		}
	}

	@Override
	public void destroy(Entity entity) {
		deflected.clear();
	}
}