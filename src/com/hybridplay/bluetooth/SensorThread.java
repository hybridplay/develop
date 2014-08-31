package com.hybridplay.bluetooth;

import android.util.Log;

public class SensorThread extends Thread {
private static final String TAG = SensorThread.class.getSimpleName();
	
	BluetoothService service;
	// flag to hold game state 
	boolean running;
	// sensor data
	int aX, aY, aZ, bat, IR;
	
	public void setRunning(boolean running) {
		this.running = running;
	}

	public SensorThread(BluetoothService s) {
		super();
		service = s;
	}
	
	@Override
	public void run() {
		Log.d(TAG, "Starting Sensor loop");
		while (running) {
			try{
				// update sensor readings
				aX = service.getAccX();
				aY = service.getAccY();
				aZ = service.getAccZ();
				bat = service.getBattery();
				IR = service.getIR();
				Thread.sleep(80); // set time here to refresh (80 ms => 12 FPS)
			}catch(InterruptedException e){
				
			}
		}
	}
	
	public boolean isRunning(){
		return running;
	}
	
	public int getX(){
		return aX;
	}
	
	public int getY(){
		return aY;
	}
	
	public int getZ(){
		return aZ;
	}
	
	public int getBat(){
		return bat;
	}
	
	public int getIR(){
		return IR;
	}
}
