package com.fullspectrum.audio;

public class AudioHandler extends Thread {

	private boolean running;
	
	public AudioHandler() {
		setDaemon(true);
	}
	
	public synchronized void startAudio() {
		start();
		running = true;
	}
	
	@Override
	public void run() {
		while(running) {
			AudioLocator.getAudio().update();
		}
	}
	
	public synchronized void stopAudio(){
		running = false;
	}
	
}
