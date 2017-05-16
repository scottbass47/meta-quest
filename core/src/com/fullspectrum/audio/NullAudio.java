package com.fullspectrum.audio;

import com.badlogic.gdx.math.Vector2;

public class NullAudio implements Audio {

	@Override
	public long playSound(Sounds sound, Vector2 pos) {
		return -1;
	}

	@Override
	public void stopSound(long soundID) {
	}

	@Override
	public void stopAllSounds() {
	}

	@Override
	public void update() {
	}
}
