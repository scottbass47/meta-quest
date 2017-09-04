package com.cpubrew.audio;

public class AudioLocator {

	private static NullAudio nullAudio = new NullAudio();
	private static Audio service;
	
	static {
		service = nullAudio;
	}
	
	public static Audio getAudio(){
		return service;
	}
	
	public static void provide(Audio audioService) {
		if(audioService == null){
			service = nullAudio;
		}
		else {
			service = audioService;
		}
	}
	
	
}
