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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scandit.datacapture.barcode.count.capture.BarcodeCount;
import com.scandit.datacapture.barcode.count.capture.BarcodeCountBasicOverlayStyle;
import com.scandit.datacapture.barcode.count.capture.BarcodeCountListener;
import com.scandit.datacapture.barcode.count.capture.BarcodeCountSession;
import com.scandit.datacapture.barcode.count.capture.BarcodeCountSettings;
import com.scandit.datacapture.barcode.count.ui.overlay.BarcodeCountBasicOverlay;
import com.scandit.datacapture.barcode.count.ui.overlay.BarcodeCountBasicOverlayListener;
import com.scandit.datacapture.barcode.count.ui.overlay.BarcodeCountBasicOverlayUiListener;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.tracking.data.TrackedBarcode;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.core.ui.style.Brush;
import com.scandit.datacapture.receivingsample.data.ScanDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class MainActivity extends CameraPermissionActivity
    implements BarcodeCountListener, BarcodeCountBasicOverlayListener,
        BarcodeCountBasicOverlayUiListener {

    // Enter your Scandit License key here.
    // Your Scandit License key is available via your Scandit SDK web account.
    public static final String SCANDIT_LICENSE_KEY = "-- ENTER YOUR SCANDIT LICENSE KEY HERE --";

    private BarcodeCount barcodeCount;
    private DataCaptureContext dataCaptureContext;
    private BarcodeCountBasicOverlay overlay;

    private boolean navigatingInternally = false;

    private Collection<TrackedBarcode> scannedBarcodes = new ArrayList<>();

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

        // Register self as a listener to get informed of tracked barcodes.
        barcodeCount.addListener(this);

        // To visualize the on-going barcode count process on screen, setup a data capture view
        // that renders the camera preview. The view must be connected to the data capture context.
        DataCaptureView dataCaptureView = DataCaptureView.newInstance(this, dataCaptureContext);

        // Add a barcode count basic overlay to the data capture view to render the tracked barcodes on
        // top of the video preview. This is optional, but recommended for better visual feedback.
        overlay = BarcodeCountBasicOverlay.newInstance(
            barcodeCount,
            dataCaptureView,
            BarcodeCountBasicOverlayStyle.ICON
        );

        // Register self as a listener to provide custom brushes and get informed of overlay events.
        overlay.setListener(this);

        // Register self as a listener to get informed of UI events.
        overlay.setUiListener(this);

        // Add the DataCaptureView to the container.
        FrameLayout container = findViewById(R.id.data_capture_view_container);
        container.addView(dataCaptureView);
    }

    @Override
    protected void onPause() {
        // Pause camera if the app is going to background,
        // but keep it on if it goes to result screen.
        // That way the session is not lost when coming back from results.
        if (!navigatingInternally) {
            CameraManager.getInstance().pauseFrameSource();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigatingInternally = false;

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
        scannedBarcodes = session.getTrackedBarcodes().values();
    }

    @Nullable
    @Override
    public Brush brushForTrackedBarcode(
        @NonNull BarcodeCountBasicOverlay overlay,
        @NonNull TrackedBarcode trackedBarcode
    ) {
        // No need to return a brush here, BarcodeCountBasicOverlayStyle.ICON style is used
        return null;
    }

    @Nullable
    @Override
    public Brush brushForUntrackedBarcode(
        @NonNull BarcodeCountBasicOverlay overlay,
        @NonNull TrackedBarcode trackedBarcode
    ) {
        // No need to return a brush here, BarcodeCountBasicOverlayStyle.ICON style is used
        return null;
    }

    @Override
    public void onListButtonTapped(
        @NonNull BarcodeCountBasicOverlay overlay
    ) {
        navigatingInternally = true;
        listLauncher.launch(
            ResultsActivity.getIntent(
                MainActivity.this,
                getScanResults(),
                ResultsActivity.DoneButtonStyle.RESUME
            )
        );
    }

    private HashMap<String, ScanDetails> getScanResults() {
        final Collection<TrackedBarcode> barcodes = scannedBarcodes;
        HashMap<String, ScanDetails> scanResults = new HashMap<>();

        for (TrackedBarcode trackedBarcode : barcodes) {
            String barcodeData = trackedBarcode.getBarcode().getData();
            if (barcodeData == null) continue;

            ScanDetails scanDetails = scanResults.get(barcodeData);
            if (scanDetails != null) {
                scanDetails.increaseQuantity();
                scanResults.put(barcodeData, scanDetails);
            } else {
                scanResults.put(barcodeData, new ScanDetails(barcodeData));
            }
        }

        return scanResults;
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
        @NonNull BarcodeCountBasicOverlay overlay
    ) {
        navigatingInternally = true;
        exitLauncher.launch(
            ResultsActivity.getIntent(
                MainActivity.this,
                getScanResults(),
                ResultsActivity.DoneButtonStyle.NEW_SCAN
            )
        );
    }

    /**
     * The launcher to use when starting the result activity clicking the exit button
     */
    ActivityResultLauncher<Intent> exitLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            resetSession();
        });

    private void resetSession() {
        scannedBarcodes.clear();
        barcodeCount.reset();
    }

    @Override
    public void onTrackedBarcodeTapped(
        @NonNull BarcodeCountBasicOverlay overlay,
        @NonNull TrackedBarcode trackedBarcode
    ) {
        // Not relevant in this sample
    }

    @Override
    public void onUntrackedBarcodeTapped(
        @NonNull BarcodeCountBasicOverlay overlay,
        @NonNull TrackedBarcode trackedBarcode
    ) {
        // Not relevant in this sample
    }

    @Override
    protected void onDestroy() {
        dataCaptureContext.removeMode(barcodeCount);
        super.onDestroy();
    }
}
