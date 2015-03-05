/*
 * Copyright © 2012 Iain Churcher
 *
 * Based on GLtron by Andreas Umbach (www.gltron.org)
 *
 * This file is part of GL TRON.
 *
 * GL TRON is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GL TRON is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GL TRON.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.hybridplay.glTron;

import com.hybridplay.app.SensorReceiver;
import com.hybridplay.bluetooth.Sensor;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class glTron extends Activity {
    /** Called when the activity is first created. */
	private OpenGLView _View;
	private CanvasView _CView;
	
	private Boolean _FocusChangeFalseSeen = false;
	private Boolean _Resume = false;
	
	int width;
    int height;
	
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
	boolean semaphoreR = true, semaphoreL = true;
	
	private SharedPreferences prefs;
	public int calibXH, calibYH, calibZH, calibXV, calibYV, calibZV, calibIR;
	// ----------------------------------------- HYBRIDPLAY SENSOR
	
	String playWith;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
		WindowManager w = getWindowManager();
	    Display d = w.getDefaultDisplay();
	    width = d.getWidth();
	    height = d.getHeight();
	   
	    super.onCreate(savedInstanceState);
	    
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
        _View = new OpenGLView(this, width, height);
        setContentView(_View);
        
        // get the game type (affect different sensor readings)
        Bundle extras = getIntent().getExtras();
        playWith = extras.getString("gameType");
        
        _CView = new CanvasView(this,playWith);
        //ViewGroup.LayoutParams mOverlayViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //addContentView(_CView, mOverlayViewParams );
        
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
    		    if(playWith.equals("Balancin")){
    				// pinza horizontal - cuatro direcciones - ejes Z Y
    		    	if (triggerZR && semaphoreR) { // RIGHT
    		    		semaphoreR = false;
    		    		_View.onSensorEvent(width-10);
    		    	}else if (triggerZL && semaphoreL) { // LEFT
    		    		semaphoreL = false;
    		    		_View.onSensorEvent(10);
    		    	}
    		    	
    		    	if(!triggerZR){
    		    		semaphoreR = true;
    		    	}
    		    	if(!triggerZL){
    		    		semaphoreL = true;
    		    	}
    		    	
          		}else if(playWith.equals("Caballito")){
          			// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
          			if (triggerXR && semaphoreR) { // RIGHT
          				semaphoreR = false;
          				_View.onSensorEvent(width-10);
    		    	}else if (triggerXL && semaphoreL) { // LEFT
    		    		semaphoreL = false;
    		    		_View.onSensorEvent(10);
    		    	}
          			
          			if(!triggerXR){
    		    		semaphoreR = true;
    		    	}
    		    	if(!triggerXL){
    		    		semaphoreL = true;
    		    	}
          			
          		}else if(playWith.equals("Columpio")){
          			// pinza vertical boton hacia abajo - oscilaci�n - eje X

          		}else if(playWith.equals("SubeBaja")){
          			// pinza horizontal - dos direcciones - eje Z
          			
          		}else if(playWith.equals("Tobogan")){
          			// we use here only IR sensor

          		}
    		    
    		    //_CView.updateSensor(triggerXL, triggerXR, triggerYL, triggerYR, triggerZL, triggerZR);
    		    
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
    public void onPause() {
    	_View.onPause();
    	unregisterReceiver(mReceiver);
    	super.onPause();
    }
    
    @Override
    public void onResume() {
    	if(!_FocusChangeFalseSeen)
    	{
    		_View.onResume();
    	}
    	_Resume = true;
    	this.mReceiver = new SensorReceiver();
		registerReceiver(this.mReceiver,new IntentFilter("com.hybridplay.SENSOR"));
    	super.onResume();
    }
    
    @Override
    public void onWindowFocusChanged(boolean focus) {
    	if(focus){
    		if(_Resume){
    			_View.onResume();
    		}
    		
    		_Resume = false;
    		_FocusChangeFalseSeen = false;
    	}else{
    		_FocusChangeFalseSeen = true;
    	}
    }   
    
    //open menu when key pressed
     public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
        	this.startActivity(new Intent(this, Preferences.class));
        }
        return super.onKeyUp(keyCode, event);
    }
}