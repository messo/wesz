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
	private WebView mWebView;
	
	public static final String DEFAULT_BASE_URL = "http://weszfrontend.jelastic.dogado.eu/";
	public static final String DEFAULT_SERVICE_URL_PART = "index.php?page=apply&serviceid=";
	
	public static final String PREF_KEY_BASE_URL = "key_base_url";
	public static final String PREF_KEY_SERVICE_URL_PART = "key_service_url_part";
	
	public static final int REQUEST_QR_CODE = 9999;
	
	private String mBaseUrl;
	private String mServiceUrlPart;
	
	private String mLoadedUrl;
	
	private SharedPreferences mPref;
	
	private String getServiceUrl(int id) {
		return mBaseUrl + mServiceUrlPart + id; 
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		mBaseUrl = mPref.getString(PREF_KEY_BASE_URL, DEFAULT_BASE_URL);
		mServiceUrlPart = mPref.getString(PREF_KEY_SERVICE_URL_PART, DEFAULT_SERVICE_URL_PART);
		
		mWebView = (WebView) findViewById(R.id.webview);
		mBtnQR = (Button) findViewById(R.id.btn_qr);
		
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(false);
		settings.setBuiltInZoomControls(false);
		
		WebViewClient client = new WebViewClient() {
			@Override
			public void onLoadResource(WebView view, String url) {
				if ( url != null ) mLoadedUrl = url;
				super.onLoadResource(view, url);
			}
		};
		
		mWebView.setWebViewClient(client);
		
		mWebView.loadUrl(mBaseUrl);
		
		mBtnQR.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, QrActivity.class);
				startActivityForResult(intent, REQUEST_QR_CODE);
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		boolean needReload = false;
		
		String newBaseUrl = mPref.getString(PREF_KEY_BASE_URL, DEFAULT_BASE_URL);
		String newServiceUrlPart = mPref.getString(PREF_KEY_SERVICE_URL_PART, DEFAULT_SERVICE_URL_PART);
		
		if ( !newBaseUrl.equals(mBaseUrl) ) {
			mBaseUrl = newBaseUrl;
			needReload = true;
		}
		
		if ( !newServiceUrlPart.equals(newServiceUrlPart) ) {
			mServiceUrlPart = newServiceUrlPart;
			needReload = true;
		}
		
		if ( needReload && mWebView != null ) {
			mWebView.loadUrl(newBaseUrl);
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
	
	@Override
	public void onBackPressed() {
		if ( mBaseUrl.equals(mLoadedUrl) || !mWebView.canGoBack() ) {
			super.onBackPressed();
		} else {
			if ( mWebView != null ) mWebView.goBack();
		}
	}

}
