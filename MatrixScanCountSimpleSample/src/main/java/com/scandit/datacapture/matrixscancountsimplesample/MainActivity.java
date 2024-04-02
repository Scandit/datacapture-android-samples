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

package com.scandit.datacapture.matrixscancountsimplesample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scandit.datacapture.barcode.count.capture.BarcodeCount;
import com.scandit.datacapture.barcode.count.capture.BarcodeCountListener;
import com.scandit.datacapture.barcode.count.capture.BarcodeCountSession;
import com.scandit.datacapture.barcode.count.capture.BarcodeCountSettings;
import com.scandit.datacapture.barcode.count.ui.view.BarcodeCountView;
import com.scandit.datacapture.barcode.count.ui.view.BarcodeCountViewListener;
import com.scandit.datacapture.barcode.count.ui.view.BarcodeCountViewStyle;
import com.scandit.datacapture.barcode.count.ui.view.BarcodeCountViewUiListener;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.tracking.data.TrackedBarcode;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.common.async.Callback;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.ui.style.Brush;

import java.util.HashSet;

public class MainActivity extends CameraPermissionActivity
    implements BarcodeCountListener, BarcodeCountViewListener,
        BarcodeCountViewUiListener {

	// There is a Scandit sample license key set below here.
	// This license key is enabled for sample evaluation only.
	// If you want to build your own application, get your license key by signing up for a trial at https://ssl.scandit.com/dashboard/sign-up?p=test
    public static final String SCANDIT_LICENSE_KEY = "Aa2k0xbKMtvDJWNgLU02Cr8aLxUjNtOuqXCjHUxVAUf/d66Y5Tm74sJ+8L0rGQUZ20e52VlMY9I7YW4W13kWbvp36R8jbqQy6yZUGS50G5n4fRItJD6525RcbTYZQjoIGHQqle9jj08ra19ZUy9RliVlOn3hHz4WrGO8vORyATmFXJpULzk0I5RpiT84ckXhG2Ri8jtIzoISX3zsoiLtXVRGjjrkbuGZzGbKA180JKEpdfSQwVyupLti5yNYHAeKihS6IOklCTz8CM1BfRC4zBdIDjbVEJPFgAsLvMU0rTyJhHkB5Ds4wfHbKNFhW0T2XkYLKkvZ7X/HnEVD5oz9Kl4T4rtRkepJfsXUWHUgVugjLO5vqwhMcHNV5XpK2Pk/SLrzGF1PDRu8f4ZhBLrWKknWq+5TSK8GWi4wmGpVvbxqHhLljzOzplYs8I5TtphZ3otJNLs10lhk1YN9cmdaxpdUuF4k0WDU1Qfco75p5G+MBlsAVVFrs0xMF9fSMJkQ+4UU+G+py5781HPkpw4kaGwmJhGrzA/Lbhf4tL+XfynseLw42oygpfVabYEYRHSQx+1j5RpFSR6V9t4jlKsJu2xgYz0A96I82gIHItRRxZkT2oEsZCgYlgCiQsFcsFdo9N9bzDL9mVR5Nj0RPIVvKc01AVtKvXLx86g2rNPv45eBaJFrdsWmv97V8+Pv6M9d+Wr1qcTeT1BY8fvWUEDmU1HF6eCJ1A6cDAM+Nq4sAP9D2lH7D6rHwK+x07F56bMZibLeDoGKanE8PhhamhxBVemE/ByCoMoItBtSbpeBubHVsSHlGF3/AAKi6flY6j0htptgPOM8eOwGXx6YvVxu3KOMF+2RBIQai8LP0YEuhVJ0ST7WX5seeVSu5RMKUx/euHoQB6qID+ydzkXGzYZLTPPskmJSWqrboJQPIjZ/ruCtJepZ/+Lr7g5nCyb01w==";

    private BarcodeCount barcodeCount;
    private DataCaptureContext dataCaptureContext;

    private boolean navigatingInternally = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize and start the barcode recognition.
        initialize();
    }

    private void initialize() {
        // Create data capture context using your license key.
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);

        // Initialize the shared camera manager.
        CameraManager.getInstance().initialize(dataCaptureContext);

        // The barcode count process is configured through barcode count settings
        // which are then applied to the barcode count instance that manages barcode count.
        BarcodeCountSettings barcodeCountSettings = new BarcodeCountSettings();

        // The settings instance initially has all types of barcodes (symbologies) disabled.
        // For the purpose of this sample we enable a very generous set of symbologies.
        // In your own app ensure that you only enable the symbologies that your app requires
        // as every additional enabled symbology has an impact on processing times.
        HashSet<Symbology> symbologies = new HashSet<>();
        symbologies.add(Symbology.EAN13_UPCA);
        symbologies.add(Symbology.EAN8);
        symbologies.add(Symbology.UPCE);
        symbologies.add(Symbology.CODE39);
        symbologies.add(Symbology.CODE128);
        barcodeCountSettings.enableSymbologies(symbologies);

        // Create barcode count and attach to context.
        barcodeCount = BarcodeCount.forDataCaptureContext(dataCaptureContext, barcodeCountSettings);

        // Initialize the shared barcode manager.
        BarcodeManager.getInstance().initialize(barcodeCount);

        // To visualize the on-going barcode count process on screen, setup a BarcodeCountView
        // that renders the camera preview. The view must be connected to the data capture context
        // and to the barcode count. This is optional, but recommended for better visual feedback.
        BarcodeCountView barcodeCountView = BarcodeCountView.newInstance(
                this,
                dataCaptureContext,
                barcodeCount,
                BarcodeCountViewStyle.ICON
        );

        // Register self as a listener to provide custom brushes and get informed of view events.
        barcodeCountView.setListener(this);

        // Register self as a listener to get informed of UI events.
        barcodeCountView.setUiListener(this);

        // Add the BarcodeCountView to the container.
        FrameLayout container = findViewById(R.id.data_capture_view_container);
        container.addView(barcodeCountView);
    }

    @Override
    protected void onPause() {
        // Pause camera if the app is going to background,
        // but keep it on if it goes to result screen.
        // That way the session is not lost when coming back from results.
        if (!navigatingInternally) {
            CameraManager.getInstance().pauseFrameSource();
        }

        // Unregister self as listener.
        barcodeCount.removeListener(this);

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!navigatingInternally) {
            // Load current barcodes as additional barcodes.
            BarcodeManager.getInstance().loadAllBarcodesAsAdditionalBarcodes();
        }

        navigatingInternally = false;

        // Register self as a listener to get informed of tracked barcodes.
        barcodeCount.addListener(this);

        // Enable the mode to start processing frames.
        barcodeCount.setEnabled(true);

        // Check for camera permission and request it, if it hasn't yet been granted.
        // Once we have the permission the onCameraPermissionGranted() method will be called.
        requestCameraPermission();
    }

    @Override
    public void onCameraPermissionGranted() {
        CameraManager.getInstance().resumeFrameSource();
    }

    @Override
    public void onObservationStarted(@NonNull BarcodeCount barcodeTracking) {
        // Not relevant in this sample
    }

    @Override
    public void onObservationStopped(@NonNull BarcodeCount barcodeTracking) {
        // Not relevant in this sample
    }

    // This function is called whenever objects are updated and it's the right place to react to
    // the tracking results.
    @Override
    public void onSessionUpdated(
        @NonNull BarcodeCount mode,
        @NonNull BarcodeCountSession session,
        @NonNull FrameData data
    ) {
        // Not relevant in this sample
    }

    @Override
    public void onScan(
        @NonNull BarcodeCount mode,
        @NonNull BarcodeCountSession session,
        @NonNull FrameData data
    ) {
        BarcodeManager.getInstance().updateWithSession(session);
    }

    @Nullable
    @Override
    public Brush brushForRecognizedBarcode(
            @NonNull BarcodeCountView view, @NonNull TrackedBarcode trackedBarcode) {
        // No need to return a brush here, BarcodeCountViewStyle.ICON style is used
        return null;
    }

    @Nullable
    @Override
    public Brush brushForUnrecognizedBarcode(
            @NonNull BarcodeCountView view, @NonNull TrackedBarcode trackedBarcode) {
        // No need to return a brush here, BarcodeCountViewStyle.ICON style is used
        return null;
    }

    @Nullable
    @Override
    public Brush brushForRecognizedBarcodeNotInList(
            @NonNull BarcodeCountView view, @NonNull TrackedBarcode trackedBarcode) {
        // No need to return a brush here, BarcodeCountViewStyle.ICON style is used
        return null;
    }

    @Override
    public void onListButtonTapped(
        @NonNull BarcodeCountView view
    ) {
        navigatingInternally = true;
        listLauncher.launch(
            ResultsActivity.getIntent(
                MainActivity.this,
                BarcodeManager.getInstance().getScanResults(),
                ResultsActivity.DoneButtonStyle.RESUME
            )
        );
    }

    /**
     * The launcher to use when starting the result activity clicking the list button
     */
    ActivityResultLauncher<Intent> listLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == ResultsActivity.CLEAR_SESSION) {
                resetSession();
            }
        });

    @Override
    public void onExitButtonTapped(
        @NonNull BarcodeCountView view
    ) {
        navigatingInternally = true;
        exitLauncher.launch(
            ResultsActivity.getIntent(
                MainActivity.this,
                BarcodeManager.getInstance().getScanResults(),
                ResultsActivity.DoneButtonStyle.NEW_SCAN
            )
        );
    }

    /**
     * The launcher to use when starting the result activity clicking the exit button
     */
    ActivityResultLauncher<Intent> exitLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> resetSession());

    private void resetSession() {
        BarcodeManager.getInstance().reset();
        barcodeCount.clearAdditionalBarcodes();
        barcodeCount.reset();
    }

    @Override
    public void onRecognizedBarcodeTapped(
            @NonNull BarcodeCountView view, @NonNull TrackedBarcode trackedBarcode) {
        // Not relevant in this sample
    }

    @Override
    public void onUnrecognizedBarcodeTapped(
            @NonNull BarcodeCountView view, @NonNull TrackedBarcode trackedBarcode) {
        // Not relevant in this sample
    }

    @Override
    public void onFilteredBarcodeTapped(
            @NonNull BarcodeCountView view, @NonNull TrackedBarcode filteredBarcode) {
        // Not relevant in this sample
    }

    @Override
    public void onRecognizedBarcodeNotInListTapped(
            @NonNull BarcodeCountView view, @NonNull TrackedBarcode trackedBarcode) {
        // Not relevant in this sample
    }

    @Override
    public void onSingleScanButtonTapped(@NonNull BarcodeCountView view) {
        // Not relevant in this sample
    }

    @Override
    public void onCaptureListCompleted(@NonNull BarcodeCountView view) {
        // Not relevant in this sample
    }

    @Override
    protected void onDestroy() {
        dataCaptureContext.removeMode(barcodeCount);
        super.onDestroy();
    }
}
