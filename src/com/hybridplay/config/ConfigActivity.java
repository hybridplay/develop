package com.hybridplay.config;

import com.hybridplay.app.R;
import com.hybridplay.app.SensorReceiver;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

public class ConfigActivity extends Activity {
	
	SensorReceiver mReceiver;
	Handler handler = new Handler();
	    
	TextView m_deviceName;
	TextView m_deviceStatus;
	BarView mBarGraph;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		m_deviceName = (TextView)findViewById(R.id.deviceName);
        m_deviceStatus = (TextView)findViewById(R.id.deviceStatus);
        mBarGraph = (BarView) findViewById(R.id.bargraph);
        
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

}
