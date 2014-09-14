package com.hybridplay.packman;

import com.hybridplay.app.SensorReceiver;
import com.hybridplay.bluetooth.Sensor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Display;

public class GameActivityPackMan extends Activity{
	final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8, CENTER = 0;
	public Boolean connected = false;
	private GameSurfaceView gameView;
	
	//change in x and y of pac-mon
	private GameEngine gameEngine;
	private SoundEngine soundEngine;
	static final String LOGGER_TAG = "GameBTActivity";
	String playWith;
	
	// ----------------------------------------- HYBRIDPLAY SENSOR
	SensorReceiver mReceiver;
	Handler handler = new Handler();

	Sensor mSensorX = new Sensor("x",280,380,0);
	Sensor mSensorY = new Sensor("y",280,380,0);
	Sensor mSensorZ = new Sensor("z",280,380,0);
	Sensor mSensorIR = new Sensor("IR",250,512,1);
	float angleX, angleY, angleZ;
	int distanceIR;
	boolean triggerXL, triggerXR, triggerYL, triggerYR, triggerZL, triggerZR;
	
	private SharedPreferences prefs;
	public int calibXH, calibYH, calibZH, calibXV, calibYV, calibZV, calibIR;
	// ----------------------------------------- HYBRIDPLAY SENSOR

	
    /** Called when the activity is first created. */
    @SuppressLint("HandlerLeak")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        int level = getIntent().getIntExtra("level", 1);

        soundEngine = new SoundEngine(this);
        gameEngine = new GameEngine(soundEngine, level);
          
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        gameView = new GameSurfaceView(this, gameEngine, width, height);
        
        // get the game type (affect different sensor readings)
        Bundle extras = getIntent().getExtras();
        playWith = extras.getString("gameType");
        gameEngine.setGameType(playWith);

        setContentView(gameView);
        
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
        			mSensorY.update(0, mReceiver.AY-mSensorY.calibH);
        			mSensorZ.update(0, mReceiver.AZ-mSensorZ.calibH);
          		}else if(playWith.equals("Caballito")){
          			// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
          			mSensorX.update(0, mReceiver.AX-mSensorX.calibV);
        			mSensorY.update(0, mReceiver.AY-mSensorY.calibV);
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
    		    gameEngine.updateSensorData(angleX,angleY,angleZ,distanceIR,triggerXL,triggerXR,triggerYL,triggerYR,triggerZL,triggerZR);
    		    
    		    if(playWith.equals("Balancin")){
    				// pinza horizontal - cuatro direcciones - ejes Z Y
    				if(gameEngine.isCHYR){
						gameEngine.setInputDir(UP);
					}else if(gameEngine.isCHYL){
						gameEngine.setInputDir(DOWN);
					}
    				if (gameEngine.isCHZL){
    					gameEngine.setInputDir(LEFT);
    				}else if (gameEngine.isCHZR){
    					gameEngine.setInputDir(RIGHT);
    				}
          		}else if(playWith.equals("Caballito")){
          			// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
          			if(gameEngine.isCHYR){
    					gameEngine.setInputDir(UP);
    				}else if(gameEngine.isCHYL){
    					gameEngine.setInputDir(DOWN);
    				}
        			if (gameEngine.isCHXL){
        				gameEngine.setInputDir(LEFT);
        			}else if (gameEngine.isCHXR){
        				gameEngine.setInputDir(RIGHT);
       				}
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
	protected void onPause() {
		gameEngine.pause();
		gameView.pause();
		unregisterReceiver(mReceiver);
		super.onPause();
		
	}

	@Override
	protected void onResume() {
		gameEngine.resume();
		gameView.resume();
		this.mReceiver = new SensorReceiver();
		registerReceiver(this.mReceiver,new IntentFilter("com.hybridplay.SENSOR"));
		if (gameEngine != null) gameEngine.setGameState(0);
		super.onResume();
	}
	
	@Override
	public void finish() {
		 Intent intent = new Intent();

			 intent.putExtra("level", gameEngine.level);
		     intent.putExtra("status", gameEngine.status);

		        setResult(RESULT_OK, intent);
		  
		soundEngine.endMusic();
		super.finish();
	}


	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you want to quit?").setCancelable(false)
				.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						GameActivityPackMan.this.finish();
					}
				})
				.setNegativeButton("Resume", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
    
}