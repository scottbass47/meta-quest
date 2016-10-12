package com.fullspectrum.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.debug.DebugInput;

public class RawInput implements InputProcessor, ControllerListener{
	
	private Array<InputProcessor> inputProcessors;
	private Array<ControllerListener> controllers;
	
	public RawInput(){
		inputProcessors = new Array<InputProcessor>();
		controllers = new Array<ControllerListener>();
	}
	
	public void registerGameInput(GameInput input){
		inputProcessors.add(input);
		controllers.add(input);
	}
	
	@Override
	public void connected(Controller controller) {
		for(ControllerListener listener : controllers){
			listener.connected(controller);
		}
	}

	@Override
	public void disconnected(Controller controller) {
		for(ControllerListener listener : controllers){
			listener.disconnected(controller);
		}
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		for(ControllerListener listener : controllers){
			listener.buttonDown(controller, buttonCode);
		}
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		for(ControllerListener listener : controllers){
			listener.buttonUp(controller, buttonCode);
		}
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		for(ControllerListener listener : controllers){
			listener.axisMoved(controller, axisCode, value);
		}
		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		for(ControllerListener listener : controllers){
			listener.povMoved(controller, povCode, value);
		}
		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		for(ControllerListener listener : controllers){
			listener.xSliderMoved(controller, sliderCode, value);
		}
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		for(ControllerListener listener : controllers){
			listener.ySliderMoved(controller, sliderCode, value);
		}
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		for(ControllerListener listener : controllers){
			listener.accelerometerMoved(controller, accelerometerCode, value);
		}
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		for(InputProcessor input : inputProcessors){
			input.keyDown(keycode);
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		for(InputProcessor input : inputProcessors){
			input.keyUp(keycode);
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		DebugInput.keyTyped(character);
		for(InputProcessor input : inputProcessors){
			input.keyTyped(character);
		}
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		for(InputProcessor input : inputProcessors){
			input.touchDown(screenX, screenY, pointer, button);
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		for(InputProcessor input : inputProcessors){
			input.touchUp(screenX, screenY, pointer, button);
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		for(InputProcessor input : inputProcessors){
			input.touchDragged(screenX, screenY, pointer);
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		for(InputProcessor input : inputProcessors){
			input.mouseMoved(screenX, screenY);
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		for(InputProcessor input : inputProcessors){
			input.scrolled(amount);
		}
		return false;
	}

	
}
