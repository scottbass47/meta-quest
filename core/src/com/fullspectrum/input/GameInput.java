package com.fullspectrum.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ArrayMap;

public class GameInput implements InputProcessor, ControllerListener, Input {

	private InputProfile profile;

	// Controller
	private Controller controller;
	public final static float ANALOG_THRESHOLD = 0.3f;

	private ArrayMap<Actions, Float> currentInput;
	private ArrayMap<Actions, Float> previousInput;

	public GameInput(InputProfile profile) {
		this.profile = profile;
		currentInput = new ArrayMap<Actions, Float>();
		previousInput = new ArrayMap<Actions, Float>();
		initInputMaps();
		if(Controllers.getControllers().size > 0){
			this.controller = Controllers.getControllers().first();
			profile.setContext("xbox_one");
		}
	}

	private void initInputMaps() {
		for (Actions a : Actions.values()) {
			currentInput.put(a, 0.0f);
			previousInput.put(a, 0.0f);
		}
	}
	
	public void update(){
		for(Actions a : Actions.values()){
			previousInput.put(a, currentInput.get(a));
		}
	}
	
//	public void update() {
//		if (Controllers.getControllers().size > 0 && !usingController) {
////			System.out.println("Controller inbound!");
//			usingController = true;
//			controller = Controllers.getControllers().get(0);
//			profile.setContext("xbox_one");
//			initInputMaps();
//		} else if(Controllers.getControllers().size <= 0 && usingController){
////			System.out.println("No controller found :(");
//			usingController = false;
//			profile.setContext("keyboard");
//			initInputMaps();
//		}
//	}

	@Override
	public boolean isJustPressed(Actions action) {
		return currentInput.get(action) == 1.0f && previousInput.get(action) == 0.0f;
	}

	@Override
	public boolean isPressed(Actions action) {
		return currentInput.get(action) == 1.0f;
	}
	
	@Override
	public Float getValue(Actions action){
		return currentInput.get(action);
	}

	public void setProfile(InputProfile profile) {
		this.profile = profile;
	}

	@Override
	public void connected(Controller controller) {
		// TAKE OUT THIS LINE TO ALLOW MULTIPLE CONTROLLERS
		if(this.controller != null) return;
		this.controller = controller;
		profile.setContext("xbox_one");
		initInputMaps();
		System.out.println("Controller Connected");
	}

	@Override
	public void disconnected(Controller controller) {
		this.controller = null;
		profile.setContext("keyboard");
		initInputMaps();
		System.out.println("Disconnected");
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		Actions action = profile.getButton(buttonCode);
		if (action != null) {
//			previousInput.put(action, currentInput.get(action));
			currentInput.put(action, 1.0f);
		}
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		Actions action = profile.getButton(buttonCode);
		if (action != null) {
//			previousInput.put(action, currentInput.get(action));
			currentInput.put(action, 0.0f);
		}
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		int axisNum = axisCode;
		String dir = value < 0.0f ? "neg" : "pos";
		AxisData axisData = new AxisData(dir, axisNum);
		Actions action = profile.getAxis(axisData);
		if(action != null){
			AxisData oppAxis = new AxisData(value > 0.0f ? "neg" : "pos", axisNum);
			Actions oppAction = profile.getAxis(oppAxis);
			if(currentInput.get(oppAction) >= 0.0f){
//				previousInput.put(oppAction, currentInput.get(oppAction));
				currentInput.put(oppAction, 0.0f);
			}
//			previousInput.put(action, currentInput.get(action));
			currentInput.put(action, Math.abs(value));
		}
		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		if (value.name().equals("center")) {
			for (Actions a : profile.getContext().getPOVActions()) {
//				previousInput.put(a, currentInput.get(a));
				currentInput.put(a, 0.0f);
			}
		}
		Actions action = profile.getPOV(value.name());
		if (action != null) {
			for (Actions a : profile.getContext().getPOVActions()) {
				if (currentInput.get(a) == 1.0f) {
//					previousInput.put(a, currentInput.get(a));
					currentInput.put(a, 0.0f);
				}
			}
//			previousInput.put(action, currentInput.get(action));
			currentInput.put(action, 1.0f);
		}
		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		System.out.println("Slider X: " + sliderCode + ", Value: " + sliderCode);
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		System.out.println("Slider Y: " + sliderCode + ", Value: " + sliderCode);
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		Actions action = profile.getKey(keycode);
		if (action != null) {
//			previousInput.put(action, currentInput.get(action));
			currentInput.put(action, 1.0f);
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		Actions action = profile.getKey(keycode);
		if (action != null) {
//			previousInput.put(action, currentInput.get(action));
			currentInput.put(action, 0.0f);
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
