package com.magicconsulting.wesz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private Button mBtnQR;
	private Button mBtnSettings;
	private WebView mWebView;
	
	public static final String DEFAULT_URL = "http://rabszolga.net16.net/wesz/";
	private static final String SERVICE_URL_PART = "index.php?page=apply&serviceid=";
	
	public static final String PREF_KEY_URL = "key_url";
	
	public static final int REQUEST_QR_CODE = 9999;
	
	private String mServerUrl;
	
	private SharedPreferences mPref;
	
	private String getServiceUrl(int id) {
		return mServerUrl + SERVICE_URL_PART + id; 
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		mServerUrl = mPref.getString(PREF_KEY_URL, DEFAULT_URL);
		
		mWebView = (WebView) findViewById(R.id.webview);
		mBtnQR = (Button) findViewById(R.id.btn_qr);
		mBtnSettings = (Button) findViewById(R.id.btn_settings);
		
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(false);
		settings.setBuiltInZoomControls(false);
		
		mWebView.setWebViewClient(new WebViewClient());
		
		mWebView.loadUrl(mServerUrl);
		
		mBtnQR.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, QrActivity.class);
				startActivityForResult(intent, REQUEST_QR_CODE);
			}
		});
		
		mBtnSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		String newUrl = mPref.getString(PREF_KEY_URL, DEFAULT_URL);
		if ( !newUrl.equals(mServerUrl) ) {
			mServerUrl = newUrl;
			if ( mWebView != null ) {
				mWebView.loadUrl(newUrl);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if ( mWebView != null ) {
			mWebView.destroy();
			mWebView = null;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ( requestCode != REQUEST_QR_CODE || resultCode != QrActivity.RESULT_SCAN_SUCCESSFUL || data == null ) return;
		
		int scanResult = data.getIntExtra(QrActivity.EXTRA_SCANNED_CODE, -1);
		if ( scanResult < 0 ) return;
		
		//Toast.makeText(this, String.format("Az eredmény: %d", scanResult), Toast.LENGTH_LONG).show();
		
		String serviceUrl = getServiceUrl(scanResult);
		mWebView.loadUrl(serviceUrl);
		
		super.onActivityResult(requestCode, resultCode, data);
	}


}
