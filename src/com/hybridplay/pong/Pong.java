package com.hybridplay.pong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.hybridplay.app.R;

public class Pong extends Activity {
	
	public String pWith;

	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.pong_title);
		setListeners();
		
		// get the game type (affect different sensor readings)
        Bundle extras = getIntent().getExtras();
        pWith = extras.getString("gameType");
	}
	
	private LinearLayout LinearLayout(View findViewById) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.pong_menu, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_preferences:
			Intent i = new Intent(this, com.hybridplay.pong.PongPreferencesActivity.class);
			startActivity(i);
			break;
		}
		return false;
	}
	
	
	protected void setListeners () {
		this.findViewById(R.id.title_btnNoPlayer)
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startGame(false,false,pWith);
			}
		});
		
		this.findViewById(R.id.title_btnOnePlayer)
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startGame(false,true,pWith);
			}
		});
		
		this.findViewById(R.id.title_btnTwoPlayer)
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startGame(true,true,pWith);
			}
		});
	}
	
	protected void startGame(boolean redPlayer, boolean bluePlayer,String typeGame) {
		Intent i = new Intent(this, com.hybridplay.pong.GameActivityPong.class);
		i.putExtra(GameActivityPong.EXTRA_BLUE_PLAYER, bluePlayer);
		i.putExtra(GameActivityPong.EXTRA_RED_PLAYER, redPlayer);
		i.putExtra("gameType",typeGame);
		startActivity(i);
	}
	
	
	public static final String
		PREF_BALL_SPEED = "ball_speed",
		PREF_STRATEGY = "strategy",
		PREF_LIVES = "lives",
		PREF_HANDICAP = "handicap",
		PREF_MUTED = "muted";
	
	public static final String
		KEY_AI_STRATEGY = "key_ai_strategy";
}