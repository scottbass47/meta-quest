package com.fullspectrum.game;

public interface IPlayerState {

	public void init(Player player);
	public void update(Player player);
	public IPlayerState handleInput();
	
}