package com.magicconsulting.wesz;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity {
	
	private EditText mServerUrlEditText;
	private Button mSaveButton;
	private Button mDefaultButton;
	
	private SharedPreferences mPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_settings);
		
		mServerUrlEditText = (EditText) findViewById(R.id.et_server);
		mSaveButton = (Button) findViewById(R.id.btn_save);
		mDefaultButton = (Button) findViewById(R.id.btn_default);
		
		mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		mServerUrlEditText.setText(mPref.getString(MainActivity.PREF_KEY_URL, MainActivity.DEFAULT_URL));
		
		
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ( mServerUrlEditText.getText().length() == 0 ) return;
				
				Editor e = mPref.edit();
				e.putString(MainActivity.PREF_KEY_URL, mServerUrlEditText.getText().toString());
				e.commit();
			}
		});
		
		mDefaultButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mServerUrlEditText.setText(MainActivity.DEFAULT_URL);
			}
		});
		
	}
}
