package com.hybridplay.buildsomething;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;

import com.hybridplay.app.R;
import com.hybridplay.bluetooth.BluetoothThread;

public class BuildSomethingActivity extends Activity {
	private GameSurfaceView gameView;
	private GameEngine gameEngine;
	
	// BLUETOOTH BLOCK
	// Message types 
	static final int BLUETOOTH_CONNECTED = 1;
	static final int BLUETOOTH_DISCONNECTED = 2;
	static final int BLUETOOTH_RECEIVED = 3;
	private final static int REQUEST_ENABLE_BT = 1;
	
	static final String BLUETOOTH_TAG = "Bluetooth Activity";
	static final String GAME_TAG = "PuzzleCity Activity";
	
	Handler m_handler;
	BluetoothThread m_thread;
	StringBuffer inputString = new StringBuffer("");
	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
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
        // ------------------------------- end bluetooth
        
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        
        // create the game engine
        gameEngine = new GameEngine(width, height);
        // get the game type (affect different sensor readings)
        Bundle extras = getIntent().getExtras();
        String playWith = extras.getString("gameType");
        
        gameEngine.setGameType(playWith);
        
        gameView = new GameSurfaceView(this, gameEngine, width, height);
        
        setContentView(gameView);
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
    
    @Override
	protected void onPause() {
		super.onPause();
		if (gameEngine != null) gameEngine.pause();
		if (gameView != null) gameView.pause();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (gameEngine != null) gameEngine.resume();
		if (gameView != null) gameView.resume();
	}
	
	@Override
	protected void onDestroy() {
		if (m_thread != null && m_thread.isAlive()) {
			m_thread.cancel();
		}
		
		super.onDestroy();
	}
	

	@Override
	public void finish() {
		 Intent intent = new Intent();

		 intent.putExtra("level", gameEngine.level);
		 intent.putExtra("status", gameEngine.status);

		 setResult(RESULT_OK, intent);
		        
		super.finish();
	}


	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.doyouwant).setCancelable(false)
				.setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						BuildSomethingActivity.this.finish();
					}
				})
				.setNegativeButton(R.string.resume, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void onBluetoothConnected() {
		if (gameEngine != null) gameEngine.setGameState(1);
	}
	
	private void onBluetoothDisconnected() {
		
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
						gameEngine.getmSensorX().update(Integer.parseInt(data[1]));
						gameEngine.getmSensorY().update(Integer.parseInt(data[2]));
						gameEngine.getmSensorZ().update(Integer.parseInt(data[3]));
						gameEngine.getmSensorIR().update(Integer.parseInt(data[4]));
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
	
}
