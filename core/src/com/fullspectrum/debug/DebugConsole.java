package com.fullspectrum.debug;

import org.lwjgl.opengl.GL11;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.assets.Assets;

public class DebugConsole implements InputProcessor{

	// Positioning
	private int x;
	private int y;
	private int width;
	private int height;
	private int lineHeight;
	private int lineHeightS;
	private int lineHeight2S;
	private int spacing;
	private GlyphLayout layout;
	private ShapeRenderer renderer;
	
	// Colors
	private final Color background = new Color(0x393845ff);// #393845ff
	private final Color inputBackground = new Color(0x23222cff); // #23222cff
	private final Color cursor = Color.WHITE; // white
	private final Color inputColor = Color.WHITE; // white
	private final Color errorColor = new Color(0xb02f2aff); // #b02f2aff
	private final Color commandColor = new Color(0x64a724ff); // #64a724ff
	
	// Font
	private BitmapFont mainFont;
	
	// Cursor
	private int cx;
	private int cy;
	private int cwidth;
	private int cheight;
	private int ups;
	private int curPos = 0;
	private boolean crender;
	
	// Console
	private StringBuilder inputText = new StringBuilder();
	private boolean open;
	private Array<String> history;
	
	// Keys
	private boolean ctrlDown = false;
	
	public DebugConsole(int x, int y, int width, int height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		renderer = new ShapeRenderer();
		mainFont = Assets.getInstance().getFont(Assets.consoleMain);
		spacing = 4;
		layout = new GlyphLayout();
		layout.setText(mainFont, "height");
		lineHeight = (int)(layout.height);
		lineHeightS = lineHeight + spacing;
		lineHeight2S = lineHeight + 2 * spacing;

		cx = x + spacing + spacing;
		cy = getInputY() + spacing / 2;
		cwidth = 2;
		cheight = lineHeightS;
		
		history = new Array<String>();
	}
	
	public void update(float delta){
		ups++;
		crender = (ups / 15) % 2 == 0;
	}
	
	public void render(SpriteBatch batch){
		Gdx.gl.glEnable(GL11.GL_BLEND);
		Gdx.gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		renderer.setProjectionMatrix(batch.getProjectionMatrix());
		renderer.begin(ShapeType.Filled);

		// Console Outline
		renderer.setColor(Color.BLACK);
		renderer.rect(x - spacing, y - spacing - lineHeight2S, width + 2 * spacing, height + lineHeight2S + 2 * spacing);
		
		// Console
		renderer.setColor(background);
		renderer.rect(x, y, width, height);

		// Input
		renderer.setColor(inputBackground);
		renderer.rect(x, getInputY(), width, lineHeight2S);
		
		// Font
		renderer.end();
		batch.begin();
		
		// Input
		mainFont.setColor(inputColor);
		mainFont.draw(batch, inputText, x + spacing, y - spacing);
		
		// History
		int historyY = y + lineHeightS;
		for(int i = history.size - 1; i >= 0; i--){
			String s = history.get(i);
			mainFont.setColor(commandColor);
			mainFont.draw(batch, s, x + spacing, historyY);
			historyY += lineHeightS;
		}
		
		batch.end();
	
		// Cursor
		if(crender){
			renderer.begin(ShapeType.Filled);
			renderer.setColor(cursor);
			renderer.rect(cx, cy, cwidth, cheight);
			renderer.end();
		}
		Gdx.gl.glDisable(GL11.GL_BLEND);
	}
	
	@Override
	public boolean keyTyped(char character) {
		if(!open) return false;
		switch(character){
		case '`':
			return false;
		case '\b':
			if(inputText.length() == 0) break;
			if(ctrlDown){
				int finalPos = curPos - 1;
				boolean alphaNumeric = isAlphaNumeric(inputText.charAt(finalPos));
				for(int i = curPos - 1; i >= 0; i--){
					char c = inputText.charAt(i);
					if((isAlphaNumeric(c) && alphaNumeric) || (!isAlphaNumeric(c) && !alphaNumeric)){
						if(i - 1 == 0) finalPos = 0;
						continue;
					}
					finalPos = i;
					break;
				}
				inputText.delete(finalPos, curPos);
				curPos = finalPos;
			}else{
				inputText.deleteCharAt(inputText.length() - 1);
				curPos--;
			}
			break;
		case '\n':
		case '\r':
			history.add(inputText.toString());
			inputText.delete(0, inputText.length());
			curPos = 0;
			break;
		default:
			inputText.insert(curPos, character);
			curPos++;
			break;
		}
		layout.setText(mainFont, inputText);
		updateCursorPos();
		return true;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT) ctrlDown = true;
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT) ctrlDown = false;
		return false;
	}
	
	/** Returns lower left corner y position of input box */
	private int getInputY(){
		return y - lineHeight2S;
	}
	
	private void updateCursorPos(){
		layout.setText(mainFont, inputText.subSequence(0, curPos));
		cx = x + spacing + (int)layout.width;
	}
	
	private boolean isNum(char c){
		return c >= '0' && c <= '9';
	}
	
	private boolean isLetter(char c){
		return Character.isAlphabetic(c);
	}
	
	private boolean isAlphaNumeric(char c){
		return isNum(c) || isLetter(c);
	}
	
	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	// ----------------------------------
	// <<<< UNECESSARY INPUT METHODS >>>>
	// ----------------------------------
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}
	
	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
}
