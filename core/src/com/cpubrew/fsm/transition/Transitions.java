package com.cpubrew.fsm.transition;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.cpubrew.component.ASMComponent;
import com.cpubrew.component.AnimationComponent;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.CollisionComponent;
import com.cpubrew.component.ESMComponent;
import com.cpubrew.component.FacingComponent;
import com.cpubrew.component.InputComponent;
import com.cpubrew.component.LevelComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.TargetComponent;
import com.cpubrew.fsm.AnimationStateMachine;
import com.cpubrew.input.GameInput;
import com.cpubrew.input.Input;
import com.cpubrew.utils.EntityUtils;
import com.cpubrew.utils.StringUtils;

public enum Transitions implements Transition{

	FALLING {
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

		@Override
		public boolean allowMultiple() {
			return false;
		}

	},
	RANDOM {
		@Override
		public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
			RandomTransitionData rtd = (RandomTransitionData) obj.data;
			if(rtd == null) return false;
			rtd.timePassed += deltaTime;
			if (rtd.timePassed >= rtd.waitTime) {
				return true;
			}
			return false;
		}

		@Override
		public boolean allowMultiple() {
			return true;
		}
	},
	ANIMATION_FINISHED {
		@Override
		public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
			AnimationComponent animComp = Mappers.animation.get(entity);
			ASMComponent asmComp = Mappers.asm.get(entity);
			ESMComponent esmComp = Mappers.esm.get(entity);
			if (animComp == null || asmComp == null) return false;
			for(AnimationStateMachine machine : asmComp.getMachines()){
				// CLEANUP MESSY!! How to handle ESMs and ASMs both having Animation Finished transitions???
				// CLEANUP MESSY!! How to handle ESMs and ASMs both having Animation Finished transitions???
				// CLEANUP MESSY!! How to handle ESMs and ASMs both having Animation Finished transitions???
				// CLEANUP MESSY!! How to handle ESMs and ASMs both having Animation Finished transitions???
				if(machine.getCurrentStateObject().getAllTransitionObjects().contains(obj) ||
					(esmComp != null && esmComp.first() != null && 
					esmComp.first().getCurrentStateObject().getAllTransitionObjects().contains(obj))) {
					
					if (machine.getAnimationTime() >= animComp.animations.get(machine.getCurrentAnimation()).getAnimationDuration() - 2 * deltaTime) {
						return true;
					}
				}
			}
			return false;
		}
		
		@Override
		public boolean allowMultiple() {
			return false;
		}
	},
	LANDED {
		@Override
		public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
			CollisionComponent collisionComp = Mappers.collision.get(entity);
			if (collisionComp == null) return false;
			if (collisionComp.onGround()) {
				return true;
			}
			return false;
		}

		@Override
		public boolean allowMultiple() {
			return false;
		}
	},
	INPUT {
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
		
		@Override
		public boolean allowMultiple() {
			return true;
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
					triggered = trigger.justPressed ? input.isJustPressed(trigger.action) : input.isPressed(trigger.action) || input.isJustPressed(trigger.action);
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
	RANGE {
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

		@Override
		public boolean allowMultiple() {
			return true;
		}
	},
	LINE_OF_SIGHT{
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
			
			boolean rayTrace = levelComp.level.performRayTrace(x1, y1, x2, y2);
			return (ltd.inSight && rayTrace || (!ltd.inSight && !rayTrace));
		}

		@Override
		public boolean allowMultiple() {
			return false;
		}
		
	},
	INVALID_ENTITY {
		@Override
		public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
			TargetComponent targetComp = Mappers.target.get(entity);
//			InvalidEntityData ied = (InvalidEntityData)obj.data;
			if(targetComp == null) return false;
			return !EntityUtils.isValid(targetComp.target);
		};
		
		@Override
		public boolean allowMultiple() {
			return false;
		}
	},
	TIME {
		@Override
		public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
			TimeTransitionData ttd = (TimeTransitionData) obj.data;
			if (ttd == null) {
				return false;
			}
			ttd.timePassed += deltaTime;
			return ttd.timePassed >= ttd.time;
		}
		
		@Override
		public boolean allowMultiple() {
			return false;
		}
	},
	COLLISION {
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
		
		@Override
		public boolean allowMultiple() {
			return true;
		}
	},
	COMPONENT {
		@Override
		public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
			ComponentTransitionData ctd = (ComponentTransitionData)obj.data;
			if(ctd == null) return false;
			Component comp = entity.getComponent(ctd.component);
			return (comp == null && ctd.remove) || (comp != null && !ctd.remove);
		}
		
		@Override
		public boolean allowMultiple() {
			return true;
		}
	};

	public String toString() {
		return StringUtils.toTitleCase(name());
	}

}
