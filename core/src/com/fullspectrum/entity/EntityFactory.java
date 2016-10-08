package com.fullspectrum.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.World;
import com.fullspectrum.component.AnimationComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.DirectionComponent;
import com.fullspectrum.component.FSMComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.GroundMovementComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.JumpComponent;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.RenderComponent;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.component.TextureComponent;
import com.fullspectrum.component.VelocityComponent;
import com.fullspectrum.component.WorldComponent;
import com.fullspectrum.entity.player.Player;
import com.fullspectrum.entity.player.PlayerAnim;
import com.fullspectrum.fsm.EntityState;
import com.fullspectrum.fsm.EntityStateMachine;
import com.fullspectrum.fsm.PlayerStates;
import com.fullspectrum.fsm.transition.InputTransitionData;
import com.fullspectrum.fsm.transition.InputTransitionData.Type;
import com.fullspectrum.fsm.transition.InputTrigger;
import com.fullspectrum.fsm.transition.RandomTransitionData;
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
		player.add(new TextureComponent(Player.animations.get(PlayerAnim.IDLE).getKeyFrame(0)));
		player.add(new InputComponent(input));
		player.add(new FacingComponent());
		player.add(new BodyComponent());
		player.add(new WorldComponent(world));
		player.add(new AnimationComponent().addAnimation(PlayerAnim.IDLE, Player.animations.get(PlayerAnim.IDLE)).addAnimation(PlayerAnim.RUNNING, Player.animations.get(PlayerAnim.RUNNING)).addAnimation(PlayerAnim.JUMP, Player.animations.get(PlayerAnim.JUMP)).addAnimation(PlayerAnim.FALLING, Player.animations.get(PlayerAnim.FALLING)).addAnimation(PlayerAnim.RANDOM_IDLE, Player.animations.get(PlayerAnim.RANDOM_IDLE)).addAnimation(PlayerAnim.RISE, Player.animations.get(PlayerAnim.RISE)).addAnimation(PlayerAnim.JUMP_APEX, Player.animations.get(PlayerAnim.JUMP_APEX)));

		EntityStateMachine fsm = new EntityStateMachine(player, "body/player.json");
		fsm.setDebugName("Entity State Machine");
		EntityState runningState = fsm.createState(PlayerStates.RUNNING)
				.add(new SpeedComponent(8.0f))
				.add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.addAnimation(PlayerAnim.RUNNING);
		runningState.addTag(TransitionTag.GROUND_STATE);

		RandomTransitionData rtd = new RandomTransitionData();
		rtd.waitTime = 4.0f;
		rtd.probability = 1.0f;

		EntityState idleState = fsm.createState(PlayerStates.IDLING)
				.add(new SpeedComponent(0.0f)).add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.addAnimation(PlayerAnim.IDLE)
				.addAnimation(PlayerAnim.RANDOM_IDLE)
				.addAnimTransition(PlayerAnim.IDLE, Transition.RANDOM, rtd, PlayerAnim.RANDOM_IDLE)
				.addAnimTransition(PlayerAnim.RANDOM_IDLE, Transition.ANIMATION_FINISHED, PlayerAnim.IDLE);
		idleState.addTag(TransitionTag.GROUND_STATE);

		EntityState fallingState = fsm.createState(PlayerStates.FALLING)
				.add(new SpeedComponent(8.0f))
				.add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.addAnimation(PlayerAnim.JUMP_APEX)
				.addAnimation(PlayerAnim.FALLING)
				.addAnimTransition(PlayerAnim.JUMP_APEX, Transition.ANIMATION_FINISHED, PlayerAnim.FALLING);
		fallingState.addTag(TransitionTag.AIR_STATE);

		EntityState divingState = fsm.createState(PlayerStates.DIVING)
				.add(new SpeedComponent(5.0f))
				.add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.add(new JumpComponent(-20.0f))
				.addAnimation(PlayerAnim.FALLING);
		divingState.addTag(TransitionTag.AIR_STATE);

		EntityState jumpingState = fsm.createState(PlayerStates.JUMPING)
				.add(new SpeedComponent(8.0f))
				.add(new DirectionComponent())
				.add(new GroundMovementComponent())
				.add(new JumpComponent(20.0f))
				.addAnimation(PlayerAnim.JUMP)
				.addAnimation(PlayerAnim.RISE)
				.addAnimTransition(PlayerAnim.JUMP, Transition.ANIMATION_FINISHED, PlayerAnim.RISE);
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

		fsm.addTransition(TransitionTag.GROUND_STATE, Transition.FALLING, PlayerStates.FALLING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(PlayerStates.RUNNING), Transition.INPUT, runningData, PlayerStates.RUNNING);
		fsm.addTransition(TransitionTag.GROUND_STATE, Transition.INPUT, jumpData, PlayerStates.JUMPING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(PlayerStates.FALLING, PlayerStates.DIVING), Transition.FALLING, PlayerStates.FALLING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(PlayerStates.JUMPING), Transition.LANDED, PlayerStates.IDLING);
		fsm.addTransition(PlayerStates.RUNNING, Transition.INPUT, idleData, PlayerStates.IDLING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(PlayerStates.IDLING), Transition.INPUT, bothData, PlayerStates.IDLING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(PlayerStates.FALLING, PlayerStates.DIVING), Transition.INPUT, diveData, PlayerStates.DIVING);

//		System.out.print(fsm.printTransitions());

		fsm.changeState(PlayerStates.IDLING);

		player.add(new FSMComponent(fsm));
		return player;
	}

}
