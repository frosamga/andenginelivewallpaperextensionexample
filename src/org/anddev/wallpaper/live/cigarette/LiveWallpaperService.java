package org.anddev.wallpaper.live.cigarette;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.entity.particle.ParticleSystem;
import org.anddev.andengine.entity.particle.modifier.AlphaModifier;
import org.anddev.andengine.entity.particle.modifier.ExpireModifier;
import org.anddev.andengine.entity.particle.modifier.VelocityInitializer;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;

public class LiveWallpaperService extends BaseLiveWallpaperService implements IAccelerometerListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 512;
	private static final int CAMERA_HEIGHT = 768;

	private static final int LAYER_SMOKE = 1;
	private static final int LAYER_CIGARETTE = 0;

	// ===========================================================
	// Fields
	// ===========================================================
	
	private Texture mTexture;

	private TextureRegion mSmokeTextureRegion;
	private TextureRegion mCigaretteTextureRegion;

	private ScreenOrientation mScreenOrientation;
	private VelocityInitializer mVelocityInitializer;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public org.anddev.andengine.engine.Engine onLoadEngine() {
		return new org.anddev.andengine.engine.Engine(new EngineOptions(true, this.mScreenOrientation, new FillResolutionPolicy(), new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT)));
	}

	@Override
	public void onLoadResources() {
		this.mTexture = new Texture(512, 128);

		/* Creates the needed texture-regions on the texture. */
		this.mCigaretteTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, this, "gfx/cigarette.png", 0, 0); // 400x120
		this.mSmokeTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, this, "gfx/smoke.png", 381, 0); // 64x64
		
		this.getEngine().getTextureManager().loadTexture(this.mTexture);
		this.enableAccelerometerSensor(this);
	}

	@Override
	public Scene onLoadScene() {
		final Scene scene = new Scene(2);

		final int cigaretteX = 0;
		final int cigaretteY = CAMERA_HEIGHT - this.mCigaretteTextureRegion.getHeight();

		scene.getLayer(LAYER_CIGARETTE).addEntity(new Sprite(cigaretteX, cigaretteY, this.mCigaretteTextureRegion));
		
		final int smokeX = cigaretteX + this.mCigaretteTextureRegion.getWidth() - 100;
		final int smokeY = cigaretteY - 25;
		final ParticleSystem smokeParticleSystem = new ParticleSystem(smokeX, smokeY, 0, 0, 6, 8, 150, this.mSmokeTextureRegion);
		smokeParticleSystem.addParticleModifier(new ExpireModifier(8,10));
		this.mVelocityInitializer = new VelocityInitializer(-20, 20, -100, -120);
		smokeParticleSystem.addParticleInitializer(this.mVelocityInitializer);
		smokeParticleSystem.addParticleModifier(new AlphaModifier(1, 0, 0, 10));
		
//		this.getEngine().registerPreFrameHandler(new FPSCounter());

		scene.getLayer(LAYER_SMOKE).addEntity(smokeParticleSystem);
		
		return scene;
	}

	@Override
	public void onLoadComplete() {
		
	}

	@Override
	public void onAccelerometerChanged(final AccelerometerData pAccelerometerData) {
		final float minVelocityX = (pAccelerometerData.getX() + 2) * 5;
		final float maxVelocityX = (pAccelerometerData.getX() - 2) * 5;
		
		final float minVelocityY = (pAccelerometerData.getY() - 8) * 10;
		final float maxVelocityY = (pAccelerometerData.getY() - 10) * 10;
		this.mVelocityInitializer.setVelocity(minVelocityX, maxVelocityX, minVelocityY, maxVelocityY);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	
}