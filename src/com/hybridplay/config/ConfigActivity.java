package com.hybridplay.config;

import com.hybridplay.app.R;
import com.hybridplay.app.SensorReceiver;

import android.app.Activity;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ConfigActivity extends Activity implements OnClickListener {
	
	SensorReceiver mReceiver;
	Handler handler = new Handler();
	    
	TextView m_deviceName;
	TextView m_deviceStatus;
	BarView mBarGraph;
	Button calibrateButH,calibrateButV,calibrateIR;
	ToggleButton tButton;
	
	// Sensor visualization
	Display display;
	DisplayMetrics metrics;
	ImageView sUP, sDOWN, sLEFT, sRIGHT;
	
	private SharedPreferences prefs;
	public int calibXH, calibYH, calibZH, calibXV, calibYV, calibZV, calibIR;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		m_deviceName = (TextView)findViewById(R.id.deviceName);
        m_deviceStatus = (TextView)findViewById(R.id.deviceStatus);
        mBarGraph = (BarView) findViewById(R.id.bargraph);
        
        calibrateButH = (Button) findViewById(R.id.button1);
        calibrateButV = (Button) findViewById(R.id.button2);
        calibrateIR = (Button) findViewById(R.id.button3);
        calibrateButH.setOnClickListener(this);
        calibrateButV.setOnClickListener(this);
        calibrateIR.setOnClickListener(this);
        
        tButton = (ToggleButton)findViewById(R.id.toggleButton1);
        tButton.setOnClickListener(this);
        
        metrics = new DisplayMetrics();
		display = getWindowManager().getDefaultDisplay();
		display.getMetrics(metrics);
		
		updateCalibration();
		
		mBarGraph.mSensorXCalib.applyVCalibration();
        mBarGraph.mSensorYCalib.applyVCalibration();
        mBarGraph.mSensorZCalib.applyVCalibration();
        
        handler.post(new Runnable(){
        	@Override
        	public void run() {
        		// update sensor readings
				mBarGraph.drawBar(mReceiver.AX,mReceiver.AY,mReceiver.AZ,mReceiver.IR);
				m_deviceName.setText(mReceiver.deviceName);
				m_deviceStatus.setText(mReceiver.deviceStatus);
        		handler.postDelayed(this,40); // set time here to refresh (40 ms => 12 FPS)
        	}
        });
	}

	@Override
	protected void onPause(){
		unregisterReceiver(mReceiver);
		super.onPause();
	}
	
	@Override
	protected void onResume(){
		this.mReceiver = new SensorReceiver();
		registerReceiver(this.mReceiver,new IntentFilter("com.hybridplay.SENSOR"));
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			calibrateSensorH();
			break;
		case R.id.button2:
			calibrateSensorV();
			break;
		case R.id.button3:
			calibrateIR();
			break;
		case R.id.toggleButton1:
			if(tButton.isChecked()){ // H
				mBarGraph.mSensorXCalib.applyHCalibration();
		        mBarGraph.mSensorYCalib.applyHCalibration();
		        mBarGraph.mSensorZCalib.applyHCalibration();
			}else{ // V
				mBarGraph.mSensorXCalib.applyVCalibration();
		        mBarGraph.mSensorYCalib.applyVCalibration();
		        mBarGraph.mSensorZCalib.applyVCalibration();
			}
			break;
		}
	}
	
	void updateCalibration(){
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
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
        
        mBarGraph.mSensorXCalib.getCalibration(calibXH, calibXV);
        mBarGraph.mSensorYCalib.getCalibration(calibYH, calibYV);
        mBarGraph.mSensorZCalib.getCalibration(calibZH, calibZV);
	}
	
	void calibrateIR(){
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("calibratedIR","1");
		editor.putInt("calIR",mReceiver.IR);
		editor.commit();
		
		Toast.makeText(this, "Sensor IR calibrated, MAX LIMIT: "+mReceiver.IR,Toast.LENGTH_LONG).show();
	}
	
	void calibrateSensorH(){
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("calibratedAH","1");
		editor.putInt("accXH",mReceiver.AX);
		editor.putInt("accYH",mReceiver.AY);
		editor.putInt("accZH",mReceiver.AZ);
		editor.commit();
		
		updateCalibration();
		
		Toast.makeText(this, "Sensor H calibrated with values: X "+mBarGraph.mSensorXCalib.calibH+", Y "+mBarGraph.mSensorYCalib.calibH+", Z "+mBarGraph.mSensorZCalib.calibH,Toast.LENGTH_LONG).show();
	}
	
	void calibrateSensorV(){
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("calibratedAV","1");
		editor.putInt("accXV",mReceiver.AX);
		editor.putInt("accYV",mReceiver.AY);
		editor.putInt("accZV",mReceiver.AZ);
		editor.commit();
		
		updateCalibration();
		
		Toast.makeText(this, "Sensor V calibrated with values: X "+mBarGraph.mSensorXCalib.calibV+", Y "+mBarGraph.mSensorYCalib.calibV+", Z "+mBarGraph.mSensorZCalib.calibV,Toast.LENGTH_LONG).show();
	}
	
	int dpToPixels(int dps){
		 int pixels = (int) (dps * metrics.density + 0.5f);
		 return pixels;
	 }

}
