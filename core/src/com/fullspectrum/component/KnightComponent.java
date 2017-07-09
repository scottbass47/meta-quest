package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.entity.EntityAnim;

public class KnightComponent implements Component, Poolable{

	// ORDER MATTERS!!!
	private Array<KnightAttack> attacks;
	public boolean first = true;
	public int index = 0;
	public int maxChains;
	public int chains = 0;
	public boolean hitAnEnemy = false;
	public ObjectSet<Entity> hitEnemies;
	
	// Chaining
	public float lungeX;
	public float lungeY;
	
	// Rolling
	public Body rollBody;
	public Body body;
	
	public KnightComponent(){
		attacks = new Array<KnightAttack>();
		hitEnemies = new ObjectSet<Entity>();
	}
	
	public KnightComponent set(int maxChains){
		this.maxChains = maxChains;
		return this;
	}
	
	/**
	 * ORDER MATTERS!!!
	 * @return
	 */
	public KnightComponent addAttack(EntityAnim idleAnticipation, EntityAnim attackAnticipation, EntityAnim swingAnimation, SwingComponent swingComp){
		attacks.add(new KnightAttack(idleAnticipation, attackAnticipation, swingAnimation, swingComp));
		return this;
	}
	
	public int numAttacks(){
		return attacks.size;
	}
	
	public KnightAttack getAttack(int index){
		return attacks.get(index);
	}
	
	public KnightAttack getCurrentAttack(){
		return getAttack(index);
	}
	
	public Array<KnightAttack> getAttacks(){
		return attacks;
	}
	
	@Override
	public void reset() {
		attacks = null;
		first = true;
		index = 0;
		maxChains = 0;
		chains = 0;
		hitAnEnemy = false;
		hitEnemies = null;
		body = null;
		rollBody = null;
	}
	
	public class KnightAttack{
		private EntityAnim idleAnticipation;
		private EntityAnim attackAnticipation;
		private EntityAnim swingAnimation;
		private SwingComponent swingComp;
		
		public KnightAttack(EntityAnim idleAnticipation, EntityAnim attackAnticipation, EntityAnim swingAnimation, SwingComponent swingComp) {
			this.idleAnticipation = idleAnticipation;
			this.attackAnticipation = attackAnticipation;
			this.swingAnimation = swingAnimation;
			this.swingComp = swingComp;
		}

		public EntityAnim getIdleAnticipation() {
			return idleAnticipation;
		}

		public EntityAnim getAnticipationAnimation() {
			return attackAnticipation;
		}

		public EntityAnim getSwingAnimation() {
			return swingAnimation;
		}

		public SwingComponent getSwingComp() {
			return swingComp;
		}
	}
}
