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

package com.scandit.datacapture.barcodeselectionsettingssample;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.SymbologyDescription;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelection;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelectionListener;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelectionSession;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelectionSettings;
import com.scandit.datacapture.barcode.selection.ui.overlay.BarcodeSelectionBasicOverlay;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.SettingsActivity;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.ui.DataCaptureView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class BarcodeScanActivity
        extends CameraPermissionActivity implements BarcodeSelectionListener {

	// There is a Scandit sample license key set below here.
	// This license key is enabled for sample evaluation only.
	// If you want to build your own application, get your license key by signing up for a trial at https://ssl.scandit.com/dashboard/sign-up?p=test
    private static final String SCANDIT_LICENSE_KEY = "AbvELRLKNvXhGsHO0zMIIg85n3IiQdKMA2p5yeVDSOSZSZg/BhX401FXc+2UHPun8Rp2LRpw26tYdgnIJlXiLAtmXfjDZNQzZmrZY2R0QaJaXJC34UtcQE12hEpIYhu+AmjA5cROhJN3CHPoHDns+ho12ibrRAoFrAocoBIwCVzuTRHr0U6pmCKoa/Mn3sNPdINHh97m1X9Al9xjh3VOTNimP6ZjrHLVWEJSOdp2QYOnqn5izP1329PVcZhn8gqlGCRh+LJytbKJYI/KIRbMy3bNOyq5kNnr2IlOqaoXRgYdz2IU+jIWw8Cby9XoSB1zkphiYMmlCUqrDzxLUmTAXF4rSWobiM+OxnoImDqISpunJBQz0a5DSeT5Zf0lwwvXQLX4ghkgXozyYYfYvIKsqxJLZoza8g1BFsJ1i3fb0JYP2Ju209OMN2NTJifAu9ZJjQKGWS76Rmr/jre13jCqGgx5SX9F2lA2ZpF2AEb6rmYYmMtL9CPwWvstM+W295WvscH+gCBccZ9q3rxfIsak6cV2T50/2uBWfJJka6kL9UOjMOG3BOGKx+O+KWT/twwvOC+GcvC8s1qMwGNNM6G+/m7fG5Xtl5wtp3QhpzPJbBHSmlkYbxXQx0SpuWBmvxygyKOi3lUzz3gRzOdykWRXzrhiMAp5bb1y6n6g4O2v2TVgzWWF8vwZ6F60ehYDUq7pbusgT4Fl3fV7fYPgLxMMvXKduMmUlWyGv3CWL9LfvoY/hLl7RxoyUryTMmSfRVBcsKs+MWYJGh1iIvWk1UhOChb9IGI2PzUsHz7+OikuYMjKhR8LZZYalXpPiEVfT66yy75M5DODcjXRoFZU";

    private DataCaptureManager dataCaptureManager;

    private DataCaptureContext dataCaptureContext;
    private BarcodeSelection barcodeSelection;
    private Camera camera;
    private DataCaptureView dataCaptureView;
    private BarcodeSelectionBasicOverlay overlay;

    private final ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            // When returning from the settings screen, update the camera, dataCaptureView, overlay,
            // barcodeSelection and its settings accordingly.
            initializeAndSetupCamera();
            updateBarcodeSelection();
            dataCaptureManager.setupDataCaptureView(dataCaptureView);
            if (overlay != null) {
                dataCaptureView.removeOverlay(overlay);
            }
            overlay = dataCaptureManager.createAndSetupBarcodeSelectionBasicOverlay(
                barcodeSelection, dataCaptureView
            );
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataCaptureManager = DataCaptureManager.getInstance(this);

        // Initialize DataCaptureContext and BarcodeSelection. This can be done only once, as the
        // FrameSource and BarcodeSelectionSettings can be updated by applying them on the same
        // instances.
        initializeAndSetupDataCaptureComponents();
        // Initialize the camera using the currently stored settings.
        initializeAndSetupCamera();
    }

    private void initializeAndSetupDataCaptureComponents() {
        // Create data capture context using your license key.
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);

        // The barcode selection process is configured through barcode selection settings
        // which are then applied to the barcode selection instance that manages barcode recognition.
        // In this case, we're applying the properties set from the settings screen.
        BarcodeSelectionSettings initialSettings = dataCaptureManager.buildBarcodeSelectionSettings();

        // Create new barcode selection mode with the settings created above.
        barcodeSelection = BarcodeSelection.forDataCaptureContext(dataCaptureContext, initialSettings);
        // Update BarcodeSelection properties from the applied settings.
        dataCaptureManager.setupBarcodeSelection(barcodeSelection);

        // Register self as a listener to get informed whenever a new barcode got recognized.
        barcodeSelection.addListener(this);

        // To visualize the on-going barcode selection process on screen, setup a data capture view
        // that renders the camera preview. The view must be connected to the data capture context.
        dataCaptureView = DataCaptureView.newInstance(this, dataCaptureContext);

        // Update DataCaptureView properties from the applied settings.
        dataCaptureManager.setupDataCaptureView(dataCaptureView);

        // Add a barcode selection overlay to the data capture view to render the location of
        // captured barcodes on top of the video preview.
        // This is optional, but recommended for better visual feedback.
        overlay = dataCaptureManager.createAndSetupBarcodeSelectionBasicOverlay(
                barcodeSelection, dataCaptureView
        );

        setContentView(dataCaptureView);
    }

    private void initializeAndSetupCamera() {
        // Use the desired camera with the camera settings retrieved from the settings  and set it
        // as the frame source of the context. The camera is off by default and must be turned on
        // to start streaming frames to the data capture context for recognition.
        // See resumeFrameSource and pauseFrameSource below.
        camera = dataCaptureManager.buildCamera();
        if (camera != null) {
            camera.applySettings(dataCaptureManager.buildCameraSettings());
            dataCaptureContext.setFrameSource(camera);
        } else {
            throw new IllegalStateException("Sample depends on a camera, which failed to initialize.");
        }
    }

    private void updateBarcodeSelection() {
        // The barcode selection process is configured through barcode selection settings
        // which are then applied to the barcode selection instance that manages barcode recognition.
        // In this case, we're applying the properties set from the settings screen.
        BarcodeSelectionSettings settings = dataCaptureManager.buildBarcodeSelectionSettings();
        // Update the barcode selection mode with the settings created above.
        barcodeSelection.applySettings(settings);
        // Update BarcodeSelection properties from the desired settings.
        // This includes the Point of Interest.
        dataCaptureManager.setupBarcodeSelection(barcodeSelection);
    }

    @Override
    protected void onPause() {
        pauseFrameSource();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // It is good practice to properly disable and remove the mode.
        barcodeSelection.setEnabled(false);
        barcodeSelection.removeListener(this);
        dataCaptureContext.removeMode(barcodeSelection);
        super.onDestroy();
    }

    private void pauseFrameSource() {
        // Switch camera off to stop streaming frames.
        barcodeSelection.setEnabled(false);
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
        barcodeSelection.setEnabled(true);
        camera.switchToDesiredState(FrameSourceState.ON, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(
            @NonNull @NotNull MenuItem item
    ) {
        if (item.getItemId() == R.id.menu_settings) {
            settingsLauncher.launch(SettingsActivity.getIntent(this));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSelectionUpdated(
            @NonNull BarcodeSelection barcodeSelection,
            @NonNull BarcodeSelectionSession session,
            @Nullable FrameData frameData
    ) {
        // Check if we have selected a barcode, if that's the case, show a toast with its info.
        List<Barcode> newlySelectedBarcodes = session.getNewlySelectedBarcodes();
        if (newlySelectedBarcodes.isEmpty()) return;

        // Get the human readable name of the symbology and assemble the result to be shown.
        Barcode barcode = newlySelectedBarcodes.get(0);
        String symbologyReadableName = SymbologyDescription.create(barcode.getSymbology()).getReadableName();
        final String result = getString(
                R.string.result_parametrised, symbologyReadableName, barcode.getData(), session.getCount(barcode)
        );

        // This callback may be executed on an arbitrary thread. We post to switch back to the
        // main thread and show a toast.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showResult(result);
            }
        });
    }

    private Toast toast = null;

    private void showResult(String result) {
        if (toast != null) {
            dismissToast();
        }
        toast = Toast.makeText(this, result, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void dismissToast() {
        toast.cancel();
        toast = null;
    }

    @Override
    public void onSessionUpdated(@NonNull BarcodeSelection barcodeSelection,
            @Nullable BarcodeSelectionSession session, @NonNull FrameData data) {}

    @Override
    public void onObservationStarted(@NonNull BarcodeSelection barcodeSelection) {}

    @Override
    public void onObservationStopped(@NonNull BarcodeSelection barcodeSelection) {}
}
