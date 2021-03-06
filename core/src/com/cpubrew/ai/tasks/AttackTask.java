package com.cpubrew.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.cpubrew.ai.AIController;
import com.cpubrew.component.InputComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.input.Actions;

public class AttackTask extends LeafTask<Entity>{

	private Actions attackAction;
	
	public AttackTask() {
		attackAction = Actions.ATTACK;
	}
	
	public AttackTask(Actions attackAction) {
		this.attackAction = attackAction;
	}
	
	@Override
	public Status execute() {
		Entity entity = getObject();
		InputComponent inputComp = Mappers.input.get(entity);
		if(!inputComp.enabled) return Status.FAILED;
		
		AIController controller = (AIController) inputComp.input;
		controller.justPress(attackAction);
		return Status.SUCCEEDED;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}
	
	public void setAttackAction(Actions attackAction) {
		this.attackAction = attackAction;
	}
	
	public Actions getAttackAction() {
		return attackAction;
	}

}
