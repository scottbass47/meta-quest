package com.cpubrew.gui;

public class KeyEvent {

	private Component source;
	private int key = -1;
	private char character;
	
	public KeyEvent(Component source, int key) {
		this.source = source;
		this.key = key;
	}

	public KeyEvent(Component source, char character) {
		this.source = source;
		this.character = character;
	}
	
	public char getCharacter() {
		if(character == '\u0000') throw new RuntimeException("Character is undefined");
		return character;
	}
	
	public int getKey() {
		if(key == -1) throw new RuntimeException("Key is undefined");
		return key;
	}
	
	public Component getSource() {
		return source;
	}
	
}
