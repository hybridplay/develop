package com.hybridplay.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SensorReceiver extends BroadcastReceiver {

public static final String SENSOR_DATA_INTENT = "com.hybridplay.SENSOR";
	
	public int AX = 0, AY = 0, AZ = 0, IR = 0;
	public String deviceName, deviceStatus;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(SENSOR_DATA_INTENT)) {
			AX = intent.getIntExtra("AX",0);
			AY = intent.getIntExtra("AY",0);
			AZ = intent.getIntExtra("AZ",0);
			IR = intent.getIntExtra("IR",0);
			
			deviceName = intent.getStringExtra("name");
			deviceStatus = intent.getStringExtra("status");
		}

	}

}
