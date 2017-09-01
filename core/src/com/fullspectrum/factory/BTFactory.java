package com.fullspectrum.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.ai.btree.decorator.AlwaysSucceed;
import com.badlogic.gdx.ai.btree.leaf.Wait;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.ai.tasks.AttackTask;
import com.fullspectrum.ai.tasks.ReachedPlatformEndTask;
import com.fullspectrum.ai.tasks.ReleaseControlsTask;
import com.fullspectrum.ai.tasks.TargetBehindMeTask;
import com.fullspectrum.ai.tasks.TurnAroundTask;
import com.fullspectrum.ai.tasks.WalkForwardTask;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TargetComponent;
import com.fullspectrum.input.Actions;
import com.fullspectrum.utils.EntityUtils;
import com.fullspectrum.utils.PhysicsUtils;

public class BTFactory {

	/**
	 * Handles walking forward on a platform and turning around when the end is reached.<br>
	 * <br><em>Success Conditions</em>: Will always succeed as long as the AI can walk forwards.
	 * 
	 * @param wait - time the AI waits before continuing his patrol after turning around
	 * @return
	 */
	public static Task<Entity> patrol(float wait) {
		Selector<Entity> root = new Selector<Entity>();
		
		Sequence<Entity> turnAroundSequence = new Sequence<Entity>();
		turnAroundSequence.addChild(new ReachedPlatformEndTask());
		turnAroundSequence.addChild(new ReleaseControlsTask());
		turnAroundSequence.addChild(new Wait<Entity>(wait));
		turnAroundSequence.addChild(new TurnAroundTask());
		
		root.addChild(turnAroundSequence);
		root.addChild(walk());
	
		return root;
	}
	
	/**
	 * Causes the AI to attack, turning the AI around first if the target is behind it<br>
	 * <br><em>Success Conditions</em>: Will always succeed as long as the AI can attack.
	 * 
	 * @param attackAction - the input action pressed during the attack
	 * @return
	 */
	public static Task<Entity> attack(Actions attackAction) {
		Sequence<Entity> attackSequence = new Sequence<Entity>();
		attackSequence.addChild(new AlwaysSucceed<Entity>(turnWhenTargetBehind()));
		attackSequence.addChild(new ReleaseControlsTask());
		attackSequence.addChild(new AttackTask(attackAction));
		
		return attackSequence;
	}
	
	/**
	 * Follows target on a platform. Only accounts for movement along the x-axis.<br>
	 * <br><em>Success Conditions</em>: Will always succeed as long as the AI can walk.
	 * 
	 * @return
	 */
	public static Task<Entity> followOnPlatform() {
		Selector<Entity> moveTowardsTarget = new Selector<Entity>();
		moveTowardsTarget.addChild(turnWhenTargetBehind());
		moveTowardsTarget.addChild(walk());
		
		return moveTowardsTarget;
	}
	
	/**
	 * Follows target on a platform staying within a certain threshold value. Only accounts for movement along the x-axis.<br>
	 * <br><em>Success Conditions</em>: Will always succeed as long as the AI can walk.
	 * 
	 * @return
	 */
	public static Task<Entity> followOnPlatform(final float threshold) {
		Selector<Entity> main = new Selector<Entity>();
		Sequence<Entity> moveTowardsTarget = new Sequence<Entity>();
		
		LeafTask<Entity> beyondThreshold = new LeafTask<Entity>() {
			@Override
			public Status execute() {
				Entity entity = getObject();
				TargetComponent targetComp = Mappers.target.get(entity);
				if(targetComp == null || !EntityUtils.isTargetable(targetComp.target)) return Status.FAILED;
				
				Vector2 myPos = PhysicsUtils.getPos(entity);
				Vector2 targetPos = PhysicsUtils.getPos(targetComp.target);
				
				return Math.abs(myPos.x - targetPos.x) > threshold ? Status.SUCCEEDED : Status.FAILED;
			}

			@Override
			protected Task<Entity> copyTo(Task<Entity> task) {
				return null;
			}
			
			@Override
			public String toString() {
				return "Outside of Threshold: " + threshold;
			}
		};
		
		moveTowardsTarget.addChild(beyondThreshold);
		moveTowardsTarget.addChild(new AlwaysSucceed<Entity>(turnWhenTargetBehind()));
		moveTowardsTarget.addChild(walk());
		
		main.addChild(moveTowardsTarget);
		main.addChild(new ReleaseControlsTask());
		
		return main;
	}
	
	/**
	 * Moves the AI forward. <br>
	 * <br><em>Success Conditions</em>: Will always succeed as long as AI can move forward.
	 * @return
	 */
	public static Task<Entity> walk() {
		Sequence<Entity> walkSequence = new Sequence<Entity>();
		walkSequence.addChild(new ReleaseControlsTask());
		walkSequence.addChild(new WalkForwardTask());
		
		return walkSequence;
	}
	
	/**
	 * Turns the AI around if the target is behind it.<br>
	 * <br><em>Success Conditions</em>: Succeeds if the target is behind the AI, fails otherwise.
	 * @return
	 */
	public static Task<Entity> turnWhenTargetBehind(){
		Sequence<Entity> turnWhenTargetBehindSequence = new Sequence<Entity>();
		turnWhenTargetBehindSequence.addChild(new TargetBehindMeTask());
		turnWhenTargetBehindSequence.addChild(new ReleaseControlsTask());
		turnWhenTargetBehindSequence.addChild(new TurnAroundTask());
		return turnWhenTargetBehindSequence;
	}
	
}
