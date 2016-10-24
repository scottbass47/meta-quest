package com.fullspectrum.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.World;
import com.fullspectrum.ai.AIController;
import com.fullspectrum.component.AIControllerComponent;
import com.fullspectrum.component.AIStateMachineComponent;
import com.fullspectrum.component.AnimationComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.DirectionComponent;
import com.fullspectrum.component.FSMComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.FollowComponent;
import com.fullspectrum.component.GroundMovementComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.JumpComponent;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.RenderComponent;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.component.TargetComponent;
import com.fullspectrum.component.TextureComponent;
import com.fullspectrum.component.VelocityComponent;
import com.fullspectrum.component.WanderingComponent;
import com.fullspectrum.component.WorldComponent;
import com.fullspectrum.entity.enemy.GoblinAssets;
import com.fullspectrum.entity.player.PlayerAssets;
import com.fullspectrum.fsm.AIState;
import com.fullspectrum.fsm.AIStateMachine;
import com.fullspectrum.fsm.EntityState;
import com.fullspectrum.fsm.EntityStateMachine;
import com.fullspectrum.fsm.transition.InputTransitionData;
import com.fullspectrum.fsm.transition.InputTransitionData.Type;
import com.fullspectrum.fsm.transition.InputTrigger;
import com.fullspectrum.fsm.transition.RandomTransitionData;
import com.fullspectrum.fsm.transition.RangeTransitionData;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionTag;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.Input;

public class EntityFactory {

	public static Entity createPlayer(Input input, World world, float x, float y) {
		// Setup Player
		Entity player = new Entity();
		player.add(new PositionComponent(x, y));
		player.add(new VelocityComponent());
		player.add(new RenderComponent());
		player.add(new TextureComponent(PlayerAssets.animations.get(EntityAnim.IDLE).getKeyFrame(0)));
		player.add(new InputComponent(input));
		player.add(new FacingComponent());
		player.add(new BodyComponent());
		player.add(new WorldComponent(world));
		player.add(new AnimationComponent()
			.addAnimation(EntityAnim.IDLE, PlayerAssets.animations.get(EntityAnim.IDLE))
			.addAnimation(EntityAnim.RUNNING, PlayerAssets.animations.get(EntityAnim.RUNNING))
			.addAnimation(EntityAnim.JUMP, PlayerAssets.animations.get(EntityAnim.JUMP))
			.addAnimation(EntityAnim.FALLING, PlayerAssets.animations.get(EntityAnim.FALLING))
			.addAnimation(EntityAnim.RANDOM_IDLE, PlayerAssets.animations.get(EntityAnim.RANDOM_IDLE))
			.addAnimation(EntityAnim.RISE, PlayerAssets.animations.get(EntityAnim.RISE))
			.addAnimation(EntityAnim.JUMP_APEX, PlayerAssets.animations.get(EntityAnim.JUMP_APEX)));

		EntityStateMachine fsm = new EntityStateMachine(player, "body/player.json");
		fsm.setDebugName("Entity State Machine");
		fsm.createState(EntityStates.RUNNING)
				.add(new SpeedComponent(8.0f))
				.add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.addAnimation(EntityAnim.RUNNING)
				.addTag(TransitionTag.GROUND_STATE);

		RandomTransitionData rtd = new RandomTransitionData();
		rtd.waitTime = 4.0f;
		rtd.probability = 1.0f;

		fsm.createState(EntityStates.IDLING)
				.add(new SpeedComponent(0.0f))
				.add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.addAnimation(EntityAnim.IDLE)
				.addAnimation(EntityAnim.RANDOM_IDLE)
				.addAnimTransition(EntityAnim.IDLE, Transition.RANDOM, rtd, EntityAnim.RANDOM_IDLE)
				.addAnimTransition(EntityAnim.RANDOM_IDLE, Transition.ANIMATION_FINISHED, EntityAnim.IDLE)
				.addTag(TransitionTag.GROUND_STATE);

		fsm.createState(EntityStates.FALLING)
				.add(new SpeedComponent(8.0f))
				.add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.addAnimation(EntityAnim.JUMP_APEX)
				.addAnimation(EntityAnim.FALLING)
				.addAnimTransition(EntityAnim.JUMP_APEX, Transition.ANIMATION_FINISHED, EntityAnim.FALLING)
				.addTag(TransitionTag.AIR_STATE);

		fsm.createState(EntityStates.DIVING)
				.add(new SpeedComponent(8.0f))
				.add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.add(new JumpComponent(-20.0f))
				.addAnimation(EntityAnim.FALLING)
				.addTag(TransitionTag.AIR_STATE);

		fsm.createState(EntityStates.JUMPING)
				.add(new SpeedComponent(5.0f))
				.add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.add(new JumpComponent(20.0f))
				.addAnimation(EntityAnim.JUMP)
				.addAnimation(EntityAnim.RISE)
				.addAnimTransition(EntityAnim.JUMP, Transition.ANIMATION_FINISHED, EntityAnim.RISE)
				.addTag(TransitionTag.AIR_STATE);

		InputTransitionData runningData = new InputTransitionData(Type.ONLY_ONE, true);
		runningData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		runningData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData jumpData = new InputTransitionData(Type.ALL, true);
		jumpData.triggers.add(new InputTrigger(Actions.JUMP, true));

		InputTransitionData idleData = new InputTransitionData(Type.ALL, false);
		idleData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		idleData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData bothData = new InputTransitionData(Type.ALL, true);
		bothData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		bothData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData diveData = new InputTransitionData(Type.ALL, true);
		diveData.triggers.add(new InputTrigger(Actions.MOVE_DOWN));

		fsm.addTransition(TransitionTag.GROUND_STATE, Transition.FALLING, EntityStates.FALLING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING), Transition.INPUT, runningData, EntityStates.RUNNING);
		fsm.addTransition(TransitionTag.GROUND_STATE, Transition.INPUT, jumpData, EntityStates.JUMPING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.FALLING, EntityStates.FALLING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.JUMPING), Transition.LANDED, EntityStates.IDLING);
		fsm.addTransition(EntityStates.RUNNING, Transition.INPUT, idleData, EntityStates.IDLING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING), Transition.INPUT, bothData, EntityStates.IDLING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.INPUT, diveData, EntityStates.DIVING);

//		System.out.print(fsm.printTransitions());

//		fsm.disableState(EntityStates.DIVING);
		fsm.changeState(EntityStates.IDLING);

		player.add(new FSMComponent(fsm));
		return player;
	}
	
	public static Entity createAIPlayer(AIController controller/*, PathFinder pathFinder*/, Entity toFollow, World world, float x, float y) {
		// Setup Player
		Entity player = new Entity();
		player.add(new PositionComponent(x, y));
		player.add(new VelocityComponent());
		player.add(new RenderComponent());
		player.add(new TextureComponent(PlayerAssets.animations.get(EntityAnim.IDLE).getKeyFrame(0)));
		player.add(new InputComponent(controller));
		player.add(new AIControllerComponent(controller));
		player.add(new TargetComponent(toFollow));
//		player.add(new PathComponent(pathFinder));
//		player.add(new FollowComponent(toFollow));
		player.add(new FacingComponent());
		player.add(new BodyComponent());
		player.add(new WorldComponent(world));
		player.add(new AnimationComponent()
			.addAnimation(EntityAnim.IDLE, PlayerAssets.animations.get(EntityAnim.IDLE))
			.addAnimation(EntityAnim.RUNNING, PlayerAssets.animations.get(EntityAnim.RUNNING))
			.addAnimation(EntityAnim.JUMP, PlayerAssets.animations.get(EntityAnim.JUMP))
			.addAnimation(EntityAnim.FALLING, PlayerAssets.animations.get(EntityAnim.FALLING))
			.addAnimation(EntityAnim.RANDOM_IDLE, PlayerAssets.animations.get(EntityAnim.RANDOM_IDLE))
			.addAnimation(EntityAnim.RISE, PlayerAssets.animations.get(EntityAnim.RISE))
			.addAnimation(EntityAnim.JUMP_APEX, PlayerAssets.animations.get(EntityAnim.JUMP_APEX)));

		EntityStateMachine fsm = new EntityStateMachine(player, "body/player.json");
		fsm.setDebugName("Entity State Machine");
		EntityState runningState = fsm.createState(EntityStates.RUNNING)
				.add(new SpeedComponent(5.0f))
				.add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.addAnimation(EntityAnim.RUNNING);
		runningState.addTag(TransitionTag.GROUND_STATE);

		RandomTransitionData rtd = new RandomTransitionData();
		rtd.waitTime = 4.0f;
		rtd.probability = 1.0f;

		EntityState idleState = fsm.createState(EntityStates.IDLING)
				.add(new SpeedComponent(0.0f))
				.add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.addAnimation(EntityAnim.IDLE)
				.addAnimation(EntityAnim.RANDOM_IDLE)
				.addAnimTransition(EntityAnim.IDLE, Transition.RANDOM, rtd, EntityAnim.RANDOM_IDLE)
				.addAnimTransition(EntityAnim.RANDOM_IDLE, Transition.ANIMATION_FINISHED, EntityAnim.IDLE);
		idleState.addTag(TransitionTag.GROUND_STATE);

		EntityState fallingState = fsm.createState(EntityStates.FALLING)
				.add(new SpeedComponent(5.0f))
				.add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.addAnimation(EntityAnim.JUMP_APEX)
				.addAnimation(EntityAnim.FALLING)
				.addAnimTransition(EntityAnim.JUMP_APEX, Transition.ANIMATION_FINISHED, EntityAnim.FALLING);
		fallingState.addTag(TransitionTag.AIR_STATE);

		EntityState divingState = fsm.createState(EntityStates.DIVING)
				.add(new SpeedComponent(5.0f))
				.add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.add(new JumpComponent(-20.0f))
				.addAnimation(EntityAnim.FALLING);
		divingState.addTag(TransitionTag.AIR_STATE);

		EntityState jumpingState = fsm.createState(EntityStates.JUMPING)
				.add(new SpeedComponent(5.0f))
				.add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.add(new JumpComponent(17.5f))
				.addAnimation(EntityAnim.JUMP)
				.addAnimation(EntityAnim.RISE)
				.addAnimTransition(EntityAnim.JUMP, Transition.ANIMATION_FINISHED, EntityAnim.RISE);
		jumpingState.addTag(TransitionTag.AIR_STATE);

		InputTransitionData runningData = new InputTransitionData(Type.ONLY_ONE, true);
		runningData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		runningData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData jumpData = new InputTransitionData(Type.ALL, true);
		jumpData.triggers.add(new InputTrigger(Actions.JUMP, true));

		InputTransitionData idleData = new InputTransitionData(Type.ALL, false);
		idleData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		idleData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData bothData = new InputTransitionData(Type.ALL, true);
		bothData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		bothData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData diveData = new InputTransitionData(Type.ALL, true);
		diveData.triggers.add(new InputTrigger(Actions.MOVE_DOWN));

		fsm.addTransition(TransitionTag.GROUND_STATE, Transition.FALLING, EntityStates.FALLING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING), Transition.INPUT, runningData, EntityStates.RUNNING);
		fsm.addTransition(TransitionTag.GROUND_STATE, Transition.INPUT, jumpData, EntityStates.JUMPING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.FALLING, EntityStates.FALLING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.JUMPING), Transition.LANDED, EntityStates.IDLING);
		fsm.addTransition(EntityStates.RUNNING, Transition.INPUT, idleData, EntityStates.IDLING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING), Transition.INPUT, bothData, EntityStates.IDLING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.INPUT, diveData, EntityStates.DIVING);

//		System.out.print(fsm.printTransitions());
		
//		fsm.disableState(EntityStates.DIVING);
		fsm.changeState(EntityStates.IDLING);
		
		player.add(new FSMComponent(fsm));
		
		AIStateMachine aism = new AIStateMachine(player);
		aism.createState(AIState.WANDERING)
			.add(new WanderingComponent(20, 1.5f));
		
		aism.createState(AIState.FOLLOWING)
			.add(new FollowComponent(toFollow));
		
		RangeTransitionData wanderingToFollow = new RangeTransitionData();
		wanderingToFollow.target = toFollow;
		wanderingToFollow.distance = 6.0f;
		wanderingToFollow.inRange = true;
		
		RangeTransitionData followToWandering = new RangeTransitionData();
		followToWandering.target = toFollow;
		followToWandering.distance = 8.0f;
		followToWandering.inRange = false;
		
		aism.addTransition(AIState.WANDERING, Transition.RANGE, wanderingToFollow, AIState.FOLLOWING);
		aism.addTransition(AIState.FOLLOWING, Transition.RANGE, followToWandering, AIState.WANDERING);
		
		aism.changeState(AIState.WANDERING);
//		aism.disableState(AIState.FOLLOWING);
		
		System.out.println(aism.printTransitions());
		
		player.add(new AIStateMachineComponent(aism));
		return player;
	}
	
	public static Entity createGoblin(AIController controller, World world, float x, float y) {
		// Setup Player
		Entity goblin = new Entity();
		goblin.add(new PositionComponent(x, y));
		goblin.add(new VelocityComponent());
		goblin.add(new RenderComponent());
		goblin.add(new TextureComponent(GoblinAssets.animations.get(EntityAnim.IDLE).getKeyFrame(0)));
		goblin.add(new InputComponent(controller));
		goblin.add(new AIControllerComponent(controller));
		goblin.add(new FacingComponent());
		goblin.add(new BodyComponent());
		goblin.add(new WorldComponent(world));
		goblin.add(new AnimationComponent()
			.addAnimation(EntityAnim.IDLE, GoblinAssets.animations.get(EntityAnim.IDLE))
			.addAnimation(EntityAnim.RUNNING, GoblinAssets.animations.get(EntityAnim.RUNNING)));

		EntityStateMachine fsm = new EntityStateMachine(goblin, "body/goblin.json");
		EntityState runningState = fsm.createState(EntityStates.RUNNING)
				.add(new SpeedComponent(8.0f))
				.add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.addAnimation(EntityAnim.RUNNING);
		runningState.addTag(TransitionTag.GROUND_STATE);

		EntityState idleState = fsm.createState(EntityStates.IDLING)
				.add(new SpeedComponent(0.0f))
				.add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.addAnimation(EntityAnim.IDLE);
		idleState.addTag(TransitionTag.GROUND_STATE);

		EntityState fallingState = fsm.createState(EntityStates.FALLING)
				.add(new SpeedComponent(8.0f))
				.add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.addAnimation(EntityAnim.IDLE);
		fallingState.addTag(TransitionTag.AIR_STATE);
//
//		EntityState divingState = fsm.createState(EntityStates.DIVING)
//				.add(new SpeedComponent(5.0f))
//				.add(new DirectionComponent())
//				.add(new GroundMovementComponent())
//				.add(new JumpComponent(-20.0f))
//				.addAnimation(EntityAnim.FALLING);
//		divingState.addTag(TransitionTag.AIR_STATE);
//
		EntityState jumpingState = fsm.createState(EntityStates.JUMPING)
				.add(new SpeedComponent(8.0f))
				.add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.add(new JumpComponent(20.0f))
				.addAnimation(EntityAnim.IDLE);
		jumpingState.addTag(TransitionTag.AIR_STATE);

		InputTransitionData runningData = new InputTransitionData(Type.ONLY_ONE, true);
		runningData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		runningData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData jumpData = new InputTransitionData(Type.ALL, true);
		jumpData.triggers.add(new InputTrigger(Actions.JUMP, true));

		InputTransitionData idleData = new InputTransitionData(Type.ALL, false);
		idleData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		idleData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData bothData = new InputTransitionData(Type.ALL, true);
		bothData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		bothData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData diveData = new InputTransitionData(Type.ALL, true);
		diveData.triggers.add(new InputTrigger(Actions.MOVE_DOWN));

		fsm.addTransition(TransitionTag.GROUND_STATE, Transition.FALLING, EntityStates.FALLING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING), Transition.INPUT, runningData, EntityStates.RUNNING);
		fsm.addTransition(TransitionTag.GROUND_STATE, Transition.INPUT, jumpData, EntityStates.JUMPING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING), Transition.FALLING, EntityStates.FALLING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.JUMPING), Transition.LANDED, EntityStates.IDLING);
		fsm.addTransition(EntityStates.RUNNING, Transition.INPUT, idleData, EntityStates.IDLING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING), Transition.INPUT, bothData, EntityStates.IDLING);
//		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.INPUT, diveData, EntityStates.DIVING);

//		System.out.print(fsm.printTransitions());

		fsm.changeState(EntityStates.IDLING);

		goblin.add(new FSMComponent(fsm));
		return goblin;
	}

}
