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

package com.scandit.datacapture.receivingsample.barcodecount;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.scandit.datacapture.barcode.count.capture.BarcodeCount;
import com.scandit.datacapture.barcode.count.capture.BarcodeCountListener;
import com.scandit.datacapture.barcode.count.capture.BarcodeCountSession;
import com.scandit.datacapture.barcode.count.capture.BarcodeCountSettings;
import com.scandit.datacapture.barcode.count.ui.view.BarcodeCountView;
import com.scandit.datacapture.barcode.count.ui.view.BarcodeCountViewStyle;
import com.scandit.datacapture.barcode.count.ui.view.BarcodeCountViewUiListener;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.receivingsample.managers.BarcodeCountCameraManager;
import com.scandit.datacapture.receivingsample.managers.BarcodeManager;

import java.util.HashSet;
import java.util.List;

public class BarcodeCountPresenter implements BarcodeCountListener, BarcodeCountViewUiListener {

    private final Context context;
    private final DataCaptureContext dataCaptureContext;
    private final FrameLayout container;
    private final BarcodeCountPresenterActions barcodeCountPresenterView;

    private BarcodeCount barcodeCount;
    private BarcodeCountView barcodeCountView;

    private boolean navigatingInternally = false;

    public BarcodeCountPresenter(
        Context context,
        DataCaptureContext dataCaptureContext,
        FrameLayout container,
        BarcodeCountPresenterActions barcodeCountPresenterView
    ) {
        this.context = context;
        this.dataCaptureContext = dataCaptureContext;
        this.container = container;
        this.barcodeCountPresenterView = barcodeCountPresenterView;
    }

    private void initialize() {
        // The barcode count process is configured through barcode count settings
        // which are then applied to the barcode count instance that manages barcode count.
        BarcodeCountSettings barcodeCountSettings = new BarcodeCountSettings();

        // The settings instance initially has all types of barcodes (symbologies) disabled.
        // For the purpose of this sample we only enable the following symbologies - EAN8,
        // EAN13_UPCA, UPCE, CODE39 and CODE128.
        // In your own app ensure that you only enable the symbologies that your app requires
        // as every additional enabled symbology has an impact on processing times.
        HashSet<Symbology> symbologies = new HashSet<>();
        symbologies.add(Symbology.EAN8);
        symbologies.add(Symbology.EAN13_UPCA);
        symbologies.add(Symbology.UPCE);
        symbologies.add(Symbology.CODE39);
        symbologies.add(Symbology.CODE128);
        barcodeCountSettings.enableSymbologies(symbologies);

        // Create barcode count, but don't attach to context.
        barcodeCount = BarcodeCount.forDataCaptureContext(null, barcodeCountSettings);

        // To visualize the on-going barcode count process on screen, setup a BarcodeCountView
        // that renders the camera preview. The view must be connected to the data capture context
        // and to the barcode count. This is optional, but recommended for better visual feedback.
        barcodeCountView = BarcodeCountView.newInstance(
            context,
            dataCaptureContext,
            barcodeCount,
            BarcodeCountViewStyle.ICON
        );

        // Enable SingleScan button, which we'll use to switch to SparkScan,
        barcodeCountView.setShouldShowSingleScanButton(true);
    }

    public void disableBarcodeCount() {
        // When disabling BarcodeCount before switching to SparkScan,
        // remove all views from the container, all listeners,
        // and detach the mode from the context.
        container.removeAllViews();
        barcodeCountView.setUiListener(null);
        barcodeCount.removeListener(this);
        dataCaptureContext.removeMode(barcodeCount);

        release();
    }

    public void enableBarcodeCount() {
        initialize();

        // Reset camera settings to the default settings for BarcodeCount.
        BarcodeCountCameraManager.getInstance().resetDefaultSettings();

        // Attach BarcodeCount to context. Since both BarcodeCount and SparkScan use
        // barcode scanning and tracking features, they can't be used at the same time,
        // so we only add BarcodeCount to the context when needed.
        dataCaptureContext.addMode(barcodeCount);

        // Check for camera permission every time the mode is enabled.
        // This also turns on the camera, which gets disabled while switching modes.
        barcodeCountPresenterView.requestCameraPermission();

        // Register self as a listener to get informed of tracked barcodes.
        barcodeCount.addListener(this);

        // Register self as a listener to get informed of UI events.
        barcodeCountView.setUiListener(this);

        // Show "clear screen" button
        barcodeCountView.setShouldShowClearHighlightsButton(true);

        // Load already scanned barcodes into the session as additional barcodes.
        loadAllBarcodesAsAdditionalBarcodes();

        // Add the BarcodeCountView to the container.
        container.addView(barcodeCountView);
    }

    private void release() {
        barcodeCountView = null;
        barcodeCount = null;
    }

    public void resetSession() {
        BarcodeManager.getInstance().reset();
        barcodeCount.clearAdditionalBarcodes();
        barcodeCount.reset();
    }

    public void onPause() {
        // Pause camera if the app is going to background,
        // but keep it on if it goes to result screen.
        // That way the session is not lost when coming back from results.
        if (!navigatingInternally) {
            BarcodeCountCameraManager.getInstance().stopFrameSource();
        }

        // Unregister self as listener.
        barcodeCount.removeListener(this);
    }

    public void onResume() {
        // Load already scanned barcodes into the session as additional barcodes.
        if (!navigatingInternally) {
            barcodeCount.reset();
            loadAllBarcodesAsAdditionalBarcodes();
        }
        navigatingInternally = false;

        // Register self as a listener to get informed of tracked barcodes.
        barcodeCount.addListener(this);

        // Enable the mode to start processing frames.
        barcodeCount.setEnabled(true);
    }

    private void loadAllBarcodesAsAdditionalBarcodes() {
        List<Barcode> barcodesToSave = BarcodeManager.getInstance().getAllBarcodes();
        barcodeCount.setAdditionalBarcodes(barcodesToSave);
    }

    public void onDestroy() {
        cleanupBarcodeCount();
    }

    public void cleanupBarcodeCount() {
        disableBarcodeCount();
        barcodeCountView = null;
        barcodeCount = null;
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

    @Override
    public void onListButtonTapped(
        @NonNull BarcodeCountView view
    ) {
        navigatingInternally = true;
        barcodeCountPresenterView.navigateToListScreen();
    }

    @Override
    public void onExitButtonTapped(
        @NonNull BarcodeCountView view
    ) {
        navigatingInternally = true;
        barcodeCountPresenterView.navigateToFinishScreen();
    }

    @Override
    public void onSingleScanButtonTapped(@NonNull BarcodeCountView view) {
        barcodeCountPresenterView.switchToSparkScan();
    }
}
