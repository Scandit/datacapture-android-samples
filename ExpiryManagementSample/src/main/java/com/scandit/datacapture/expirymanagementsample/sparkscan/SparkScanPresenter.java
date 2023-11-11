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

package com.scandit.datacapture.expirymanagementsample.sparkscan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.spark.capture.SparkScan;
import com.scandit.datacapture.barcode.spark.capture.SparkScanListener;
import com.scandit.datacapture.barcode.spark.capture.SparkScanSession;
import com.scandit.datacapture.barcode.spark.capture.SparkScanSettings;
import com.scandit.datacapture.barcode.spark.capture.SparkScanViewUiListener;
import com.scandit.datacapture.barcode.spark.feedback.SparkScanViewFeedback;
import com.scandit.datacapture.barcode.spark.ui.SparkScanCoordinatorLayout;
import com.scandit.datacapture.barcode.spark.ui.SparkScanView;
import com.scandit.datacapture.barcode.spark.ui.SparkScanViewSettings;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.time.TimeInterval;
import com.scandit.datacapture.expirymanagementsample.R;
import com.scandit.datacapture.expirymanagementsample.managers.BarcodeManager;
import com.scandit.datacapture.expirymanagementsample.results.ExtraButtonStyle;
import com.scandit.datacapture.expirymanagementsample.results.FeedbackCallback;
import com.scandit.datacapture.expirymanagementsample.results.ResultsListPresenter;

import java.util.HashSet;
import java.util.List;

public class SparkScanPresenter implements SparkScanListener, SparkScanViewUiListener {

    private final FrameLayout container;
    private final SparkScanPresenterView sparkScanPresenterView;

    private SparkScan sparkScan;
    private SparkScanView sparkScanView;
    private SparkScanCoordinatorLayout sparkScanLayout;

    private final SparkScanFeedbackCallback feedbackCallback;

    private final ResultsListPresenter resultsListPresenter;

    public SparkScanPresenter(
        Context context,
        DataCaptureContext dataCaptureContext,
        FrameLayout container,
        SparkScanPresenterView sparkScanPresenterView
    ) {
        this.container = container;
        this.sparkScanPresenterView = sparkScanPresenterView;

        feedbackCallback = new SparkScanFeedbackCallback();

        // The spark scan process is configured through SparkScan settings
        // which are then applied to the spark scan instance that manages the spark scan.
        SparkScanSettings sparkScanSettings = new SparkScanSettings();

        // The settings instance initially has all types of barcodes (symbologies) disabled.
        // For the purpose of this sample we only enable DataMatrix and Code 128 symbologies.
        // In your own app ensure that you only enable the symbologies that your app requires
        // as every additional enabled symbology has an impact on processing times.
        HashSet<Symbology> symbologies = new HashSet<>();
        symbologies.add(Symbology.DATA_MATRIX);
        symbologies.add(Symbology.CODE128);
        sparkScanSettings.enableSymbologies(symbologies);

        // Create the spark scan instance.
        // Spark scan will automatically apply and maintain the optimal camera settings.
        sparkScan = new SparkScan(sparkScanSettings);

        // The SparkScanCoordinatorLayout container will make sure that the main layout of the view
        // will not break when the SparkScanView will be attached.
        // When creating the SparkScanView instance use the SparkScanCoordinatorLayout
        // as a parent view.
        sparkScanLayout = (SparkScanCoordinatorLayout) LayoutInflater.from(context)
            .inflate(R.layout.view_spark_scan, container, false);

        // Find the container for the results list in the SparkScan layout,
        // and initialize the results list presenter with it.
        ViewGroup listContainer = sparkScanLayout.findViewById(R.id.list_container);
        resultsListPresenter = new ResultsListPresenter(
            context,
            listContainer,
            ExtraButtonStyle.NONE,
            true
        );

        // You can customize the SparkScanView using SparkScanViewSettings.
        SparkScanViewSettings settings = new SparkScanViewSettings();

        // Creating the instance of SparkScanView. The instance will be automatically added
        // to the container.
        sparkScanView =
            SparkScanView.newInstance(sparkScanLayout, dataCaptureContext, sparkScan, settings);

        // Enable BarcodeCount button
        sparkScanView.setBarcodeCountButtonVisible(true);

        // Disable FastFind button
        sparkScanView.setFastFindButtonVisible(false);
    }

    public void disableSparkScan() {
        // When disabling SparkScan before switching to BarcodeCount,
        // remove all views from the container and all listeners.
        container.removeAllViews();
        sparkScanView.setListener(null);
        sparkScan.removeListener(this);
    }

    public void enableSparkScan() {
        // Check for camera permission every time the mode is enabled.
        // This also turns on the camera, which gets disabled while switching modes.
        sparkScanPresenterView.requestCameraPermission();

        // Register self as a listener to get informed of tracked barcodes.
        sparkScan.addListener(this);

        // Register self as a listener to get informed of UI events.
        sparkScanView.setListener(this);

        // Add the SparkScanLayout to the container.
        container.addView(sparkScanLayout);

        // Refresh the list with any changes from BarcodeCount mode.
        resultsListPresenter.refresh();
    }

    public void onPause() {
        sparkScanView.onPause();
    }

    public void onResume() {
        sparkScanView.onResume();
    }

    public void onDestroy() {
        cleanupSparkScan();
    }

    public void cleanupSparkScan() {
        disableSparkScan();
        sparkScan.removeListener(this);
        sparkScanLayout.removeAllViews();
        sparkScanLayout = null;
        sparkScanView = null;
        sparkScan = null;
    }

    @Override
    public void onSessionUpdated(
        @NonNull SparkScan sparkScan, @NonNull SparkScanSession session,
        @Nullable FrameData data
    ) {
        // Not relevant in this sample
    }

    @Override
    public void onBarcodeScanned(
        @NonNull SparkScan sparkScan, @NonNull SparkScanSession session,
        @Nullable FrameData data
    ) {
        List<Barcode> barcodes = session.getNewlyRecognizedBarcodes();
        Barcode barcode = barcodes.get(0);

        if (isValidBarcode(barcode)) {
            validBarcodeScanned(barcode);
        } else {
            invalidBarcodeScanned();
        }
    }

    private boolean isValidBarcode(Barcode barcode) {
        return barcode.getData() != null;
    }

    private void validBarcodeScanned(Barcode barcode) {
        // Add a new valid barcode to the BarcodeManager and to the list presenter.
        sparkScanPresenterView.runOnMainThread(() -> {
            BarcodeManager.getInstance().saveBarcode(barcode);
            resultsListPresenter.addToList(barcode, feedbackCallback);
        });
    }

    private void invalidBarcodeScanned() {
        // Emit sound and vibration feedback
        sparkScanView.emitFeedback(
            new SparkScanViewFeedback.Error("There was an error scanning the barcode.",
                TimeInterval.seconds(60f)));
    }

    @Override
    public void onFastFindButtonTap(@NonNull SparkScanView view) {
        // Not relevant in this sample
    }

    @Override
    public void onBarcodeCountButtonTap(@NonNull SparkScanView view) {
        sparkScanPresenterView.switchToBarcodeCount();
    }

    private class SparkScanFeedbackCallback implements FeedbackCallback {

        @Override
        public void emitFeedback(Context context, Barcode barcode, boolean isExpired) {
            // Emit sound and vibration feedback
            if (isExpired) {
                sparkScanView.emitFeedback(
                    new SparkScanViewFeedback.Error(
                        context.getString(R.string.item_expired),
                        TimeInterval.seconds(60f)
                    ));
            } else {
                sparkScanView.emitFeedback(new SparkScanViewFeedback.Success());
            }
        }
    }
}
