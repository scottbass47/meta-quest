package com.fullspectrum.entity;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.fullspectrum.component.DirectionComponent;
import com.fullspectrum.component.EngineComponent;
import com.fullspectrum.component.ForceComponent;
import com.fullspectrum.component.GroundMovementComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.JumpComponent;
import com.fullspectrum.component.KnockBackComponent;
import com.fullspectrum.component.LadderComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.component.BarrierComponent;
import com.fullspectrum.component.SwingComponent;
import com.fullspectrum.component.SwordComponent;
import com.fullspectrum.component.SwordStatsComponent;
import com.fullspectrum.component.TimeListener;
import com.fullspectrum.component.TimerComponent;
import com.fullspectrum.component.WallComponent;
import com.fullspectrum.fsm.EntityState;
import com.fullspectrum.fsm.EntityStateMachine;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateChangeListener;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionTag;
import com.fullspectrum.input.Actions;

public class StateFactory {
	
	public static class EntityStateBuilder{
		
		private Engine engine;
		private EntityStateMachine esm;
		
		public EntityStateBuilder(Engine engine, Entity entity, String bodyPath){
			this.engine = engine;
			esm = new EntityStateMachine(entity, bodyPath);
		}
		
		/**
		 * Creates an Idle state.<br><br>
		 * Anim - IDLE<br>
		 * State - IDLING<br>
		 * Tags - Ground
		 * @return
		 */
		public EntityStateBuilder idle(){
			esm.createState(EntityStates.IDLING)
				.add(engine.createComponent(SpeedComponent.class).set(0.0f))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.addAnimation(EntityAnim.IDLE)
				.addTag(TransitionTag.GROUND_STATE);
			return this;
		}
		
		/**
		 * Creates a Run state with a specified max speed.<br><br>
		 * Anim - RUNNING<br>
		 * State - RUNNING<br>
		 * Tags - Ground
		 * @param speed
		 * @return
		 */
		public EntityStateBuilder run(float speed){
			esm.createState(EntityStates.RUNNING)
				.add(engine.createComponent(SpeedComponent.class).set(speed))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.addAnimation(EntityAnim.RUNNING)
				.addTag(TransitionTag.GROUND_STATE);
			return this;
		}
		
		/**
		 * Creates a Fall state with a specified max air speed and an optional
		 * boolean for whether or not this state will use an apex animation.<br><br>
		 * Anim - FALLING (optional APEX)<br>
		 * State - FALLING <br>
		 * Tags - Air
		 * 
		 * @param airSpeed
		 * @param withApex
		 * @return
		 */
		public EntityStateBuilder fall(float airSpeed, boolean withApex){
			EntityState state = esm.createState(EntityStates.FALLING)
				.add(engine.createComponent(SpeedComponent.class).set(airSpeed))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.addTag(TransitionTag.AIR_STATE);
			if(withApex){
				state.addAnimation(EntityAnim.JUMP_APEX)
					.addAnimation(EntityAnim.FALLING)
					.addAnimTransition(EntityAnim.JUMP_APEX, Transition.ANIMATION_FINISHED, EntityAnim.FALLING);
			}else{
				state.addAnimation(EntityAnim.FALLING);
			}
			return this;
		}
		
		/**
		 * Creates a Jump state with a specified max jump force and max air speed.<br><br>
		 * Anim - JUMP (initial frames), RISE <br>
		 * State - JUMPING <br>
		 * Tags - Air
		 * 
		 * @param jumpForce
		 * @param airSpeed
		 * @param withStateChangeListener 
		 * @return
		 */
		public EntityStateBuilder jump(float jumpForce, float airSpeed, boolean withStateChangeListener){
			EntityState state = esm.createState(EntityStates.JUMPING)
				.add(engine.createComponent(SpeedComponent.class).set(airSpeed))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.add(engine.createComponent(JumpComponent.class).set(jumpForce))
				.addAnimation(EntityAnim.JUMP)
				.addAnimation(EntityAnim.RISE)
				.addAnimTransition(EntityAnim.JUMP, Transition.ANIMATION_FINISHED, EntityAnim.RISE)
				.addTag(TransitionTag.AIR_STATE);
			if(withStateChangeListener){
				state.addChangeListener(new StateChangeListener(){
					@Override
					public void onEnter(State prevState, Entity entity) {
						JumpComponent jumpComp = Mappers.jump.get(entity);
						InputComponent inputComp = Mappers.input.get(entity);						
						jumpComp.multiplier = inputComp.input.getValue(Actions.JUMP);
					}
					@Override
					public void onExit(State nextState, Entity entity) {
					}
				});
			}
			return this;
		}
		
		/**
		 * Creates a Climb state with a specified max climb speed. <br><br>
		 * Anim - CLIMBING<br>
		 * State - CLIMBING<br>
		 * Tags - none
		 * 
		 * @param climbSpeed
		 * @return
		 */
		public EntityStateBuilder climb(float climbSpeed){
			esm.createState(EntityStates.CLIMBING)
				.add(engine.createComponent(LadderComponent.class).set(climbSpeed, climbSpeed))
				.addAnimation(EntityAnim.CLIMBING)
				.addChangeListener(new StateChangeListener(){
					@Override
					public void onEnter(State prevState, Entity entity) {
						Mappers.body.get(entity).body.setGravityScale(0.0f);
					}
	
					@Override
					public void onExit(State nextState, Entity entity) {
						Mappers.body.get(entity).body.setGravityScale(1.0f);
					}
				});
			return this;
		}
		
		/**
		 * Creates a Swing Attack state with a specified start angle, rotation amount, duration and cost. Start angle determines at what
		 * degree the swing should start at (in degrees using standard unit circle degree measures). Rotation amount determines
		 * how many degrees the sword is swung for. Duration is the time in seconds the entire swing lasts. Cost is the amount of stamina
		 * required for the attack.<br><br>
		 * 
		 * Anim - SWING<br>
		 * State - SWING_ATTACK<br>
		 * Tags - Static, Ground<br>
		 * 
		 * @param sword
		 * @param startAngle
		 * @param rotationAmount
		 * @param duration
		 * @return
		 */
		public EntityStateBuilder swingAttack(Entity sword, float startAngle, float rotationAmount, float duration, final float staminaCost){
			esm.createState(EntityStates.SWING_ATTACK)
				.add(engine.createComponent(SpeedComponent.class).set(0.0f))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.add(engine.createComponent(SwordComponent.class).set(sword))
				.add(engine.createComponent(SwingComponent.class).set(startAngle, rotationAmount, duration))
				.addAnimation(EntityAnim.SWING)
				.addTag(TransitionTag.GROUND_STATE)
				.addTag(TransitionTag.STATIC_STATE)
				.addChangeListener(new StateChangeListener(){
					@Override
					public void onEnter(State prevState, Entity entity) {
						// Setup Sword Swing
						SwingComponent swingComp = Mappers.swing.get(entity);
						swingComp.time = 0;
						
						SwordComponent swordComp = Mappers.sword.get(entity);
						SwordStatsComponent swordStats = Mappers.swordStats.get(swordComp.sword);
						swordStats.hitEntities.clear();
						
						EngineComponent engineComp = Mappers.engine.get(entity);
						engineComp.engine.addEntity(swordComp.sword);
						
						// Lower Stamina
						BarrierComponent staminaComp = Mappers.barrier.get(entity);
						if(staminaComp != null){
							staminaComp.locked = true;
							staminaComp.timeElapsed = 0;
							staminaComp.barrier = MathUtils.clamp(staminaComp.barrier - staminaCost, 0, staminaComp.maxBarrier);
						}
					}
	
					@Override
					public void onExit(State nextState, Entity entity) {
						SwordComponent swordComp = Mappers.sword.get(entity);
						
						EngineComponent engineComp = Mappers.engine.get(entity);
						engineComp.engine.removeEntity(swordComp.sword);
						
						Mappers.body.get(swordComp.sword).body.setActive(false);
						
						// Unlock Stamina
						BarrierComponent staminaComp = Mappers.barrier.get(entity);
						if(staminaComp != null){
							staminaComp.locked = false;
						}
					}
				});
			return this;
		}
		
		public EntityStateBuilder wallSlide(){
			esm.createState(EntityStates.WALL_SLIDING)
				.add(engine.createComponent(SpeedComponent.class).set(0.0f))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(WallComponent.class))
				.addAnimation(EntityAnim.WALL_SLIDING)
				.addChangeListener(new StateChangeListener(){
					@Override
					public void onEnter(State prevState, Entity entity) {
						Mappers.body.get(entity).body.setLinearVelocity(0.0f, -2.5f);
						Mappers.body.get(entity).body.setGravityScale(0.3f);
					}

					@Override
					public void onExit(State nextState, Entity entity) {
						Mappers.body.get(entity).body.setGravityScale(1.0f);
					}
				});
			return this;
		}
		
		public EntityStateBuilder knockBack(){
			esm.createState(EntityStates.KNOCK_BACK)
//			.add(engine.createComponent(SpeedComponent.class).set(0.0f))
//			.add(engine.createComponent(DirectionComponent.class))
//			.add(engine.createComponent(GroundMovementComponent.class))
			.addAnimation(EntityAnim.IDLE)
			.addTag(TransitionTag.STATIC_STATE)
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
					KnockBackComponent knockBackComp = Mappers.knockBack.get(entity);
					float fx = knockBackComp.speed * MathUtils.cosDeg(knockBackComp.angle);
					float fy = knockBackComp.speed * MathUtils.sinDeg(knockBackComp.angle);
					entity.getComponent(ForceComponent.class).add(fx, fy);
					
					float time = knockBackComp.distance / knockBackComp.speed;
					TimerComponent timerComp = Mappers.timer.get(entity);
					timerComp.add("knockBack_life", time, false, new TimeListener(){
						@Override
						public void onTime(Entity entity) {
							entity.remove(KnockBackComponent.class);
						}
					});
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					KnockBackComponent knockBackComp = Mappers.knockBack.get(entity);
					float fx = knockBackComp.speed * MathUtils.cosDeg(knockBackComp.angle);
					float fy = knockBackComp.speed * MathUtils.sinDeg(knockBackComp.angle);
					entity.getComponent(ForceComponent.class).add(-fx, -fy);
				}
			});
			return this;
		}
		
		public EntityStateMachine build(){
			return esm;
		}
	}
}