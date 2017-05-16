package com.fullspectrum.audio;

import com.badlogic.gdx.math.Vector2;

public interface Audio {

	public long playSound(Sounds sound, Vector2 pos);
	public void stopSound(long soundID);
	public void stopAllSounds();
	public void update();
	
}
