/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scandit.datacapture.expirymanagementsample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.expirymanagementsample.barcodecount.BarcodeCountPresenter;
import com.scandit.datacapture.expirymanagementsample.barcodecount.BarcodeCountPresenterView;
import com.scandit.datacapture.expirymanagementsample.managers.BarcodeCountCameraManager;
import com.scandit.datacapture.expirymanagementsample.managers.BarcodeManager;
import com.scandit.datacapture.expirymanagementsample.results.ExtraButtonStyle;
import com.scandit.datacapture.expirymanagementsample.results.ResultsActivity;
import com.scandit.datacapture.expirymanagementsample.sparkscan.SparkScanPresenter;
import com.scandit.datacapture.expirymanagementsample.sparkscan.SparkScanPresenterView;

public class MainActivity extends CameraPermissionActivity
    implements SparkScanPresenterView, BarcodeCountPresenterView {

	// There is a Scandit sample license key set below here.
	// This license key is enabled for sample evaluation only.
	// If you want to build your own application, get your license key by signing up for a trial at https://ssl.scandit.com/dashboard/sign-up?p=test
    public static final String SCANDIT_LICENSE_KEY = "AYjTKgwFKLhZGtmHmyNAawklGVUpLfmaJ2JN39hPFcbHRdb8Sh3UX45m7PRkJtORsQzsAeBZw7aAZ/VBZlp5ykVZZOOYUI8ZAxAsZ3tOrh5HXX2CzFyh2yNzGtUXQuR5eFHqhXNx8+mfbsvN2zErPt0+TW4TESKXSx4764U8HnIF/01crbTR4/qxeWvIgdmGJkoV2YZc4wfZjpQI2Uvd3/J2jFcv/WrVHgWZ/VAC2lHTzC3JdwtTNJKxxDpsqKp1sDlARxGjw4hlebrAUbft3aWMjbtpVn2T4D+tBN3GVuwlD9Uo7MN3Sto17fSVSD1JLymYPHP7zxsnByy9mCBhKqTf3YKCh8DughdNJpIIWaaoY6t6OTof+TxY25XAboYM1Ii3FdaK1MjK2x9bVujInqaIYzPRYRwQj6lPyVaYSiRRJTsR6l3RLXyorSeqM6Mjyspyb9Gl3ht1grXe8TzMwVUFLYwBlV1zYcKfCVxHIaPo8irO1X7+sImu0166pNeK962FxzUx+rJMsvEIhy8mzF//yRI8WBLZvuBS5AH8EJHBb5p6DcdLgNVf3AwQWw6S5ENIw1Nu+eS2p+nm7msRRWP5jbqo8TfwgoellmtHaljlvmQ47kXfZvo9feDd7qZtGvWuX22yZkb+3k0OEfNKZaBKLrfzKU6X5TlmMvyhU7mF6mMdkBwex+NuKhRl1fYVjzD1hk75j70/QgXyjMv9nJpSEIXEt//AVHZTG4lGvAT0l3hPOie/zS0ixEH11+LJvbzsZQXYngggsJ40oCbajRxnvrMEcJQ5Lcxnp/Ov8qTmApOqK+XmLAV/s+MdeeIatFNTk6o9xGar+cB8";

    private BarcodeCountPresenter barcodeCountPresenter;
    private SparkScanPresenter sparkScanPresenter;

    private boolean inBarcodeCountMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize and start the barcode recognition.
        initialize();
    }

    private void initialize() {
        // Create data capture context using your license key.
        DataCaptureContext dataCaptureContext =
            DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);

        // Initialize the camera manager.
        BarcodeCountCameraManager.getInstance().initialize(dataCaptureContext);

        // Initialize the shared barcode manager.
        BarcodeManager.getInstance().initialize(dataCaptureContext);

        // Initialize the shared container for the modes' views.
        FrameLayout container = findViewById(R.id.container);

        // Initialize the presenters which will take care of each mode.
        sparkScanPresenter =
            new SparkScanPresenter(this, dataCaptureContext, container, this);
        barcodeCountPresenter =
            new BarcodeCountPresenter(this, dataCaptureContext, container, this);

        // Enable SparkScan initially.
        sparkScanPresenter.enableSparkScan();
    }

    @Override
    protected void onPause() {
        if (inBarcodeCountMode) {
            barcodeCountPresenter.onPause();
        } else {
            sparkScanPresenter.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (inBarcodeCountMode) {
            barcodeCountPresenter.onResume();
        } else {
            sparkScanPresenter.onResume();
        }

        // Check for camera permission and request it, if it hasn't yet been granted.
        // Once we have the permission the onCameraPermissionGranted() method will be called.
        requestCameraPermission();
    }

    @Override
    protected void onDestroy() {
        sparkScanPresenter.onDestroy();
        barcodeCountPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onCameraPermissionGranted() {
        BarcodeCountCameraManager.getInstance().resumeFrameSource();
    }

    @Override
    public void requestCameraPermission() {
        super.requestCameraPermission();
    }

    @Override
    public void runOnMainThread(Runnable action) {
        runOnUiThread(action);
    }

    @Override
    public void switchToBarcodeCount() {
        sparkScanPresenter.disableSparkScan();
        barcodeCountPresenter.enableBarcodeCount();
        inBarcodeCountMode = true;
    }

    @Override
    public void switchToSparkScan() {
        barcodeCountPresenter.disableBarcodeCount();
        sparkScanPresenter.enableSparkScan();
        inBarcodeCountMode = false;
    }

    @Override
    public void navigateToListScreen() {
        listLauncher.launch(
            ResultsActivity.getIntent(
                MainActivity.this,
                ExtraButtonStyle.RESUME
            )
        );
    }

    /**
     * The launcher to use when starting the result activity clicking the list button
     */
    private final ActivityResultLauncher<Intent> listLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == ResultsActivity.CLEAR_SESSION) {
                barcodeCountPresenter.resetSession();
            }
        });

    @Override
    public void navigateToFinishScreen() {
        exitLauncher.launch(
            ResultsActivity.getIntent(
                MainActivity.this,
                ExtraButtonStyle.NEW_SCAN
            )
        );
    }

    /**
     * The launcher to use when starting the result activity clicking the exit button
     */
    private final ActivityResultLauncher<Intent> exitLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> barcodeCountPresenter.resetSession());
}
