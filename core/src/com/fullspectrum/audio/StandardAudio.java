package com.fullspectrum.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.LongMap;
import com.fullspectrum.assets.AssetLoader;

// IMPORTANT Need a way to clear entries from the two maps when the sound effect is finished playing
public class StandardAudio implements Audio {

	private int head = 0;
	private int tail = 0;
	private static final int MAX_PENDING = 16;
	private PlayMessage[] pending;
	private long id = 1;
	private LongMap<Long> idMap; // maps generated id to real id
	private ArrayMap<Long, Sound> soundMap; // maps real id to sound

	public StandardAudio() {
		pending = new PlayMessage[MAX_PENDING];

		// Load in template PlayMessages
		for (int i = 0; i < MAX_PENDING; i++) {
			pending[i] = new PlayMessage();
		}
		
		idMap = new LongMap<Long>();
		soundMap = new ArrayMap<Long, Sound>();
	}

	/**
	 * Adds a play message to the sound queue and returns the generated id
	 */
	@Override
	public synchronized long playSound(Sounds soundEffect, Vector2 pos) {
		if ((tail + 1) % MAX_PENDING == head) {
			Gdx.app.log("Audio", "Over " + MAX_PENDING + " sound requests.");
		}
		
		float volume = 1.0f; // calculate volume here
		for(int i = head; i != tail; i = (i + 1) % MAX_PENDING) {
			if(pending[i].getSound() == soundEffect) {
				pending[i].volume = Math.max(volume, pending[i].getVolume());
				return pending[i].id;
			}
		}
		pending[tail].set(soundEffect, volume, id++);
		tail = (tail + 1) % MAX_PENDING;
		return id - 1;
	}

	@Override
	public void stopSound(long soundID) {
		// Note: soundID is the user's id, not the real id
		if(!idMap.containsKey(soundID)) return;
		long realID = idMap.get(soundID);
		soundMap.get(realID).stop(realID);
		
		// Remove unnecessary entries
		idMap.remove(soundID);
		soundMap.removeKey(realID);
	}

	@Override
	public void stopAllSounds() {
		for(Sound sound : soundMap.values()) {
			sound.stop();
		}
		soundMap.clear();
		idMap.clear();
	}

	@Override
	public void update() {
		if (tail == head) return;

		PlayMessage message = pending[head];
		Sound sound = AssetLoader.getInstance().getSound(message.getSound());
		long realID = sound.play(message.getVolume());
		idMap.put(message.getId(), realID);
		soundMap.put(realID, sound);
		
		head = (head + 1) % MAX_PENDING;
	}

	private static class PlayMessage {
		private Sounds sound;
		private float volume;
		private long id;

		public PlayMessage() {
			this(null, 0.0f, -1);
		}

		public PlayMessage(Sounds sound, float volume, long id) {
			this.sound = sound;
			this.volume = volume;
			this.id = id;
		}

		public Sounds getSound() {
			return sound;
		}

		public float getVolume() {
			return volume;
		}
		
		public long getId() {
			return id;
		}

		public void set(Sounds sound, float volume, long id) {
			this.sound = sound;
			this.volume = volume;
			this.id = id;
		}
	}

}
