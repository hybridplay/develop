package com.hybridplay.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;


public class BluetoothThread extends Thread {
	// Logger name (there's probably a better way to organize this)
	static final String LOGGER_TAG = "DeviceActivity";
	
	//Message types
	static final int BLUETOOTH_CONNECTED = 1;
	static final int BLUETOOTH_DISCONNECTED = 2;
	static final int BLUETOOTH_RECEIVED = 3;

	Handler m_handler;
	BluetoothDevice m_device;
	BluetoothSocket m_socket;
	OutputStream m_output;
	InputStream m_input;

	public BluetoothThread(Handler handler, BluetoothDevice device) {
		m_handler = handler;
		m_device = device;
		Log.i(LOGGER_TAG, "DeviceThread running");
		Log.i(LOGGER_TAG, String.format("Received device: %s", device.getName()));
	}

	private void connect() {
		try {
			UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
			Log.i(LOGGER_TAG, "Create BluetoothSocket");
			m_socket = m_device.createRfcommSocketToServiceRecord(uuid);
			Log.i(LOGGER_TAG, "Connect BluetoothSocket");
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
			Log.i(LOGGER_TAG, String.format("Sent command \"%s\" to device", command));
		} catch (IOException e) {
			Log.e(LOGGER_TAG, String.format("Caught IOException e: %s", e.toString()));
			m_socket = null;
			m_handler.obtainMessage(BLUETOOTH_DISCONNECTED).sendToTarget();
		}
	}

	public void sendBlinkCommand(int period) {
		String blinkCommand = "BLINK," + String.valueOf(period) + "\n";
		sendCommand(blinkCommand);
	}
}
