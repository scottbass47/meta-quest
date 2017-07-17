package com.fullspectrum.ability.monk;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.ability.OnGroundConstraint;
import com.fullspectrum.ability.TimedAbility;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.effects.EffectType;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.factory.EntityFactory;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.input.Actions;
import com.fullspectrum.level.EntityGrabber;
import com.fullspectrum.level.Level;
import com.fullspectrum.utils.EntityUtils;
import com.fullspectrum.utils.Maths;
import com.fullspectrum.utils.PhysicsUtils;

// INCOMPLETE What happens is wall is placed off of ledge?
public class InstaWallAbility extends TimedAbility{

	private boolean createdWall = false;
	private boolean animFinished = false;
	private int maxHeight;
	private Entity wall;
	private Animation<TextureRegion> animation;
	
	public InstaWallAbility(float cooldown, Actions input, Animation<TextureRegion> animation, float duration, int maxHeight) {
		super(AbilityType.INSTA_WALL, AssetLoader.getInstance().getRegion(Asset.INSTA_WALL_ICON), cooldown, input, duration, true);
		setAbilityConstraints(new OnGroundConstraint());
		addTemporaryImmunties(EffectType.KNOCKBACK);
		lockFacing();
		
		this.maxHeight = maxHeight;
		this.animation = animation;
	}

	@Override
	protected void init(Entity entity) {
		Mappers.esm.get(entity).get(EntityStates.INSTA_WALL).changeState(EntityStates.INSTA_WALL);
	}

	@Override
	public void onUpdate(Entity entity, float delta) {
		int frame = (int)(elapsed / GameVars.ANIM_FRAME);
		
		if(frame >= 3 && !createdWall) {
			createdWall = true;
			
			Vector2 pos = PhysicsUtils.getPos(entity);
			BodyComponent bodyComp = Mappers.body.get(entity);
			Level level = Mappers.level.get(entity).level;
			boolean facingRight = Mappers.facing.get(entity).facingRight;
			
			float x = pos.x + (facingRight ? 1.5f : -1.5f);
			float y = pos.y - bodyComp.getAABB().height * 0.5f + 0.02f; // add 0.02f to account for tile grid rounding errors
			
			int row = Maths.toGridCoord(y);
			int col = Maths.toGridCoord(x);

			if(level.isSolid(row, col) && !level.isSolid(row + 1, col)) row++;
			
			int endRow = row;
			int height = 0;
			
			for(int r = row; r <= level.getMaxRow(); r++) {
				height++;
				if((height > maxHeight && maxHeight > 0) || level.isSolid(r, col)) {
					endRow = r - 1;
					break;
				}
				
				if(r == level.getMaxRow()) {
					endRow = r;
				}
			}
			
			if(endRow >= row) {
				final Rectangle wallHitbox = new Rectangle(x - 0.5f, row, 1.0f, endRow - row + 1);
				
				// Look for entities that might be colliding with the wall and push them away
				Array<Entity> collidingEntities = Mappers.level.get(entity).levelHelper.getEntities(new EntityGrabber() {
					@Override
					public boolean validEntity(Entity me, Entity other) {
						BodyComponent bodyComp = Mappers.body.get(other);
						Rectangle aabb = bodyComp.getAABB();
						Rectangle hbox = new Rectangle(aabb);
						
						Vector2 pos = PhysicsUtils.getPos(other);
						hbox.setCenter(pos.x, pos.y);
						
						return wallHitbox.overlaps(hbox);
					}
					
					@SuppressWarnings("unchecked")
					@Override
					public Family componentsNeeded() {
						return Family.all(BodyComponent.class).get();
					}
				});
				
				// Move entities back
				for(Entity e : collidingEntities) {
					BodyComponent bComp = Mappers.body.get(e);
					
					Rectangle hitbox = bComp.getAABB();
					Vector2 p = PhysicsUtils.getPos(e);
					
					Vector2 newPos = new Vector2(p);
					
					if(facingRight) {
						newPos.x = x + 0.51f + hitbox.width * 0.5f; // move hitbox just to the right of the wall
					} else {
						newPos.x = x - 0.51f - hitbox.width * 0.5f; // move hitbox just to the left of the wall
					}
					
					// If we're good, make the move
					if(level.isValidPoint(newPos.x, newPos.y, hitbox)) {
						bComp.body.setTransform(newPos.x, newPos.y, 0.0f);
						continue;
					}
					// Otherwise, we move to the otherside of the wall
					else {
						if(facingRight) {
							newPos.x = x - 0.51f - hitbox.width * 0.5f; // move hitbox just to the left of the wall
						} else {
							newPos.x = x + 0.51f + hitbox.width * 0.5f; // move hitbox just to the right of the wall
						}
					}
					
					bComp.body.setTransform(newPos.x, newPos.y, 0.0f);
				}
				wall = EntityFactory.createInstaWall(x, row, endRow);
				EntityManager.addEntity(wall);
			}
			
		}
		
		if(elapsed >= animation.getAnimationDuration() - 0.05f && !animFinished) {
			animFinished = true;
			Mappers.esm.get(entity).get(EntityStates.INSTA_WALL).changeState(EntityStates.IDLING);
			Mappers.facing.get(entity).locked = false;
			Mappers.ability.get(entity).unlockAllBlocking();
		}
	}

	@Override
	protected void destroy(Entity entity) {
		createdWall = false;
		animFinished = false;
		if(EntityUtils.isValid(wall)) EntityManager.cleanUp(wall);
	}
	
	@Override
	public boolean unblockOnDestroy() {
		return false;
	}

}
