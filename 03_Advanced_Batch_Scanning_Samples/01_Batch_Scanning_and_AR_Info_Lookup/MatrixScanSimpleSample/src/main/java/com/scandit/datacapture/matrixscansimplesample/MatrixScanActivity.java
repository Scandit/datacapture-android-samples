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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.batch.capture.BarcodeBatch;
import com.scandit.datacapture.barcode.batch.capture.BarcodeBatchListener;
import com.scandit.datacapture.barcode.batch.capture.BarcodeBatchSession;
import com.scandit.datacapture.barcode.batch.capture.BarcodeBatchSettings;
import com.scandit.datacapture.barcode.batch.data.TrackedBarcode;
import com.scandit.datacapture.barcode.batch.ui.overlay.BarcodeBatchBasicOverlay;
import com.scandit.datacapture.barcode.batch.ui.overlay.BarcodeBatchBasicOverlayStyle;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.CameraSettings;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.source.VideoResolution;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.matrixscansimplesample.data.ScanResult;

import java.util.HashSet;

public class MatrixScanActivity extends CameraPermissionActivity
        implements BarcodeBatchListener {

    // Add your license key to `secrets.properties` and it will be automatically added to the BuildConfig field
    // `BuildConfig.SCANDIT_LICENSE_KEY`
    public static final String SCANDIT_LICENSE_KEY = BuildConfig.SCANDIT_LICENSE_KEY;

    private Camera camera;
    private BarcodeBatch barcodeBatch;
    private DataCaptureContext dataCaptureContext;

    private final HashSet<ScanResult> scanResults = new HashSet<>();

    private final ActivityResultLauncher<Intent> resultsLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == ResultsActivity.RESULT_CODE_CLEAN) {
                synchronized (scanResults) {
                    scanResults.clear();
                }
            }
        });

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
                    ScanResult[] resultsArray = new ScanResult[scanResults.size()];
                    int i = 0;
                    for (ScanResult result : scanResults) {
                        resultsArray[i++] = result;
                    }
                    Intent intent = ResultsActivity.getIntent(MatrixScanActivity.this, resultsArray);
                    resultsLauncher.launch(intent);
                }
            }
        });
    }

    private void initialize() {
        // Create data capture context using your license key.
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);

        // Use the recommended camera settings for the BarcodeBatch mode.
        CameraSettings cameraSettings = BarcodeBatch.createRecommendedCameraSettings();
        // Adjust camera settings - set Full HD resolution.
        cameraSettings.setPreferredResolution(VideoResolution.FULL_HD);
        // Use the default camera and set it as the frame source of the context.
        // The camera is off by default and must be turned on to start streaming frames to the data
        // capture context for recognition.
        // See resumeFrameSource and pauseFrameSource below.
        camera = Camera.getDefaultCamera(cameraSettings);
        if (camera != null) {
            dataCaptureContext.setFrameSource(camera);
        } else {
            throw new IllegalStateException("Sample depends on a camera, which failed to initialize.");
        }

        // The barcode tracking process is configured through barcode batch settings
        // which are then applied to the barcode batch instance that manages barcode tracking.
        BarcodeBatchSettings barcodeBatchSettings = new BarcodeBatchSettings();

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

        barcodeBatchSettings.enableSymbologies(symbologies);

        // Create barcode batch and attach to context.
        barcodeBatch =
                BarcodeBatch.forDataCaptureContext(dataCaptureContext, barcodeBatchSettings);

        // Register self as a listener to get informed of tracked barcodes.
        barcodeBatch.addListener(this);

        // To visualize the on-going barcode tracking process on screen, setup a data capture view
        // that renders the camera preview. The view must be connected to the data capture context.
        DataCaptureView dataCaptureView = DataCaptureView.newInstance(this, dataCaptureContext);

        // Add a barcode batch overlay to the data capture view to render the tracked barcodes on
        // top of the video preview. This is optional, but recommended for better visual feedback.
        BarcodeBatchBasicOverlay.newInstance(
            barcodeBatch,
                dataCaptureView,
                BarcodeBatchBasicOverlayStyle.FRAME
        );

        // Add the DataCaptureView to the container.
        FrameLayout container = findViewById(R.id.data_capture_view_container);
        container.addView(dataCaptureView);
    }

    @Override
    protected void onPause() {
        pauseFrameSource();
        super.onPause();
    }

    private void pauseFrameSource() {
        // Switch camera off to stop streaming frames.
        // The camera is stopped asynchronously and will take some time to completely turn off.
        // Until it is completely stopped, it is still possible to receive further results, hence
        // it's a good idea to first disable barcode tracking as well.
        barcodeBatch.setEnabled(false);
        camera.switchToDesiredState(FrameSourceState.OFF, null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check for camera permission and request it, if it hasn't yet been granted.
        // Once we have the permission the onCameraPermissionGranted() method will be called.
        requestCameraPermission();
    }

    @Override
    public void onCameraPermissionGranted() {
        resumeFrameSource();
    }

    private void resumeFrameSource() {
        // Switch camera on to start streaming frames.
        // The camera is started asynchronously and will take some time to completely turn on.
        barcodeBatch.setEnabled(true);
        camera.switchToDesiredState(FrameSourceState.ON, null);
    }

    @Override
    public void onObservationStarted(@NonNull BarcodeBatch barcodeBatch) {
        // Nothing to do.
    }

    @Override
    public void onObservationStopped(@NonNull BarcodeBatch barcodeBatch) {
        // Nothing to do.
    }

    // This function is called whenever objects are updated and it's the right place to react to
    // the tracking results.
    @Override
    public void onSessionUpdated(
            @NonNull BarcodeBatch mode,
            @NonNull BarcodeBatchSession session,
            @NonNull FrameData data
    ) {
        synchronized (scanResults) {
            for (TrackedBarcode trackedBarcode : session.getAddedTrackedBarcodes()) {
                scanResults.add(new ScanResult(trackedBarcode.getBarcode()));
            }
        }
    }

    @Override
    protected void onDestroy() {
        dataCaptureContext.removeCurrentMode();
        super.onDestroy();
    }
}
