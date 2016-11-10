package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.entity.EntityUtils;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;

public class RangeTransition extends TransitionSystem {

	private static RangeTransition instance;
	private ShapeRenderer sRenderer;

	private RangeTransition() {
		sRenderer = new ShapeRenderer();
	}

	public static RangeTransition getInstance() {
		if (instance == null) {
			instance = new RangeTransition();
		}
		return instance;
	}

	@Override
	public void update(float deltaTime) {
		for (StateMachine<? extends State, ? extends StateObject> machine : machines) {
			Entity entity = machine.getEntity();

			BodyComponent bodyComp = Mappers.body.get(entity);
			LevelComponent levelComp = Mappers.level.get(entity);

			for (TransitionObject obj : machine.getCurrentState().getData(Transition.RANGE)) {
				RangeTransitionData rtd = (RangeTransitionData) obj.data;
				if (rtd == null || rtd.target == null || !EntityUtils.isValid(rtd.target)) continue;
				BodyComponent otherBody = Mappers.body.get(rtd.target);

				Body b1 = bodyComp.body;
				Body b2 = otherBody.body;

				float x1 = b1.getPosition().x;
				float y1 = b1.getPosition().y;
				float x2 = b2.getPosition().x;
				float y2 = b2.getPosition().y;
				float distance = rtd.distance;
				boolean inside = rtd.inRange;
				
				if (inRange(x1, y1, x2, y2, distance, inside)) {
					if((rtd.rayTrace && levelComp.level.performRayTrace(x1, y1, x2, y2)) || !rtd.rayTrace){
						machine.changeState(obj);
						break;
					}
				}
				else if(!inside && !rtd.rayTrace && !levelComp.level.performRayTrace(x1, y1, x2, y2)){
					machine.changeState(obj);
					break;
				}
			}
		}
	}

	private boolean inRange(float x1, float y1, float x2, float y2, float r, boolean inside) {
		return inside ? (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) < r * r : (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) > r * r;
	}
	
	public void render(SpriteBatch batch) {
		sRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		sRenderer.begin(ShapeType.Line);
		for (StateMachine<? extends State, ? extends StateObject> machine : machines) {
			Entity entity = machine.getEntity();

			BodyComponent bodyComp = Mappers.body.get(entity);
			LevelComponent levelComp = Mappers.level.get(entity);

			TransitionObject obj = machine.getCurrentState().getFirstData(Transition.RANGE);
			RangeTransitionData rtd = (RangeTransitionData) obj.data;
			if (rtd == null || rtd.target == null || !EntityUtils.isValid(rtd.target)) continue;
			BodyComponent otherBody = Mappers.body.get(rtd.target);

			Body b1 = bodyComp.body;
			Body b2 = otherBody.body;

			float x1 = b1.getPosition().x;
			float y1 = b1.getPosition().y;
			float x2 = b2.getPosition().x;
			float y2 = b2.getPosition().y;
			
			Color color = new Color(1, 0, 0, 1);
			if(inRange(x1, y1, x2, y2, rtd.distance, true)){
				color = new Color(0, 1, 0, 1);
			}
			sRenderer.setColor(color);
			sRenderer.circle(x1, y1, rtd.distance, 32);
			
			color = new Color(1, 0, 0, 1);
			if(levelComp.level.performRayTrace(x1, y1, x2, y2)){
				color = new Color(0, 1, 0, 1);
			}
			sRenderer.setColor(color);
			sRenderer.line(x1, y1, x2, y2);
		}
		sRenderer.end();
	}
}
