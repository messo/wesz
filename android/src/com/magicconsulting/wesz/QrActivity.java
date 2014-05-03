/*
 * Basic no frills app which integrates the ZBar barcode scanner with
 * the camera.
 * 
 * Created by lisah0 on 2012-02-24
 */
package com.magicconsulting.wesz;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;

public class QrActivity extends Activity
{
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    FrameLayout previewParent;

    ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;
    
    public static final int RESULT_SCAN_SUCCESSFUL = 99;
    public static final String EXTRA_SCANNED_CODE = "extra_qr_code";
    
    private static final String settingsCode = "settings";
    
    static {
        System.loadLibrary("iconv");
    } 

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qr);

        autoFocusHandler = new Handler();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        previewParent = (FrameLayout)findViewById(R.id.cameraPreview);
    }

    public void onPause() {
        super.onPause();
        releaseCameraAndPreview();
    }
    
    public void onStart() {
    	super.onStart();
    	mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        previewParent.addView(mPreview);
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            previewing = false;
            
            previewParent.removeView(mPreview);
            mPreview = null;
            
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
            public void run() {
                if (previewing)
                    mCamera.autoFocus(autoFocusCB);
            }
        };

	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters parameters = camera.getParameters();
			Size size = parameters.getPreviewSize();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = scanner.scanImage(barcode);

			if (result != 0) {
				SymbolSet syms = scanner.getResults();

				if (syms == null)
					return;

				// Getting first result
				String scanResultString = null;

				for (Symbol sym : syms) {
					scanResultString = sym.getData();
				}

				if (scanResultString == null || scanResultString.length() == 0)
					return;

				// Check for settings code
				if (settingsCode.equals(scanResultString)) {
					Intent intent = new Intent(QrActivity.this,
							SettingsActivity.class);
					QrActivity.this.startActivity(intent);
					
					endScanningAndFinish();
				}

				// Lets try to parse positive number or 0

				int resultNumber = -1;

				try {
					resultNumber = Integer.parseInt(scanResultString);
				} catch (Exception e) {
					// Bad result
					return;
				}

				// If we get this far, we have a number, negative number is bad
				// :d
				if (resultNumber < 0)
					return;

				// We have a valid result, lets finish this Activity, and send
				// back result
				endScanning();

				Intent intent = new Intent();
				intent.putExtra(EXTRA_SCANNED_CODE, resultNumber);

				QrActivity.this.setResult(RESULT_SCAN_SUCCESSFUL, intent);
				QrActivity.this.finish();
			}
		}
	};

    // Mimic continuous auto-focusing
	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};
	
	private void endScanning() {
		barcodeScanned = true;
		previewing = false;
		if ( mCamera != null ) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
		}
	}
	
	private void endScanningAndFinish() {
		endScanning();
		finish();
	}
}
