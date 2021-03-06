package com.cpubrew.factory;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.cpubrew.component.CollisionComponent;
import com.cpubrew.component.DirectionComponent;
import com.cpubrew.component.GroundMovementComponent;
import com.cpubrew.component.InputComponent;
import com.cpubrew.component.JumpComponent;
import com.cpubrew.component.LadderComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.SpeedComponent;
import com.cpubrew.component.SwingComponent;
import com.cpubrew.component.WallComponent;
import com.cpubrew.entity.EntityAnim;
import com.cpubrew.entity.EntityStates;
import com.cpubrew.entity.EntityStats;
import com.cpubrew.fsm.EntityState;
import com.cpubrew.fsm.EntityStateMachine;
import com.cpubrew.fsm.MultiTransition;
import com.cpubrew.fsm.State;
import com.cpubrew.fsm.StateChangeListener;
import com.cpubrew.fsm.StateChangeResolver;
import com.cpubrew.fsm.transition.InputTransitionData;
import com.cpubrew.fsm.transition.InputTrigger;
import com.cpubrew.fsm.transition.TimeTransitionData;
import com.cpubrew.fsm.transition.TransitionTag;
import com.cpubrew.fsm.transition.Transitions;
import com.cpubrew.fsm.transition.InputTransitionData.Type;
import com.cpubrew.input.Actions;
import com.cpubrew.utils.EntityUtils;

public class StateFactory {
	
	public static EntityStateMachine createBaseBipedal(Entity entity, EntityStats stats) {
		EntityStateBuilder builder = new EntityStateBuilder(Mappers.entity.get(entity).type.toString(), Mappers.engine.get(entity).engine, entity);
		
		// Build the states
		builder.idle();
		builder.run(stats.get("ground_speed"));
		builder.jump(stats.get("jump_force"), stats.get("jump_float_amount"), stats.get("air_speed"), true, true);
		builder.fall(stats.get("air_speed"), true);

		EntityStateMachine esm = builder.build();
		
		esm.getState(EntityStates.IDLING).addChangeListener(new StateChangeListener() {
			@Override
			public void onEnter(State prevState, Entity entity) {
				if(prevState == EntityStates.JUMPING) {
					Mappers.body.get(entity).body.setLinearVelocity(Mappers.body.get(entity).body.getLinearVelocity().x, 0);
				}
			}

			@Override
			public void onExit(State nextState, Entity entity) {
			}
		});
		
		esm.getState(EntityStates.IDLING).setChangeResolver(new StateChangeResolver() {
			@Override
			public State resolve(Entity entity, State oldState) {
				CollisionComponent collisionComp = Mappers.collision.get(entity);
				
				if(!collisionComp.onGround()) {
					Body body = Mappers.body.get(entity).body;
					
					boolean falling = body.getLinearVelocity().y <= 0;
					return falling ? EntityStates.FALLING : EntityStates.FALLING; // INCOMPLETE Can't go to jumping because jump state adds a jump component
				}
				return EntityStates.IDLING;
			}
		});
		
		// Setup transitions
		InputTransitionData runningData = new InputTransitionData(Type.ONLY_ONE, true);
		runningData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		runningData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData jumpData = new InputTransitionData(Type.ALL, true);
		jumpData.triggers.add(new InputTrigger(Actions.JUMP, false));

		InputTransitionData attackData = new InputTransitionData(Type.ALL, true);
		attackData.triggers.add(new InputTrigger(Actions.ATTACK, true));
		
//		InputTransitionData ladderInputData = new InputTransitionData(Type.ANY_ONE, true);
//		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_UP, false));
//		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_DOWN, false));
		
//		CollisionTransitionData ladderCollisionData = new CollisionTransitionData(CollisionType.LADDER, true);
//		
//		MultiTransition ladderTransition = new MultiTransition(Transitions.INPUT, ladderInputData)
//					.and(Transitions.COLLISION, ladderCollisionData);
//		
//		CollisionTransitionData ladderFall = new CollisionTransitionData(CollisionType.LADDER, false);
		
		esm.addTransition(TransitionTag.GROUND_STATE, Transitions.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING, TransitionTag.STATIC_STATE), Transitions.INPUT, runningData, EntityStates.RUNNING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(TransitionTag.STATIC_STATE), Transitions.INPUT, jumpData, EntityStates.JUMPING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING), Transitions.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING, TransitionTag.STATIC_STATE), InputFactory.idle(), EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING)/*.exclude(EntityStates.JUMPING)*/, new MultiTransition(Transitions.LANDED).and(Transitions.TIME, new TimeTransitionData(0.25f)), EntityStates.IDLING);
		esm.addTransition(EntityStates.FALLING, Transitions.LANDED, EntityStates.IDLING);	
//		esm.addTransition(esm.one(TransitionTag.AIR_STATE, TransitionTag.GROUND_STATE), ladderTransition, EntityStates.CLIMBING);
//		esm.addTransition(EntityStates.CLIMBING, Transitions.COLLISION, ladderFall, EntityStates.FALLING);
//		esm.addTransition(EntityStates.CLIMBING, Transitions.LANDED, EntityStates.IDLING);
		
		return builder.build();
	}
	
	/**
	 * Stats need <code>ground_speed</code>
	 * @param entity
	 * @param stats
	 * @return
	 */
	public static EntityStateMachine createBaseBipedalNoJump(Entity entity, EntityStats stats) {
		EntityStateMachine esm = new EntityStateBuilder(Mappers.entity.get(entity).type.toString(), Mappers.engine.get(entity).engine, entity)
				.idle()
				.run(stats.get("ground_speed"))
				.build();
		
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING), InputFactory.idle(), EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING), Transitions.INPUT, InputFactory.run(), EntityStates.RUNNING);
		return esm;
	}
	
	public static class EntityStateBuilder{
		
		private Engine engine;
		private EntityStateMachine esm;
		
		public EntityStateBuilder(String debugName, Engine engine, Entity entity){
			this.engine = engine;
			esm = new EntityStateMachine(entity);
			esm.setDebugName(debugName);
		}
		
		public EntityStateBuilder(EntityStateMachine esm) {
			this.esm = esm;
			engine = Mappers.engine.get(esm.getEntity()).engine;
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
				.addAnimation(EntityAnim.RUN)
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
					.addAnimTransition(EntityAnim.JUMP_APEX, Transitions.ANIMATION_FINISHED, EntityAnim.FALLING);
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
		public EntityStateBuilder jump(final float jumpForce, final float floatAmount, float airSpeed, boolean withStateChangeListener, final boolean jumpParticle){
			EntityState state = esm.createState(EntityStates.JUMPING)
				.add(engine.createComponent(SpeedComponent.class).set(airSpeed))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.addAnimation(EntityAnim.JUMP)
				.addAnimation(EntityAnim.RISE)
				.addAnimTransition(EntityAnim.JUMP, Transitions.ANIMATION_FINISHED, EntityAnim.RISE)
				.addTag(TransitionTag.AIR_STATE);
			if(withStateChangeListener){
				state.addChangeListener(new StateChangeListener(){
					@Override
					public void onEnter(State prevState, Entity entity) {
						JumpComponent jumpComp = EntityUtils.add(entity, JumpComponent.class).set(jumpForce, floatAmount);
						InputComponent inputComp = Mappers.input.get(entity);						
						jumpComp.multiplier = inputComp.input.getValue(Actions.JUMP);
						if(jumpParticle) ParticleFactory.spawnJumpParticle(entity);
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
		public EntityStateBuilder swingAttack(float rx, float ry, float startAngle, float endAngle, float delay, float damage, float knockback){
			esm.createState(EntityStates.SWING_ATTACK)
				.add(engine.createComponent(SpeedComponent.class).set(0.0f))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.add(engine.createComponent(SwingComponent.class).set(rx, ry, startAngle, endAngle, delay, damage, knockback))
				.addAnimation(EntityAnim.SWING)
				.addTag(TransitionTag.GROUND_STATE)
				.addTag(TransitionTag.STATIC_STATE)
				.addChangeListener(new StateChangeListener() {
					@Override
					public void onEnter(State prevState, Entity entity) {
						Mappers.swing.get(entity).shouldSwing = true;
					}
					
					@Override
					public void onExit(State nextState, Entity entity) {
						
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
		
//		public EntityStateBuilder knockBack(EntityStates returnToState){
//			esm.createState(EntityStates.KNOCK_BACK)
////			.add(engine.createComponent(SpeedComponent.class).set(0.0f))
////			.add(engine.createComponent(DirectionComponent.class))
////			.add(engine.createComponent(GroundMovementComponent.class))
//			.addAnimation(EntityAnim.IDLE)
//			.addTag(TransitionTag.STATIC_STATE)
//			.addChangeListener(new StateChangeListener(){
//				@Override
//				public void onEnter(State prevState, Entity entity) {
//					KnockBackComponent knockBackComp = Mappers.knockBack.get(entity);
//					EngineComponent engineComp = Mappers.engine.get(entity);
//					float fx = knockBackComp.speed * MathUtils.cosDeg(knockBackComp.angle);
//					float fy = knockBackComp.speed * MathUtils.sinDeg(knockBackComp.angle);
//					entity.add(engineComp.engine.createComponent(ForceComponent.class).set(fx, fy));
//					
//					float time = knockBackComp.distance / knockBackComp.speed;
//					TimerComponent timerComp = Mappers.timer.get(entity);
//					timerComp.add("knockBack_life", time, false, new TimeListener(){
//						@Override
//						public void onTime(Entity entity) {
//							entity.remove(KnockBackComponent.class);
//						}
//					});
//				}
//
//				@Override
//				public void onExit(State nextState, Entity entity) {
//					
//				}
//			});
//			
//			// Add Knockback Transition
//			esm.addTransition(esm.all(TransitionTag.ALL).exclude(EntityStates.DYING), Transitions.COMPONENT, new ComponentTransitionData(KnockBackComponent.class, false), EntityStates.KNOCK_BACK);
//			esm.addTransition(EntityStates.KNOCK_BACK, Transitions.COMPONENT, new ComponentTransitionData(KnockBackComponent.class, true), returnToState);
//			return this;
//		}
		
		public EntityStateMachine build(){
			return esm;
		}
	}
}