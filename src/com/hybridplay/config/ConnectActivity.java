package com.hybridplay.config;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hybridplay.app.R;

public class ConnectActivity extends Activity {
	// Logger name (there's probably a better way to organize this)
	static final String LOGGER_TAG = "ConnectActivity";
	
	// Request codes
	static final int REQUEST_ENABLE_BT = 1;
	
	ArrayList<BluetoothDevice> m_devices;
	ArrayList<String> m_deviceNames;
	ArrayAdapter<String> m_arrayAdapter;
	
	public void connectDevice(int position) {
		Log.i(LOGGER_TAG, String.format("Connect to device: %d", position));
		BluetoothDevice device = m_devices.get(position);
		Intent connectIntent = new Intent(this, DeviceActivity.class);
		connectIntent.putExtra("device", device);
		startActivity(connectIntent);
	}
	
	public void connectDevice(String name) {
		Log.i(LOGGER_TAG, "Connect to device: "+ name);
		for (int x =0; x < m_devices.size(); x++){
			Log.i(LOGGER_TAG, "device pared: "+ m_devices.get(x).getName());
			if (m_devices.get(x).getName().equals(name)){
				BluetoothDevice device = m_devices.get(x);
				Intent connectIntent = new Intent(this, DeviceActivity.class);
				connectIntent.putExtra("device", device);
				startActivity(connectIntent);
				break;
			}
		}
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(LOGGER_TAG, "Started ConnectActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        // Setup the device list
        m_devices = new ArrayList<BluetoothDevice>();
        m_deviceNames = new ArrayList<String>();
        m_deviceNames.add("No devices");
        m_arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, m_deviceNames);
        ListView view = (ListView)findViewById(R.id.deviceList);
        view.setAdapter(m_arrayAdapter);
        view.setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				Log.i(LOGGER_TAG, String.format("Item clicked: %d", position));
				connectDevice(position);				
			}
        });
        
        // Enable Bluetooth
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
        	Log.i(LOGGER_TAG, "Found Bluetooth adapter");
        	if (!adapter.isEnabled()) {
        		Log.i(LOGGER_TAG, "Bluetooth disabled, launch enable intent");
        		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        	}
        	if (adapter.isEnabled()) {
        		Log.i(LOGGER_TAG, "Bluetooth enabled, find paired devices");
        		Set<BluetoothDevice> devices = adapter.getBondedDevices();
        		if (!devices.isEmpty()) {
        			m_devices.clear();
        			m_arrayAdapter.clear();
        			for (BluetoothDevice device : devices) {
        				Log.i(LOGGER_TAG, String.format("Found bluetooth device: name %s", device.getName()));
        				m_devices.add(device);
        				m_arrayAdapter.add(device.getName());
        			}
        		}
        	}

        	connectDevice("HYBRIDPLAY"); //directly connect to our sensor previously pared
        }
    }
}
