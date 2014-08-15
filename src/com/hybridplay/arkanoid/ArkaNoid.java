package com.hybridplay.arkanoid;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.hybridplay.app.R;
import com.hybridplay.arkanoid.controllers.SoundController;
import com.hybridplay.bluetooth.BluetoothThread;
import com.hybridplay.bluetooth.MySensor;

public class ArkaNoid extends Activity implements SensorEventListener  {

	private static final int MENU_START = 0;
	private static final int MENU_TILT = 1;
	private ArkaNoidView arkaDroidView;
	private ArkaNoidGameThread gameThread;
	private TiltListener tiltListener;
	
	private SensorManager mySensorManager;
	private Sensor myAccelerometer;
	
	//hybridPlay sensor
	public MySensor mSensorX, mSensorY, mSensorZ, mSensorIR;
	
	// BLUETOOTH BLOCK
	// Message types 
	static final int BLUETOOTH_CONNECTED = 1;
	static final int BLUETOOTH_DISCONNECTED = 2;
	static final int BLUETOOTH_RECEIVED = 3;
	private final static int REQUEST_ENABLE_BT = 1;

	static final String BLUETOOTH_TAG = "Bluetooth Activity";

	Handler m_handler;
	BluetoothThread m_thread;
	StringBuffer inputString = new StringBuffer("");
	String playWith;
	private final double PADDLE_SPEED = 4.0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// ------------------------------- start bluetooth
        m_handler = new Handler() {
        	public void handleMessage(Message msg) {
        		onMessage(msg);
        	}
        };
        
        // Get the device
        ArrayList<BluetoothDevice> m_devices = initBluetooth(); //inicializamos el BT y devolvemos lista de devices pareados
        BluetoothDevice device = connectDevice("HYBRIDPLAY", m_devices); //directly connect to our sensor previously pared
         
        // Start the thread
        if (device != null) {
        	Log.i(BLUETOOTH_TAG, "Start new DeviceThread");
        	m_thread = new BluetoothThread(m_handler, device);
        	m_thread.start();
        }
        
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorManager.registerListener(this, myAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        
        // create sensors for hybriplay
        mSensorX = new MySensor("x");
        mSensorY = new MySensor("y");
        mSensorZ = new MySensor("z");
        mSensorIR = new MySensor("IR");
        // ------------------------------- end bluetooth

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
	
	private ArrayList<BluetoothDevice> initBluetooth(){
		 
    	ArrayList<BluetoothDevice> m_devices = new ArrayList<BluetoothDevice>();
    	ArrayList<String> m_deviceNames = new ArrayList<String>();
    	m_deviceNames.add("No devices");
        
		// Enable Bluetooth
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
        	Log.i(BLUETOOTH_TAG, "Found Bluetooth adapter");
        	if (!adapter.isEnabled()) {
        		Log.i(BLUETOOTH_TAG, "Bluetooth disabled, launch enable intent");
        		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        	}
        	if (adapter.isEnabled()) {
        		Log.i(BLUETOOTH_TAG, "Bluetooth enabled, find paired devices");
        		Set<BluetoothDevice> devices = adapter.getBondedDevices();
        		if (!devices.isEmpty()) {
        			m_devices.clear();
        			for (BluetoothDevice device : devices) {
        				Log.i(BLUETOOTH_TAG, String.format("Found bluetooth device: name %s", device.getName()));
        				m_devices.add(device);
        			}
        		}
        		return m_devices;
        	}
        }
        return null;
    }
    
	public BluetoothDevice connectDevice(String name,ArrayList<BluetoothDevice> m_devices) {
		Log.i(BLUETOOTH_TAG, "Connect to device: "+ name);
		for (int x =0; x < m_devices.size(); x++){
			Log.i(BLUETOOTH_TAG, "device pared: "+ m_devices.get(x).getName());
			if (m_devices.get(x).getName().equals(name)){
				BluetoothDevice device = m_devices.get(x);
				return device;
			}
		}
		return null;
	}
        
    
	// Called by our handler
    public void onMessage(Message msg) {
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
    	Log.i(BLUETOOTH_TAG, "Bluetooth connected");
    	gameThread.setGameConnecter(true);
	}
	
	private void onBluetoothDisconnected() {
		Log.i(BLUETOOTH_TAG, "Bluetooth disconnected");
		gameThread.setGameConnecter(false);
	}
	
	private void onBluetoothRead(byte[] buffer, int len) {
		String inputStr = new String(buffer, 0, len);
		char HEADER = 'H'; // character to identify the start of a message
		
		inputString.append(inputStr);
		
		if(inputString.toString().contains("\n") && inputString.toString().contains("H")){
			if (inputString.toString().indexOf("H") < inputString.toString().indexOf("\n")){
				String dataTmp = inputString.toString().substring(inputString.toString().indexOf("H"));
				dataTmp = dataTmp.substring(0, dataTmp.indexOf("\n"));
				String [] data = dataTmp.split(","); // Split the comma-separated message
				inputString.delete(0, inputString.length()); //borramos el inputString
				if(data[0].charAt(0) == HEADER && data.length > 5) { // si empieza por H y tiene tres grupos de datos
					try{
						getmSensorX().update(Integer.parseInt(data[1]));
						getmSensorY().update(Integer.parseInt(data[2]));
						getmSensorZ().update(Integer.parseInt(data[3]));
						getmSensorIR().update(Integer.parseInt(data[4]));
					}
					catch(Exception e){ 
						Log.d("log error",e.toString());
					}
				}
			}else {
				while (!inputString.toString().startsWith("H")){
					inputString.delete(0,1);//borramos un caracter hasta llegar a la H en primer lugar
				}
			}
			
		}

	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	//get values of accelerometer
  	public void onSensorChanged(SensorEvent event) {
  		
  		try {
  			Thread.sleep(16);
  		} catch (InterruptedException e) {
  			e.printStackTrace();
  		}
  		
  		if(playWith.equals("Balancin")){
  			// pinza horizontal - cuatro direcciones - ejes Z Y
  			if (mSensorZ.isFireMaxActive()) { // RIGHT
  				gameThread.changedBoth(PADDLE_SPEED, false, true);
			}else if (mSensorZ.isFireMinActive()) { // LEFT
				gameThread.changedBoth(PADDLE_SPEED, true, false);
			}
  		}else if(playWith.equals("Caballito")){
  			// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
  			if (mSensorX.isFireMaxActive()) {
  				gameThread.changedBoth(PADDLE_SPEED, false, true);
			}else if (mSensorX.isFireMinActive()) { // LEFT
				gameThread.changedBoth(PADDLE_SPEED, true, false);
			}
  		}else if(playWith.equals("Columpio")){
  			// pinza vertical boton hacia abajo - oscilaci�n - eje X
  			
  		}else if(playWith.equals("SubeBaja")){
  			// pinza horizontal - dos direcciones - eje Z
  			if (mSensorZ.isFireMaxActive()) {
  				gameThread.changedBoth(PADDLE_SPEED, false, true);
			}else if (mSensorZ.isFireMinActive()) { // LEFT
				gameThread.changedBoth(PADDLE_SPEED, true, false);
			}
  		}else if(playWith.equals("Tobogan")){
  			// we use here only IR sensor
  			
  		}
  		
  	}
	
	protected void onStop() {
    	super.onStop();
    }
	
	@Override
	protected void onPause() {
		super.onPause();
		arkaDroidView.getGameThread().pause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		if (m_thread != null && m_thread.isAlive()) {
			m_thread.cancel();
		}
		mySensorManager.unregisterListener(this);
		super.onDestroy();
	}
	
	@Override
	public void finish() {
		super.finish();
	}
	
	public MySensor getmSensorX() {
		return mSensorX;
	}

	public void setmSensorX(MySensor mSensorX) {
		this.mSensorX = mSensorX;
	}

	public MySensor getmSensorY() {
		return mSensorY;
	}

	public void setmSensorY(MySensor mSensorY) {
		this.mSensorY = mSensorY;
	}

	public MySensor getmSensorZ() {
		return mSensorZ;
	}

	public void setmSensorZ(MySensor mSensorZ) {
		this.mSensorZ = mSensorZ;
	}

	public MySensor getmSensorIR() {
		return mSensorIR;
	}

	public void setmSensorIR(MySensor mSensorIR) {
		this.mSensorIR = mSensorIR;
	}
	
}
