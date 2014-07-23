package com.hybridplay.pong;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.hybridplay.app.R;

public class PongPreferencesActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.pong_preferences);
	}
}