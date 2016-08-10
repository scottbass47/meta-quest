package com.fullspectrum.entity.player;

import com.fullspectrum.input.GameInput;

public interface IPlayerState {

	/** Called upon once when the state switches */
	public void init(Player player);
	public void update(Player player);
	public IPlayerState handleInput(GameInput input);
	public void animFinished(Player player);
	
}