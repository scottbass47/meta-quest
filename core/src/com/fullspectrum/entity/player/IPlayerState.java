package com.fullspectrum.entity.player;

public interface IPlayerState {

	/** Called upon once when the state switches */
	public void init(Player player);
	public void update(Player player);
	public IPlayerState handleInput();
	public void animFinished(Player player);
	
}