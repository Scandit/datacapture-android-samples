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
    public static final String SCANDIT_LICENSE_KEY = "Aa2k0xbKMtvDJWNgLU02Cr8aLxUjNtOuqXCjHUxVAUf/d66Y5Tm74sJ+8L0rGQUZ20e52VlMY9I7YW4W13kWbvp36R8jbqQy6yZUGS50G5n4fRItJD6525RcbTYZQjoIGHQqle9jj08ra19ZUy9RliVlOn3hHz4WrGO8vORyATmFXJpULzk0I5RpiT84ckXhG2Ri8jtIzoISX3zsoiLtXVRGjjrkbuGZzGbKA180JKEpdfSQwVyupLti5yNYHAeKihS6IOklCTz8CM1BfRC4zBdIDjbVEJPFgAsLvMU0rTyJhHkB5Ds4wfHbKNFhW0T2XkYLKkvZ7X/HnEVD5oz9Kl4T4rtRkepJfsXUWHUgVugjLO5vqwhMcHNV5XpK2Pk/SLrzGF1PDRu8f4ZhBLrWKknWq+5TSK8GWi4wmGpVvbxqHhLljzOzplYs8I5TtphZ3otJNLs10lhk1YN9cmdaxpdUuF4k0WDU1Qfco75p5G+MBlsAVVFrs0xMF9fSMJkQ+4UU+G+py5781HPkpw4kaGwmJhGrzA/Lbhf4tL+XfynseLw42oygpfVabYEYRHSQx+1j5RpFSR6V9t4jlKsJu2xgYz0A96I82gIHItRRxZkT2oEsZCgYlgCiQsFcsFdo9N9bzDL9mVR5Nj0RPIVvKc01AVtKvXLx86g2rNPv45eBaJFrdsWmv97V8+Pv6M9d+Wr1qcTeT1BY8fvWUEDmU1HF6eCJ1A6cDAM+Nq4sAP9D2lH7D6rHwK+x07F56bMZibLeDoGKanE8PhhamhxBVemE/ByCoMoItBtSbpeBubHVsSHlGF3/AAKi6flY6j0htptgPOM8eOwGXx6YvVxu3KOMF+2RBIQai8LP0YEuhVJ0ST7WX5seeVSu5RMKUx/euHoQB6qID+ydzkXGzYZLTPPskmJSWqrboJQPIjZ/ruCtJepZ/+Lr7g5nCyb01w==";

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
