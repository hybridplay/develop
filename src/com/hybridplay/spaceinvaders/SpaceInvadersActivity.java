/*
 *  Space Invaders
 *
 *  Copyright (C) 2012 Glow Worm Applications
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Street #330, Boston, MA 02111-1307, USA.
 */

package com.hybridplay.spaceinvaders;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.hybridplay.app.R;
import com.hybridplay.app.SensorReceiver;
import com.hybridplay.bluetooth.Sensor;

public class SpaceInvadersActivity extends Activity{
    /** Called when the activity is first created. */
    
    private InvaderView mView = null;
    
    private PowerManager mPowerManager;
    private WakeLock mWakeLock;
    
    private Timer mUpdate;
    private InvaderTimer mEvent;
    
    private SharedPreferences mPrefs; 
    
    private ObjectManager mObjects = new ObjectManager();
    
    private boolean mFire = false;
    private boolean mMove = false;
    private boolean mMoveLeft = false;
	private boolean mTouch = false;
	private boolean mTap = false;
    
    // ----------------------------------------- HYBRIDPLAY SENSOR
 	SensorReceiver mReceiver;
 	Handler handler = new Handler();

 	Sensor mSensorX = new Sensor("x",0,360,0);
	Sensor mSensorY = new Sensor("y",0,360,0);
	Sensor mSensorZ = new Sensor("z",0,360,0);
 	Sensor mSensorIR = new Sensor("IR",250,512,1);
 	float angleX, angleY, angleZ;
 	int distanceIR;
 	boolean triggerXL, triggerXR, triggerYL, triggerYR, triggerZL, triggerZR;
 	
 	private SharedPreferences prefs;
	public int calibXH, calibYH, calibZH, calibXV, calibYV, calibZV, calibIR;
 	// ----------------------------------------- HYBRIDPLAY SENSOR
 	
 	String playWith;
 	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get an instance of the PowerManager
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
       

        // Create a bright wake lock
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
        							getClass().getName());

        //get a copy of the shared preferences
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        
        // get the game type (affect different sensor readings)
        Bundle extras = getIntent().getExtras();
        playWith = extras.getString("gameType");

        mView = new InvaderView(getApplicationContext());
        setContentView(mView);
        
        mUpdate = new Timer();
        mEvent = new InvaderTimer();
        mUpdate.schedule(mEvent, 0 ,80);
        
        updateCalibration();
        
        handler.post(new Runnable(){
        	@Override
        	public void run() {
        		// ------------------------------------ update sensor readings
        		mSensorX.update(0, mReceiver.AX);
    			mSensorY.update(0, mReceiver.AY);
    			mSensorZ.update(0, mReceiver.AZ);
    			mSensorIR.update(0, mReceiver.IR);
    			
    			// CALIBRATION
        		if(playWith.equals("Balancin")){
    				// pinza horizontal - cuatro direcciones - ejes Z Y
        			mSensorY.applyHCalibration();
        			mSensorZ.applyHCalibration();
          		}else if(playWith.equals("Caballito")){
          			// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
          			
          			// --------> CAMBIAR LA Y POR LA Z SI LA PINZA VA EN LA CABEZA DEL CABALLITO
          			
          			mSensorX.applyVCalibration();
        			mSensorY.applyVCalibration();
          		}else if(playWith.equals("Columpio")){
          			// pinza vertical boton hacia abajo - oscilacion - eje X

          		}else if(playWith.equals("SubeBaja")){
          			// pinza horizontal - dos direcciones - eje Z
          			
          		}else if(playWith.equals("Tobogan")){
          			// we use here only IR sensor

          		}

    			angleX 		= mSensorX.getDegrees();
    			angleY 		= mSensorY.getDegrees();
    			angleZ 		= mSensorZ.getDegrees();
    		    distanceIR 	= mSensorIR.getDistanceIR();
    		    triggerXL 	= mSensorX.getTriggerMin();
    		    triggerXR 	= mSensorX.getTriggerMax();
    		    triggerYL	= mSensorY.getTriggerMin();
    		    triggerYR	= mSensorY.getTriggerMax();
    		    triggerZL	= mSensorZ.getTriggerMin();
    		    triggerZR	= mSensorZ.getTriggerMax();
    		    // ------------------------------------ game interaction
    		    
    		    // AUTO FIRE
    		    //mFire = true;
    		    
    		    mTouch = true;
				mTap = true;
				
    		    if(playWith.equals("Balancin")){
    				// pinza horizontal - cuatro direcciones - ejes Z Y
    		    	if (triggerZR) { // RIGHT
    		    		mMoveLeft = false;
    					mMove = true;
    		    		
    		    	}else if (triggerZL) { // LEFT
    		    		mMoveLeft = true;
    					mMove = true;
    		    	}
    		    	
    		    	if(!triggerZR && !triggerZL){
    		    		mMove = false;
    		    	}
    		    	
          		}else if(playWith.equals("Caballito")){
          			// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
          			if (triggerXR) { // RIGHT
          				mMoveLeft = false;
    					mMove = true;
    		    	}else if (triggerXL) { // LEFT
    		    		mMoveLeft = true;
    					mMove = true;
    		    	}
          			
          			if(!triggerXR && !triggerXL){
    		    		mMove = false;
    		    	}
          			
          		}else if(playWith.equals("Columpio")){
          			// pinza vertical boton hacia abajo - oscilaci�n - eje X

          		}else if(playWith.equals("SubeBaja")){
          			// pinza horizontal - dos direcciones - eje Z
          			if (triggerZR) { // RIGHT
    		    		mMoveLeft = false;
    					mMove = true;
    		    		
    		    	}else if (triggerZL) { // LEFT
    		    		mMoveLeft = true;
    					mMove = true;
    		    	}
    		    	
    		    	if(!triggerZR && !triggerZL){
    		    		mMove = false;
    		    	}
          			
          		}else if(playWith.equals("Tobogan")){
          			// we use here only IR sensor

          		}
    		    
    		    handler.postDelayed(this,40); // set time here to refresh (40 ms => 12 FPS)
        	}
        });
    }
    
    void updateCalibration(){
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("calibratedAH", "") != null){
        	calibXH = prefs.getInt("accXH",0);
        	calibYH = prefs.getInt("accYH",0);
        	calibZH = prefs.getInt("accZH",0);
        }else{
        	calibXH = 0;
        	calibYH = 0;
        	calibZH = 0;
        }
        if(prefs.getString("calibratedAV", "") != null){
        	calibXV = prefs.getInt("accXV",0);
        	calibYV = prefs.getInt("accYV",0);
        	calibZV = prefs.getInt("accZV",0);
        }else{
        	calibXV = 0;
        	calibYV = 0;
        	calibZV = 0;
        }
        if(prefs.getString("calibratedIR", "") != null){
        	calibIR = prefs.getInt("calIR", 0);
        }else{
        	calibIR = 10;
        }
        
        mSensorX.getCalibration(calibXH, calibXV);
        mSensorY.getCalibration(calibYH, calibYV);
        mSensorZ.getCalibration(calibZH, calibZV);
        mSensorIR.setMaxIR(calibIR);
	}

    @Override
    protected void onResume() {
    	this.mReceiver = new SensorReceiver();
		registerReceiver(this.mReceiver,new IntentFilter("com.hybridplay.SENSOR"));
        super.onResume();
        mWakeLock.acquire();
        if(mView!=null){
        	mView.updatePrefs();
        }
    }

    @Override
    protected void onPause() {
    	unregisterReceiver(mReceiver);
    	super.onPause();
    	mWakeLock.release();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.spaceinvaders_menu, menu);
         return true;

     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
             
             case R.id.preferences:
             	startActivity(new Intent(this, InvaderPreferenceActivity.class));
             	return true;
             	
             case R.id.restart:
                 mView = new InvaderView(getApplicationContext());
                 setContentView(mView);
              	 return true;
              	 
             case R.id.author:
            	 Intent i = new Intent(Intent.ACTION_VIEW);
            	 i.setData(Uri.parse("market://search?q=pub:Glow Worm Applications"));
            	 startActivity(i);
              	 return true;
              	 
             case R.id.credits:
            	 startActivity(new Intent(this, CreditsActivity.class));              	
            	 return true;
         	}
         return false;
         }
    
    class InvaderTimer extends TimerTask {
        @Override
        public void run() {
         runOnUiThread(new Runnable() {
          public void run() {
           mView.postInvalidate();
          }
         });
        }
       }
    
    class InvaderView extends View{

    	private Invader[][] mInvaders= new Invader[mObjects.maxInvaders()][];
    	

    	//default values not used under normal conditions
    	//but create robustness in exceptional circumstances
    	private int mW=100,mH=100,mXoff=0,mYoff=0;
    	private int mGridWidth=100;
    	private int mGridHeight=100;
    	
    	private ArrayList<Shell> mShells = new ArrayList<Shell>();
    	private ArrayList<Shield> mShields = new ArrayList<Shield>();
    	private ArrayList<Spaceship> mShips = new ArrayList<Spaceship>();
    	
    	int mLevel = 3;
    	int mLives = 3;
    	
    	Boolean mInvMvRight = true;
    	Boolean mResetTank  = true;
    	Tank mTank;
    	

    	int mTouchX, mTouchY;
    	int mInvMoveCount = 8/mLevel;
    	int mInvFireCount = 100/mLevel;
    	int mSpaceshipCount = 2000/mLevel;
    	
    	Bitmap mSplash;
    	boolean mDrawSplash = true;
    	int mSplashCount = 0;
    	
    	SoundPool mSoundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
    	
    	int mSoundAlienBullet;
    	int mSoundAlienDeath;
    	int mSoundBlaster;
    	int mSoundDeath;
    	int mSoundExtraLife;
    	int mSoundFortressHit;
    	int mSoundGameOver;
    	
    	boolean mPlaySounds = true;
    	
    	boolean mRedFlash = false;
    	
    	Paint mScorePaint = new Paint();
    	
    	boolean mDrawBackgrounds = false;
    	
    	//////////////////////////////////SENSOR REFERENCE
    	public Bitmap sUP_ON, sDOWN_ON, sLEFT_ON, sRIGHT_ON;
    	public Bitmap sUP_OFF, sDOWN_OFF, sLEFT_OFF, sRIGHT_OFF;
    	public Rect srcRect_UP, srcRect_DOWN, srcRect_LEFT, srcRect_RIGHT;
    	public Rect dstRect_UP, dstRect_DOWN, dstRect_LEFT, dstRect_RIGHT;
    	//////////////////////////////////SENSOR REFERENCE
    	
		public InvaderView(Context context) {
			super(context);
			
			mLevel= mPrefs.getInt("starting_level", 3);
			mLives= mPrefs.getInt("starting_lives", 3);
			
			initSensorGraphics();
			
			updatePrefs();
			
			loadSounds();
			
			resetInvaders();
			if(mDrawBackgrounds){
				mSplash = BitmapFactory.decodeResource( getResources(), R.drawable.splash);
			} else {
				mSplash = BitmapFactory.decodeResource( getResources(), R.drawable.splash_classic);
			}
		    Typeface tmp = Typeface.createFromAsset(getAssets(), "coolthreepixels.ttf");
		    mScorePaint.setColor(0x50FFFFFF);
		    mScorePaint.setTypeface(tmp);
		    mScorePaint.setTextSize(15);
		}
		
		public void initSensorGraphics(){
			sUP_ON = BitmapFactory.decodeResource(getResources(), R.drawable.arriba_on);
			sUP_OFF = BitmapFactory.decodeResource(getResources(), R.drawable.arriba_off);
			
			sDOWN_ON = BitmapFactory.decodeResource(getResources(), R.drawable.abajo_on);
			sDOWN_OFF = BitmapFactory.decodeResource(getResources(), R.drawable.abajo_off);
			
			sLEFT_ON = BitmapFactory.decodeResource(getResources(), R.drawable.derecha_on);
			sLEFT_OFF = BitmapFactory.decodeResource(getResources(), R.drawable.derecha_off);
			
			sRIGHT_ON = BitmapFactory.decodeResource(getResources(), R.drawable.izquierda_on);
			sRIGHT_OFF = BitmapFactory.decodeResource(getResources(), R.drawable.izquierda_off);
			
			srcRect_UP = new Rect(0,0,sUP_ON.getWidth(),sUP_ON.getHeight());
			srcRect_DOWN = new Rect(0,0,sDOWN_ON.getWidth(),sDOWN_ON.getHeight());
			srcRect_RIGHT = new Rect(0,0,sLEFT_ON.getWidth(),sLEFT_ON.getHeight());
			srcRect_LEFT = new Rect(0,0,sRIGHT_ON.getWidth(),sRIGHT_ON.getHeight());
			
			dstRect_UP = new Rect(60,20,60+sUP_ON.getWidth(),20+sUP_ON.getHeight());
			dstRect_DOWN = new Rect(60,106,60+sDOWN_ON.getWidth(),106+sDOWN_ON.getHeight());
			dstRect_RIGHT = new Rect(30,50,30+sLEFT_ON.getWidth(),50+sLEFT_ON.getHeight());
			dstRect_LEFT = new Rect(118,50,118+sRIGHT_ON.getWidth(),50+sRIGHT_ON.getHeight());
		}
		
		public void updatePrefs(){
			//mDrawBackgrounds = mPrefs.getBoolean("backgrounds_on", true);			
			mPlaySounds = mPrefs.getBoolean("play_sounds", true);
			mObjects.setColourScheme(mPrefs.getString("colour_scheme", "Default"));
		}
		
		protected void loadSounds(){
			Context c = this.getContext();
	    	mSoundAlienBullet = mSoundPool.load(c,R.raw.alien_bullet,1);
	    	mSoundAlienDeath  = mSoundPool.load(c,R.raw.alien_death,1);
	    	mSoundBlaster     = mSoundPool.load(c,R.raw.blaster,1);
	    	mSoundDeath       = mSoundPool.load(c,R.raw.death,1);
	    	mSoundExtraLife   = mSoundPool.load(c,R.raw.extra_life,1);
	    	mSoundFortressHit = mSoundPool.load(c,R.raw.fortress_hit,1);
	    	mSoundGameOver    = mSoundPool.load(c,R.raw.game_over,1);
		}
		protected void playSound(int s){
			if(mPlaySounds){	
				mSoundPool.play( s , 1.0f, 1.0f, 0, 0, 1.0f);
			}
		}
		private void resetInvaders(){
		
			int width = 5;
			int height = 3;
			
			if(mLevel> 2){
				width = 6;
				height = 4;
			}
			if(mLevel> 4){
				width = 6;
				height = 5;
			}
			if(mLevel> 6){
				width = 8;
				height = 5;
			}
			
			mInvaders = new Invader[height][];
			
			for(int i=0;i<height;i++){
				
				
				Invader[] tmp =new Invader[width];
				
				for(int j = 0; j<width;j++){
					tmp[j] = new Invader( mObjects.invader(i),
									      j*ObjectManager.INVADER_WIDTH,
									      i*ObjectManager.INVADER_HEIGHT);	
				}
				mInvaders[i]=tmp;
			}
		}
		private void resetShields(){
			mShields.clear();
			for(int i = 1; i< 4; i++){
				mShields.add(new Shield(mObjects.shield(),
										(mGridWidth*i)/4 -ObjectManager.SHIELD_WIDTH/2,
										mGridHeight-
										ObjectManager.TANK_HEIGHT -
										ObjectManager.SHIELD_HEIGHT -
										1  ));
			}
			
		}
		protected void killShields(){
			for(int i = 0; i<mShells.size(); i++){
				for(int j =0; j< mShields.size(); j++){
					if(mShells.get(i).collisionDetect(mShields.get(j))){
						playSound(mSoundFortressHit);
						mShields.get(j).collision(mShells.get(i));
						mShells.remove(i);
						i--;
						break;
					}
				}
			}
		}
		protected void doSpaceships(){
			mSpaceshipCount--;
			if(mSpaceshipCount<=0){
				mSpaceshipCount = 2000/mLevel;
				mShips.add(new Spaceship( mObjects.ship(), mGridWidth, 0));
			}
			for(int i = 0; i< mShips.size(); i++){
				mShips.get(i).advance();
				if(!mShips.get(i).isValid()){
					mShips.remove(i);
					i--;
				}
			}
			for(int i = 0; i< mShips.size(); i++){
				for(int j=0; j< mShells.size(); j++){
					if(mShells.get(j).collisionDetect(mShips.get(i))){
						mShips.remove(i);
						mShells.remove(j);
						i--;
						j--;
						mLives++;
						playSound(mSoundExtraLife);
						break;
					}
				}
			}
		}
		protected void invadersFire(){
			if(mInvFireCount<0){
				mInvFireCount = 100/mLevel;
				Random r = new Random();
				int j = r.nextInt(mInvaders[0].length) ;
				int i;
				for(i = mInvaders.length-1; i >= 0; i-- ){
					if(mInvaders[i][j] != null){
						mShells.add(new Shell(
											mInvaders[i][j].getX()+
											  ObjectManager.INVADER_WIDTH/2,
											mInvaders[i][j].getY()+
											  ObjectManager.INVADER_HEIGHT+1,
											false )
									);
						playSound(mSoundAlienBullet);
						break;
					}
				}
			}
			mInvFireCount--;
		}
		@Override
		protected void onDraw(Canvas c) {
			
			setSizes();
			
			if(mDrawSplash){
				if(mSplashCount>0){
					mSplashCount --;
				}
				drawSplash(c);
				return;
			}
			
			c.drawColor(0);
			

			if(mResetTank){
				mTank = new Tank(mObjects.tank(),
								 (mGridWidth+ObjectManager.TANK_WIDTH)/2,
								 mGridHeight-ObjectManager.TANK_HEIGHT,
								 mGridWidth);
				resetShields();
				mResetTank = false;
			}
			if(mTouch||mTap){
				mTap=false;
	        	Shell tmp = mTank.update(mFire, mMove, mMoveLeft);
	        	if(tmp != null){
	        		mShells.add(tmp);
	        		playSound(mSoundBlaster);
	        	}
			}
			mTank.allowNewMove();
			advanceShells();
			killShields();
			killInvaders();
			doSpaceships();
			testEndConditions();
			invadersFire();
			
			if(invadersDead()){
				nextLevel();
			}
			mInvMoveCount--;
			if(mInvMoveCount<=0){
				mInvMoveCount = 16/mLevel;
				if(invadersCanMove()){
					moveInvadersHorrizontal();
				} else {
					advanceInvaders();
				}
			}
			drawBackground(c);
			drawShips(c,mW,mH,mXoff,mYoff);
			drawInvaders(c,mW,mH,mXoff,mYoff);
			drawTank(c,mW,mH,mXoff,mYoff);
			drawShields(c,mW,mH,mXoff,mYoff);
			drawShells(c,mW,mH,mXoff,mYoff);
			drawText(c);
			drawSensor(c);
			
		}
		protected void setSizes(){
			
			//FOR LOOP ABUSE!!!!
			for(int i=1 ; (mGridWidth=this.getWidth()/i) >= 200 ; i++ );
			
			mW = this.getWidth()/mGridWidth;
			mH = mW;
			
			mXoff = ( this.getWidth() - (mW*mGridWidth) )/2;
			
			mGridHeight= this.getHeight()/mH;
			mYoff =( this.getHeight() - (mH*mGridHeight) )/2;
			
		}
		
		protected boolean invadersCanMove(){
			for(int i=0; i<mInvaders.length;i++){
    			
    			for(int j=0;j<mInvaders[i].length;j++){
    	
    				if(mInvaders[i][j]!=null){	
    				
    					if(mInvaders[i][j].moveInvalid( 2 ,
    												   mInvMvRight,
    												   mGridWidth)){
    						return false;
    					}
    				}
    			}
    		}
			return true;
		}
		protected void moveInvadersHorrizontal(){
			for(int i=0; i<mInvaders.length;i++){		
    			for(int j=0;j<mInvaders[i].length;j++){
    				if(mInvaders[i][j]!=null){	
    					mInvaders[i][j].horizontalMove(2, mInvMvRight);
    
    					
    				}
    			}
			}
		}
		protected void advanceInvaders(){
			for(int i=0; i<mInvaders.length;i++){		
    			for(int j=0;j<mInvaders[i].length;j++){
    				if(mInvaders[i][j]!=null){	
    					mInvaders[i][j].verticalMove();
    				}
    			}
			}
			mInvMvRight = !mInvMvRight;
		}
		protected void advanceShells(){
			for(int i = 0; i< mShells.size(); i++){
				if(! mShells.get(i).progress(mGridHeight)){
					mShells.remove(i);
					i--;
				}
			}
		}
		protected void drawShells(Canvas c, int w, int h,int xoff, int yoff){
			Paint p = new Paint();
			p.setColor(mObjects.shellColour());
			for(int i = 0; i< mShells.size(); i++){
				drawGridSquare( c,
						   		mShells.get(i).pos(),
						   		w, h, xoff, yoff,
						   		p );
			}
		}
		protected void drawShields(Canvas c, int w, int h,int xoff, int yoff){
			Paint p = new Paint();
			p.setColor(mObjects.shieldColour());
			for(int i = 0; i< mShields.size(); i++){
				drawObject(c,
						   mShields.get(i),
						   w,h,xoff,yoff,
						   p);
			}
		}
		protected void drawShips(Canvas c, int w, int h,int xoff, int yoff){
			Paint p = new Paint();
			p.setColor(mObjects.shipColour());
			for(int i = 0; i< mShips.size(); i++){
				drawObject(c,
						   mShips.get(i),
						   w,h,xoff,yoff,
						   p);
			}
		}
		protected void killInvaders(){
			for(int i=mInvaders.length-1; i>=0;i--){
				for(int j=0; j<mInvaders[i].length;j++){
					if(mInvaders[i][j] != null){	
						for(int k =0; k < mShells.size(); k++){
							if(mShells.
									get(k).
									collisionDetect(
									mInvaders[i][j])){
								mInvaders[i][j]=null;
								mShells.remove(k);
								playSound(mSoundAlienDeath);
								break;
							}
						}
					}
				}
			}
		}
		protected void testEndConditions(){
			if(!mShields.isEmpty()){
				for(int i=0; i<mInvaders.length;i++){
					for(int j=0;j<mInvaders[i].length;j++){
						if(mInvaders[i][j]!=null){
							if(mInvaders[i][j].getY()
									+ObjectManager.INVADER_HEIGHT>
									mShields.get(0).getY()){
							
								mShields.clear();
								return;
							}
						}
					}
				}
			}
			for(int i=0; i<mInvaders.length;i++){
				for(int j=0;j<mInvaders[i].length;j++){
					if(mInvaders[i][j]!=null){
						if(mInvaders[i][j].getY()
								+ObjectManager.INVADER_WIDTH>
								mTank.getY()){
								invadersToTop();
								mLives--;
								mRedFlash=true;
								if(mLives<0){
									gameOver();
									playSound(mSoundGameOver);
									return;
								}
								playSound(mSoundDeath);
								return;
						}
					}
				}
			}
			for(int i =0; i < mShells.size(); i++){
				if(mShells.
						get(i).
						collisionDetect(
						mTank)){
					mTank = new Tank(mObjects.tank(),
							 (mGridWidth+ObjectManager.TANK_WIDTH)/2,
							 mGridHeight-ObjectManager.TANK_HEIGHT,
							 mGridWidth);
					mShells.remove(i);
					mLives--;
					mRedFlash=true;
					if(mLives<0){
						gameOver();
						playSound(mSoundGameOver);
						return;
					}
					playSound(mSoundDeath);
					return;
				}
			}
		}
		protected void invadersToTop(){
			
			
			for(int i=0;i<mInvaders.length;i++){
				
				for(int j = 0; j<mInvaders[i].length;j++){
					if(mInvaders[i][j]!=null){
						mInvaders[i][j] = new Invader( mObjects.invader(i),
									      j*ObjectManager.INVADER_WIDTH,
									      i*ObjectManager.INVADER_HEIGHT);	
					}
				}
				
			}
		}
    	protected boolean invadersDead(){
    		
    		for(int i=0; i<mInvaders.length;i++){
    			for(int j=0;j<mInvaders[i].length;j++){
    	
    				if(mInvaders[i][j]!=null){	
    					return false;
    				}
    			}
    		}
    		return true;
    	}
    	public void nextLevel(){
    		mLevel++;
    		resetInvaders();
    		resetShields();
    		mShells.clear();
    		if(mSpaceshipCount>2000/mLevel){
    			mSpaceshipCount = 2000/mLevel;
    		}
    	}
    	public void gameOver(){
    		
    		mLevel= mPrefs.getInt("starting_level", 3);
    		mLives= mPrefs.getInt("starting_lives", 3);
    		
    		resetInvaders();
    		resetShields();
    		mShells.clear();
    		mSpaceshipCount = 2000/mLevel;

    		mRedFlash=false;
    		mTouch=false;
    		mTap=false;
    		if(mDrawBackgrounds){
		        mSplash = BitmapFactory.decodeResource( getResources(), R.drawable.game_over);
    		}else{
    			mSplash = BitmapFactory.decodeResource( getResources(), R.drawable.game_over_classic);
    		}
		    mDrawSplash = true;
		    mSplashCount = 30;
            
    	}
    	protected void drawInvaders(Canvas c, int w, int h,int xoff, int yoff){
    		Paint p = new Paint();
    		for(int i=0; i<mInvaders.length;i++){
    			p.setColor(mObjects.invaderColour(i));
    			
    			for(int j=0;j<mInvaders[i].length;j++){
    	
    				if(mInvaders[i][j]!=null){	
    				
    				drawObject(c,
    						   mInvaders[i][j],
    						   w,h,xoff,yoff,
    						   p
    					);
    				}
    			}
    		}
    	}
    	protected void drawTank(Canvas c, int w, int h,int xoff, int yoff){
    		Paint p = new Paint();
    		p.setColor(mObjects.tankColour(mRedFlash));
    		mRedFlash=false;
			drawObject(c,
					   mTank,
					   w,h,xoff,yoff,
					   p
						);
    	}
    	protected void drawObject( Canvas c,
    							   GridObject o,
    							   int w, int h,int xoff, int yoff,
    							   Paint p){
    		o.reset();
    		Coordinate co;
    		while( (co=o.next() ) != null ){
    			drawGridSquare(c,co,w,h,xoff,yoff,p);
    		}
    	}
      	protected void drawGridSquare( Canvas c,
      								   Coordinate co,
      								   int w, int h,int xoff, int yoff,
      								   Paint p){
      			c.drawRect(xoff + w*co.x,
      					   yoff + h*co.y,
      					   xoff + w*(co.x+1),
      					   yoff + h*(co.y+1),
      					   p);
      	}
      	protected void drawText(Canvas c){
      		mScorePaint.setTextAlign(Paint.Align.LEFT);
      		c.drawText("level:"+mLevel,0,10,mScorePaint);
      		mScorePaint.setTextAlign(Paint.Align.RIGHT);
      		c.drawText("lives:"+mLives,getWidth(),10,mScorePaint);
      	}
      	protected void drawBackground(Canvas c){
      		if(mDrawBackgrounds){
      			c.drawBitmap(mObjects.background((mLevel-1)%(ObjectManager.NUM_BACKGROUNDS),
      											 getWidth(), 
      											 getResources()),
      					0,
      					getHeight()-getWidth(),
      					null);
      		}
      	}
    	protected void drawSplash(Canvas c){
    		if(mSplash.getWidth()!=getWidth()){
    			mSplash = Bitmap.createScaledBitmap(mSplash, 
    							getWidth(), getWidth(), false);
    		}
      		c.drawBitmap(mSplash,
      					 0,
      					 getHeight()-getWidth(),
      					 null);
      	}
    	
    	private void drawSensor(Canvas canvas){
    		if(playWith.equals("Columpio")){
    			// pinza vertical boton hacia abajo - oscilacion - eje Z
    			if(triggerZR){ // LEFT
    				canvas.drawBitmap(sLEFT_ON, srcRect_LEFT, dstRect_LEFT, null);
    			}else{
    				canvas.drawBitmap(sLEFT_OFF, srcRect_LEFT, dstRect_LEFT, null);
    			}
    			
    			if(triggerZL){ // RIGHT
    				canvas.drawBitmap(sRIGHT_ON, srcRect_RIGHT, dstRect_RIGHT, null);
    			}else{
    				canvas.drawBitmap(sRIGHT_OFF, srcRect_RIGHT, dstRect_RIGHT, null);
    			}
    		}else if(playWith.equals("Tobogan")){
    			// we use here only IR sensor
    			
    		}else if(playWith.equals("SubeBaja")){
    			// pinza horizontal - dos direcciones - eje Z
    			if(triggerZR){ // UP
    				canvas.drawBitmap(sUP_ON, srcRect_UP, dstRect_UP, null);
    			}else{
    				canvas.drawBitmap(sUP_OFF, srcRect_UP, dstRect_UP, null);
    			}
    			
    			if(triggerZL){ // DOWN
    				canvas.drawBitmap(sDOWN_ON, srcRect_DOWN, dstRect_DOWN, null);
    			}else{
    				canvas.drawBitmap(sDOWN_OFF, srcRect_DOWN, dstRect_DOWN, null);
    			}
    		}else if(playWith.equals("Balancin")){
    			// pinza horizontal - cuatro direcciones - ejes Z Y
    			if(triggerYR){ // UP
    				canvas.drawBitmap(sUP_ON, srcRect_UP, dstRect_UP, null);
    			}else{
    				canvas.drawBitmap(sUP_OFF, srcRect_UP, dstRect_UP, null);
    			}
    			
    			if(triggerYL){ // DOWN
    				canvas.drawBitmap(sDOWN_ON, srcRect_DOWN, dstRect_DOWN, null);
    			}else{
    				canvas.drawBitmap(sDOWN_OFF, srcRect_DOWN, dstRect_DOWN, null);
    			}
    			
    			if(triggerZR){ // LEFT
    				canvas.drawBitmap(sLEFT_ON, srcRect_LEFT, dstRect_LEFT, null);
    			}else{
    				canvas.drawBitmap(sLEFT_OFF, srcRect_LEFT, dstRect_LEFT, null);
    			}
    			if(triggerZL){ // RIGHT
    				canvas.drawBitmap(sRIGHT_ON, srcRect_RIGHT, dstRect_RIGHT, null);
    			}else{
    				canvas.drawBitmap(sRIGHT_OFF, srcRect_RIGHT, dstRect_RIGHT, null);
    			}
    		}else if(playWith.equals("Caballito")){
    			// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
    			
    			// --------> CAMBIAR LA Y POR LA Z SI LA PINZA VA EN LA CABEZA DEL CABALLITO
    			
    			if(triggerYR){ // UP
    				canvas.drawBitmap(sUP_ON, srcRect_UP, dstRect_UP, null);
    			}else{
    				canvas.drawBitmap(sUP_OFF, srcRect_UP, dstRect_UP, null);
    			}
    			
    			if(triggerYL){ // DOWN
    				canvas.drawBitmap(sDOWN_ON, srcRect_DOWN, dstRect_DOWN, null);
    			}else{
    				canvas.drawBitmap(sDOWN_OFF, srcRect_DOWN, dstRect_DOWN, null);
    			}
    			
    			if(triggerXR){ // LEFT
    				canvas.drawBitmap(sLEFT_ON, srcRect_LEFT, dstRect_LEFT, null);
    			}else{
    				canvas.drawBitmap(sLEFT_OFF, srcRect_LEFT, dstRect_LEFT, null);
    			}
    			if(triggerXL){ // RIGHT
    				canvas.drawBitmap(sRIGHT_ON, srcRect_RIGHT, dstRect_RIGHT, null);
    			}else{
    				canvas.drawBitmap(sRIGHT_OFF, srcRect_RIGHT, dstRect_RIGHT, null);
    			}
    		}
    	}
    	
    	@Override
        public boolean onTouchEvent(MotionEvent mot){
      		
      		if(mDrawSplash && mSplashCount <= 0){
      			mDrawSplash = false;
      			return true;
      		}
      		
      		if(mot.getAction() == MotionEvent.ACTION_DOWN || mot.getAction() == MotionEvent.ACTION_POINTER_DOWN	){  
      			mFire = true;
    		    //mTouch = true;
				//mTap = true;
      		}else if(mot.getAction() == MotionEvent.ACTION_UP){
      			//mTouch = false;
      			mFire = false;
      			//mTap = false;
      		}
      		
      		return true;
        }
        
    }
}
