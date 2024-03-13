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

package com.scandit.datacapture.receivingsample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.receivingsample.barcodecount.BarcodeCountPresenter;
import com.scandit.datacapture.receivingsample.barcodecount.BarcodeCountPresenterActions;
import com.scandit.datacapture.receivingsample.managers.BarcodeCountCameraManager;
import com.scandit.datacapture.receivingsample.results.ExtraButtonStyle;
import com.scandit.datacapture.receivingsample.results.ResultsActivity;
import com.scandit.datacapture.receivingsample.sparkscan.SparkScanPresenter;
import com.scandit.datacapture.receivingsample.sparkscan.SparkScanPresenterActions;

public class MainActivity extends CameraPermissionActivity
    implements SparkScanPresenterActions, BarcodeCountPresenterActions {

	// There is a Scandit sample license key set below here.
	// This license key is enabled for sample evaluation only.
	// If you want to build your own application, get your license key by signing up for a trial at https://ssl.scandit.com/dashboard/sign-up?p=test
    public static final String SCANDIT_LICENSE_KEY = "AbvELRLKNvXhGsHO0zMIIg85n3IiQdKMA2p5yeVDSOSZSZg/BhX401FXc+2UHPun8Rp2LRpw26tYdgnIJlXiLAtmXfjDZNQzZmrZY2R0QaJaXJC34UtcQE12hEpIYhu+AmjA5cROhJN3CHPoHDns+ho12ibrRAoFrAocoBIwCVzuTRHr0U6pmCKoa/Mn3sNPdINHh97m1X9Al9xjh3VOTNimP6ZjrHLVWEJSOdp2QYOnqn5izP1329PVcZhn8gqlGCRh+LJytbKJYI/KIRbMy3bNOyq5kNnr2IlOqaoXRgYdz2IU+jIWw8Cby9XoSB1zkphiYMmlCUqrDzxLUmTAXF4rSWobiM+OxnoImDqISpunJBQz0a5DSeT5Zf0lwwvXQLX4ghkgXozyYYfYvIKsqxJLZoza8g1BFsJ1i3fb0JYP2Ju209OMN2NTJifAu9ZJjQKGWS76Rmr/jre13jCqGgx5SX9F2lA2ZpF2AEb6rmYYmMtL9CPwWvstM+W295WvscH+gCBccZ9q3rxfIsak6cV2T50/2uBWfJJka6kL9UOjMOG3BOGKx+O+KWT/twwvOC+GcvC8s1qMwGNNM6G+/m7fG5Xtl5wtp3QhpzPJbBHSmlkYbxXQx0SpuWBmvxygyKOi3lUzz3gRzOdykWRXzrhiMAp5bb1y6n6g4O2v2TVgzWWF8vwZ6F60ehYDUq7pbusgT4Fl3fV7fYPgLxMMvXKduMmUlWyGv3CWL9LfvoY/hLl7RxoyUryTMmSfRVBcsKs+MWYJGh1iIvWk1UhOChb9IGI2PzUsHz7+OikuYMjKhR8LZZYalXpPiEVfT66yy75M5DODcjXRoFZU";

    private BarcodeCountPresenter barcodeCountPresenter;
    private SparkScanPresenter sparkScanPresenter;

    private boolean inBarcodeCountMode = false;

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

    /**
     * The launcher to use when starting the result activity clicking the exit button
     */
    private final ActivityResultLauncher<Intent> exitLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> barcodeCountPresenter.resetSession()
    );

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

    @Override
    public void navigateToFinishScreen() {
        exitLauncher.launch(
            ResultsActivity.getIntent(
                MainActivity.this,
                ExtraButtonStyle.NEW_SCAN
            )
        );
    }
}
