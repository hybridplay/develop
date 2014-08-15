package com.hybridplay.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.hybridplay.app.R;

public class DeviceActivity extends Activity {
	
	// Logger name (there's probably a better way to organize this)
	static final String LOGGER_TAG = "DeviceActivity";
	
	// Message types
	static final int BLUETOOTH_CONNECTED = 1;
	static final int BLUETOOTH_DISCONNECTED = 2;
	static final int BLUETOOTH_RECEIVED = 3;
	
	private class DeviceThread extends Thread {
		Handler m_handler;
		BluetoothDevice m_device;
		BluetoothSocket m_socket;
		OutputStream m_output;
		InputStream m_input;
		
		DeviceThread(Handler handler, BluetoothDevice device) {
			m_handler = handler;
			m_device = device;
			//Log.i(LOGGER_TAG, "DeviceThread running");
			//Log.i(LOGGER_TAG, String.format("Received device: %s", device.getName()));
		}
				
		private void connect() {
			try {
	        	UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        		//Log.i(LOGGER_TAG, "Create BluetoothSocket");
				m_socket = m_device.createRfcommSocketToServiceRecord(uuid);
        		//Log.i(LOGGER_TAG, "Connect BluetoothSocket");
				m_socket.connect();
				m_handler.obtainMessage(BLUETOOTH_CONNECTED).sendToTarget();
				m_output = m_socket.getOutputStream();
				m_input = m_socket.getInputStream();
			} catch (IOException e) {
				Log.e(LOGGER_TAG, String.format("Caught IOException e: %s", e.toString()));
				m_socket = null;
				m_handler.obtainMessage(BLUETOOTH_DISCONNECTED).sendToTarget();
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
					Log.e(LOGGER_TAG, String.format("Caught IOException e: %s", e.toString()));
					m_socket = null;
					m_handler.obtainMessage(BLUETOOTH_DISCONNECTED).sendToTarget();
				}
			}
		}
		
		public void cancel() {
			try {
				m_socket.close();
			} catch (IOException e) {
				Log.e(LOGGER_TAG, String.format("Caught IOException e: %s", e.toString()));
			} finally {
				m_socket = null;
				m_handler.obtainMessage(BLUETOOTH_DISCONNECTED).sendToTarget();
			}
		}
		
		public void sendCommand(String command) {
			try {
				m_output.write(command.getBytes());
				m_output.flush();
				//Log.i(LOGGER_TAG, String.format("Sent command \"%s\" to device", command));
			} catch (IOException e) {
				Log.e(LOGGER_TAG, String.format("Caught IOException e: %s", e.toString()));
				m_socket = null;
				m_handler.obtainMessage(BLUETOOTH_DISCONNECTED).sendToTarget();
			}
		}
		
		public void sendBlinkCommand() {
			sendBlinkCommand(500);
		}
		
		public void sendBlinkCommand(int period) {
			String blinkCommand = "BLINK," + String.valueOf(period) + "\n";
			sendCommand(blinkCommand);
		}
	}
	
	Handler m_handler;
	DeviceThread m_thread;
	EditText m_deviceCommand;
	TextView m_deviceStatus;
	TextView m_deviceOutput;
	StringBuffer inputString = new StringBuffer("");
	private GraphView mGraph; 
	private BarView mBarGraph;
	
    @SuppressLint("HandlerLeak")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	//Log.i(LOGGER_TAG, "Started DeviceActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        mGraph = (GraphView)findViewById(R.id.graph);
        mGraph.setMaxValue(1024); //el valor maximo del sensor
        
        mBarGraph = (BarView) findViewById(R.id.bargraph);
        mBarGraph.setMaxValue(1024);
        
        //Log.d("LOG ANCHO", "" + mBarGraph.getWidth() );
        //Log.d("LOG ALTO", "" + mBarGraph.getHeight() );

        m_handler = new Handler() {
        	public void handleMessage(Message msg) {
        		onMessage(msg);
        	}
        };
 
        // Get the device
        Intent connectIntent = getIntent();
        Bundle extras = connectIntent.getExtras();
        BluetoothDevice device = (BluetoothDevice)extras.get("device");
        
        // Configure the text views
        TextView deviceName = (TextView)findViewById(R.id.deviceName);
        deviceName.setText(device.getName());
        //m_deviceCommand = (EditText)findViewById(R.id.deviceCommand);
        m_deviceStatus = (TextView)findViewById(R.id.deviceStatus);
        m_deviceStatus.setText("Connecting...");
        //m_deviceOutput = (TextView)findViewById(R.id.deviceOutput);
        
        // Configure the button listener
        /*
        Button deviceCommandSend = (Button)findViewById(R.id.deviceCommandSend);
        deviceCommandSend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (m_thread != null) {
					String command = m_deviceCommand.getText().toString();
					m_thread.sendCommand(command + "\n");
					m_deviceCommand.setText("");
				}
			}
		});
        */
        // Start the thread
        if (device != null) {
        	//Log.i(LOGGER_TAG, "Start new DeviceThread");
        	m_thread = new DeviceThread(m_handler, device);
        	m_thread.start();
        }
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
	protected void onDestroy() {
		if (m_thread != null && m_thread.isAlive()) {
			m_thread.cancel();
		}
		super.onDestroy();
	}

	
	private void onBluetoothConnected() {
		//Log.i(LOGGER_TAG, "Bluetooth connected");
		m_deviceStatus.setText("Connected");
		m_thread.sendBlinkCommand();
		mGraph.connected = true;
	}
	
	private void onBluetoothDisconnected() {
		//Log.i(LOGGER_TAG, "Bluetooth disconnected");
		m_deviceStatus.setText("Disconnected");
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
						
						mGraph.addDataPoint(Integer.parseInt(data[1]),Integer.parseInt(data[2]),
								Integer.parseInt(data[3]), Integer.parseInt(data[4]), Integer.parseInt(data[4]));
						mBarGraph.drawBar(Integer.parseInt(data[1]),Integer.parseInt(data[2]),
								Integer.parseInt(data[3]), Integer.parseInt(data[4]));

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
