package com.cpubrew.debug;

import java.util.Stack;

import com.badlogic.gdx.Gdx;

public class Time {

	private static Stack<Long> startTimes = new Stack<Long>();
	private static Stack<String> names = new Stack<String>();
	
	public static void start(String name){
		startTimes.push(System.nanoTime());
		names.push(name);
	}
	
	public static void stop(){
		if(startTimes.size() == 0) throw new RuntimeException("Can't stop timer with no available start times.");
		long startTime = startTimes.pop();
		double seconds =(System.nanoTime() - startTime) / 1000000000d;
		Gdx.app.debug(names.pop(), seconds + "s");
	}
	
}
