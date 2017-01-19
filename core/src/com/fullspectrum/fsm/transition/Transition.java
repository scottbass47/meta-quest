package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.component.AbilityComponent;
import com.fullspectrum.component.AnimationComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.ESMComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TargetComponent;
import com.fullspectrum.entity.EntityUtils;
import com.fullspectrum.fsm.EntityStateMachine;
import com.fullspectrum.input.GameInput;
import com.fullspectrum.input.Input;
import com.fullspectrum.utils.StringUtils;

public enum Transition {

	FALLING(false) {
		@Override
		public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
			BodyComponent bodyComp = Mappers.body.get(entity);
			CollisionComponent collisionComp = Mappers.collision.get(entity);
			if (bodyComp == null || collisionComp == null) return false;
			if (bodyComp.body.getLinearVelocity().y < 0 && !collisionComp.onGround()) {
				return true;
			}
			return false;
		}

	},
	RANDOM(true) {
		@Override
		public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
			RandomTransitionData rtd = (RandomTransitionData) obj.data;
			if (rtd == null) {
				rtd = new RandomTransitionData();
			}
			rtd.timePassed += deltaTime;
			if (rtd.timePassed < rtd.waitTime) return false;
			if (rtd.probability / deltaTime > Math.random()) {
				return true;
			}
			return false;
		}
	},
	ANIMATION_FINISHED(false) {
		@Override
		public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
			AnimationComponent animComp = Mappers.animation.get(entity);
			ESMComponent esmComp = Mappers.esm.get(entity);
			if (animComp == null || esmComp == null) return false;
			EntityStateMachine esm = esmComp.esm;
			if (esm.getAnimationTime() >= animComp.animations.get(esm.getAnimation()).getAnimationDuration() - 2 * deltaTime) {
				return true;
			}
			return false;
		}
	},
	LANDED(false) {
		@Override
		public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
			CollisionComponent collisionComp = Mappers.collision.get(entity);
			if (collisionComp == null) return false;
			if (collisionComp.onGround()) {
				return true;
			}
			return false;
		}
	},
	INPUT(true) {
		@Override
		public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
			InputComponent inputComp = Mappers.input.get(entity);
			if (inputComp == null || !inputComp.enabled) return false;
			InputTransitionData itd = (InputTransitionData) obj.data;
			if (itd == null) return false;
			if (checkInput(itd, inputComp.input)) {
				return true;
			}
			return false;
		}
		
		private boolean checkInput(InputTransitionData itd, Input input) {
			int counter = 0;
			for (InputTrigger trigger : itd.triggers) {
				boolean triggered = false;
				// If its a game input, it must be past the analog threshold to be considered an action
				if(input instanceof GameInput){
					triggered = trigger.justPressed ? input.isJustPressed(trigger.action) : input.getValue(trigger.action) > GameInput.ANALOG_THRESHOLD;
				}
				else{
					triggered = trigger.justPressed ? input.isJustPressed(trigger.action) : input.isPressed(trigger.action);
				}
				triggered = (triggered && itd.pressed) || (!triggered && !itd.pressed);
				if (triggered && itd.type == InputTransitionData.Type.ANY_ONE) return true;
				if (triggered) counter++;
			}
			switch (itd.type) {
			case ANY_ONE:
				return false;
			case ONLY_ONE:
				return counter == 1;
			case ALL:
				return counter == itd.triggers.size;
			default:
				return false;
			}
		}
	},
	RANGE(true) {
		@Override
		public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
			LevelComponent levelComp = Mappers.level.get(entity);
			BodyComponent bodyComp = Mappers.body.get(entity);
			if(levelComp == null || bodyComp == null) return false;
			
			TargetComponent targetComp = Mappers.target.get(entity);
			RangeTransitionData rtd = (RangeTransitionData)obj.data;
			if(rtd == null || targetComp == null || !EntityUtils.isValid(targetComp.target)) return false;
			
			FacingComponent facingComp = Mappers.facing.get(entity);
			BodyComponent otherBodyComp = Mappers.body.get(targetComp.target);
			
			Body b1 = bodyComp.body;
			Body b2 = otherBodyComp.body;

			float x1 = b1.getPosition().x;
			float y1 = b1.getPosition().y;
			float x2 = b2.getPosition().x;
			float y2 = b2.getPosition().y;
			float r = rtd.distance * rtd.distance;
			boolean inside = rtd.inRange;
			
			float d = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
			
			float opposite = y2 - y1;
			float adjacent = facingComp.facingRight ? x2 - x1 : x1 - x2;
			float angle = MathUtils.atan2(opposite, adjacent) * MathUtils.radiansToDegrees;
			angle = angle < 0 ? angle + 360 : angle;

//			float halfAngle = angle * 0.5f;
			float halfFov = rtd.fov * 0.5f;
			boolean inFov = angle < halfFov || angle > 360 - halfFov;
			
			return (inside && d <= r && inFov) || (!inside && (d >= r || !inFov));
		}
	},
	LINE_OF_SIGHT(false){
		@Override
		public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
			LevelComponent levelComp = Mappers.level.get(entity);
			BodyComponent bodyComp = Mappers.body.get(entity);
			if(levelComp == null || bodyComp == null) return false;
			
			TargetComponent targetComp = Mappers.target.get(entity);
			LOSTransitionData ltd = (LOSTransitionData)obj.data;
			if(targetComp == null || !EntityUtils.isValid(targetComp.target)) return false;
			
			BodyComponent otherBodyComp = Mappers.body.get(targetComp.target);
			
			Body b1 = bodyComp.body;
			Body b2 = otherBodyComp.body;

			float x1 = b1.getPosition().x;
			float y1 = b1.getPosition().y;
			float x2 = b2.getPosition().x;
			float y2 = b2.getPosition().y;
			
			return (ltd.inSight && levelComp.level.performRayTrace(x1, y1, x2, y2)) || (!ltd.inSight && !levelComp.level.performRayTrace(x1, y1, x2, y2));
		}
		
	},
	INVALID_ENTITY(true) {
		@Override
		public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
			TargetComponent targetComp = Mappers.target.get(entity);
//			InvalidEntityData ied = (InvalidEntityData)obj.data;
			if(targetComp == null) return false;
			return !EntityUtils.isValid(targetComp.target);
		};
	},
	TIME(false) {
		@Override
		public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
			TimeTransitionData ttd = (TimeTransitionData) obj.data;
			if (ttd == null) {
				return false;
			}
			ttd.timePassed += deltaTime;
			return ttd.timePassed >= ttd.time;
		}
	},
	COLLISION(true) {
		@Override
		public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
			CollisionComponent collisionComp = Mappers.collision.get(entity);
			CollisionTransitionData data = (CollisionTransitionData)obj.data;
			boolean shouldTransition = false;
			switch(data.type){
			case CEILING:
				if(collisionComp.hittingCeiling()) shouldTransition = true;
				break;
			case GROUND:
				if(collisionComp.onGround()) shouldTransition = true;
				break;
			case LADDER:
				if(collisionComp.onLadder()) shouldTransition = true;  
				break;
			case LEFT_WALL:
				if(collisionComp.onLeftWall()) shouldTransition = true;
				break;
			case RIGHT_WALL:
				if(collisionComp.onRightWall()) shouldTransition = true;
				break;
			default:
				break;
			}
			return (data.onCollide && shouldTransition) || (!data.onCollide && !shouldTransition);
		}
	},
	COMPONENT(true) {
		@Override
		public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
			ComponentTransitionData ctd = (ComponentTransitionData)obj.data;
			if(ctd == null) return false;
			Component comp = entity.getComponent(ctd.component);
			return (comp == null && ctd.remove) || (comp != null && !ctd.remove);
		}
	},
	ABILITY(true){
		@Override
		public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
			AbilityComponent abilityComp = Mappers.ability.get(entity);
			if(abilityComp == null) return false;
			AbilityTransitionData atd = (AbilityTransitionData) obj.data;
			if(atd == null) return false;
			return abilityComp.isAbilityReady(atd.type);
		}
	};

	public final boolean allowMultiple;

	private Transition(boolean allowMultiple) {
		this.allowMultiple = allowMultiple;
	}

	public abstract boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime);

	public String toString() {
		return StringUtils.toTitleCase(name());
	}

}
