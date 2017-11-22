package com.cpubrew.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import com.cpubrew.assets.Asset;
import com.cpubrew.assets.AssetLoader;
import com.cpubrew.factory.EntityFactory;
import com.cpubrew.game.GameVars;
import com.cpubrew.utils.RenderUtils;

public enum EntityIndex {

	// Player
	KNIGHT {
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createKnight(x, y);
		}

		@Override
		public Animation<TextureRegion> getIdleAnimation() {
			return AssetLoader.getInstance().getAnimation(Asset.KNIGHT_IDLE);
		}

		@Override
		public Rectangle getHitBox() {
			return new Rectangle(0, 0, 15, 26);
		}
	},
	ROGUE {
		private Animation<TextureRegion> animation;
		
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createRogue(x, y);
		}

		@Override
		public Animation<TextureRegion> getIdleAnimation() {
			// Rogue is a pain in the ass because of the split upper and lower body
			// So we build the animation once with upper and lower body parts
			if(animation == null) {
				Animation<TextureRegion> upper = AssetLoader.getInstance().getAnimation(Asset.ROGUE_IDLE_ARMS);
				Animation<TextureRegion> lower = AssetLoader.getInstance().getAnimation(Asset.ROGUE_IDLE_LEGS);
				SpriteBatch batch = new SpriteBatch();
				OrthographicCamera cam = new OrthographicCamera();
				
				TextureRegion[] regions = new TextureRegion[(int)(upper.getAnimationDuration() / upper.getFrameDuration())];
				for(int i = 0; i < regions.length; i++) {
					TextureRegion upperRegion = upper.getKeyFrame(i * GameVars.ANIM_FRAME);
					TextureRegion lowerRegion = lower.getKeyFrame(i * GameVars.ANIM_FRAME);

					FrameBuffer buffer = new FrameBuffer(Format.RGBA8888, upperRegion.getRegionWidth(), upperRegion.getRegionHeight(), false);
					cam.setToOrtho(false, buffer.getWidth(), buffer.getHeight());
					buffer.begin();
					
					Gdx.gl.glClearColor(1f, 1f, 1f, 0f);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
					
					batch.setProjectionMatrix(cam.combined);
					
					batch.begin();
					batch.draw(lowerRegion, 0, 0);
					batch.draw(upperRegion, 0, 0);
					batch.end();
					
					buffer.end();
					regions[i] = new TextureRegion(buffer.getColorBufferTexture());
					regions[i].flip(false, true);
					regions[i].getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
				}
				animation = new Animation<TextureRegion>(GameVars.ANIM_FRAME, regions);
			}
			return animation;
		}

		@Override
		public Rectangle getHitBox() {
			return new Rectangle(0, 0, 15, 26);
		}
	},
	MONK {
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createMonk(x, y);
		}

		@Override
		public Animation<TextureRegion> getIdleAnimation() {
			return AssetLoader.getInstance().getAnimation(Asset.MONK_IDLE);
		}

		@Override
		public Rectangle getHitBox() {
			return new Rectangle(0, 0, 15, 26);
		}
	},
	
	// Enemies
	CLUB_GREMLIN {
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createClubGremlin(x, y);
		}

		@Override
		public Animation<TextureRegion> getIdleAnimation() {
			return AssetLoader.getInstance().getAnimation(Asset.CLUB_GREMLIN_IDLE);
		}

		@Override
		public Rectangle getHitBox() {
			return new Rectangle(0, 0, 15, 26);
		}
	},
	SPAWNER{
		private Animation<TextureRegion> animation;
		
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createSpawner(x, y);
		}

		@Override
		public Animation<TextureRegion> getIdleAnimation() {
			if(animation == null) {
				Pixmap pixmap = new Pixmap(24, 24, Format.RGBA8888);
				pixmap.setColor(Color.MAGENTA);
				pixmap.fill();
				
				TextureRegion region = new TextureRegion(new Texture(pixmap));
				animation = new Animation<TextureRegion>(GameVars.ANIM_FRAME, region);
			}
			
			return animation;
		}

		@Override
		public Rectangle getHitBox() {
			return new Rectangle(0, 0, 24, 24);
		}
	},
	BOAR {
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createBoar(x, y);
		}

		@Override
		public Animation<TextureRegion> getIdleAnimation() {
			return AssetLoader.getInstance().getAnimation(Asset.BOAR_IDLE);
		}

		@Override
		public Rectangle getHitBox() {
			return new Rectangle(0, 0, 30, 20);
		}
		
	},
	GUN_GREMLIN {
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createGunGremlin(x, y);
		}

		@Override
		public Animation<TextureRegion> getIdleAnimation() {
			return AssetLoader.getInstance().getAnimation(Asset.GUN_GREMLIN_IDLE_RANDOM);
		}

		@Override
		public Rectangle getHitBox() {
			return new Rectangle(0, 0, 14, 22);
		}
	},
	ROCKY {
		private Animation<TextureRegion> idle = RenderUtils.scaleAnimation(
				AssetLoader.getInstance().getAnimation(Asset.ROCKY_IDLE),
				2.0f
		);
		
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createRocky(x, y);
		}

		@Override
		public Animation<TextureRegion> getIdleAnimation() {
			return idle;
		}

		@Override
		public Rectangle getHitBox() {
			return new Rectangle(0, 0, 30, 50);
		}
	},
	GRUNT_GREMLIN {
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createGruntGremlin(x, y);
		}

		@Override
		public Animation<TextureRegion> getIdleAnimation() {
			return AssetLoader.getInstance().getAnimation(Asset.GRUNT_GREMLIN_IDLE);
		}

		@Override
		public Rectangle getHitBox() {
			return new Rectangle(0, 0, 12, 18);
		}
	},
	PROJECTILE_GREMLIN {
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createProjectileGremlin(x, y);
		}

		@Override
		public Animation<TextureRegion> getIdleAnimation() {
			return AssetLoader.getInstance().getAnimation(Asset.PROJECTILE_GREMLIN_IDLE);
		}

		@Override
		public Rectangle getHitBox() {
			return new Rectangle(0, 0, 14, 22);
		}
	},
	BIRD {
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createBird(x, y);
		}

		@Override
		public Animation<TextureRegion> getIdleAnimation() {
			return AssetLoader.getInstance().getAnimation(Asset.BIRD_IDLE);
		}

		@Override
		public Rectangle getHitBox() {
			return new Rectangle(0, 0, 20, 20);
		}
	},
	DRILL_GREMLIN {
		@Override
		public Entity create(float x, float y) {
			return EntityFactory.createDrillGremiln(x, y);
		}

		@Override
		public Animation<TextureRegion> getIdleAnimation() {
			return AssetLoader.getInstance().getAnimation(Asset.DRILL_GREMLIN_IDLE);
		}

		@Override
		public Rectangle getHitBox() {
			return new Rectangle(0, 0, 14, 20);
		}
	};
	
	// TODO Consider including Input as a needed argument
	public abstract Entity create(float x, float y);
	public abstract Animation<TextureRegion> getIdleAnimation();
	
	/** Hit boxes are in pixels and are manually entered. */
	public abstract Rectangle getHitBox();
	
	public String getName(){
		return name().toLowerCase();
	}
	
	public short shortIndex(){
		return (short)ordinal();
	}
	
	public static EntityIndex get(String name){
		for(EntityIndex index : EntityIndex.values()){
			if(index.name().equalsIgnoreCase(name)) return index;
		}
		return null;
	}
	
	public static boolean isPlayer(EntityIndex index) {
		return index == KNIGHT || index == ROGUE || index == MONK;
	}
	
	
	
}
