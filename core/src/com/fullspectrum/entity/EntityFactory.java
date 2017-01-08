package com.fullspectrum.entity;

import static com.fullspectrum.game.GameVars.PPM_INV;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.ai.AIController;
import com.fullspectrum.assets.Assets;
import com.fullspectrum.component.AIControllerComponent;
import com.fullspectrum.component.AIStateMachineComponent;
import com.fullspectrum.component.AbilityComponent;
import com.fullspectrum.component.AnimationComponent;
import com.fullspectrum.component.AttackComponent;
import com.fullspectrum.component.BarrierComponent;
import com.fullspectrum.component.BlinkComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.BulletStatsComponent;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.CombustibleComponent;
import com.fullspectrum.component.DirectionComponent;
import com.fullspectrum.component.DropMovementComponent;
import com.fullspectrum.component.DropTypeComponent;
import com.fullspectrum.component.ESMComponent;
import com.fullspectrum.component.EngineComponent;
import com.fullspectrum.component.FSMComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.FlowFieldComponent;
import com.fullspectrum.component.FlowFollowComponent;
import com.fullspectrum.component.FlyingComponent;
import com.fullspectrum.component.FollowComponent;
import com.fullspectrum.component.ForceComponent;
import com.fullspectrum.component.GroundMovementComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.KnockBackComponent;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.MoneyComponent;
import com.fullspectrum.component.OffsetComponent;
import com.fullspectrum.component.ParentComponent;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.ProjectileComponent;
import com.fullspectrum.component.RemoveComponent;
import com.fullspectrum.component.RenderComponent;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.component.SwordStatsComponent;
import com.fullspectrum.component.TargetComponent;
import com.fullspectrum.component.TextRenderComponent;
import com.fullspectrum.component.TextureComponent;
import com.fullspectrum.component.TimeListener;
import com.fullspectrum.component.TimerComponent;
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
import com.fullspectrum.fsm.transition.AbilityTransitionData;
import com.fullspectrum.fsm.transition.CollisionTransitionData;
import com.fullspectrum.fsm.transition.CollisionTransitionData.CollisionType;
import com.fullspectrum.fsm.transition.ComponentTransitionData;
import com.fullspectrum.fsm.transition.InputTransitionData;
import com.fullspectrum.fsm.transition.InputTransitionData.Type;
import com.fullspectrum.fsm.transition.InputTrigger;
import com.fullspectrum.fsm.transition.InvalidEntityData;
import com.fullspectrum.fsm.transition.LOSTransitionData;
import com.fullspectrum.fsm.transition.RandomTransitionData;
import com.fullspectrum.fsm.transition.RangeTransitionData;
import com.fullspectrum.fsm.transition.TimeTransitionData;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionTag;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.Input;
import com.fullspectrum.level.FlowField;
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
		Entity player = new EntityBuilder(engine, world, level)
				.animation(animMap)
				.mob(input, EntityType.FRIENDLY, 2500f)
				.physics(null, x, y, true)
				.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0), true)
				.build();
		player.add(engine.createComponent(MoneyComponent.class));

		EntityStateMachine knightESM = createKnight(engine, world, level, x, y, player);
		EntityStateMachine rogueESM = createRogue(engine, world, level, x, y, player);
		EntityStateMachine mageESM = createMage(engine, world, level, x, y, player);
		StateMachine<EntityStates, StateObject> rogueAttackFSM = createRogueAttackMachine(player);

		StateMachine<PlayerState, StateObject> playerStateMachine = new StateMachine<PlayerState, StateObject>(player, new StateObjectCreator(), PlayerState.class, StateObject.class);
		
		playerStateMachine.createState(PlayerState.KNIGHT)
			.add(engine.createComponent(BarrierComponent.class).set(500.0f, 500.0f, 50.0f, 1.0f))
			.add(engine.createComponent(ESMComponent.class).set(knightESM))
			.add(engine.createComponent(TintComponent.class).set(new Color(123 / 255f, 123 / 255f, 184 / 255f, 1.0f)))
			.add(engine.createComponent(AbilityComponent.class))
			.addSubstateMachine(knightESM);
		
		playerStateMachine.createState(PlayerState.ROGUE)
			.add(engine.createComponent(BarrierComponent.class).set(200.0f, 200.0f, 50.0f, 1.0f))
			.add(engine.createComponent(ESMComponent.class).set(rogueESM))
//			.add(engine.createComponent(FSMComponent.class).set(rogueAttackFSM))
			.add(engine.createComponent(TintComponent.class).set(new Color(176 / 255f, 47 / 255f, 42 / 255f, 1.0f)))
			.add(engine.createComponent(AbilityComponent.class))
			.addSubstateMachine(rogueESM)
			.addSubstateMachine(rogueAttackFSM);
		
		playerStateMachine.createState(PlayerState.MAGE)
			.add(engine.createComponent(BarrierComponent.class).set(75.0f, 75.0f, 50.0f, 1.0f))
			.add(engine.createComponent(ESMComponent.class).set(mageESM))
			.add(engine.createComponent(TintComponent.class).set(new Color(165 / 255f, 65 / 255f, 130 / 255f, 1.0f)))
			.add(engine.createComponent(AbilityComponent.class)
					.add(AbilityType.MANA_BOMB, assets.getSpriteAnimation(Assets.blueCoin).getKeyFrame(0.0f), 1.0f))
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
		
		playerStateMachine.setDebugName("Player FSM");
//		System.out.println(playerStateMachine.printTransitions(true));
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
			.knockBack()
			.build();
		
		esm.setDebugName("Knight ESM");
				
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
		
		InputTransitionData ladderInputData = new InputTransitionData(Type.ANY_ONE, true);
		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_UP, false));
		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_DOWN, false));
		
		CollisionTransitionData ladderCollisionData = new CollisionTransitionData(CollisionType.LADDER, true);
		
		MultiTransition ladderTransition = new MultiTransition(Transition.INPUT, ladderInputData)
					.and(Transition.COLLISION, ladderCollisionData);
		
		CollisionTransitionData ladderFall = new CollisionTransitionData(CollisionType.LADDER, false);
		
		esm.addTransition(TransitionTag.GROUND_STATE, Transition.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING, TransitionTag.STATIC_STATE), Transition.INPUT, runningData, EntityStates.RUNNING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(TransitionTag.STATIC_STATE), Transition.INPUT, jumpData, EntityStates.JUMPING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.JUMPING), Transition.LANDED, EntityStates.IDLING);
		esm.addTransition(EntityStates.RUNNING, Transition.INPUT, idleData, EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING, TransitionTag.STATIC_STATE), Transition.INPUT, bothData, EntityStates.IDLING);
//		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.INPUT, diveData, EntityStates.DIVING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.SWING_ATTACK), Transition.INPUT, attackData, EntityStates.SWING_ATTACK);
		esm.addTransition(EntityStates.SWING_ATTACK, Transition.ANIMATION_FINISHED, EntityStates.IDLING);
		esm.addTransition(esm.one(TransitionTag.AIR_STATE, TransitionTag.GROUND_STATE), ladderTransition, EntityStates.CLIMBING);
		esm.addTransition(EntityStates.CLIMBING, Transition.COLLISION, ladderFall, EntityStates.FALLING);
		esm.addTransition(EntityStates.CLIMBING, Transition.LANDED, EntityStates.IDLING);
		
		// Knock Back Transition
		esm.addTransition(esm.all(TransitionTag.ALL), Transition.COMPONENT, new ComponentTransitionData(KnockBackComponent.class, false), EntityStates.KNOCK_BACK);
		esm.addTransition(EntityStates.KNOCK_BACK, Transition.COMPONENT, new ComponentTransitionData(KnockBackComponent.class, true), EntityStates.IDLING);
//		System.out.print(esm.printTransitions(true));
		return esm;
	}
	
	private static StateMachine<EntityStates, StateObject> createRogueAttackMachine(Entity player){
		StateMachine<EntityStates, StateObject> rogueSM = new StateMachine<EntityStates, StateObject>(player, new StateObjectCreator(), EntityStates.class, StateObject.class);
		rogueSM.createState(EntityStates.IDLING);
		rogueSM.createState(EntityStates.PROJECTILE_ATTACK)
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
					ProjectileFactory.spawnBullet(entity, 5.0f, 5.0f, 25f, 25f);
				}

				@Override
				public void onExit(State nextState, Entity entity) {
				}
			});
		
		InputTransitionData attacking = new InputTransitionData.Builder(Type.ALL, true).add(Actions.ATTACK).build();
		
		rogueSM.addTransition(EntityStates.IDLING, Transition.INPUT, attacking, EntityStates.PROJECTILE_ATTACK);
		rogueSM.addTransition(EntityStates.PROJECTILE_ATTACK, Transition.TIME, new TimeTransitionData(0.2f), EntityStates.IDLING);
		
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
			.knockBack()
			.build();
		
		esm.setDebugName("Rogue ESM");
		
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
		
		esm.createState(EntityStates.DASH)
			.addAnimation(EntityAnim.SWING)
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
					BodyComponent bodyComp = Mappers.body.get(entity);
					FacingComponent facingComp = Mappers.facing.get(entity);
					ForceComponent forceComp = Mappers.engine.get(entity).engine.createComponent(ForceComponent.class);
					
					Body body = bodyComp.body;
					body.setLinearVelocity(body.getLinearVelocity().x, 0);
					body.setGravityScale(0.0f);
					
					forceComp.set(facingComp.facingRight ? 30f : -30f, 0);
					entity.add(forceComp);
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					BodyComponent bodyComp = Mappers.body.get(entity);
					Body body = bodyComp.body;
					
					body.setLinearVelocity(0.0f, 0.0f);
					body.setGravityScale(1.0f);
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
		
		MultiTransition idleTransition = new MultiTransition(Transition.INPUT, idleData).or(Transition.INPUT, bothData);

		InputTransitionData attackData = new InputTransitionData(Type.ALL, true);
		attackData.triggers.add(new InputTrigger(Actions.ATTACK, true));
		
		InputTransitionData dashData = new InputTransitionData.Builder(Type.ALL, true).add(Actions.MOVEMENT, true).build();
		TimeTransitionData dashTime = new TimeTransitionData(0.1f);
		
		InputTransitionData ladderInputData = new InputTransitionData.Builder(Type.ANY_ONE, true)
					.add(Actions.MOVE_UP)
					.add(Actions.MOVE_DOWN)
					.build();
		
		CollisionTransitionData ladderCollisionData = new CollisionTransitionData(CollisionType.LADDER, true);
		
		MultiTransition ladderTransition = new MultiTransition(Transition.INPUT, ladderInputData)
			.and(Transition.COLLISION, ladderCollisionData);
		
		CollisionTransitionData ladderFall = new CollisionTransitionData(CollisionType.LADDER, false);
		CollisionTransitionData onRightWallData = new CollisionTransitionData(CollisionType.RIGHT_WALL, true);
		CollisionTransitionData onLeftWallData = new CollisionTransitionData(CollisionType.LEFT_WALL, true);
		CollisionTransitionData offRightWallData = new CollisionTransitionData(CollisionType.RIGHT_WALL, false);
		CollisionTransitionData offLeftWallData = new CollisionTransitionData(CollisionType.LEFT_WALL, false);

		InputTransitionData rightWallInput = new InputTransitionData.Builder(Type.ALL, true).add(Actions.MOVE_RIGHT).build();
		InputTransitionData leftWallInput = new InputTransitionData.Builder(Type.ALL, true).add(Actions.MOVE_LEFT).build();
		
		MultiTransition rightSlideTransition = new MultiTransition(Transition.FALLING)
			.and(Transition.COLLISION, onRightWallData)
			.and(Transition.INPUT, rightWallInput);
		
		MultiTransition leftSlideTransition = new MultiTransition(Transition.FALLING)
			.and(Transition.FALLING)
			.and(Transition.COLLISION, onLeftWallData)
			.and(Transition.INPUT, leftWallInput);
		
		MultiTransition wallSlideTransition = new MultiTransition(rightSlideTransition).or(leftSlideTransition);
		
		InputTransitionData offRightWallInput = new InputTransitionData.Builder(Type.ALL, false).add(Actions.MOVE_RIGHT).build();
		InputTransitionData offLeftWallInput = new InputTransitionData.Builder(Type.ALL, false).add(Actions.MOVE_LEFT).build();
		
		MultiTransition offRightWall = new MultiTransition(Transition.COLLISION, onRightWallData)
				.and(Transition.INPUT, offRightWallInput);
		MultiTransition offLeftWall = new MultiTransition(Transition.COLLISION, onLeftWallData)
				.and(Transition.INPUT, offLeftWallInput);
		
		// (offWallLeft && offWallRight) || (onWallLeft && offInputLeft) || (onWallRight && offInputRight)
		MultiTransition offWall = new MultiTransition(new MultiTransition(Transition.COLLISION, offRightWallData)
				.and(Transition.COLLISION, offLeftWallData))
				.or(offRightWall)
				.or(offLeftWall);
		
		esm.addTransition(TransitionTag.GROUND_STATE, Transition.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING, TransitionTag.STATIC_STATE), Transition.INPUT, runningData, EntityStates.RUNNING);
		esm.addTransition(esm.one(TransitionTag.GROUND_STATE).exclude(TransitionTag.STATIC_STATE), Transition.INPUT, jumpData, EntityStates.JUMPING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.one(TransitionTag.AIR_STATE, EntityStates.WALL_SLIDING).exclude(EntityStates.JUMPING), Transition.LANDED, EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING, TransitionTag.STATIC_STATE), idleTransition, EntityStates.IDLING);
		esm.addTransition(esm.one(TransitionTag.AIR_STATE, TransitionTag.GROUND_STATE), ladderTransition, EntityStates.CLIMBING);
		esm.addTransition(EntityStates.CLIMBING, Transition.COLLISION, ladderFall, EntityStates.FALLING);
		esm.addTransition(EntityStates.CLIMBING, Transition.LANDED, EntityStates.IDLING);
		esm.addTransition(EntityStates.FALLING, wallSlideTransition, EntityStates.WALL_SLIDING);
		esm.addTransition(EntityStates.WALL_SLIDING, offWall, EntityStates.FALLING);
		esm.addTransition(EntityStates.WALL_SLIDING, Transition.INPUT, jumpData, EntityStates.WALL_JUMP);
		esm.addTransition(esm.one(EntityStates.JUMPING, EntityStates.WALL_JUMP), Transition.INPUT, dashData, EntityStates.DASH);
		esm.addTransition(EntityStates.DASH, Transition.TIME, dashTime, EntityStates.FALLING);
		
		// Knock Back Transition
		esm.addTransition(esm.all(TransitionTag.ALL), Transition.COMPONENT, new ComponentTransitionData(KnockBackComponent.class, false), EntityStates.KNOCK_BACK);
		esm.addTransition(EntityStates.KNOCK_BACK, Transition.COMPONENT, new ComponentTransitionData(KnockBackComponent.class, true), EntityStates.IDLING);
//		esm.addTransition(EntityStates.DASH, Transition.COLLISION, onRightWallData, EntityStates.FALLING);
//		esm.addTransition(EntityStates.DASH, Transition.COLLISION, onLeftWallData, EntityStates.FALLING);
//		System.out.print(esm.printTransitions(false));
		return esm;
	}
	
	private static EntityStateMachine createMage(Engine engine, World world, Level level, float x, float y, Entity player){
		float PLAYER_SPEED = 6.0f;

		RandomTransitionData rtd = new RandomTransitionData();
		rtd.waitTime = 4.0f;
		rtd.probability = 1.0f;
		
//		Entity sword = createSword(engine, world, level, player, x, y, 100);
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder(engine, player, "body/player.json")
			.idle()
			.run(PLAYER_SPEED)
			.jump(12f, PLAYER_SPEED, true)
			.fall(PLAYER_SPEED, true)
			.climb(4.0f)
			.knockBack()
//			.swingAttack(sword, 150f, 210f, 0.6f, 25f)
			.build();
		
		esm.setDebugName("Mage ESM");
		
		esm.createState(EntityStates.PROJECTILE_ATTACK)
			.add(engine.createComponent(SpeedComponent.class).set(0.0f))
			.add(engine.createComponent(DirectionComponent.class))
			.add(engine.createComponent(GroundMovementComponent.class))
			.addAnimation(EntityAnim.SWING)
			.addTag(TransitionTag.STATIC_STATE)
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
					ProjectileFactory.spawnExplosiveProjectile(entity, 0.0f, 5.0f, 10f, 50f, 45f, 5.0f, 20.0f, 5.0f);
					AbilityComponent abilityComp = Mappers.ability.get(entity);
					abilityComp.resetTime(AbilityType.MANA_BOMB);
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

//		InputTransitionData diveData = new InputTransitionData(Type.ALL, true);
//		diveData.triggers.add(new InputTrigger(Actions.MOVE_DOWN));
		
		// Attack
		InputTransitionData attackData = new InputTransitionData.Builder(Type.ALL, true).add(Actions.ATTACK, true).build();
		AbilityTransitionData manaBombAbility = new AbilityTransitionData(AbilityType.MANA_BOMB);
		MultiTransition attackTransition = new MultiTransition(Transition.INPUT, attackData)
				.and(Transition.ABILITY, manaBombAbility);
		
		InputTransitionData ladderInputData = new InputTransitionData(Type.ANY_ONE, true);
		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_UP, false));
		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_DOWN, false));
		
		CollisionTransitionData ladderCollisionData = new CollisionTransitionData(CollisionType.LADDER, true);
		
		MultiTransition ladderTransition = new MultiTransition(Transition.INPUT, ladderInputData)
			.and(Transition.COLLISION, ladderCollisionData);
		
		CollisionTransitionData ladderFall = new CollisionTransitionData(CollisionType.LADDER, false);
		
		esm.addTransition(TransitionTag.GROUND_STATE, Transition.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING, TransitionTag.STATIC_STATE), Transition.INPUT, runningData, EntityStates.RUNNING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(TransitionTag.STATIC_STATE), Transition.INPUT, jumpData, EntityStates.JUMPING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.JUMPING), Transition.LANDED, EntityStates.IDLING);
		esm.addTransition(EntityStates.RUNNING, Transition.INPUT, idleData, EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING, TransitionTag.STATIC_STATE), Transition.INPUT, bothData, EntityStates.IDLING);
//		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.INPUT, diveData, EntityStates.DIVING);
//		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.SWING_ATTACK), attackTransition, EntityStates.SWING_ATTACK);
//		esm.addTransition(EntityStates.SWING_ATTACK, Transition.ANIMATION_FINISHED, EntityStates.IDLING);
		esm.addTransition(esm.one(TransitionTag.AIR_STATE, TransitionTag.GROUND_STATE), ladderTransition, EntityStates.CLIMBING);
		esm.addTransition(EntityStates.CLIMBING, Transition.COLLISION, ladderFall, EntityStates.FALLING);
		esm.addTransition(EntityStates.CLIMBING, Transition.LANDED, EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.PROJECTILE_ATTACK), attackTransition, EntityStates.PROJECTILE_ATTACK);
		esm.addTransition(EntityStates.PROJECTILE_ATTACK, Transition.ANIMATION_FINISHED, EntityStates.IDLING);
//		esm.addTransition(EntityStates.BASE_ATTACK, Transition.TIME, new TimeTransitionData(0.2f), EntityStates.IDLING);
		
		// Knock Back Transition
		esm.addTransition(esm.all(TransitionTag.ALL), Transition.COMPONENT, new ComponentTransitionData(KnockBackComponent.class, false), EntityStates.KNOCK_BACK);
		esm.addTransition(EntityStates.KNOCK_BACK, Transition.COMPONENT, new ComponentTransitionData(KnockBackComponent.class, true), EntityStates.IDLING);
//		System.out.print(esm.printTransitions(true));

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
			.knockBack()
			.build();
		
		esm.setDebugName("AI ESM");
			
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
		
		MultiTransition attackTransition = new MultiTransition(Transition.INPUT, attackData)
				.and(Transition.TIME, attackCooldown);
		
		InputTransitionData ladderInputData = new InputTransitionData(Type.ANY_ONE, true);
		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_UP, false));
		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_DOWN, false));
		
		CollisionTransitionData ladderCollisionData = new CollisionTransitionData(CollisionType.LADDER, true);
		
		MultiTransition ladderTransition = new MultiTransition(Transition.INPUT, ladderInputData)
				.and(Transition.COLLISION, ladderCollisionData);
		
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
		// Knock Back Transition
		esm.addTransition(esm.all(TransitionTag.ALL), Transition.COMPONENT, new ComponentTransitionData(KnockBackComponent.class, false), EntityStates.KNOCK_BACK);
		esm.addTransition(EntityStates.KNOCK_BACK, Transition.COMPONENT, new ComponentTransitionData(KnockBackComponent.class, true), EntityStates.IDLING);
//		System.out.print(esm.printTransitions());
		
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
		
		LOSTransitionData inSightData = new LOSTransitionData(toFollow, true);
		LOSTransitionData outOfSightData = new LOSTransitionData(toFollow, false);
		
		RangeTransitionData wanderInRange = new RangeTransitionData();
		wanderInRange.target = toFollow;
		wanderInRange.distance = 15.0f;
		wanderInRange.inRange = true;
		
		MultiTransition wanderToFollow = new MultiTransition(Transition.RANGE, wanderInRange)
			.and(Transition.LINE_OF_SIGHT, inSightData);
		
		RangeTransitionData followOutOfRange = new RangeTransitionData();
		followOutOfRange.target = toFollow;
		followOutOfRange.distance = 15.0f;
		followOutOfRange.inRange = false;

		MultiTransition followToWander = new MultiTransition(Transition.RANGE, followOutOfRange)
			.or(Transition.LINE_OF_SIGHT, outOfSightData);
		
		RangeTransitionData inAttackRange = new RangeTransitionData();
		inAttackRange.target = toFollow;
		inAttackRange.distance = 1.5f;
		inAttackRange.inRange = true;
		
		MultiTransition toAttackTransition = new MultiTransition(Transition.RANGE, inAttackRange)
			.and(Transition.LINE_OF_SIGHT, inSightData);
		
		RangeTransitionData outOfAttackRange = new RangeTransitionData();
		outOfAttackRange.target = toFollow;
		outOfAttackRange.distance = 2.5f;
		outOfAttackRange.inRange = false;
		
		MultiTransition fromAttackTransition = new MultiTransition(Transition.RANGE, outOfAttackRange)
			.or(Transition.LINE_OF_SIGHT, outOfSightData);
		
		InvalidEntityData invalidEntity = new InvalidEntityData(toFollow);
		
		aism.addTransition(AIState.WANDERING, wanderToFollow, AIState.FOLLOWING);
		aism.addTransition(AIState.FOLLOWING, followToWander, AIState.WANDERING);
		aism.addTransition(aism.one(AIState.FOLLOWING, AIState.ATTACKING), Transition.INVALID_ENTITY, invalidEntity, AIState.WANDERING);
		aism.addTransition(aism.one(AIState.WANDERING, AIState.FOLLOWING), toAttackTransition, AIState.ATTACKING);
		aism.addTransition(AIState.ATTACKING, fromAttackTransition, AIState.FOLLOWING);
		
		aism.changeState(AIState.WANDERING);
//		System.out.println(aism.printTransitions());
		player.add(engine.createComponent(AIStateMachineComponent.class).set(aism));
		return player;
	}
	
	public static Entity createFlyingEnemy(Engine engine, World world, Level level, FlowField field, float x, float y, Entity toFollow, int money){
		AIController controller = new AIController();
		Entity entity = new EntityBuilder(engine, world, level)
				.physics(null, x, y, false)
				.mob(controller, EntityType.ENEMY, 100f)
				.build();
		entity.add(engine.createComponent(AIControllerComponent.class).set(controller));
		entity.add(engine.createComponent(MoneyComponent.class).set(money));
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder(engine, entity, "body/winged.json")
			.knockBack()
			.build();
		esm.createState(EntityStates.FLYING)
			.add(engine.createComponent(SpeedComponent.class).set(8.0f))
			.add(engine.createComponent(FlyingComponent.class))
			.add(engine.createComponent(FlowFieldComponent.class).set(field));
		
		esm.changeState(EntityStates.FLYING);
		
		Mappers.body.get(entity).body.setGravityScale(0.0f);
		entity.add(engine.createComponent(ESMComponent.class).set(esm));
		
		// Knock Back Transition
		esm.addTransition(esm.all(TransitionTag.ALL), Transition.COMPONENT, new ComponentTransitionData(KnockBackComponent.class, false), EntityStates.KNOCK_BACK);
		esm.addTransition(EntityStates.KNOCK_BACK, Transition.COMPONENT, new ComponentTransitionData(KnockBackComponent.class, true), EntityStates.FLYING);
		
		AIStateMachine aism = new  AIStateMachine(entity);
		aism.createState(AIState.WANDERING);
		aism.createState(AIState.FOLLOWING)
			.add(engine.createComponent(FlowFollowComponent.class))
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					Mappers.aiController.get(entity).controller.releaseAll();
				}
			});
		aism.createState(AIState.ATTACKING);
		
		LOSTransitionData inSightData = new LOSTransitionData(toFollow, true);
		LOSTransitionData outOfSightData = new LOSTransitionData(toFollow, false);
		
		RangeTransitionData wanderInRange = new RangeTransitionData();
		wanderInRange.target = toFollow;
		wanderInRange.distance = 15.0f;
		wanderInRange.inRange = true;
		
		MultiTransition wanderToFollow = new MultiTransition(Transition.RANGE, wanderInRange);
//			.and(Transition.LINE_OF_SIGHT, inSightData);
		
		RangeTransitionData followOutOfRange = new RangeTransitionData();
		followOutOfRange.target = toFollow;
		followOutOfRange.distance = 15.0f;
		followOutOfRange.inRange = false;

		MultiTransition followToWander = new MultiTransition(Transition.RANGE, followOutOfRange);
//			.or(Transition.LINE_OF_SIGHT, outOfSightData);
		
		RangeTransitionData inAttackRange = new RangeTransitionData();
		inAttackRange.target = toFollow;
		inAttackRange.distance = 2.5f;
		inAttackRange.inRange = true;
		
		MultiTransition toAttackTransition = new MultiTransition(Transition.RANGE, inAttackRange);
//			.and(Transition.LINE_OF_SIGHT, inSightData);
		
		RangeTransitionData outOfAttackRange = new RangeTransitionData();
		outOfAttackRange.target = toFollow;
		outOfAttackRange.distance = 3.5f;
		outOfAttackRange.inRange = false;
		
		MultiTransition fromAttackTransition = new MultiTransition(Transition.RANGE, outOfAttackRange);
//			.or(Transition.LINE_OF_SIGHT, outOfSightData);
		
		InvalidEntityData invalidEntity = new InvalidEntityData(toFollow);
		
		aism.addTransition(AIState.WANDERING, wanderToFollow, AIState.FOLLOWING);
		aism.addTransition(AIState.FOLLOWING, followToWander, AIState.WANDERING);
		aism.addTransition(aism.one(AIState.FOLLOWING, AIState.ATTACKING), Transition.INVALID_ENTITY, invalidEntity, AIState.WANDERING);
		aism.addTransition(aism.one(AIState.WANDERING, AIState.FOLLOWING), toAttackTransition, AIState.ATTACKING);
		aism.addTransition(AIState.ATTACKING, fromAttackTransition, AIState.FOLLOWING);
		
		aism.changeState(AIState.WANDERING);

		entity.add(engine.createComponent(AIStateMachineComponent.class).set(aism));
		return entity;
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
	
	public static Entity createProjectile(Engine engine, World world, Level level, String physicsBody, float speed, float angle, float x, float y, boolean isArc, EntityType type){
		Entity projectile = new EntityBuilder(engine, world, level)
				.physics(null, x, y, false)
				.build();
		projectile.add(engine.createComponent(TypeComponent.class).set(type));
		projectile.add(engine.createComponent(ForceComponent.class).set(speed * MathUtils.cosDeg(angle), speed * MathUtils.sinDeg(angle)));
		projectile.add(engine.createComponent(ProjectileComponent.class).set(x, y, speed, angle, isArc));
		
		Body body = PhysicsUtils.createPhysicsBody(Gdx.files.internal(physicsBody), world, new Vector2(x, y), projectile, true);
		if(isArc) body.setGravityScale(1.0f);
		projectile.getComponent(BodyComponent.class).set(body);
		
		return projectile;
	}
	

	public static Entity createBullet(Engine engine, World world, Level level, float speed, float angle, float x, float y, float damage, boolean isArc, EntityType type){
		Entity bullet = createProjectile(engine, world, level, "body/bullet.json", speed, angle, x, y, isArc, type);
		bullet.add(engine.createComponent(BulletStatsComponent.class).set(damage));
		return bullet;
	}
	
	public static Entity createExplosiveProjectile(Engine engine, World world, Level level, float speed, float angle, float x, float y, float damage, boolean isArc, EntityType type, float radius, float radiusGrowRate, float damageDropOffRate){
		Entity explosive = createProjectile(engine, world, level, "body/explosive.json", speed, angle, x, y, isArc, type);
		explosive.add(engine.createComponent(CombustibleComponent.class).set(radius, radiusGrowRate, damage, damageDropOffRate));
		return explosive;
	}
	
	public static Entity createExplosiveParticle(Engine engine, World world, Level level, Entity parent, float speed, float angle, float x, float y){
		Entity particle = createProjectile(engine, world, level, "body/explosive_particle.json", speed, angle, x, y, false, Mappers.type.get(parent).type);
		CombustibleComponent combustibleComp = Mappers.combustible.get(parent);
		particle.add(engine.createComponent(ParentComponent.class).set(parent));
		particle.add(engine.createComponent(TimerComponent.class).add("particle_life", combustibleComp.radius / combustibleComp.speed, false, new TimeListener(){
			@Override
			public void onTime(Entity entity) {
				entity.add(new RemoveComponent());
			}
		}));
		return particle;
	}
	
	public static Entity createDamageText(Engine engine, World world, Level level, String text, Color color, BitmapFont font, float x, float y, float speed){
		Entity entity = new EntityBuilder(engine, world, level).build();
		entity.add(engine.createComponent(TextRenderComponent.class).set(font, color, text));
		entity.add(engine.createComponent(PositionComponent.class).set(x, y));
		entity.add(engine.createComponent(VelocityComponent.class).set(0, speed));
		entity.add(engine.createComponent(TimerComponent.class).add("text_life", 0.4f, false, new TimeListener(){
			@Override
			public void onTime(Entity entity) {
				entity.add(new RemoveComponent());
			}
		}));
		return entity;
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