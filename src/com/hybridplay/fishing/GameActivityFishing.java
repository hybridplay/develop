package com.hybridplay.fishing;

import com.hybridplay.app.SensorReceiver;
import com.hybridplay.bluetooth.Sensor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;

public class GameActivityFishing extends Activity {
	private GameSurfaceView gameView;
	private GameEngine gameEngine;
	static final String GAME_TAG = "FISHING Activity";
	String playWith;
	
	// ----------------------------------------- HYBRIDPLAY SENSOR
	SensorReceiver mReceiver;
	Handler handler = new Handler();

	Sensor mSensorX = new Sensor("x",280,380,0);
	Sensor mSensorY = new Sensor("y",280,380,0);
	Sensor mSensorZ = new Sensor("z",280,380,0);
	Sensor mSensorIR = new Sensor("IR",200,512,1);
	float angleX, angleY, angleZ;
	int distanceIR;
	boolean triggerXL, triggerXR, triggerYL, triggerYR, triggerZL, triggerZR;
	// ----------------------------------------- HYBRIDPLAY SENSOR
	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        
        // create the game engine
        gameEngine = new GameEngine(width, height);
        // get the game type (affect different sensor readings)
        Bundle extras = getIntent().getExtras();
        playWith = extras.getString("gameType");
        gameEngine.setGameType(playWith);
        
        gameView = new GameSurfaceView(this, gameEngine, width, height);
        setContentView(gameView);
        
        handler.post(new Runnable(){
        	@Override
        	public void run() {
        		// ------------------------------------ update sensor readings
        		mSensorX.update(0, mReceiver.AX);
    			mSensorY.update(0, mReceiver.AY);
    			mSensorZ.update(0, mReceiver.AZ);
    			mSensorIR.update(0, mReceiver.IR);

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
    		    
    		    handler.postDelayed(this,40); // set time here to refresh (40 ms => 12 FPS)
        	}
        });
	}
    
    @Override
	protected void onPause() {
		if (gameEngine != null) gameEngine.pause();
		if (gameView != null) gameView.pause();
		unregisterReceiver(mReceiver);
		super.onPause();
		
	}

	@Override
	protected void onResume() {
		if (gameEngine != null) gameEngine.resume();
		if (gameView != null) gameView.resume();
		this.mReceiver = new SensorReceiver();
		registerReceiver(this.mReceiver,new IntentFilter("com.hybridplay.SENSOR"));
		if (gameEngine != null) gameEngine.setGameState(1);
		super.onResume();
	}

	@Override
	public void finish() {
		 Intent intent = new Intent();

		 intent.putExtra("level", gameEngine.level);
		 intent.putExtra("status", gameEngine.status);

		 setResult(RESULT_OK, intent);
		        
		super.finish();
	}


	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you want to quit?").setCancelable(false)
				.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						GameActivityFishing.this.finish();
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
