package com.hybridplay.pong;

import java.util.ArrayList;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.hybridplay.app.R;
import com.hybridplay.bluetooth.BluetoothThread;
import com.hybridplay.bluetooth.MySensor;

public class GameActivityPong extends Activity implements SensorEventListener {
	private PongView mPongView;
	private AlertDialog mAboutBox;
	protected PowerManager.WakeLock mWakeLock;
	
	private SensorManager mySensorManager;
	private Sensor myAccelerometer;
	
	//BLUETOOTH
	// Message types 
	static final int BLUETOOTH_CONNECTED = 1;
	static final int BLUETOOTH_DISCONNECTED = 2;
	static final int BLUETOOTH_RECEIVED = 3;
	
	Handler m_handler;
	BluetoothThread m_thread;
	StringBuffer inputString = new StringBuffer("");
	static final String LOGGER_TAG = "GameBTActivity";
	
	//hybridPlay sensor
	private MySensor mSensorX, mSensorY, mSensorZ, mSensorIR;
	public String playWith;
	public int dWidth, dHeight;
	public float mappedSensor;
	
	public static final String
		EXTRA_RED_PLAYER = "red-is-player",
		EXTRA_BLUE_PLAYER = "blue-is-player";
	
	
    @SuppressLint("HandlerLeak")
	@Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
      //start bluetooth
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
        	Log.i(LOGGER_TAG, "Start new DeviceThread");
        	m_thread = new BluetoothThread(m_handler, device);
        	m_thread.start();
        }
        
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorManager.registerListener(this, myAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        
        //end bluetooth
        
        // Init sensors for hybriplay
        mSensorX = new MySensor("x");
        mSensorY = new MySensor("y");
        mSensorZ = new MySensor("z");
        mSensorIR = new MySensor("IR");
        
        Display display = getWindowManager().getDefaultDisplay();
        dWidth = display.getWidth();
        dHeight = display.getHeight();
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        setContentView(R.layout.pong_view);
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
    }
    
    protected void onStop() {
    	super.onStop();
		mPongView.stop();
    }
    
    protected void onResume() {
    	super.onResume();
    	mPongView.resume();
    }
    
    protected void onDestroy() {
    	if (m_thread != null && m_thread.isAlive()) {
			m_thread.cancel();
		}
		
		mySensorManager.unregisterListener(this);
    	super.onDestroy();
    	mPongView.release();
    	mWakeLock.release();
    }
   
    public void hideAboutBox() {
    	if(mAboutBox != null) {
    		mAboutBox.hide();
    		mAboutBox = null;
    	}
    }
    
    private ArrayList<BluetoothDevice> initBluetooth(){
    	 
    	ArrayList<BluetoothDevice> m_devices = new ArrayList<BluetoothDevice>();
    	ArrayList<String> m_deviceNames = new ArrayList<String>();
    	m_deviceNames.add("No devices");
        
		// Enable Bluetooth
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
        	Log.i(LOGGER_TAG, "Found Bluetooth adapter");
        	if (!adapter.isEnabled()) {
        		Log.i(LOGGER_TAG, "Bluetooth disabled, launch enable intent");
        		//Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        		//startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        	}
        	if (adapter.isEnabled()) {
        		Log.i(LOGGER_TAG, "Bluetooth enabled, find paired devices");
        		Set<BluetoothDevice> devices = adapter.getBondedDevices();
        		if (!devices.isEmpty()) {
        			m_devices.clear();
        			//m_arrayAdapter.clear();
        			for (BluetoothDevice device : devices) {
        				Log.i(LOGGER_TAG, String.format("Found bluetooth device: name %s", device.getName()));
        				m_devices.add(device);
        				//m_arrayAdapter.add(device.getName());
        			}
        		}
        		return m_devices;
        	}
        }
        return null;
    }
    
	public BluetoothDevice connectDevice(String name,ArrayList<BluetoothDevice> m_devices) {
		Log.i(LOGGER_TAG, "Connect to device: "+ name);
		for (int x =0; x < m_devices.size(); x++){
			Log.i(LOGGER_TAG, "device pared: "+ m_devices.get(x).getName());
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
		Log.i(LOGGER_TAG, "Bluetooth connected");
		//m_deviceStatus.setText("Connected");
		//m_thread.sendBlinkCommand();
	}
	
	private void onBluetoothDisconnected() {
		Log.i(LOGGER_TAG, "Bluetooth disconnected");
		//m_deviceStatus.setText("Disconnected");
	}
	
	
	private void onBluetoothRead(byte[] buffer, int len) {
		//Log.i(LOGGER_TAG, String.format("Received %d bytes", len));
		String inputStr = new String(buffer, 0, len);
		char HEADER = 'H'; // character to identify the start of a message
		
		inputString.append(inputStr);
		
		if(inputString.toString().contains("\n") && inputString.toString().contains("H")){
			//	("log", "detectado final de carro");
			if (inputString.toString().indexOf("H") < inputString.toString().indexOf("\n")){
				//Log.d("log",inputString.toString()); // imprimimos el inputString
				String dataTmp = inputString.toString().substring(inputString.toString().indexOf("H"));
				dataTmp = dataTmp.substring(0, dataTmp.indexOf("\n"));
				String [] data = dataTmp.split(","); // Split the comma-separated message
				inputString.delete(0, inputString.length()); //borramos el inputString
				if(data[0].charAt(0) == HEADER && data.length > 5) { // si empieza por H y tiene tres grupos de datos
					try{
						mSensorX.update(Integer.parseInt(data[1]));
						mSensorY.update(Integer.parseInt(data[2]));
						mSensorZ.update(Integer.parseInt(data[3]));
						mSensorIR.update(Integer.parseInt(data[4]));
						
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
  			Thread.sleep(30);
  		} catch (InterruptedException e) {
  			e.printStackTrace();
  		}
  		
  		if(playWith.equals("Balancin")){
  			// pinza horizontal - cuatro direcciones - ejes Z Y
  			mappedSensor = map((float)mSensorZ.getActualValue(),0f,(float)mSensorZ.getMaxValue(),0f,(float)dWidth);
  			if (mSensorZ.isFireMaxActive()) {
  				mPongView.movePaddle(1); // RIGHT
			}else if (mSensorZ.isFireMinActive()) { // LEFT
				mPongView.movePaddle(0);
			}
  			//mPongView.paddleGoTo(mappedSensor);
  		}else if(playWith.equals("Caballito")){
  			// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
  			mappedSensor = map((float)mSensorX.getActualValue(),0f,(float)mSensorX.getMaxValue(),0f,(float)dWidth);
  			if (mSensorX.isFireMaxActive()) {
  				mPongView.movePaddle(1); // RIGHT
			}else if (mSensorX.isFireMinActive()) { // LEFT
				mPongView.movePaddle(0);
			}
  		}else if(playWith.equals("Columpio")){
  			// pinza vertical boton hacia abajo - oscilaci�n - eje X
  			
  		}else if(playWith.equals("SubeBaja")){
  			// pinza horizontal - dos direcciones - eje Z
  			mappedSensor = map((float)mSensorZ.getActualValue(),0f,(float)mSensorZ.getMaxValue(),0f,(float)dWidth);
  			if (mSensorZ.isFireMaxActive()) {
  				mPongView.movePaddle(1); // RIGHT
			}else if (mSensorZ.isFireMinActive()) { // LEFT
				mPongView.movePaddle(0);
			}
  		}else if(playWith.equals("Tobogan")){
  			// we use here only IR sensor
  			
  		}
  		
  	}
	
	public float map(float value, float inputMin, float inputMax, float outputMin, float outputMax){
		if (Math.abs(inputMin - inputMax) < Float.MIN_VALUE){
			return outputMin;
		} else {
			float outVal = ((value - inputMin) / (inputMax - inputMin) * (outputMax - outputMin) + outputMin);
		
			if(outputMax < outputMin){
				if( outVal < outputMax )outVal = outputMax;
				else if( outVal > outputMin )outVal = outputMin;
			}else{
				if( outVal > outputMax )outVal = outputMax;
				else if( outVal < outputMin )outVal = outputMin;
			}
			
			return outVal;
		}
	}
	
}