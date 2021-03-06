package com.hybridplay.app;

import java.util.ArrayList;

import com.hybridplay.bluetooth.BluetoothService;
import com.hybridplay.bluetooth.SensorThread;
import com.hybridplay.bluetooth.BluetoothService.LocalBinder;
import android.app.ExpandableListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class GamesExpandable extends ExpandableListActivity {

	ArrayList<String> groupItem = new ArrayList<String>();
	ArrayList<Object> childItem = new ArrayList<Object>();
	ArrayList<String> groupItemDescription = new ArrayList<String>();

	// ---------------------------------------------- BLUETOOTH SERVICE BLOCK
	public static final String SENSOR_DATA_INTENT = "com.hybridplay.SENSOR";
	Intent BTintent;
	BluetoothService mService;
	SensorThread thread;
	Runnable mRunnable;
	Handler handler = new Handler();
	boolean mBound = false;
	int fakeAX, fakeAY, fakeAZ, fakeIR;
	boolean sensorConnected = false;
	// ---------------------------------------------- BLUETOOTH SERVICE BLOCK
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setContentView(R.layout.gameslist);
		findViewById(R.id.gamesListFrameLayout).setVisibility(View.INVISIBLE);

		ExpandableListView expandbleLis = getExpandableListView();
		//expandbleLis.setDividerHeight(2);
		expandbleLis.setGroupIndicator(null);
		expandbleLis.setClickable(true);

		setGroupData();
		setGroupDescription();
		setChildGroupData();

		NewAdapter mNewAdapter = new NewAdapter(groupItem, groupItemDescription, childItem);
		mNewAdapter
		.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),this);
		//getExpandableListView().setAdapter(mNewAdapter);
		expandbleLis.setAdapter(mNewAdapter);
		// expandbleLis.setOnChildClickListener(this);
		registerForContextMenu(expandbleLis);

		expandbleLis.setOnGroupClickListener(new OnGroupClickListener(){

			@Override
			public boolean onGroupClick(ExpandableListView expListView, View arg1,int groupPos, long arg3) {

				//Log.i("log groupPos", Integer.toString(groupPos));

				//expListView.getCount();
				//				if(expListView.isGroupExpanded(groupPos-1)){
				//					expListView.collapseGroup(groupPos-1);
				//				}

				return false;
			}
		});


		expandbleLis.setOnChildClickListener(new OnChildClickListener(){

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				//l.makeText(GamesExpandable.this, "Clicked On Child",
				//Toast.LENGTH_SHORT).show();

				if (groupPosition == 0){
					startSpaceKid(childPosition);
				}else if (groupPosition == 1){
					startPacMan(childPosition);
				}else if (groupPosition == 2){
					startPong(childPosition);
				}else if (groupPosition == 3){
					startPuzzleCity(childPosition);
				}else if (groupPosition == 4){
					startArcaDroid(childPosition);
				}else if (groupPosition == 5){
					startBuild(childPosition);
				}else if (groupPosition == 6){
					startFising(childPosition);
				}else if (groupPosition == 7){
					startSpaceInvaders(childPosition);
				}else if (groupPosition == 8){
					startTron(childPosition);
				}else if (groupPosition == 9){
					startConfig(childPosition);
				}
				return true;
			}
		});


	}

	@Override
	protected void onStart() {
		super.onStart();
		bindBluetoothService(); // BLUETOOTH SERVICE
	}
	
	@Override
	public void onBackPressed() {
		if (thread != null) {
		    thread.interrupt();
		}
		handler.removeCallbacks(mRunnable);
		unbindBluetoothService(); // BLUETOOTH SERVICE
		finish();
	}
	
	@Override
	public void onDestroy() {
		unbindBluetoothService(); // BLUETOOTH SERVICE
		super.onDestroy();
		
	}

	public void setGroupData() {	  
		groupItem.add("SpaceKid");
		groupItem.add("PackMan");
		groupItem.add("Pong");
		groupItem.add("PuzzleCity");
		groupItem.add("Arkanoid");
		groupItem.add("Building Something");
		groupItem.add("Fishing");
		groupItem.add("SpaceInvaders");
		groupItem.add("Tron");
		groupItem.add("Config");
	}

	public void setGroupDescription() {
		groupItemDescription.add("Collect space trash to clean the galaxy");
		groupItemDescription.add("Classic PacMan adapted to Hybrid Play");
		groupItemDescription.add("Classic Pong adapted to Hybrid Play");
		groupItemDescription.add("Find puzzle pieces and change the city");
		groupItemDescription.add("Classic Arkanoid adapted to Hybrid Play");
		groupItemDescription.add("Collect the pieces to build objects");
		groupItemDescription.add("Collect pearls");
		groupItemDescription.add("Classic SpaceInvaders adapted to Hybrid Play");
		groupItemDescription.add("Classic Tron adapted to Hybrid Play");
		groupItemDescription.add("Sensor data visualization");
	}


	public void setChildGroupData() {
		/**
		 * Add Data For SpaceKid
		 */
		ArrayList<String> child = new ArrayList<String>();
		child.add("Balancin");
		child.add("Caballito");
		//child.add("SubeBaja");

		childItem.add(child);

		/**
		 * Add Data For PackMan
		 */
		child = new ArrayList<String>();
		child.add("Balancin");
		child.add("Caballito");

		childItem.add(child);
		/**
		 * Add Data For Pong
		 */
		child = new ArrayList<String>();
		child.add("Balancin");
		child.add("Caballito");
		//child.add("SubeBaja");

		childItem.add(child);
		/**
		 * Add Data For PuzzleCity
		 */
		child = new ArrayList<String>();
		child.add("Balancin");
		child.add("Caballito");
		child.add("Columpio");
		child.add("Tobogan");
		child.add("SubeBaja");

		childItem.add(child);

		/**
		 * Add Data For Arkanoid
		 */
		child = new ArrayList<String>();
		child.add("Balancin");
		child.add("Caballito");
		//child.add("SubeBaja");

		childItem.add(child);

		/**
		 * Add Data For Building
		 */
		child = new ArrayList<String>();
		child.add("Balancin");
		child.add("Caballito");
		//child.add("SubeBaja");

		childItem.add(child);

		/**
		 * Add Data For Fishing
		 */
		child = new ArrayList<String>();
		child.add("Balancin");
		child.add("Caballito");
		child.add("SubeBaja");

		childItem.add(child);
		
		/**
		 * Add Data For SpaceInvaders
		 */
		child = new ArrayList<String>();
		child.add("Balancin");
		child.add("Caballito");
		child.add("SubeBaja");

		childItem.add(child);
		
		/**
		 * Add Data For Tron
		 */
		child = new ArrayList<String>();
		child.add("Balancin");
		child.add("Caballito");

		childItem.add(child);

		/**
		 * Add Data For Config
		 */
		child = new ArrayList<String>();
		child.add("Balancin");
		//		  child.add("Caballito");
		//		  child.add("SubeBaja");

		childItem.add(child);
	}


	@SuppressWarnings("unchecked")
	public void startSpaceKid(int position) {
		Intent sGame = new Intent(this, com.hybridplay.spacekids.propeller.GameActivitySpaceKidsPropeller.class);
		ArrayList<String> tempChild = new ArrayList<String>();
		tempChild = (ArrayList<String>) childItem.get(0);
		sGame.putExtra("gameType", tempChild.get(position));
		startActivityForResult(sGame, 0);
	}
	
	@SuppressWarnings("unchecked")
	public void startPacMan(int position){
		Intent sGame = new Intent(this, com.hybridplay.packman.GameActivityPackMan.class);
		ArrayList<String> tempChild = new ArrayList<String>();
		tempChild = (ArrayList<String>) childItem.get(1);
		sGame.putExtra("gameType", tempChild.get(position));
		startActivityForResult(sGame, 0);
	}

	@SuppressWarnings("unchecked")
	public void startPong(int position) {
		Intent sGame = new Intent(this, com.hybridplay.pong.Pong.class);
		ArrayList<String> tempChild = new ArrayList<String>();
		tempChild = (ArrayList<String>) childItem.get(2);
		sGame.putExtra("gameType", tempChild.get(position));
		startActivityForResult(sGame, 0);
	}

	@SuppressWarnings("unchecked")
	public void startPuzzleCity(int position){
		Intent sGame = new Intent(this, com.hybridplay.puzzlecity.GameActivityPuzzleCity.class);
		ArrayList<String> tempChild = new ArrayList<String>();
		tempChild = (ArrayList<String>) childItem.get(3);
		sGame.putExtra("gameType", tempChild.get(position));
		startActivityForResult(sGame, 0);
	}

	@SuppressWarnings("unchecked")
	public void startArcaDroid(int position){
		Intent sGame = new Intent(this, com.hybridplay.arkanoid.ArkaNoid.class);
		ArrayList<String> tempChild = new ArrayList<String>();
		tempChild = (ArrayList<String>) childItem.get(4);
		sGame.putExtra("gameType", tempChild.get(position));
		startActivityForResult(sGame, 0);
	}

	@SuppressWarnings("unchecked")
	public void startBuild(int position){
		Intent sGame = new Intent(this, com.hybridplay.buildsomething.BuildSomethingActivity.class);
		ArrayList<String> tempChild = new ArrayList<String>();
		tempChild = (ArrayList<String>) childItem.get(5);
		sGame.putExtra("gameType", tempChild.get(position));
		startActivityForResult(sGame, 0);
	}

	@SuppressWarnings("unchecked")
	public void startFising(int position){
		Intent sGame = new Intent(this, com.hybridplay.fishing.GameActivityFishing.class);
		ArrayList<String> tempChild = new ArrayList<String>();
		tempChild = (ArrayList<String>) childItem.get(6);
		sGame.putExtra("gameType", tempChild.get(position));
		startActivityForResult(sGame, 0);
	}
	
	@SuppressWarnings("unchecked")
	public void startSpaceInvaders(int position){
		Intent sGame = new Intent(this, com.hybridplay.spaceinvaders.SpaceInvadersActivity.class);
		ArrayList<String> tempChild = new ArrayList<String>();
		tempChild = (ArrayList<String>) childItem.get(7);
		sGame.putExtra("gameType", tempChild.get(position));
		startActivityForResult(sGame, 0);
	}
	
	@SuppressWarnings("unchecked")
	public void startTron(int position){
		Intent sGame = new Intent(this, com.hybridplay.glTron.glTron.class);
		ArrayList<String> tempChild = new ArrayList<String>();
		tempChild = (ArrayList<String>) childItem.get(8);
		sGame.putExtra("gameType", tempChild.get(position));
		startActivityForResult(sGame, 0);
	}

	@SuppressWarnings("unchecked")
	public void startConfig(int position){
		Intent sGame = new Intent(this, com.hybridplay.config.ConfigActivity.class);
		ArrayList<String> tempChild = new ArrayList<String>();
		tempChild = (ArrayList<String>) childItem.get(9);
		sGame.putExtra("gameType", tempChild.get(position));
		startActivityForResult(sGame, 0);
	}

	// ---------------------------------------------- BLUETOOTH SERVICE
	public void bindBluetoothService(){
		BTintent = new Intent(this, BluetoothService.class);
		bindService(BTintent, mConnection, Context.BIND_AUTO_CREATE);
	}

	public void unbindBluetoothService(){
		thread.setRunning(false);
		// Unbind from the service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the thread
				Log.d("EXCEPTION",e.getMessage());
			}
		}
	}

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,IBinder service) {
			// Bound to LocalService, cast the IBinder and get LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mService.initBluetoothService();
			mBound = true;

			// create the game loop thread
			thread = new SensorThread(mService);
			thread.setRunning(true);
			thread.start();

			handler.post(mRunnable = new Runnable(){
				@Override
				public void run() {
					// update
					if (thread.isRunning()) {
						// check if bluetooth is connected
						if(!mService.getBluetoothConnected()){
							mService.initBluetoothService();
							sensorConnected = false;
						}
						
						if(mService.getIsSensorConnected()){
							findViewById(R.id.loadingLayout).setVisibility(View.INVISIBLE);
							findViewById(R.id.gamesListFrameLayout).setVisibility(View.VISIBLE);
						}
						
						// update sensor readings (send broadcast if values have changed)
						if(fakeAX != thread.getX() || fakeAY != thread.getY() || fakeAZ != thread.getZ() || fakeIR != thread.getIR()){
							broadcastIntent(thread.getX(),thread.getY(),thread.getZ(),thread.getIR(),mService.getDeviceName(),mService.getDeviceStatus());
						}
						
						fakeAX = thread.getX();
						fakeAY = thread.getY();
						fakeAZ = thread.getZ();
						fakeIR = thread.getIR();
					}
					handler.postDelayed(this,80); // set time here to refresh (80 ms => 12 FPS)
				}
			});         
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	public void broadcastIntent(int aX, int aY, int aZ, int IR, String s1, String s2){
		Intent intent = new Intent();
		intent.setAction(SENSOR_DATA_INTENT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.putExtra("AX",aX);
		intent.putExtra("AY",aY);
		intent.putExtra("AZ",aZ);
		intent.putExtra("IR",IR);
		intent.putExtra("name",s1);
		intent.putExtra("status",s2);
		sendBroadcast(intent);
	}
	// ---------------------------------------------- BLUETOOTH SERVICE BLOCK

}
