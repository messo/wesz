package com.magicconsulting.wesz;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	
	private EditText mServerUrlEditText;
	private Button mServerUrlSaveButton;
	private Button mServerUrlDefaultButton;
	
	private EditText mServiceUrlEditText;
	private Button mServiceUrlSaveButton;
	private Button mServiceUrlDefaultButton;
	
	private OnClickListener mButtonListener;
	
	private TextView mExampleTextView;
	
	private SharedPreferences mPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_settings);
		
		mServerUrlEditText = (EditText) findViewById(R.id.et_server_url);
		mServerUrlSaveButton = (Button) findViewById(R.id.btn_save_server_url);
		mServerUrlDefaultButton = (Button) findViewById(R.id.btn_default_server_url);
		mServiceUrlEditText = (EditText) findViewById(R.id.et_service_url);
		mServiceUrlSaveButton = (Button) findViewById(R.id.btn_save_service_url);
		mServiceUrlDefaultButton = (Button) findViewById(R.id.btn_default_service_url);
		
		mExampleTextView = (TextView) findViewById(R.id.tv_example);
		
		mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		mServerUrlEditText.setText(mPref.getString(MainActivity.PREF_KEY_BASE_URL, MainActivity.DEFAULT_BASE_URL));
		mServiceUrlEditText.setText(mPref.getString(MainActivity.PREF_KEY_SERVICE_URL_PART, MainActivity.DEFAULT_SERVICE_URL_PART));
		
		mButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ( v == mServerUrlSaveButton && mServerUrlEditText.getText().length() != 0 ) {
					Editor e = mPref.edit();
					e.putString(MainActivity.PREF_KEY_BASE_URL, mServerUrlEditText.getText().toString());
					e.commit();
				} else if ( v == mServerUrlDefaultButton ) {
					mServerUrlEditText.setText(MainActivity.DEFAULT_BASE_URL);
				} else if ( v == mServiceUrlSaveButton && mServiceUrlEditText.getText().length() != 0) {
					Editor e = mPref.edit();
					e.putString(MainActivity.PREF_KEY_SERVICE_URL_PART, mServerUrlEditText.getText().toString());
					e.commit();
				} else if ( v == mServiceUrlDefaultButton ) {
					mServerUrlEditText.setText(MainActivity.DEFAULT_BASE_URL);
				}
				
				updateExample();
			}
		};
		
		mServerUrlSaveButton.setOnClickListener(mButtonListener);
		mServerUrlDefaultButton.setOnClickListener(mButtonListener);
		mServiceUrlSaveButton.setOnClickListener(mButtonListener);
		mServiceUrlDefaultButton.setOnClickListener(mButtonListener);
		
		updateExample();
		
	}
	
	private void updateExample() {
		String url = mServerUrlEditText.getText().toString() + mServiceUrlEditText.getText().toString() + 9;
		mExampleTextView.setText(url);
	}
}
