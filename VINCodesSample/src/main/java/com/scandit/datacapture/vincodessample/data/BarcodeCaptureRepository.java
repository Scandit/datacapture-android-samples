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

package com.scandit.datacapture.vincodessample.data;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.scandit.datacapture.barcode.capture.BarcodeCapture;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureListener;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSession;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.feedback.BarcodeCaptureFeedback;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.time.TimeInterval;
import com.scandit.datacapture.core.ui.viewfinder.LaserlineViewfinder;
import com.scandit.datacapture.core.ui.viewfinder.LaserlineViewfinderStyle;

import org.jetbrains.annotations.NotNull;

/**
 * The repository to interact with the BarcodeCapture mode.
 */
public class BarcodeCaptureRepository implements BarcodeCaptureListener {
    /**
     * Overlays provide UI to aid the user in the capture process and need to be attached
     * to DataCaptureView.
     */
    private final BarcodeCaptureOverlay overlay;

    /**
     * The stream of barcodes that encode VIN codes.
     */
    private final MutableLiveData<Barcode> capturedBarcodes = new MutableLiveData<>();

    /**
     * The DataCaptureContext that the current IdCapture is attached to.
     */
    public final DataCaptureContext dataCaptureContext;

    /**
     * The current BarcodeCapture.
     */
    private final BarcodeCapture barcodeCapture;

    public BarcodeCaptureRepository(DataCaptureContext dataCaptureContext) {
        this.dataCaptureContext = dataCaptureContext;

        BarcodeCaptureSettings settings = new BarcodeCaptureSettings();

        /*
         * The settings instance initially has all types of barcodes (symbologies) disabled.
         * For the purpose of this sample we enable a Code39 symbology.
         * In your own app ensure that you only enable the symbologies that your app requires
         * as every additional enabled symbology has an impact on processing times.
         */
        settings.enableSymbology(Symbology.CODE39, true);

        /*
         * Within 5s, receive the result for a given barcode only once.
         */
        settings.setCodeDuplicateFilter(TimeInterval.millis(5000));

        barcodeCapture = BarcodeCapture.forDataCaptureContext(dataCaptureContext, settings);
        barcodeCapture.addListener(this);

        /*
         * Disable the default feedback. We will trigger a feedback manually for successfully
         * decoded VINs.
         */
        barcodeCapture.setFeedback(new BarcodeCaptureFeedback());

        /*
         * Setup an overlay that provides UI to aid the user in the capture process. It later needs
         * to be attached to DataCaptureView.
         */
        overlay = BarcodeCaptureOverlay.newInstance(barcodeCapture, null);
        overlay.setViewfinder(new LaserlineViewfinder(LaserlineViewfinderStyle.ANIMATED));
    }

    /**
     * Overlays provide UI to aid the user in the capture process and need to be attached
     * to DataCaptureView.
     */
    public BarcodeCaptureOverlay getBarcodeCaptureOverlay() {
        return overlay;
    }

    /**
     * The stream of barcodes that encode VIN codes.
     */
    public LiveData<Barcode> capturedBarcodes() {
        return capturedBarcodes;
    }

    /**
     * Enable BarcodeCapture so it may process the incoming frames.
     */
    public void enableBarcodeCapture() {
        barcodeCapture.setEnabled(true);
    }

    /**
     * Stop BarcodeCapture from further processing of frames. This happens asynchronously, so some
     * results may still be delivered.
     */
    public void disableBarcodeCapture() {
        barcodeCapture.setEnabled(false);
    }

    @Override
    @WorkerThread
    public void onBarcodeScanned(
            @NotNull BarcodeCapture mode,
            @NotNull BarcodeCaptureSession session,
            @NotNull FrameData data
    ) {
        if (session.getNewlyRecognizedBarcodes().isEmpty()) {
            return;
        }

        /*
         * Implement to handle the captured barcodes that encode VINs.
         *
         * This callback is executed on the background thread. We post the value to the LiveData
         * in order to return to the UI thread.
         */
        capturedBarcodes.postValue(session.getNewlyRecognizedBarcodes().get(0));
    }

    @Override
    public void onSessionUpdated(
            @NonNull BarcodeCapture barcodeCapture,
            @NonNull BarcodeCaptureSession session,
            @NonNull FrameData data
    ) {
        // In this sample, we are not interested in this callback.
    }

    @Override
    @WorkerThread
    public void onObservationStarted(@NotNull BarcodeCapture mode) {
        // In this sample, we are not interested in this callback.
    }

    @Override
    @WorkerThread
    public void onObservationStopped(@NotNull BarcodeCapture mode) {
        // In this sample, we are not interested in this callback.
    }
}
