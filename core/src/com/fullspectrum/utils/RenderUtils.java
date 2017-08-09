package com.fullspectrum.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.game.GameVars;

public class RenderUtils {

	public static void renderTrajectory(ShapeRenderer sRender, float x, float y, boolean right, float time, float jumpForce, float speed, int segments){
		float step = time / segments;
		for(float t = 0; t < time; t+=step){
			float x1 = x + t * speed * (right ? 1.0f : -1.0f);
			float x2 = x + (t + step) * speed * (right ? 1.0f : -1.0f);
			float y1 = y + jumpForce * t + 0.5f * GameVars.GRAVITY * t * t;
			float y2 = y + jumpForce * (t + step) + 0.5f * GameVars.GRAVITY * (t + step) * (t + step);
			sRender.line(x1, y1, x2, y2);
		}
	}
	
	public static Texture createBackground(int width, int height, Color color) {
		Pixmap pix = new Pixmap(width, height, Format.RGBA8888);
		pix.setColor(color);
		pix.fill();
		
		Texture background = new Texture(pix);
		pix.dispose();
		
		return background;
	}
	
	public static Animation<TextureRegion> scaleAnimation(Animation<TextureRegion> animation, float scale){
		Array<TextureRegion> frames = new Array<TextureRegion>();
		
		SpriteBatch batch = new SpriteBatch();
		OrthographicCamera cam = new OrthographicCamera();
		for(int i = 0; i < (int)(animation.getAnimationDuration() / animation.getFrameDuration()); i++) {
			TextureRegion frame = animation.getKeyFrame(i * animation.getFrameDuration());
			
			FrameBuffer buffer = new FrameBuffer(frame.getTexture().getTextureData().getFormat(), (int)(frame.getRegionWidth() * scale), (int)(frame.getRegionHeight() * scale), false);
			cam.setToOrtho(false, buffer.getWidth(), buffer.getHeight());
			buffer.begin();
			
			Gdx.gl.glClearColor(1f, 1f, 1f, 0f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			batch.setProjectionMatrix(cam.combined);
			
			batch.begin();
			batch.draw(frame, 0, 0, 0, 0, frame.getRegionWidth(), frame.getRegionHeight(), scale, scale, 0.0f);
			batch.end();
			
			buffer.end();
			
			TextureRegion result = new TextureRegion(buffer.getColorBufferTexture());
			result.flip(false, true);
			result.getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
			frames.add(result);
		}
		return new Animation<TextureRegion>(GameVars.ANIM_FRAME, frames, PlayMode.LOOP);
	}
}
