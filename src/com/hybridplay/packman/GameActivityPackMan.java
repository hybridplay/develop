package com.hybridplay.packman;

import java.util.ArrayList;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;

import com.hybridplay.bluetooth.BluetoothThread;

public class GameActivityPackMan extends Activity implements SensorEventListener{
	final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8, CENTER = 0;
	public Boolean connected = false;
	private GameSurfaceView gameView;
	private SensorManager mySensorManager;
	private Sensor myAccelerometer;
	private boolean isPriorityY;
	
	//change in x and y of pac-mon
	private float xAccel;
	private float yAccel;
	private GameEngine gameEngine;
	private SoundEngine soundEngine;
	
	//BLUETOOTH
	// Message types 
	static final int BLUETOOTH_CONNECTED = 1;
	static final int BLUETOOTH_DISCONNECTED = 2;
	static final int BLUETOOTH_RECEIVED = 3;
	
	Handler m_handler;
	BluetoothThread m_thread;
	StringBuffer inputString = new StringBuffer("");
	// Logger name (there's probably a better way to organize this)
	static final String LOGGER_TAG = "GameBTActivity";

	
    /** Called when the activity is first created. */
    @SuppressLint("HandlerLeak")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
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
        
        //end bluetooth
        
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorManager.registerListener(this, myAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    
        int level = getIntent().getIntExtra("level", 1);

        soundEngine = new SoundEngine(this);
        gameEngine = new GameEngine(soundEngine, level);
        
        
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        gameView = new GameSurfaceView(this, gameEngine, width, height);
        
        // get the game type (affect different sensor readings)
        Bundle extras = getIntent().getExtras();
        String playWith = extras.getString("gameType");
        gameEngine.setGameType(playWith);

        setContentView(gameView);
       
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
        		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
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


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		gameEngine.pause();
		gameView.pause();
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		gameEngine.resume();
		gameView.resume();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (m_thread != null && m_thread.isAlive()) {
			m_thread.cancel();
		}
		
		mySensorManager.unregisterListener(this);
		super.onDestroy();
		//gameView.pause();
	}
	

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
		 Intent intent = new Intent();

			 intent.putExtra("level", gameEngine.level);
		     intent.putExtra("status", gameEngine.status);

		        setResult(RESULT_OK, intent);
		  
		soundEngine.endMusic();
		super.finish();
	}


	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you want to quit?").setCancelable(false)
				.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						GameActivityPackMan.this.finish();
					}
				})
				.setNegativeButton("Resume", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	//get values of accelerometer
	public void onSensorChanged(SensorEvent event) {
		
		try {
			Thread.sleep(16);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		 * nota: aqui hay que ajustar que nuestro sensor puede tener direcciones compuestas, 
		 * ejemplo arriba y derecha / arriba e izquierda
		 * SOLUCION> que activela predominante, en base a ???
		 */
		
		if(gameEngine.getmSensorY().getDistance() < gameEngine.getmSensorX().getDistance()){
			isPriorityY = false;
		}else{
			isPriorityY = true;
		}
			
		if(gameEngine.getGameType().equals("Balancin")){ // ---------------- Balancin
			// pinza horizontal - cuatro direcciones - ejes Z Y
			if(isPriorityY){
				if(gameEngine.getmSensorY().getActualValue() > gameEngine.getmSensorY().getCenterValue()){
					gameEngine.setInputDir(UP);
				}else if(gameEngine.getmSensorY().getActualValue() < gameEngine.getmSensorY().getCenterValue()){
					gameEngine.setInputDir(DOWN);
				}
			}else{
				if (gameEngine.getmSensorZ().getActualValue() < gameEngine.getmSensorZ().getCenterValue()){
					gameEngine.setInputDir(LEFT);
				}else if (gameEngine.getmSensorZ().getActualValue() > gameEngine.getmSensorZ().getCenterValue()){
					gameEngine.setInputDir(RIGHT);
				}
			}
			
		}else if(gameEngine.getGameType().equals("Caballito")){ // ---------- Caballito
			// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
			if(isPriorityY){
				if(gameEngine.getmSensorY().getActualValue() > gameEngine.getmSensorY().getCenterValue()){
					gameEngine.setInputDir(UP);
				}else if(gameEngine.getmSensorY().getActualValue() < gameEngine.getmSensorY().getCenterValue()){
					gameEngine.setInputDir(DOWN);
				}
			}else{
				if (gameEngine.getmSensorX().getActualValue() < gameEngine.getmSensorX().getCenterValue()){
					gameEngine.setInputDir(LEFT);
				}else if (gameEngine.getmSensorX().getActualValue() > gameEngine.getmSensorX().getCenterValue()){
					gameEngine.setInputDir(RIGHT);
				}
			}
			
		}else if(gameEngine.getGameType().equals("Columpio")){ // ---------- Columpio
			// pinza vertical boton hacia abajo - oscilaci�n - eje X
			
		}else if(gameEngine.getGameType().equals("Rueda")){ // ---------- Rueda
			// pinza vertical boton hacia abajo - oscilaci�n - eje X
			
		}else if(gameEngine.getGameType().equals("SubeBaja")){ // ---------- SubeBaja
			
		}else if(gameEngine.getGameType().equals("Tobogan")){ // ---------- Tobogan
			// we use here only IR sensor
		}	
	
	}
	
	private void onBluetoothConnected() {
		Log.i(LOGGER_TAG, "Bluetooth connected");
		//m_deviceStatus.setText("Connected");
		//m_thread.sendBlinkCommand();
		gameEngine.setGameState(0); // 0 = ready
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
						gameEngine.getmSensorX().update(Integer.parseInt(data[1]));
						gameEngine.getmSensorY().update(Integer.parseInt(data[2]));
						gameEngine.getmSensorZ().update(Integer.parseInt(data[3]));
						gameEngine.getmSensorIR().update(Integer.parseInt(data[4]));
						
						/*
						mGraph.addDataPoint(Integer.parseInt(data[1]),Integer.parseInt(data[2]),
								Integer.parseInt(data[3]), Integer.parseInt(data[4]), Integer.parseInt(data[4]));
						mBarGraph.drawBar(Integer.parseInt(data[1]),Integer.parseInt(data[2]),
								Integer.parseInt(data[3]), Integer.parseInt(data[4]));
*/
					}
					catch(Exception e){ 
						Log.d("log error",e.toString());
					}
					//for( int i = 1; i < data.length-1; i++) {// skip the header & end if line
						//Log.d("log", "Value " + i + " = " + data[i]); // Print the field values
					//}
				}
			}else {
				while (!inputString.toString().startsWith("H")){
					inputString.delete(0,1);//borramos un caracter hasta llegar a la H en primer lugar
					//inputString.delete(0, inputString.indexOf("H")) no va porque se quedan \r y \n en medio
				}
			}
			
		}

	}
    
}