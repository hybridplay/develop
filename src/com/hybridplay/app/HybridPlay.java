package com.hybridplay.app;

import android.app.AlertDialog;
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
        	//Log.i(LOGGER_TAG, "Found Bluetooth adapter");
        	if (!adapterBT.isEnabled()) {
        		//Log.i(LOGGER_TAG, "Bluetooth disabled, launch enable intent");
        		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        	}
        }
	}

    @Override
    public void onBackPressed() {
    	
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.doyouwant).setCancelable(false)
				.setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.setNegativeButton(R.string.resume, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
    	
        // Return to previous page when we press back button
    	/*
        if (this.pager.getCurrentItem() == 0)
            super.onBackPressed();
        else
            this.pager.setCurrentItem(this.pager.getCurrentItem() - 1);
    	*/
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
