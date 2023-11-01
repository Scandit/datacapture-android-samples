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
    private static final String SCANDIT_LICENSE_KEY = "AWHjFzlFHa+fLq/kfS8GCBU/hT60NkQeVGQOWhhtRVcDZxJfsD0OY9NK0YErLuxTtTKLC1BLdrvDdsJ1dnxmcx9fDIeeaQlxawtkiq1pmEFxHOvYa3emcbAfOeiwbFPtQEWCWvdc95KoIFxAuDiYcfccdywzH2KONgwmnV9cEcX11FhIPLtX74RLua7VkOukFfNTOGExxhiCq96qZnzGgrgViuagpL0ekK6xv8K4bYt7lVkxloUMM6dFRSZ4aummJ2Q1uZNR78kSGCpCn/uJjaf/5lyNbYWpnxYvsYRPI7jOFYZykI0nIjhjt/ncukCEsz4BQLAh5hp1qocvQ2+dw3ADD8LJLXcnX7JaCOKV5cfHEHGSLR4moTxNtxPXdUNlM5w75iHZub5BsIfkJCknKrLn5oJ15k5Rx4/JnFj11tGLqtfRs+jdtXSGxAb86BxwPM1mEBO/Va1yV//CGku5UWR5MwspCf7pl8OUH7frkCtV4kDB6y5jusSMSIEGnKCLd2sWKE04mAURrpWt8pgsIB89xXPPTgPh1C+nAeMuuEN3dPYAJYrJKvy44w130JrUvxWLcTM1oFVWikC6CluLC7WGgRhZCew0eROnv9neITolB6Gmy04dlF0euA595dJcw2lLTwwxEydGp5gGIIDtofviho7JdHtPrMer/Ptz1/LOVeF55OY9eg8z1Lq2CkZf6cgWZBPa1uakuZzxWXZUprJMdTquhInmqP4ELLxGXhv+CXoT2n0p022+wyiWAXatmhvcK+n2uCWX30SL0Sri1qPmf6Ldtgqj2aFEMLM+LouJg6Ukv0PKUTXlgPW7L0vYrNGtPjvRlaR7Nwph";

    private DataCaptureManager dataCaptureManager;

    private DataCaptureContext dataCaptureContext;
    private BarcodeSelection barcodeSelection;
    private Camera camera;
    private DataCaptureView dataCaptureView;
    private BarcodeSelectionBasicOverlay overlay;

    private static final int SETTINGS_ACTIVITY_REQUEST_CODE = 100;

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
            startActivityForResult(SettingsActivity.getIntent(this), SETTINGS_ACTIVITY_REQUEST_CODE);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SETTINGS_ACTIVITY_REQUEST_CODE) {
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
        } else {
            super.onActivityResult(requestCode, resultCode, data);
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
