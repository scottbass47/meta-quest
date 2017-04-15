package com.fullspectrum.ability.rogue;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.ability.AnimationAbility;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TimeListener;
import com.fullspectrum.debug.DebugRender;
import com.fullspectrum.effects.Effects;
import com.fullspectrum.entity.EntityAnim;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.factory.EntityFactory;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.input.Actions;
import com.fullspectrum.level.EntityGrabber;
import com.fullspectrum.utils.PhysicsUtils;

public class FlashPowderAbility extends AnimationAbility{

	private int throwFrame = 1;
	private float stunDuration;
	
	public FlashPowderAbility(float cooldown, Actions input, Animation animation, float stunDuration){
		super(AbilityType.FLASH_POWDER, AssetLoader.getInstance().getRegion(Asset.FLASH_POWDER_ICON), cooldown, input, animation);
		this.stunDuration = stunDuration;
	}

	@Override
	protected void init(Entity entity) {
		Mappers.asm.get(entity).get(EntityAnim.FLASH_POWDER_ARMS).changeState(EntityAnim.FLASH_POWDER_ARMS);
		Mappers.timer.get(entity).add("flash_powder_delay", throwFrame * GameVars.ANIM_FRAME, false, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				throwFlashPowder(entity);
				
				boolean facingRight = Mappers.facing.get(entity).facingRight;
				Vector2 pos = PhysicsUtils.getPos(entity);
				Entity powder = EntityFactory.createFlashPowder(facingRight ?  pos.x + 2.0f : pos.x - 2.0f, pos.y, facingRight);
				EntityManager.addEntity(powder);
			}
		});
	}
	
	private void throwFlashPowder(Entity entity){
		boolean facingRight = Mappers.facing.get(entity).facingRight;
		Vector2 pos = PhysicsUtils.getPos(entity);
		
		float xRange = 3.5f;
		float yRange = 1.0f;
		
		final Rectangle rect = new Rectangle(
				facingRight ? pos.x : pos.x - xRange,
				pos.y - yRange * 0.5f,
				xRange, 
				yRange);
		
//		DebugRender.setColor(Color.RED);
//		DebugRender.setType(ShapeType.Line);
//		DebugRender.rect(rect.x, rect.y, rect.width, rect.height, 1.0f);
		
		Array<Entity> hit = Mappers.level.get(entity).levelHelper.getEntities(new EntityGrabber() {
			@Override
			public boolean validEntity(Entity me, Entity other) {
				if(!Mappers.type.get(me).type.getOpposite().equals(Mappers.type.get(other).type)) return false;
				
				Vector2 targetPos = PhysicsUtils.getPos(other);
				Rectangle aabb = Mappers.body.get(other).getAABB();
				Rectangle hitBox = new Rectangle(aabb).setCenter(targetPos);
				return rect.overlaps(hitBox);
			}
			
			@Override
			public Family componentsNeeded() {
				return Family.all(HealthComponent.class, BodyComponent.class).get();
			}
		});
		
		for(Entity e : hit){
			Effects.giveStun(e, stunDuration);
		}
	}

	@Override
	public void onUpdate(Entity entity, float delta) {
	}

	@Override
	protected void destroy(Entity entity) {
	}
	
}
