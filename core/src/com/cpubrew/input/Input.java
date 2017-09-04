package com.cpubrew.input;

public interface Input {

	/**
	 * Returns true if the current input state is true (pressed down) and the
	 * previous input state is false (was not pressed down). Use this when
	 * performing actions that should not repeat continuously as long as the
	 * input is held down (e.g. attacking).
	 * 
	 * @param action
	 * @return
	 */
	public boolean isJustPressed(Actions action);

	/**
	 * Returns true only if the current state is true (pressed down). Use this
	 * for continuous actions such as running.
	 * 
	 * @param action
	 * @return
	 */
	public boolean isPressed(Actions action);
	
	/**
	 * Returns a float value between 0 and 1 (both inclusive).
	 * 
	 * E.g. how far over an analog stick is on a controller, 1 being all the way over, 0 being in the center. <br>
	 * <br/>
	 * Note: For all binary controls this function will only output either 0 or 1. (e.g. keys, buttons, dpad, etc..)
	 * 
	 * @param action
	 * @return
	 */
	public Float getValue(Actions action);
	
}
