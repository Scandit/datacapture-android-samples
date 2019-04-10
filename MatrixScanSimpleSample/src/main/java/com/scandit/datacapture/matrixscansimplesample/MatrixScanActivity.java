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

package com.scandit.datacapture.matrixscansimplesample;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTracking;
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingListener;
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingSession;
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingSettings;
import com.scandit.datacapture.barcode.tracking.data.TrackedBarcode;
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingBasicOverlay;
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingBasicOverlayListener;
import com.scandit.datacapture.matrixscansimplesample.data.ScanResult;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.CameraSettings;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.source.VideoResolution;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.core.ui.style.Brush;

import java.util.HashSet;

public class MatrixScanActivity extends AppCompatActivity implements BarcodeTrackingListener {

    // Enter your Scandit License key here.
    // Your Scandit License key is available via your Scandit SDK web account.
    public static final String scanditLicenseKey = "-- ENTER YOUR SCANDIT LICENSE KEY HERE --";

    private static final int CAMERA_PERMISSION_REQUEST = 0;
    public static final int REQUEST_CODE_SCAN_RESULTS = 1;

    private Camera camera;
    private BarcodeTracking barcodeTracking;
    private DataCaptureContext dataCaptureContext;

    private boolean deniedCameraAccess = false;
    private boolean paused = true;

    private final HashSet<ScanResult> scanResults = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix_scan);

        // Initialize and start the barcode recognition.
        initialize();

        Button doneButton = findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (scanResults) {
                    // Show new screen displaying a list of all barcodes that have been scanned.
                    Intent intent = ResultsActivity.getIntent(
                            MatrixScanActivity.this, scanResults);
                    startActivityForResult(intent, REQUEST_CODE_SCAN_RESULTS);
                }
            }
        });
    }

    private void initialize() {
        dataCaptureContext = DataCaptureContext.forLicenseKey(scanditLicenseKey);

        // Device's camera will serve as a frame source.
        camera = Camera.getDefaultCamera();
        if (camera != null) {
            // Use the settings recommended by barcode tracking.
            CameraSettings cameraSettings = BarcodeTracking.createRecommendedCameraSettings();
            // Adjust camera settings - set HD resolution.
            cameraSettings.setPreferredResolution(VideoResolution.HD);
            camera.applySettings(cameraSettings, null);
            dataCaptureContext.setFrameSource(camera, null);
        }

        // The scanning behavior is configured through barcode tracking settings. We start with 
        // empty settings and enable a bunch of symbologies.
        // Note: in order to achieve the best possible performance in your own apps,
        // only enable the symbologies you actually need.
        BarcodeTrackingSettings barcodeTrackingSettings = new BarcodeTrackingSettings();
        HashSet<Symbology> symbologies = new HashSet<>();

        symbologies.add(Symbology.EAN13_UPCA);
        symbologies.add(Symbology.EAN8);
        symbologies.add(Symbology.UPCE);
        symbologies.add(Symbology.CODE39);
        symbologies.add(Symbology.CODE128);

        barcodeTrackingSettings.enableSymbologies(symbologies);

        // Set the expected number of objects that can be recognized at one time.
        barcodeTrackingSettings.setExpectedNumberOfBarcodesPerFrame(8);

        // Create barcode tracking and attach to context.
        barcodeTracking = BarcodeTracking.forContext(dataCaptureContext, barcodeTrackingSettings);
        barcodeTracking.addListener(this);
        barcodeTracking.setEnabled(true);

        // Create a view.
        DataCaptureView dataCaptureView = DataCaptureView.newInstance(this, dataCaptureContext);

        // Add overlay to display recognised barcodes.
        BarcodeTrackingBasicOverlay overlay =
                BarcodeTrackingBasicOverlay.newInstance(barcodeTracking, dataCaptureView);

        // Configure how barcodes are highlighted - apply default brush or create your own.
        // final Brush defaultBrush = new Brush(Color.BLUE, Color.RED, 5f);
        final Brush defaultBrush = overlay.getDefaultBrush();
        overlay.setDefaultBrush(defaultBrush);

        // Modify brush dynamically.
        int rejectedFillColor = getResources().getColor(R.color.barcode_rejected);
        int rejectedBorderColor = getResources().getColor(R.color.barcode_rejected_border);
        final Brush rejectedBrush = new Brush(rejectedFillColor, rejectedBorderColor, 1f);
        overlay.setListener(new BarcodeTrackingBasicOverlayListener() {
            @Override
            @NonNull
            public Brush brushForTrackedBarcode(
                    @NonNull BarcodeTrackingBasicOverlay overlay, @NonNull TrackedBarcode trackedBarcode) {
                if (isValidBarcode(trackedBarcode.getBarcode())) {
                    return defaultBrush;
                } else {
                    return rejectedBrush;
                }
            }

            @Override
            public void onTrackedBarcodeTapped(
                    @NonNull BarcodeTrackingBasicOverlay overlay, @NonNull TrackedBarcode trackedBarcode) {
                // Handle barcode click if necessary.
            }
        });

        // Add view to the container.
        FrameLayout container = findViewById(R.id.data_capture_view_container);
        container.addView(dataCaptureView);
    }

    @Override
    protected void onPause() {
        pauseFrameSource();
        super.onPause();
    }

    private void pauseFrameSource() {
        paused = true;
        camera.switchToDesiredState(FrameSourceState.OFF, null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        paused = false;
        // Handle permissions for Marshmallow and onwards...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            grantCameraPermissionsThenStartScanning();
        } else {
            // Once the activity is in the foreground again, restart scanning.
            resumeFrameSource();
        }
    }

    private void resumeFrameSource() {
        camera.switchToDesiredState(FrameSourceState.ON, null);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void grantCameraPermissionsThenStartScanning() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // We already have the permission.
            resumeFrameSource();
        } else if (!deniedCameraAccess) {
            // It's pretty clear for why the camera is required. We don't need to give
            // a detailed reason.
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                deniedCameraAccess = false;
                if (!paused) {
                    resumeFrameSource();
                }
            } else {
                deniedCameraAccess = true;
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SCAN_RESULTS
                && resultCode == ResultsActivity.RESULT_CODE_CLEAN) {
            synchronized (scanResults) {
                scanResults.clear();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onObservationStarted(@NonNull BarcodeTracking barcodeTracking) {
        // Nothing to do.
    }

    @Override
    public void onObservationStopped(@NonNull BarcodeTracking barcodeTracking) {
        // Nothing to do.
    }

    @Override
    public void onSessionUpdated(
            @NonNull BarcodeTracking mode, @NonNull BarcodeTrackingSession session, @NonNull FrameData data) {
        synchronized (scanResults) {
            for (TrackedBarcode trackedBarcode : session.getAddedTrackedBarcodes()) {
                if (isValidBarcode(trackedBarcode.getBarcode())) {
                    scanResults.add(new ScanResult(trackedBarcode.getBarcode()));
                }
            }
        }
    }

    // Method with custom logic for accepting/rejecting recognized barcodes.
    private boolean isValidBarcode(Barcode barcode) {
        // Reject invalid barcodes.
        if (barcode.getData().isEmpty()) return false;

        // Reject barcodes based on your logic.
        if (barcode.getData().startsWith("7")) return false;

        return true;
    }

    @Override
    protected void onDestroy() {
        dataCaptureContext.removeMode(barcodeTracking);
        super.onDestroy();
    }
}
