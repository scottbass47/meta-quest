package com.cpubrew.ability.knight;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.cpubrew.ability.AbilityType;
import com.cpubrew.ability.TimedAbility;
import com.cpubrew.assets.Asset;
import com.cpubrew.assets.AssetLoader;
import com.cpubrew.component.BarrierComponent;
import com.cpubrew.component.BlacksmithComponent;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.TintComponent;
import com.cpubrew.entity.EntityManager;
import com.cpubrew.factory.EntityFactory;
import com.cpubrew.input.Actions;
import com.cpubrew.utils.EntityUtils;
import com.cpubrew.utils.Maths;

public class BlacksmithAbility extends TimedAbility{
	
	private float conversionChance;
	private float conversionPercent;
	private Entity entity;
	private float maxShield;

	public BlacksmithAbility(float cooldown, Actions input, float duration, float conversionChance, float conversionPercent, float maxShield) {
		super(AbilityType.BLACKSMITH, AssetLoader.getInstance().getRegion(Asset.BLACKSMITH_ICON), cooldown, input, duration, false);
		this.conversionChance = conversionChance;
		this.conversionPercent = conversionPercent;
		this.maxShield = maxShield;
	}

	@Override
	protected void init(Entity entity) {
		this.entity = entity;
		entity.add(Mappers.engine.get(entity).engine.createComponent(TintComponent.class).set(Color.BLUE));
	}
	
	@Override
	public void onUpdate(Entity entity, float delta) {
	}

	@Override
	protected void destroy(Entity entity) {
		entity.remove(TintComponent.class);
	}
	
	public float getConversionChance() {
		return conversionChance;
	}
	
	public float getConversionPercent() {
		return conversionPercent;
	}

	public void convertIntoShield(float amount) {
		float gained = amount * conversionPercent;
		float actualGained = 0.0f;
		BarrierComponent barrierComp = Mappers.barrier.get(entity);
		if(barrierComp.barrier < barrierComp.maxBarrier){
			float overflow = Maths.getOverflow(barrierComp.barrier + gained, barrierComp.maxBarrier);

			float before = barrierComp.barrier;
			// If there is no overflow, then fill up the barrier as much as possible
			if(MathUtils.isEqual(overflow, 0.0f)){
				barrierComp.barrier += gained;
				gained = 0;
			} else{
				barrierComp.barrier = barrierComp.maxBarrier;
				gained = overflow;
			}
			actualGained += barrierComp.barrier - before;
		} 
		if(gained > 0){
			BlacksmithComponent blacksmithComp = EntityUtils.lazyAdd(entity, BlacksmithComponent.class).set(maxShield);
			
			float before = blacksmithComp.shield;
			blacksmithComp.shield = MathUtils.clamp(blacksmithComp.shield + gained, 0, blacksmithComp.shieldMax);
			actualGained += blacksmithComp.shield - before;
		}
		BodyComponent bodyComp = Mappers.body.get(entity);
		Body body = bodyComp.body;
		float x = body.getPosition().x;
		float y = body.getPosition().y + bodyComp.getAABB().height * 0.5f + 0.5f;
		EntityManager.addEntity(EntityFactory.createDamageText(
				"+" + (int)actualGained, 
				Color.CYAN, 
				AssetLoader.getInstance().getFont(AssetLoader.font18), 
				x, 
				y, 
				2.0f));
	}

}
