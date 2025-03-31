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

package com.scandit.datacapture.matrixscancounttotemappingsample;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.scandit.datacapture.barcode.count.capture.BarcodeCount;
import com.scandit.datacapture.barcode.count.capture.BarcodeCountListener;
import com.scandit.datacapture.barcode.count.capture.BarcodeCountSession;
import com.scandit.datacapture.barcode.count.capture.BarcodeCountSettings;
import com.scandit.datacapture.barcode.count.capture.map.BarcodeCountMappingFlowSettings;
import com.scandit.datacapture.barcode.count.capture.map.BarcodeSpatialGrid;
import com.scandit.datacapture.barcode.count.ui.view.BarcodeCountView;
import com.scandit.datacapture.barcode.count.ui.view.BarcodeCountViewStyle;
import com.scandit.datacapture.barcode.count.ui.view.BarcodeCountViewUiListener;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends CameraPermissionActivity
    implements BarcodeCountListener, BarcodeCountViewUiListener {

    // Add your license key to `secrets.properties` and it will be automatically added to the BuildConfig field
    // `BuildConfig.SCANDIT_LICENSE_KEY`
    public static final String SCANDIT_LICENSE_KEY = BuildConfig.SCANDIT_LICENSE_KEY;

    private BarcodeCount barcodeCount;
    private DataCaptureContext dataCaptureContext;
    private FrameLayout container;

    private AtomicBoolean generateMapOnNextFrame = new AtomicBoolean(false);

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

        // Mapping must be enabled in order to use the tote mapping feature.
        barcodeCountSettings.setMappingEnabled(true);

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
        symbologies.add(Symbology.QR);
        symbologies.add(Symbology.DATA_MATRIX);
        symbologies.add(Symbology.ARUCO);
        barcodeCountSettings.enableSymbologies(symbologies);

        // Create barcode count and attach to context.
        barcodeCount = BarcodeCount.forDataCaptureContext(dataCaptureContext, barcodeCountSettings);

        container = findViewById(R.id.data_capture_view_container);
    }

    private BarcodeCountView createBarcodeCountView() {
        // Create mapping flow settings. Here we use the default ones.
        BarcodeCountMappingFlowSettings mappingFlowSettings = new BarcodeCountMappingFlowSettings();

        // To visualize the on-going barcode count process on screen, setup a BarcodeCountView
        // that renders the camera preview. The view must be connected to the data capture context
        // and to the barcode count. This is optional, but recommended for better visual feedback.
        // For tote mapping, we use a specialized constructor that creates a BarcodeCountView
        // instance specifically configured for mapping.
        BarcodeCountView barcodeCountView = BarcodeCountView.newInstanceForMapping(
            this,
            dataCaptureContext,
            barcodeCount,
            BarcodeCountViewStyle.ICON,
            mappingFlowSettings
        );

        // Disable the toolbar, not needed for this sample.
        barcodeCountView.setShouldShowToolbar(false);

        // Enable tap-to-uncount, very useful for discarding unneeded barcodes.
        barcodeCountView.setTapToUncountEnabled(true);

        // Register self as a listener to get informed of UI events.
        barcodeCountView.setUiListener(this);

        // This allows the app to keep receiving session updates after the Finish button
        // has been tapped.
        barcodeCountView.setShouldDisableModeOnExitButtonTapped(false);

        return barcodeCountView;
    }

    @Override
    protected void onPause() {
        // Pause camera when the app is going to background.
        CameraManager.getInstance().pauseFrameSource();

        // Unregister self as listener.
        barcodeCount.removeListener(this);

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register self as a listener to get informed of tracked barcodes.
        barcodeCount.addListener(this);

        // Enable the mode to start processing frames.
        barcodeCount.setEnabled(true);

        // Add the BarcodeCountView to the container.
        container.removeAllViews();
        container.addView(createBarcodeCountView());

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
        if (generateMapOnNextFrame.compareAndSet(true, false)) {
            if (session.getRecognizedBarcodes().isEmpty()) {
                runOnUiThread(this::showErrorDialog);
                return;
            }

            // Retrieve the spatial map.
            BarcodeSpatialGrid spatialMap = session.getSpatialMap(4, 2);
            if (spatialMap == null) {
                runOnUiThread(this::showErrorDialog);
                return;
            }

            // Store the spatial map in a shared singleton object.
            SpatialGridManager.getInstance().setBarcodeSpatialGrid(spatialMap);

            // Navigate to map grid activity, which will get the spatial map from the singleton.
            Intent intent = new Intent(this, MapGridActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onScan(
        @NonNull BarcodeCount mode,
        @NonNull BarcodeCountSession session,
        @NonNull FrameData data
    ) {
        // Not relevant in this sample
    }

    @Override
    public void onListButtonTapped(
        @NonNull BarcodeCountView view
    ) {
        // Not relevant in this sample
    }

    @Override
    public void onExitButtonTapped(
        @NonNull BarcodeCountView view
    ) {
        // Trigger grid map creation on the next frame.
        // This is important because we need the session for creating the map.
        generateMapOnNextFrame.set(true);
    }

    @Override
    public void onSingleScanButtonTapped(@NonNull BarcodeCountView view) {
        // Not relevant in this sample
    }

    @Override
    protected void onDestroy() {
        dataCaptureContext.removeCurrentMode();
        super.onDestroy();
    }

    @UiThread
    private void showErrorDialog() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.error_dialog_title)
            .setMessage(R.string.error_dialog_message)
            .setPositiveButton(
                R.string.error_dialog_button_confirm,
                (dialog, which) -> {
                    dialog.dismiss();
                    recreate();// Recreate the activity to restart the flow.
                }
            )
            .show();
    }
}
