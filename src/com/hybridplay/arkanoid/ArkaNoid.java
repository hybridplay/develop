package com.hybridplay.arkanoid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.hybridplay.app.R;
import com.hybridplay.app.SensorReceiver;
import com.hybridplay.arkanoid.controllers.SoundController;
import com.hybridplay.bluetooth.Sensor;

public class ArkaNoid extends Activity{

	private static final int MENU_START = 0;
	private static final int MENU_TILT = 1;
	private ArkaNoidView arkaDroidView;
	private ArkaNoidGameThread gameThread;
	private TiltListener tiltListener;
	
	// HYBRIDPLAY SENSOR
	SensorReceiver mReceiver;
	Handler handler = new Handler();
	
	Sensor mSensorX = new Sensor("x",280,380,0);
	Sensor mSensorY = new Sensor("y",280,380,0);
    Sensor mSensorZ = new Sensor("z",280,380,0);
    Sensor mSensorIR = new Sensor("IR",20,512,1);
    float angleX, angleY, angleZ;
    int distanceIR;
    boolean triggerXL, triggerXR, triggerYL, triggerYR, triggerZL, triggerZR;

	String playWith;
	private final double PADDLE_SPEED = 2.0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SoundController.init(getBaseContext());
		// turn off the window's title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // tell system to use the layout defined in our XML file
        this.setContentView(R.layout.arkanoid);

        // get handles to the LunarView from XML, and its LunarThread
        arkaDroidView = (ArkaNoidView) findViewById(R.id.arkanoid);
        gameThread = arkaDroidView.getGameThread();
        tiltListener = new TiltListener(this, gameThread);
        
        Bundle extras = getIntent().getExtras();
        playWith = extras.getString("gameType");
        //Log.d("ARKANOID",playWith);
        	
        if (savedInstanceState == null) {
            // we were just launched: set up a new game
            // gameThread.setState(LunarThread.STATE_READY);
            Log.w(this.getClass().getName(), "SIS is null");
        } else {
            Log.w(this.getClass().getName(), "sis isn't null. that's odd.");
        }
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
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
    		    if(playWith.equals("Balancin")){
    		    	// pinza horizontal - cuatro direcciones - ejes Z Y
    		    	if (triggerZR) { // RIGHT
    		    		gameThread.changedBoth(PADDLE_SPEED, false, true);
    		    	}else if (triggerZL) { // LEFT
    		    		gameThread.changedBoth(PADDLE_SPEED, true, false);
    		    	}
    		    }else if(playWith.equals("Caballito")){
    		    	// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
    		    	if (triggerXR) { // RIGHT
    		    		gameThread.changedBoth(PADDLE_SPEED, false, true);
    		    	}else if (triggerXL) { // LEFT
    		    		gameThread.changedBoth(PADDLE_SPEED, true, false);
    		    	}
    		    }else if(playWith.equals("Columpio")){
    		    	// pinza vertical boton hacia abajo - oscilaci�n - eje X

    		    }else if(playWith.equals("SubeBaja")){
    		    	// pinza horizontal - dos direcciones - eje Z
    		    	if (triggerZR) { // RIGHT
    		    		gameThread.changedBoth(PADDLE_SPEED, false, true);
    		    	}else if (triggerZL) { // LEFT
    		    		gameThread.changedBoth(PADDLE_SPEED, true, false);
    		    	}
    		    }else if(playWith.equals("Tobogan")){
    		    	// we use here only IR sensor

    		    }
    		    
    		    handler.postDelayed(this,40); // set time here to refresh (40 ms => 12 FPS)
        	}
        });
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_START, Menu.NONE, R.string.menu_start);
		menu.add(0, MENU_TILT, Menu.NONE, R.string.tilt_toggle_on);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case MENU_START: gameThread.gameGo(); return true;
			case MENU_TILT:
				if(tiltListener.isOn()){
					item.setTitle(R.string.tilt_toggle_on);
					tiltListener.stop();
				}
				else{
					item.setTitle(R.string.tilt_toggle_off);
					tiltListener.start();
				}
				return true;
		}
		return false;
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(mReceiver);
		arkaDroidView.getGameThread().pause();
		gameThread.setGameConnecter(false);
		super.onPause();
	}
	
	@Override
	protected void onResume(){
		this.mReceiver = new SensorReceiver();
		registerReceiver(this.mReceiver,new IntentFilter("com.hybridplay.SENSOR"));
		gameThread.setGameConnecter(true);
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you want to quit?").setCancelable(false)
				.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ArkaNoid.this.finish();
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
