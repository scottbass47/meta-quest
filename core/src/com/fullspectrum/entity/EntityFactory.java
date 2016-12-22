package com.fullspectrum.entity;

import static com.fullspectrum.game.GameVars.PPM_INV;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.ai.AIController;
import com.fullspectrum.assets.Assets;
import com.fullspectrum.component.AIControllerComponent;
import com.fullspectrum.component.AIStateMachineComponent;
import com.fullspectrum.component.AnimationComponent;
import com.fullspectrum.component.AttackComponent;
import com.fullspectrum.component.BlinkComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.BulletStatsComponent;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.DropMovementComponent;
import com.fullspectrum.component.DropTypeComponent;
import com.fullspectrum.component.ESMComponent;
import com.fullspectrum.component.EngineComponent;
import com.fullspectrum.component.FSMComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.FollowComponent;
import com.fullspectrum.component.ForceComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.MoneyComponent;
import com.fullspectrum.component.OffsetComponent;
import com.fullspectrum.component.ParentComponent;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.RemoveComponent;
import com.fullspectrum.component.RenderComponent;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.component.StaminaComponent;
import com.fullspectrum.component.SwordStatsComponent;
import com.fullspectrum.component.TargetComponent;
import com.fullspectrum.component.TextureComponent;
import com.fullspectrum.component.TintComponent;
import com.fullspectrum.component.TypeComponent;
import com.fullspectrum.component.TypeComponent.EntityType;
import com.fullspectrum.component.VelocityComponent;
import com.fullspectrum.component.WanderingComponent;
import com.fullspectrum.component.WorldComponent;
import com.fullspectrum.fsm.AIState;
import com.fullspectrum.fsm.AIStateMachine;
import com.fullspectrum.fsm.EntityStateMachine;
import com.fullspectrum.fsm.MultiTransition;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateChangeListener;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;
import com.fullspectrum.fsm.StateObjectCreator;
import com.fullspectrum.fsm.transition.CollisionTransitionData;
import com.fullspectrum.fsm.transition.CollisionTransitionData.CollisionType;
import com.fullspectrum.fsm.transition.InputTransitionData;
import com.fullspectrum.fsm.transition.InputTransitionData.Type;
import com.fullspectrum.fsm.transition.InputTrigger;
import com.fullspectrum.fsm.transition.InvalidEntityData;
import com.fullspectrum.fsm.transition.RandomTransitionData;
import com.fullspectrum.fsm.transition.RangeTransitionData;
import com.fullspectrum.fsm.transition.StaminaTransitionData;
import com.fullspectrum.fsm.transition.TimeTransitionData;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionTag;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.Input;
import com.fullspectrum.level.Level;
import com.fullspectrum.utils.PhysicsUtils;

public class EntityFactory {

	private static Assets assets = Assets.getInstance();
	
	private EntityFactory(){}
	
	public static Entity createPlayer(Engine engine, Level level, Input input, World world, float x, float y) {
		// Setup Animations
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, assets.getSpriteAnimation(Assets.SHADOW_IDLE));
		animMap.put(EntityAnim.RUNNING, assets.getSpriteAnimation(Assets.SHADOW_RUN));
		animMap.put(EntityAnim.JUMP, assets.getSpriteAnimation(Assets.SHADOW_JUMP));
		animMap.put(EntityAnim.FALLING, assets.getSpriteAnimation(Assets.SHADOW_FALL));
		animMap.put(EntityAnim.RANDOM_IDLE, assets.getSpriteAnimation(Assets.SHADOW_IDLE));
		animMap.put(EntityAnim.RISE, assets.getSpriteAnimation(Assets.SHADOW_RISE));
		animMap.put(EntityAnim.JUMP_APEX, assets.getSpriteAnimation(Assets.SHADOW_APEX));
		animMap.put(EntityAnim.CLIMBING, assets.getSpriteAnimation(Assets.SHADOW_IDLE));
		animMap.put(EntityAnim.SWING, assets.getSpriteAnimation(Assets.SHADOW_PUNCH));
		animMap.put(EntityAnim.WALL_SLIDING, assets.getSpriteAnimation(Assets.SHADOW_IDLE));
		
		// Setup Player
		Entity player = new EntityBuilder(engine, world, level)
				.animation(animMap)
				.mob(input, EntityType.FRIENDLY, 2500f)
				.physics(null, x, y, true)
				.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0), true)
				.build();
		player.add(engine.createComponent(MoneyComponent.class));
		player.add(engine.createComponent(StaminaComponent.class).set(100, 100, 25, 0.3f));

		EntityStateMachine knightESM = createKnight(engine, world, level, x, y, player);
		EntityStateMachine rogueESM = createRogue(engine, world, level, x, y, player);
		EntityStateMachine mageESM = createMage(engine, world, level, x, y, player);
		StateMachine<EntityStates, StateObject> rogueAttackFSM = createRogueAttackMachine(player);

		StateMachine<PlayerState, StateObject> playerStateMachine = new StateMachine<PlayerState, StateObject>(player, new StateObjectCreator(), PlayerState.class, StateObject.class);
		
		playerStateMachine.createState(PlayerState.KNIGHT)
			.add(engine.createComponent(ESMComponent.class).set(knightESM))
			.add(engine.createComponent(TintComponent.class).set(new Color(123 / 255f, 123 / 255f, 184 / 255f, 1.0f)))
			.addSubstateMachine(knightESM);
		
		playerStateMachine.createState(PlayerState.ROGUE)
			.add(engine.createComponent(ESMComponent.class).set(rogueESM))
			.add(engine.createComponent(FSMComponent.class).set(rogueAttackFSM))
			.add(engine.createComponent(TintComponent.class).set(new Color(176 / 255f, 47 / 255f, 42 / 255f, 1.0f)))
			.addSubstateMachine(rogueESM)
			.addSubstateMachine(rogueAttackFSM);
		
		playerStateMachine.createState(PlayerState.MAGE)
			.add(engine.createComponent(ESMComponent.class).set(mageESM))
			.add(engine.createComponent(TintComponent.class).set(new Color(165 / 255f, 65 / 255f, 130 / 255f, 1.0f)))
			.addSubstateMachine(mageESM);
		
		
		InputTransitionData rightCycleData = new InputTransitionData(Type.ALL, true);
		rightCycleData.triggers.add(new InputTrigger(Actions.CYCLE_RIGHT, true));
		
		InputTransitionData leftCycleData = new InputTransitionData(Type.ALL, true);
		leftCycleData.triggers.add(new InputTrigger(Actions.CYCLE_LEFT, true));
		
		// Knight, Rogue, Mage
		playerStateMachine.addTransition(PlayerState.KNIGHT, Transition.INPUT, rightCycleData, PlayerState.ROGUE);
		playerStateMachine.addTransition(PlayerState.KNIGHT, Transition.INPUT, leftCycleData, PlayerState.MAGE);
		playerStateMachine.addTransition(PlayerState.ROGUE, Transition.INPUT, rightCycleData, PlayerState.MAGE);
		playerStateMachine.addTransition(PlayerState.ROGUE, Transition.INPUT, leftCycleData, PlayerState.KNIGHT);
		playerStateMachine.addTransition(PlayerState.MAGE, Transition.INPUT, rightCycleData, PlayerState.KNIGHT);
		playerStateMachine.addTransition(PlayerState.MAGE, Transition.INPUT, leftCycleData, PlayerState.ROGUE);
		
		playerStateMachine.changeState(PlayerState.KNIGHT);
		player.add(engine.createComponent(FSMComponent.class).set(playerStateMachine));
		return player;
	}
	
	private static EntityStateMachine createKnight(Engine engine, World world, Level level, float x, float y, Entity player){
		float PLAYER_SPEED = 8.0f;

		RandomTransitionData rtd = new RandomTransitionData();
		rtd.waitTime = 4.0f;
		rtd.probability = 1.0f;
		
		Entity sword = createSword(engine, world, level, player, x, y, 100);
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder(engine, player, "body/player.json")
			.idle()
			.run(PLAYER_SPEED)
			.jump(14f, PLAYER_SPEED, true)
			.fall(PLAYER_SPEED, true)
			.climb(5.0f)
			.swingAttack(sword, 150f, 210f, 0.6f, 25f)
			.build();
				
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

//		InputTransitionData diveData = new InputTransitionData(Type.ALL, true);
//		diveData.triggers.add(new InputTrigger(Actions.MOVE_DOWN));
		
		InputTransitionData attackData = new InputTransitionData(Type.ALL, true);
		attackData.triggers.add(new InputTrigger(Actions.ATTACK, true));
		
		StaminaTransitionData attackStamina = new StaminaTransitionData(25f);
		
		MultiTransition attackTransition = new MultiTransition();
		attackTransition.addTransition(Transition.INPUT, attackData);
		attackTransition.addTransition(Transition.STAMINA, attackStamina);
		
		InputTransitionData ladderInputData = new InputTransitionData(Type.ANY_ONE, true);
		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_UP, false));
		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_DOWN, false));
		
		CollisionTransitionData ladderCollisionData = new CollisionTransitionData(CollisionType.LADDER, true);
		
		MultiTransition ladderTransition = new MultiTransition();
		ladderTransition.addTransition(Transition.INPUT, ladderInputData);
		ladderTransition.addTransition(Transition.COLLISION, ladderCollisionData);
		
		CollisionTransitionData ladderFall = new CollisionTransitionData(CollisionType.LADDER, false);
		
		esm.addTransition(TransitionTag.GROUND_STATE, Transition.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING, TransitionTag.STATIC_STATE), Transition.INPUT, runningData, EntityStates.RUNNING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(TransitionTag.STATIC_STATE), Transition.INPUT, jumpData, EntityStates.JUMPING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.JUMPING), Transition.LANDED, EntityStates.IDLING);
		esm.addTransition(EntityStates.RUNNING, Transition.INPUT, idleData, EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING, TransitionTag.STATIC_STATE), Transition.INPUT, bothData, EntityStates.IDLING);
//		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.INPUT, diveData, EntityStates.DIVING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.SWING_ATTACK), attackTransition, EntityStates.SWING_ATTACK);
		esm.addTransition(EntityStates.SWING_ATTACK, Transition.ANIMATION_FINISHED, EntityStates.IDLING);
		esm.addTransition(esm.one(TransitionTag.AIR_STATE, TransitionTag.GROUND_STATE), ladderTransition, EntityStates.CLIMBING);
		esm.addTransition(EntityStates.CLIMBING, Transition.COLLISION, ladderFall, EntityStates.FALLING);
		esm.addTransition(EntityStates.CLIMBING, Transition.LANDED, EntityStates.IDLING);
		
//		System.out.print(fsm.printTransitions());
		return esm;
	}
	
	private static StateMachine<EntityStates, StateObject> createRogueAttackMachine(Entity player){
		StateMachine<EntityStates, StateObject> rogueSM = new StateMachine<EntityStates, StateObject>(player, new StateObjectCreator(), EntityStates.class, StateObject.class);
		rogueSM.createState(EntityStates.IDLING);
		rogueSM.createState(EntityStates.BASE_ATTACK)
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
					BulletFactory.spawnBullet(entity, 5.0f, 5.0f, 25f, 100f);
				}

				@Override
				public void onExit(State nextState, Entity entity) {
				}
			});
		
		InputTransitionData attacking = new InputTransitionData.Builder(Type.ALL, true).add(Actions.ATTACK).build();
		
		rogueSM.addTransition(EntityStates.IDLING, Transition.INPUT, attacking, EntityStates.BASE_ATTACK);
		rogueSM.addTransition(EntityStates.BASE_ATTACK, Transition.TIME, new TimeTransitionData(0.2f), EntityStates.IDLING);
		
		return rogueSM;
	}
	
	private static EntityStateMachine createRogue(Engine engine, World world, Level level, float x, float y, Entity player){
		final float PLAYER_SPEED = 10.0f;

		RandomTransitionData rtd = new RandomTransitionData();
		rtd.waitTime = 4.0f;
		rtd.probability = 1.0f;
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder(engine, player, "body/player.json")
			.idle()
			.run(PLAYER_SPEED)
			.jump(15.0f, PLAYER_SPEED, true)
			.fall(PLAYER_SPEED, true)
			.climb(6.0f)
			.wallSlide()
			.build();
		
		esm.createState(EntityStates.WALL_JUMP)
			.add(engine.createComponent(SpeedComponent.class).set(PLAYER_SPEED))
			.addAnimation(EntityAnim.JUMP)
			.addAnimation(EntityAnim.RISE)
			.addAnimTransition(EntityAnim.JUMP, Transition.ANIMATION_FINISHED, EntityAnim.RISE)
			.addTag(TransitionTag.AIR_STATE)
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
					EngineComponent engineComp = Mappers.engine.get(entity);
					CollisionComponent collisionComp = Mappers.collision.get(entity);
					FacingComponent facingComp = Mappers.facing.get(entity);
					
					// Get the jump force and remove the component
					float fx = PLAYER_SPEED;
					float fy = 15.0f;
					if(collisionComp.onRightWall()) fx = -fx;
					facingComp.facingRight = Math.signum(fx) < 0 ? false : true;
					entity.add(engineComp.engine.createComponent(ForceComponent.class).set(fx, fy));
				}

				@Override
				public void onExit(State nextState, Entity entity) {
				}
			});
				
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

		InputTransitionData attackData = new InputTransitionData(Type.ALL, true);
		attackData.triggers.add(new InputTrigger(Actions.ATTACK, true));
		
		StaminaTransitionData attackStamina = new StaminaTransitionData(25f);
		
		MultiTransition attackTransition = new MultiTransition();
		attackTransition.addTransition(Transition.INPUT, attackData);
		attackTransition.addTransition(Transition.STAMINA, attackStamina);
		
		InputTransitionData ladderInputData = new InputTransitionData.Builder(Type.ANY_ONE, true)
					.add(Actions.MOVE_UP)
					.add(Actions.MOVE_DOWN)
					.build();
		
		CollisionTransitionData ladderCollisionData = new CollisionTransitionData(CollisionType.LADDER, true);
		
		MultiTransition ladderTransition = new MultiTransition();
		ladderTransition.addTransition(Transition.INPUT, ladderInputData);
		ladderTransition.addTransition(Transition.COLLISION, ladderCollisionData);
		
		CollisionTransitionData ladderFall = new CollisionTransitionData(CollisionType.LADDER, false);
		CollisionTransitionData onRightWallData = new CollisionTransitionData(CollisionType.RIGHT_WALL, true);
		CollisionTransitionData onLeftWallData = new CollisionTransitionData(CollisionType.LEFT_WALL, true);
		CollisionTransitionData offRightWallData = new CollisionTransitionData(CollisionType.RIGHT_WALL, false);
		CollisionTransitionData offLeftWallData = new CollisionTransitionData(CollisionType.LEFT_WALL, false);

		InputTransitionData rightWallInput = new InputTransitionData.Builder(Type.ALL, true).add(Actions.MOVE_RIGHT).build();
		InputTransitionData leftWallInput = new InputTransitionData.Builder(Type.ALL, true).add(Actions.MOVE_LEFT).build();
		
		MultiTransition rightSlideTransition = new MultiTransition();
		rightSlideTransition.addTransition(Transition.FALLING);
		rightSlideTransition.addTransition(Transition.COLLISION, onRightWallData);
		rightSlideTransition.addTransition(Transition.INPUT, rightWallInput);
		
		MultiTransition leftSlideTransition = new MultiTransition();
		leftSlideTransition.addTransition(Transition.FALLING);
		leftSlideTransition.addTransition(Transition.COLLISION, onLeftWallData);
		leftSlideTransition.addTransition(Transition.INPUT, leftWallInput);

		MultiTransition offWall = new MultiTransition();
		offWall.addTransition(Transition.COLLISION, offRightWallData);
		offWall.addTransition(Transition.COLLISION, offLeftWallData);
		
		InputTransitionData offRightWallInput = new InputTransitionData.Builder(Type.ALL, false).add(Actions.MOVE_RIGHT).build();
		InputTransitionData offLeftWallInput = new InputTransitionData.Builder(Type.ALL, false).add(Actions.MOVE_LEFT).build();
		
		MultiTransition offRightWall = new MultiTransition()
				.addTransition(Transition.COLLISION, onRightWallData)
				.addTransition(Transition.INPUT, offRightWallInput);
		MultiTransition offLeftWall = new MultiTransition()
				.addTransition(Transition.COLLISION, onLeftWallData)
				.addTransition(Transition.INPUT, offLeftWallInput);
		
		esm.addTransition(TransitionTag.GROUND_STATE, Transition.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING, TransitionTag.STATIC_STATE), Transition.INPUT, runningData, EntityStates.RUNNING);
		esm.addTransition(esm.one(TransitionTag.GROUND_STATE).exclude(TransitionTag.STATIC_STATE), Transition.INPUT, jumpData, EntityStates.JUMPING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.one(TransitionTag.AIR_STATE, EntityStates.WALL_SLIDING).exclude(EntityStates.JUMPING), Transition.LANDED, EntityStates.IDLING);
		esm.addTransition(EntityStates.RUNNING, Transition.INPUT, idleData, EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING, TransitionTag.STATIC_STATE), Transition.INPUT, bothData, EntityStates.IDLING);
		esm.addTransition(esm.one(TransitionTag.AIR_STATE, TransitionTag.GROUND_STATE), ladderTransition, EntityStates.CLIMBING);
		esm.addTransition(EntityStates.CLIMBING, Transition.COLLISION, ladderFall, EntityStates.FALLING);
		esm.addTransition(EntityStates.CLIMBING, Transition.LANDED, EntityStates.IDLING);
		esm.addTransition(EntityStates.FALLING, Array.with(leftSlideTransition, rightSlideTransition), EntityStates.WALL_SLIDING);
		esm.addTransition(EntityStates.WALL_SLIDING, Array.with(offWall, offRightWall, offLeftWall), EntityStates.FALLING);
		esm.addTransition(EntityStates.WALL_SLIDING, Transition.INPUT, jumpData, EntityStates.WALL_JUMP);
//		System.out.print(esm.printTransitions());
		return esm;
	}
	
	private static EntityStateMachine createMage(Engine engine, World world, Level level, float x, float y, Entity player){
		float PLAYER_SPEED = 6.0f;

		RandomTransitionData rtd = new RandomTransitionData();
		rtd.waitTime = 4.0f;
		rtd.probability = 1.0f;
		
		Entity sword = createSword(engine, world, level, player, x, y, 100);
		
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder(engine, player, "body/player.json")
			.idle()
			.run(PLAYER_SPEED)
			.jump(12f, PLAYER_SPEED, true)
			.fall(PLAYER_SPEED, true)
			.climb(4.0f)
			.swingAttack(sword, 150f, 210f, 0.6f, 25f)
			.build();
				
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

//		InputTransitionData diveData = new InputTransitionData(Type.ALL, true);
//		diveData.triggers.add(new InputTrigger(Actions.MOVE_DOWN));
		
		InputTransitionData attackData = new InputTransitionData(Type.ALL, true);
		attackData.triggers.add(new InputTrigger(Actions.ATTACK, true));
		
		StaminaTransitionData attackStamina = new StaminaTransitionData(25f);
		
		MultiTransition attackTransition = new MultiTransition();
		attackTransition.addTransition(Transition.INPUT, attackData);
		attackTransition.addTransition(Transition.STAMINA, attackStamina);
		
		InputTransitionData ladderInputData = new InputTransitionData(Type.ANY_ONE, true);
		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_UP, false));
		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_DOWN, false));
		
		CollisionTransitionData ladderCollisionData = new CollisionTransitionData(CollisionType.LADDER, true);
		
		MultiTransition ladderTransition = new MultiTransition();
		ladderTransition.addTransition(Transition.INPUT, ladderInputData);
		ladderTransition.addTransition(Transition.COLLISION, ladderCollisionData);
		
		CollisionTransitionData ladderFall = new CollisionTransitionData(CollisionType.LADDER, false);
		
		esm.addTransition(TransitionTag.GROUND_STATE, Transition.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING, TransitionTag.STATIC_STATE), Transition.INPUT, runningData, EntityStates.RUNNING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(TransitionTag.STATIC_STATE), Transition.INPUT, jumpData, EntityStates.JUMPING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.JUMPING), Transition.LANDED, EntityStates.IDLING);
		esm.addTransition(EntityStates.RUNNING, Transition.INPUT, idleData, EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING, TransitionTag.STATIC_STATE), Transition.INPUT, bothData, EntityStates.IDLING);
//		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.INPUT, diveData, EntityStates.DIVING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.SWING_ATTACK), attackTransition, EntityStates.SWING_ATTACK);
		esm.addTransition(EntityStates.SWING_ATTACK, Transition.ANIMATION_FINISHED, EntityStates.IDLING);
		esm.addTransition(esm.one(TransitionTag.AIR_STATE, TransitionTag.GROUND_STATE), ladderTransition, EntityStates.CLIMBING);
		esm.addTransition(EntityStates.CLIMBING, Transition.COLLISION, ladderFall, EntityStates.FALLING);
		esm.addTransition(EntityStates.CLIMBING, Transition.LANDED, EntityStates.IDLING);
		
//		System.out.print(fsm.printTransitions());

//		fsm.disableState(EntityStates.DIVING);
//		esm.changeState(EntityStates.IDLING);
		return esm;
	}
	
	public static Entity createAIPlayer(Engine engine, Level level, AIController controller/*, PathFinder pathFinder*/, Entity toFollow, World world, float x, float y, int value) {
		// Setup Animations
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, assets.getSpriteAnimation(Assets.KNIGHT_IDLE));
		animMap.put(EntityAnim.RUNNING, assets.getSpriteAnimation(Assets.KNIGHT_WALK));
		animMap.put(EntityAnim.JUMP, assets.getSpriteAnimation(Assets.KNIGHT_JUMP));
		animMap.put(EntityAnim.FALLING, assets.getSpriteAnimation(Assets.KNIGHT_FALL));
		animMap.put(EntityAnim.RANDOM_IDLE, assets.getSpriteAnimation(Assets.KNIGHT_IDLE));
		animMap.put(EntityAnim.RISE, assets.getSpriteAnimation(Assets.KNIGHT_RISE));
		animMap.put(EntityAnim.JUMP_APEX, assets.getSpriteAnimation(Assets.KNIGHT_APEX));
		animMap.put(EntityAnim.CLIMBING, assets.getSpriteAnimation(Assets.KNIGHT_IDLE));
		animMap.put(EntityAnim.SWING, assets.getSpriteAnimation(Assets.KNIGHT_ATTACK_OVERHEAD));
		
		// Setup Player
		Entity player = new EntityBuilder(engine, world, level)
				.animation(animMap)
				.mob(controller, EntityType.ENEMY, 100f)
				.physics(null, x, y, true)
				.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0), true)
				.build();
		player.add(engine.createComponent(MoneyComponent.class).set(value));
		player.add(engine.createComponent(AIControllerComponent.class).set(controller));
		player.add(engine.createComponent(TargetComponent.class).set(toFollow));

		Entity sword = createSword(engine, world, level, player, x, y, 25);
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder(engine, player, "body/player.json")
			.idle()
			.run(5.0f)
			.fall(5.0f, true)
			.jump(17.5f, 5.0f, true)
			.climb(5.0f)
			.swingAttack(sword, 150f, 210f, 0.6f, 0)
			.build();
			
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

		InputTransitionData attackData = new InputTransitionData(Type.ALL, true);
		attackData.triggers.add(new InputTrigger(Actions.ATTACK, true));
		
		TimeTransitionData attackCooldown = new TimeTransitionData(0.5f);
		
		MultiTransition attackTransition = new MultiTransition();
		attackTransition.addTransition(Transition.INPUT, attackData);
		attackTransition.addTransition(Transition.TIME, attackCooldown);
		
		InputTransitionData ladderInputData = new InputTransitionData(Type.ANY_ONE, true);
		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_UP, false));
		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_DOWN, false));
		
		CollisionTransitionData ladderCollisionData = new CollisionTransitionData(CollisionType.LADDER, true);
		
		MultiTransition ladderTransition = new MultiTransition();
		ladderTransition.addTransition(Transition.INPUT, ladderInputData);
		ladderTransition.addTransition(Transition.COLLISION, ladderCollisionData);
		
		CollisionTransitionData ladderFall = new CollisionTransitionData(CollisionType.LADDER, false);

		esm.addTransition(TransitionTag.GROUND_STATE, Transition.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING, TransitionTag.STATIC_STATE), Transition.INPUT, runningData, EntityStates.RUNNING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(TransitionTag.STATIC_STATE), Transition.INPUT, jumpData, EntityStates.JUMPING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING), Transition.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.JUMPING), Transition.LANDED, EntityStates.IDLING);
		esm.addTransition(EntityStates.RUNNING, Transition.INPUT, idleData, EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING, TransitionTag.STATIC_STATE), Transition.INPUT, bothData, EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.SWING_ATTACK), attackTransition, EntityStates.SWING_ATTACK);
		esm.addTransition(EntityStates.SWING_ATTACK, Transition.ANIMATION_FINISHED, EntityStates.IDLING);
		esm.addTransition(esm.one(TransitionTag.AIR_STATE, TransitionTag.GROUND_STATE), ladderTransition, EntityStates.CLIMBING);
		esm.addTransition(EntityStates.CLIMBING, Transition.COLLISION, ladderFall, EntityStates.FALLING);
		esm.addTransition(EntityStates.CLIMBING, Transition.LANDED, EntityStates.IDLING);
//		System.out.print(fsm.printTransitions());
		
		esm.changeState(EntityStates.IDLING);
		
		player.add(engine.createComponent(ESMComponent.class).set(esm));
		
		AIStateMachine aism = new AIStateMachine(player);
		aism.createState(AIState.WANDERING)
			.add(engine.createComponent(WanderingComponent.class).set(20, 1.5f));
		
		aism.createState(AIState.FOLLOWING)
			.add(engine.createComponent(FollowComponent.class).set(toFollow));
		
		aism.createState(AIState.ATTACKING)
			.add(engine.createComponent(TargetComponent.class).set(toFollow))
			.add(engine.createComponent(AttackComponent.class));
		
		RangeTransitionData wanderingToFollow = new RangeTransitionData();
		wanderingToFollow.target = toFollow;
		wanderingToFollow.distance = 15.0f;
		wanderingToFollow.inRange = true;
		wanderingToFollow.rayTrace = true;
		
		RangeTransitionData followToWandering = new RangeTransitionData();
		followToWandering.target = toFollow;
		followToWandering.distance = 15.0f;
		followToWandering.inRange = false;
		followToWandering.rayTrace = false;
		
		RangeTransitionData toAttack = new RangeTransitionData();
		toAttack.target = toFollow;
		toAttack.distance = 1.5f;
		toAttack.inRange = true;
		toAttack.rayTrace = true;
		
		RangeTransitionData fromAttack = new RangeTransitionData();
		fromAttack.target = toFollow;
		fromAttack.distance = 2.5f;
		fromAttack.inRange = false;
		fromAttack.rayTrace = false;
		
		InvalidEntityData invalidEntity = new InvalidEntityData(toFollow);
		
		aism.addTransition(AIState.WANDERING, Transition.RANGE, wanderingToFollow, AIState.FOLLOWING);
		aism.addTransition(AIState.FOLLOWING, Transition.RANGE, followToWandering, AIState.WANDERING);
		aism.addTransition(aism.one(AIState.FOLLOWING, AIState.ATTACKING), Transition.INVALID_ENTITY, invalidEntity, AIState.WANDERING);
		aism.addTransition(aism.one(AIState.WANDERING, AIState.FOLLOWING), Transition.RANGE, toAttack, AIState.ATTACKING);
		aism.addTransition(AIState.ATTACKING, Transition.RANGE, fromAttack, AIState.FOLLOWING);
		
		aism.changeState(AIState.WANDERING);
		
		player.add(engine.createComponent(AIStateMachineComponent.class).set(aism));
		return player;
	}
	
	public static Entity createSword(Engine engine, World world, Level level, Entity owner, float x, float y, int damage){
//		Entity sword = initPhysicsEntity(engine, world, level, null, x, y);
		Entity sword = new EntityBuilder(engine, world, level)
				.physics(null, x, y, false)
				.build();
		Body body = PhysicsUtils.createPhysicsBody(Gdx.files.internal("body/sword.json"), world, new Vector2(x, y), sword, true);
		sword.add(engine.createComponent(ParentComponent.class).set(owner));
		sword.add(engine.createComponent(OffsetComponent.class).set(16.0f * PPM_INV, 0.0f * PPM_INV, true));
		sword.add(engine.createComponent(SwordStatsComponent.class).set(damage));
		sword.getComponent(BodyComponent.class).set(body);
		sword.getComponent(BodyComponent.class).body.setActive(false);
		
		return sword;
	}
	
	public static Entity createCoin(Engine engine, World world, Level level, float x, float y, float fx, float fy, int amount){
		Animation animation = null;
		CoinType coinType = CoinType.getCoin(amount);
		switch(coinType){
		case BLUE:
			animation = assets.getSpriteAnimation(Assets.blueCoin);
			break;
		case GOLD:
			animation = assets.getSpriteAnimation(Assets.goldCoin);
			break;
		case SILVER:
			animation = assets.getSpriteAnimation(Assets.silverCoin);
			break;
		}
		Entity coin = createDrop(engine, world, level, x, y, fx, fy, "body/coin.json", animation, assets.getSpriteAnimation(Assets.disappearCoin), DropType.COIN);
		coin.add(engine.createComponent(MoneyComponent.class).set(amount));
		return coin;
	}
	
	private static Entity createDrop(Engine engine, World world, Level level, float x, float y, float fx, float fy, String physicsBody, Animation dropIdle, Animation dropDisappear, DropType type){
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.DROP_IDLE, dropIdle);
		animMap.put(EntityAnim.DROP_DISAPPEAR, dropDisappear);
		
		Entity drop = new EntityBuilder(engine, world, level)
				.animation(animMap)
				.physics(null, x, y, false)
				.render(dropIdle.getKeyFrame(0), false)
				.build();
		
		drop.add(engine.createComponent(ForceComponent.class).set(fx, fy));
		drop.add(engine.createComponent(DropTypeComponent.class).set(type));
		drop.add(engine.createComponent(TypeComponent.class).set(EntityType.NEUTRAL));
		
		EntityStateMachine esm = new EntityStateMachine(drop, physicsBody);
		esm.createState(EntityStates.IDLING)
			.add(engine.createComponent(DropMovementComponent.class))
			.addAnimation(EntityAnim.DROP_IDLE);
		
		esm.createState(EntityStates.DYING)
			.add(engine.createComponent(BlinkComponent.class).addBlink(2.0f, 0.4f).addBlink(2.0f, 0.2f).addBlink(1.0f, 0.1f))
			.addAnimation(EntityAnim.DROP_IDLE)
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					if(entity.getComponent(RenderComponent.class) == null){
						entity.add(new RenderComponent());
					}
				}
			});
		
		esm.createState(EntityStates.CLEAN_UP)
			.addAnimation(EntityAnim.DROP_DISAPPEAR)
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
				}
				
				@Override
				public void onExit(State nextState, Entity entity) {
					entity.add(new RemoveComponent());
				}
			});
			
		TimeTransitionData timeToBlink = new TimeTransitionData(10.0f);
		TimeTransitionData timeToDisappear = new TimeTransitionData(5.0f);
		
		esm.addTransition(EntityStates.IDLING, Transition.TIME, timeToBlink, EntityStates.DYING);
		esm.addTransition(EntityStates.DYING, Transition.TIME, timeToDisappear, EntityStates.CLEAN_UP);
		esm.addTransition(EntityStates.CLEAN_UP, Transition.ANIMATION_FINISHED, EntityStates.IDLING);
		
		esm.changeState(EntityStates.IDLING);
		drop.add(engine.createComponent(ESMComponent.class).set(esm));
		return drop;
	}
	
	public static Entity createBullet(Engine engine, World world, Level level, float speed, float angle, float x, float  y, float damage, boolean isArc, boolean friendly){
		Entity bullet = new EntityBuilder(engine, world, level)
				.physics(null, x, y, false)
				.build();
		bullet.add(engine.createComponent(TypeComponent.class).set(friendly ? EntityType.FRIENDLY : EntityType.ENEMY));
		bullet.add(engine.createComponent(ForceComponent.class).set(speed * MathUtils.cosDeg(angle), speed * MathUtils.sinDeg(angle)));
		bullet.add(engine.createComponent(BulletStatsComponent.class).set(damage));
		
		Body body = PhysicsUtils.createPhysicsBody(Gdx.files.internal("body/bullet.json"), world, new Vector2(x, y), bullet, true);
		if(isArc) body.setGravityScale(1.0f);
		bullet.getComponent(BodyComponent.class).set(body);
		
		return bullet;
	}
	
	private static class EntityBuilder{
		private Engine engine;
		private Entity entity;
		
		/**
		 * Setups base entity with Engine, World and Level components.
		 * @param engine
		 * @param world
		 * @param level
		 */
		public EntityBuilder(Engine engine, World world, Level level){
			this.engine = engine;
			entity = engine.createEntity();
			entity.add(engine.createComponent(EngineComponent.class).set(engine));
			entity.add(engine.createComponent(WorldComponent.class).set(world));
			entity.add(engine.createComponent(LevelComponent.class).set(level));
		}
		
		/**
		 * Adds Render, Texture and optional Facing components.
		 * 
		 * @param frame
		 * @param facing
		 * @return
		 */
		public EntityBuilder render(TextureRegion frame, boolean facing){
			entity.add(engine.createComponent(RenderComponent.class));
			entity.add(engine.createComponent(TextureComponent.class).set(frame));
			if(facing) entity.add(engine.createComponent(FacingComponent.class));
			return this;
		}
		
		/**
		 * Adds Animation component.
		 * @param animMap
		 * @return
		 */
		public EntityBuilder animation(ArrayMap<State, Animation> animMap){
			entity.add(engine.createComponent(AnimationComponent.class));
			entity.getComponent(AnimationComponent.class).animations.putAll(animMap);
			return this;
		}
		
		/**
		 * Adds Body, Position, Velocity and optional Collision components.
		 * 
		 * @param body
		 * @param x
		 * @param y
		 * @param collideable
		 * @return
		 */
		public EntityBuilder physics(Body body, float x, float y, boolean collideable){
			entity.add(engine.createComponent(BodyComponent.class).set(body));
			entity.add(engine.createComponent(PositionComponent.class).set(x, y));
			entity.add(engine.createComponent(VelocityComponent.class));
			if(collideable) entity.add(engine.createComponent(CollisionComponent.class));
			return this;
		}
		
		/**
		 * Adds Input, Type and Health components.
		 * 
		 * @param input
		 * @param type
		 * @param health
		 * @return
		 */
		public EntityBuilder mob(Input input, EntityType type, float health){
			entity.add(engine.createComponent(InputComponent.class).set(input));
			entity.add(engine.createComponent(TypeComponent.class).set(type));
			entity.add(engine.createComponent(HealthComponent.class).set(health, health));
			return this;
		}
		
		public Entity build(){
			return entity;
		}
	}
}