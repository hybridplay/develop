package com.hybridplay.pong;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hybridplay.app.R;
import com.hybridplay.app.SensorReceiver;
import com.hybridplay.bluetooth.Sensor;

public class GameActivityPong extends Activity{
	private PongView mPongView;
	private AlertDialog mAboutBox;
	protected PowerManager.WakeLock mWakeLock;
	
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
	
	ImageView sUP, sDOWN, sLEFT, sRIGHT;
	// ----------------------------------------- HYBRIDPLAY SENSOR
	
	public String playWith;
	public int dWidth, dHeight;
	
	public static final String
		EXTRA_RED_PLAYER = "red-is-player",
		EXTRA_BLUE_PLAYER = "blue-is-player";
	
	
    @SuppressLint("HandlerLeak")
	@Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        Display display = getWindowManager().getDefaultDisplay();
        dWidth = display.getWidth();
        dHeight = display.getHeight();
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        setContentView(R.layout.pong_view);
        
        sUP = (ImageView)findViewById(R.id.imageView2);
        sDOWN = (ImageView)findViewById(R.id.imageView3);
        sLEFT = (ImageView)findViewById(R.id.imageView1);
        sRIGHT = (ImageView)findViewById(R.id.imageView4);
        
        mPongView = (PongView) findViewById(R.id.pong);
        
        Intent i = getIntent();
        Bundle b = i.getExtras();
        mPongView.setPlayerControl(b.getBoolean(EXTRA_RED_PLAYER, false),
        	b.getBoolean(EXTRA_BLUE_PLAYER, false)
        );
        
        // get the game type (affect different sensor readings)
        playWith = b.getString("gameType");
        Log.d("PONG Log","Play with: "+playWith);
        
        mPongView.setDimension(dWidth,dHeight);
        mPongView.update();
        
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        final PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Pong");
        mWakeLock.acquire();
        
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
        			mSensorZ.applyVCalibration();
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
    		    if(playWith.equals("Balancin")){
    				// pinza horizontal - cuatro direcciones - ejes Z Y
          			if (triggerZR) { // RIGHT
          				mPongView.movePaddle(1);
        			}else if (triggerZL) { // LEFT
        				mPongView.movePaddle(0);
        			}
          			
          			if(!triggerZR && !triggerZL){
          				mPongView.movePaddle(2);
    		    	}
          		}else if(playWith.equals("Caballito")){
          			// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
          			if (triggerXR) { // RIGHT
          				mPongView.movePaddle(1);
        			}else if (triggerXL) { // LEFT
        				mPongView.movePaddle(0);
        			}
          			
          			if(!triggerXR && !triggerXL){
          				mPongView.movePaddle(2);
    		    	}
          			
          		}else if(playWith.equals("Columpio")){
          			// pinza vertical boton hacia abajo - oscilaci�n - eje X

          		}else if(playWith.equals("SubeBaja")){
          			// pinza horizontal - dos direcciones - eje Z
          			if (triggerZR || triggerXR) { // RIGHT
          				mPongView.movePaddle(1);
        			}else if (triggerZL || triggerXL) { // LEFT
        				mPongView.movePaddle(0);
        			}
          			
          			if((!triggerZR && !triggerZL) && (!triggerXR && !triggerXL)){
          				mPongView.movePaddle(2);
    		    	}
          		}else if(playWith.equals("Tobogan")){
          			// we use here only IR sensor

          		}
    		    
    		    updateGraphicSensor();
    		    
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
    
    public void updateGraphicSensor(){
		if(playWith.equals("Columpio")){
			// pinza vertical boton hacia abajo - oscilacion - eje Z
			if(triggerZL){ // LEFT
				sLEFT.setImageResource(R.drawable.izquierda_on);
			}else{
				sLEFT.setImageResource(R.drawable.izquierda_off);
			}
			
			if(triggerZR){ // RIGHT
				sRIGHT.setImageResource(R.drawable.derecha_on);
			}else{
				sRIGHT.setImageResource(R.drawable.derecha_off);
			}
		}else if(playWith.equals("Tobogan")){
			// we use here only IR sensor
			
		}else if(playWith.equals("SubeBaja")){
			// pinza horizontal - dos direcciones - eje Z
			if(triggerZR || triggerXR){ // UP
				sUP.setImageResource(R.drawable.arriba_on);
			}else{
				sUP.setImageResource(R.drawable.arriba_off);
			}
			
			if(triggerZL || triggerXL){ // DOWN
				sDOWN.setImageResource(R.drawable.abajo_on);
			}else{
				sDOWN.setImageResource(R.drawable.abajo_off);
			}
		}else if(playWith.equals("Balancin")){
			// pinza horizontal - cuatro direcciones - ejes Z Y
			if(triggerYR){ // UP
				sUP.setImageResource(R.drawable.arriba_on);
			}else{
				sUP.setImageResource(R.drawable.arriba_off);
			}
			
			if(triggerYL){ // DOWN
				sDOWN.setImageResource(R.drawable.abajo_on);
			}else{
				sDOWN.setImageResource(R.drawable.abajo_off);
			}
			
			if(triggerZL){ // LEFT
				sLEFT.setImageResource(R.drawable.izquierda_on);
			}else{
				sLEFT.setImageResource(R.drawable.izquierda_off);
			}
			if(triggerZR){ // RIGHT
				sRIGHT.setImageResource(R.drawable.derecha_on);
			}else{
				sRIGHT.setImageResource(R.drawable.derecha_off);
			}
		}else if(playWith.equals("Caballito")){
			// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
			
			// --------> CAMBIAR LA Y POR LA Z SI LA PINZA VA EN LA CABEZA DEL CABALLITO
			
			if(triggerZR){ // UP
				sUP.setImageResource(R.drawable.arriba_on);
			}else{
				sUP.setImageResource(R.drawable.arriba_off);
			}
			
			if(triggerZL){ // DOWN
				sDOWN.setImageResource(R.drawable.abajo_on);
			}else{
				sDOWN.setImageResource(R.drawable.abajo_off);
			}
			
			if(triggerXL){ // LEFT
				sLEFT.setImageResource(R.drawable.izquierda_on);
			}else{
				sLEFT.setImageResource(R.drawable.izquierda_off);
			}
			if(triggerXR){ // RIGHT
				sRIGHT.setImageResource(R.drawable.derecha_on);
			}else{
				sRIGHT.setImageResource(R.drawable.derecha_off);
			}
		}
	}
    
    @Override
    protected void onStart() {
        super.onStart();
    }
    
    protected void onStop() {
		mPongView.stop();
		super.onStop();
    }
    
    protected void onPause(){
    	mWakeLock.release();
    	unregisterReceiver(mReceiver);
    	super.onPause();
    }
    
    protected void onResume() {
    	mPongView.resume();
    	mWakeLock.acquire();
    	this.mReceiver = new SensorReceiver();
		registerReceiver(this.mReceiver,new IntentFilter("com.hybridplay.SENSOR"));
    	super.onResume();
    }
    
    protected void onDestroy() {
    	super.onDestroy();
    	mPongView.release();
    }
   
    public void hideAboutBox() {
    	if(mAboutBox != null) {
    		mAboutBox.hide();
    		mAboutBox = null;
    	}
    }
	
}