package com.hybridplay.app;

import java.util.List;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;

public class HybridPlay extends FragmentActivity {

	/**
	 * The pager widget, which handles animation and allows swiping horizontally
	 * to access previous and next pages.
	 */
	ViewPager pager = null;

	/**
	 * The pager adapter, which provides the pages to the view pager widget.
	 */
	MyFragmentPagerAdapter pagerAdapter;

	/**
	 * Request codes
	 */
	static final int REQUEST_ENABLE_BT = 1;

	//public static GameStorage gamesToPlay;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.hybrid_play);

		//gamesToPlay = new GameStorageConstant();

		// Instantiate a ViewPager
		this.pager = (ViewPager) this.findViewById(R.id.pager);

		// Create an adapter with the fragments we show on the ViewPager
		final MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(
				getSupportFragmentManager());

		adapter.addFragment(ScreenSlidePageFragment.newInstance(Color.WHITE, 0, "null", R.drawable.logo));
		adapter.addFragment(ScreenSlidePageFragment.newInstance(Color.WHITE, 1, "null", R.drawable.intro1));
		adapter.addFragment(ScreenSlidePageFragment.newInstance(Color.WHITE, 2, "null", R.drawable.intro2));
		adapter.addFragment(ScreenSlidePageFragment.newInstance(Color.WHITE, 3, "null", R.drawable.intro3));
		adapter.addFragment(ScreenSlidePageFragment.newInstance(Color.WHITE, 4, "null", R.drawable.intro4));
		adapter.addFragment(ScreenSlidePageFragment.newInstance(Color.WHITE, 5, "null", R.drawable.logo));

		this.pager.setAdapter(adapter);

		//detecting the page where we are
		this.pager.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) { }

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) { }

			@Override
			public void onPageSelected(int position) {
				//            	Log.d("log posicion", Integer.toString(position));
				//            	Log.d("log tamaño adapter", Integer.toString(adapter.getCount()));
				if (position+1 == adapter.getCount()){
					//Log.d("log", "hemos detectado el final");
					lanzarGameList();
					//finish();
				}

			}        

		});

		// Enable Bluetooth
		BluetoothAdapter adapterBT = BluetoothAdapter.getDefaultAdapter();
		if (adapterBT != null) {
			if (!adapterBT.isEnabled()) {
				Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		clearMemory(getBaseContext());
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.doyouwant).setCancelable(false)
		.setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {	
				clearMemory(getBaseContext());
				//finish();
			}
		})
		.setNegativeButton(R.string.resume, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
		super.onBackPressed();
	}
	
	public static void killThisPackageIfRunning(final Context context, String packageName){
	    ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
	    activityManager.killBackgroundProcesses(packageName);
	}
	
	public static void clearMemory(Context context) {
        ActivityManager activityManger = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = activityManger.getRunningAppProcesses();
        if (list != null)
            for (int i = 0; i < list.size(); i++) {
                ActivityManager.RunningAppProcessInfo apinfo = list.get(i);

                String[] pkgList = apinfo.pkgList;

                if (apinfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE ) {
                    for (int j = 0; j < pkgList.length; j++) {
                        activityManger.killBackgroundProcesses(pkgList[j]);
                    }
                }
            }
    }

	public void lanzarGameList(){
		Intent i = new Intent(this, GamesExpandable.class);
		startActivity(i);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//aqui hay que a;andir el layout the dibujo de sensores y ajutar los limites para lanzar acciones
		getMenuInflater().inflate(R.menu.hybrid_play, menu);
		return true;
	}

}
