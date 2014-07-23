package com.hybridplay.arkanoid;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class TiltListener implements SensorEventListener{

	private final int DEADSPOT = 5;
	private final double TILTSPEED = 3.0;
	private boolean on = false;
	private SensorManager sensorManager;
	private List<Sensor> sensors;
	private Sensor sensor;
	private long lastUpdate = -1;
	private long currentTime = -1;
	private enum Tilt {NONE, LEFT, RIGHT};
	private Tilt tilt = Tilt.NONE;
	
	
	private ArkaNoidGameThread gameThread;

	public TiltListener(Activity parent, ArkaNoidGameThread gameThread) {

		SensorManager sensorService = (SensorManager) parent.getSystemService(Context.SENSOR_SERVICE);

		this.sensorManager = sensorService;
		this.gameThread = gameThread;
		this.sensors = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);

		if (sensors.size() > 0) {
			sensor = sensors.get(0);
		}

	}

	public void start() {
		if (sensor != null) {
			sensorManager.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_GAME);
			on = true;
		}
	}

	public void stop() {
		sensorManager.unregisterListener(this);
		on = false;
	}
	
	public boolean isOn(){
		return on;
	}

	public void onAccuracyChanged(Sensor s, int valu) {

	}

	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.getType() != Sensor.TYPE_ORIENTATION  || event.values.length < 3) return;

		currentTime = System.currentTimeMillis();

		if ((currentTime - lastUpdate) > 100) {
			lastUpdate = currentTime;
			double absvalue = Math.abs(event.values[2]);
			double speed = 0.0;
			boolean left = false;
			boolean right = false;
			if (absvalue > DEADSPOT) {
				if (event.values[2] > 0){
					if (tilt==Tilt.RIGHT) right = false;
					left = true;
					speed = (absvalue-DEADSPOT)/TILTSPEED;
					tilt = Tilt.LEFT;
				}
				else{
					if (tilt==Tilt.LEFT) left = false;
					right = true;
					speed = (absvalue-DEADSPOT)/TILTSPEED;
					tilt = Tilt.RIGHT;
				}
				gameThread.changedBoth(speed, left, right);
				
			} else {
				if (tilt==Tilt.LEFT) gameThread.changedLeft(0, false);
				else if (tilt==Tilt.RIGHT) gameThread.changedRight(0, false);
				tilt = Tilt.NONE;
			}
		}
	}
}
