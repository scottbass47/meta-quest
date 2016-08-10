package com.fullspectrum.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ArrayMap;

public class GameInput implements InputProcessor, ControllerListener {

	private InputProfile profile;

	// Controller
	private boolean usingController;
	private Controller controller;

	private ArrayMap<Actions, Boolean> currentInput;
	private ArrayMap<Actions, Boolean> previousInput;

	public GameInput(InputProfile profile) {
		this.profile = profile;
		currentInput = new ArrayMap<Actions, Boolean>();
		previousInput = new ArrayMap<Actions, Boolean>();
		initInputMaps();
		Gdx.input.setInputProcessor(this);
		Controllers.addListener(this);
	}

	private void initInputMaps() {
		for (Actions a : Actions.values()) {
			currentInput.put(a, false);
			previousInput.put(a, false);
		}
	}
	
	public void update() {
		if (Controllers.getControllers().size > 0 && !usingController) {
//			System.out.println("Controller inbound!");
			usingController = true;
			controller = Controllers.getControllers().get(0);
			profile.setContext("xbox_one");
			initInputMaps();
		} else if(Controllers.getControllers().size <= 0 && usingController){
//			System.out.println("No controller found :(");
			usingController = false;
			profile.setContext("keyboard");
			initInputMaps();
		}
	}

	/**
	 * Returns true if the current input state is true (pressed down) and the
	 * previous input state is false (was not pressed down). Use this when
	 * performing actions that should not repeat continuously as long as the
	 * input is held down (e.g. attacking).
	 * 
	 * @param action
	 * @return
	 */
	public boolean isAction(Actions action) {
		return currentInput.get(action) && !previousInput.get(action);
	}

	/**
	 * Returns true only if the current state is true (pressed down). Use this
	 * for continuous actions such as running.
	 * 
	 * @param action
	 * @return
	 */
	public boolean isPressed(Actions action) {
		return currentInput.get(action);
	}

	public void setProfile(InputProfile profile) {
		this.profile = profile;
	}

	@Override
	public void connected(Controller controller) {
		System.out.println("Connected");
	}

	@Override
	public void disconnected(Controller controller) {
		System.out.println("Disconnected");
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		Actions action = profile.getButton(buttonCode);
		if (action != null) {
			previousInput.put(action, currentInput.get(action));
			currentInput.put(action, true);
		}
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		Actions action = profile.getButton(buttonCode);
		if (action != null) {
			previousInput.put(action, currentInput.get(action));
			currentInput.put(action, false);
		}
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		if (axisCode == 1)
			System.out.println("Axis: " + axisCode + ", Value: " + value);
		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		System.out.println(value);
		if (value.name().equals("center")) {
			for (Actions a : profile.getContext().getPOVActions()) {
				previousInput.put(a, currentInput.get(a));
				currentInput.put(a, false);
			}
		}
		Actions action = profile.getPOV(value.name());
		if (action != null) {
			for (Actions a : profile.getContext().getPOVActions()) {
				if (currentInput.get(a)) {
					previousInput.put(a, currentInput.get(a));
					currentInput.put(a, false);
				}
			}
			previousInput.put(action, currentInput.get(action));
			currentInput.put(action, true);
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
			previousInput.put(action, currentInput.get(action));
			currentInput.put(action, true);
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		Actions action = profile.getKey(keycode);
		if (action != null) {
			previousInput.put(action, currentInput.get(action));
			currentInput.put(action, false);
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
