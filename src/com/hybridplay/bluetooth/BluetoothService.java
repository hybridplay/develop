package com.hybridplay.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class BluetoothService extends Service {

	static final String BLUETOOTH_DEVICE_NAME = "HYBRIDPLAY";
	static final String BLUETOOTH_SERVICE_TAG = "BluetoothService";
	static final int BLUETOOTH_CONNECTED = 1;
	static final int BLUETOOTH_DISCONNECTED = 2;
	static final int BLUETOOTH_RECEIVED = 3;
	
	char HEADER = 'H';
	
	ArrayList<BluetoothDevice> m_devices;
	
	Handler m_handler;
	DeviceThread m_thread;
	boolean bluetoothConnected = false;
	
	String m_deviceName;
	String m_deviceStatus;
	int accX, accY, accZ, bat, IR;
	
	// Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    
    public class LocalBinder extends Binder {
    	public BluetoothService getService() {
            // Return this instance of BluetoothService so clients can call public methods
            return BluetoothService.this;
        }
    }
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return false;
    }
	
	@Override
	public void onDestroy() {
		
	}
	
	public void stopBluetoothService(){
		stopSelf();
	}
	
	@SuppressLint("HandlerLeak")
	private void connectDevice(String name) {
		// Setup the device list
        m_devices = new ArrayList<BluetoothDevice>();
        
        m_handler = new Handler() {
        	public void handleMessage(Message msg) {
        		onMessage(msg);
        	}
        };
        
        // Enable Bluetooth
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
        	Log.i(BLUETOOTH_SERVICE_TAG, "Found Bluetooth adapter");
        	if (!adapter.isEnabled()) {
        		Log.i(BLUETOOTH_SERVICE_TAG, "Bluetooth disabled, launch enable intent");
        		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        		enableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		this.startActivity(enableIntent);
        	}
        	if (adapter.isEnabled()) {
        		Log.i(BLUETOOTH_SERVICE_TAG, "Bluetooth enabled, find paired devices");
        		Set<BluetoothDevice> devices = adapter.getBondedDevices();
        		if (!devices.isEmpty()) {
        			m_devices.clear();
        			for (BluetoothDevice device : devices) {
        				Log.i(BLUETOOTH_SERVICE_TAG, String.format("Found bluetooth device: name %s", device.getName()));
        				m_devices.add(device);
        			}
        		}
        	}
        	Log.i(BLUETOOTH_SERVICE_TAG, "Connect to device: "+ name);
    		for (int x =0; x < m_devices.size(); x++){
    			if (m_devices.get(x).getName().equals(name)){
    				BluetoothDevice device = m_devices.get(x);
    				if (device != null) {
    		        	Log.i(BLUETOOTH_SERVICE_TAG, "Start new DeviceThread");
    		        	m_deviceName = name;
    		        	m_thread = new DeviceThread(m_handler, device);
    		        	m_thread.start();
    		        	bluetoothConnected = true;
    		        }
    				break;
    			}
    		}
        }
		
	}
	
	public void disconnectDevice() {
		m_thread.disconnect();
	}
	
	// Called by our handler
    private void onMessage(Message msg) {
    	switch (msg.what) {
    	case BLUETOOTH_CONNECTED:
    		onBluetoothConnected();
    		break;
    	case BLUETOOTH_DISCONNECTED:
    		onBluetoothDisconnected();
    		break;
    	case BLUETOOTH_RECEIVED:
			byte[] buffer = (byte[])msg.obj;
			int len = msg.arg1;
			if (len > 0 && buffer != null) {
				onBluetoothRead(buffer, len);
			}
			break;
    	}
    }
	
	private void onBluetoothConnected() {
		Log.i(BLUETOOTH_SERVICE_TAG, "Bluetooth connected");
		m_deviceStatus = "Connected";
	}
	
	private void onBluetoothDisconnected() {
		Log.i(BLUETOOTH_SERVICE_TAG, "Bluetooth disconnected");
		m_deviceStatus = "Disconnected";
	}
	
	
	private void onBluetoothRead(byte[] buffer, int len) {
		String inputStr = new String(buffer, 0, len);
		String [] data = inputStr.split(",");
		if(len >= 11){ // 11 bytes from Arduino (1 HEADER, 2 ACCX, 2 ACCY, 2 ACCZ, 2 BATTERY, 2 IR)
			if(buffer[0] == HEADER){
				accX = readArduinoBinary(buffer[1],buffer[2]);
				accY = readArduinoBinary(buffer[3],buffer[4]);
				accZ = readArduinoBinary(buffer[5],buffer[6]);
				bat = readArduinoBinary(buffer[7],buffer[8]);
				//IR	= readArduinoBinary(buffer[9],buffer[10]);
				try{
					if(Integer.parseInt(data[1]) > 200){
						IR = Integer.parseInt(data[1]);
					}
				}catch(Exception e){
					Log.d("log error",e.toString());
				}
			}
		}

	}
	
	private int readArduinoBinary(byte least, byte most){
		int val;
		
		val = least;
		val = most*256 + val;
		
		return val;
		
	}
	
	/** methods for clients */
	public void initBluetoothService(){
		connectDevice(BLUETOOTH_DEVICE_NAME);
	}
	
	public int getAccX(){
		return accX;
	}
	
	public int getAccY(){
		return accY;
	}

	public int getAccZ(){
		return accZ;
	}
	
	public int getBattery(){
		return bat;
	}

	public int getIR(){
		return IR;
	}
	
	public String getDeviceName(){
		return m_deviceName;
	}
	
	public String getDeviceStatus(){
		return m_deviceStatus;
	}
	
	public boolean getBluetoothConnected(){
		return bluetoothConnected;
	}
    
	/* Custom Threaded Class for Bluetooth Device*/
    private class DeviceThread extends Thread {
		Handler m_handler;
		BluetoothDevice m_device;
		BluetoothSocket m_socket;
		//OutputStream m_output;
		InputStream m_input;
		
		DeviceThread(Handler handler, BluetoothDevice device) {
			m_handler = handler;
			m_device = device;
			Log.d(BLUETOOTH_SERVICE_TAG, "DeviceThread running");
			Log.d(BLUETOOTH_SERVICE_TAG, String.format("Received device: %s", device.getName()));
		}
				
		private void connect() {
			try {
	        	UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        		Log.d(BLUETOOTH_SERVICE_TAG, "Create BluetoothSocket");
				m_socket = m_device.createRfcommSocketToServiceRecord(uuid);
        		Log.d(BLUETOOTH_SERVICE_TAG, "Connect BluetoothSocket");
				m_socket.connect();
				m_handler.obtainMessage(BLUETOOTH_CONNECTED).sendToTarget();
				//m_output = m_socket.getOutputStream();
				m_input = m_socket.getInputStream();
			} catch (IOException e) {
				Log.d(BLUETOOTH_SERVICE_TAG, String.format("Caught IOException e: %s", e.toString()));
				m_socket = null;
				m_handler.obtainMessage(BLUETOOTH_DISCONNECTED).sendToTarget();
			}
		}
		
		private void disconnect() {
			try {
				m_socket.close();
			} catch (IOException e) {
				Log.d(BLUETOOTH_SERVICE_TAG, String.format("Caught IOException e: %s", e.toString()));
			}
		}
		
		public void run() {
			// Connect to the socket
			connect();
			
			// Now loop, listening to the socket, issuing callbacks whenever we receive data
			byte[] buffer = new byte[1024];
			while (m_socket != null) {
				try {
					int len = m_input.read(buffer);
					if (len > 0) {
						m_handler.obtainMessage(BLUETOOTH_RECEIVED, len, -1, buffer).sendToTarget();
					}
				} catch (IOException e) {
					Log.d(BLUETOOTH_SERVICE_TAG, String.format("Caught IOException e: %s", e.toString()));
					m_socket = null;
					m_handler.obtainMessage(BLUETOOTH_DISCONNECTED).sendToTarget();
				}
			}
		}
	}

}
