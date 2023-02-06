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

package com.scandit.datacapture.barcodecaptureviewssample.modes.splitview;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import com.scandit.datacapture.barcode.capture.BarcodeCapture;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureListener;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSession;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcodecaptureviewssample.models.DataCaptureManager;
import com.scandit.datacapture.core.area.RadiusLocationSelection;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.capture.DataCaptureContextListener;
import com.scandit.datacapture.core.capture.DataCaptureMode;
import com.scandit.datacapture.core.common.ContextStatus;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.FrameSource;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.time.TimeInterval;

import java.util.ArrayList;
import java.util.List;

public class SplitViewScanViewModel extends ViewModel implements BarcodeCaptureListener,
        DataCaptureContextListener {

    private final DataCaptureManager dataCaptureManager = DataCaptureManager.CURRENT;

    private static final long SCAN_TIMEOUT_DURATION = 10 * 1000;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Listener listener;
    private boolean inTimeoutState = false;

    final List<Barcode> results = new ArrayList<>();

    private final CountDownTimer scanTimeoutTimer =
            new CountDownTimer(SCAN_TIMEOUT_DURATION, SCAN_TIMEOUT_DURATION) {
                @Override
                public void onTick(long millisUntilFinished) {}

                @Override
                public void onFinish() {
                    inTimeoutState = true;
                    notifyInactiveTimeout();
                }
            };

    public SplitViewScanViewModel() {
        dataCaptureManager.barcodeCapture.applySettings(getBarcodeCaptureSettings());

        // Register self as a listener to get informed of Scandit license status changes.
        dataCaptureManager.dataCaptureContext.addListener(this);

        // Register self as a listener to get informed whenever a new barcode got recognized.
        dataCaptureManager.barcodeCapture.addListener(this);
    }

    private BarcodeCaptureSettings getBarcodeCaptureSettings() {
        // The barcode capturing process is configured through barcode capture settings
        // which are then applied to the barcode capture instance that manages barcode recognition.
        BarcodeCaptureSettings barcodeCaptureSettings = new BarcodeCaptureSettings();

        // The settings instance initially has all types of barcodes (symbologies) disabled.
        // For the purpose of this sample we enable a very generous set of symbologies.
        // In your own app ensure that you only enable the symbologies that your app requires
        // as every additional enabled symbology has an impact on processing times.
        barcodeCaptureSettings.enableSymbology(Symbology.EAN13_UPCA, true);
        barcodeCaptureSettings.enableSymbology(Symbology.EAN8, true);
        barcodeCaptureSettings.enableSymbology(Symbology.UPCE, true);
        barcodeCaptureSettings.enableSymbology(Symbology.QR, true);
        barcodeCaptureSettings.enableSymbology(Symbology.DATA_MATRIX, true);
        barcodeCaptureSettings.enableSymbology(Symbology.CODE39, true);
        barcodeCaptureSettings.enableSymbology(Symbology.CODE128, true);
        barcodeCaptureSettings.enableSymbology(Symbology.INTERLEAVED_TWO_OF_FIVE, true);

        // Setting the code duplicate filter to one thousand milliseconds means that the scanner
        // won't report the same code as recognized for one second once it's recognized.
        barcodeCaptureSettings.setCodeDuplicateFilter(TimeInterval.millis(1000L));

        // In order not to pick up barcodes outside of the view finder,
        // restrict the code location selection to match the laser line's center.
        barcodeCaptureSettings.setLocationSelection(
                new RadiusLocationSelection(new FloatWithUnit(0f, MeasureUnit.FRACTION))
        );
        return barcodeCaptureSettings;
    }

    DataCaptureContext getDataCaptureContext() {
        return dataCaptureManager.dataCaptureContext;
    }

    BarcodeCapture getBarcodeCapture() {
        return dataCaptureManager.barcodeCapture;
    }

    boolean isInTimeoutState() {
        return inTimeoutState;
    }

    void clearCurrentResults() {
        results.clear();
        notifyCodeReset();
    }

    void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    void resumeScanning() {
        inTimeoutState = false;
        dataCaptureManager.barcodeCapture.setEnabled(true);
        scanTimeoutTimer.cancel();
        if (dataCaptureManager.isLicenseValid) {
            scanTimeoutTimer.start();
        }
    }

    void pauseScanning() {
        dataCaptureManager.barcodeCapture.setEnabled(false);
        scanTimeoutTimer.cancel();
    }

    void startFrameSource() {
        if (dataCaptureManager.camera != null) {
            dataCaptureManager.camera.switchToDesiredState(FrameSourceState.ON);
        }
    }

    void stopFrameSource() {
        if (dataCaptureManager.camera != null) {
            dataCaptureManager.camera.switchToDesiredState(FrameSourceState.OFF);
        }
    }

    private void notifyCodeReset() {
        if (listener != null) {
            listener.onResetCodes();
        }
    }

    private void notifyCodeAdded(Barcode barcodeResult) {
        if (listener != null) {
            listener.onCodeAdded(barcodeResult);
        }
    }

    private void notifyInactiveTimeout() {
        if (listener != null) {
            listener.onInactiveTimeout();
        }
    }

    private void notifyClearTimeoutOverlay() {
        if (listener != null) {
            listener.clearTimeoutOverlay();
        }
    }

    @Override
    public void onBarcodeScanned(
            @NonNull BarcodeCapture barcodeCapture,
            @NonNull BarcodeCaptureSession session,
            @NonNull FrameData data
    ) {}

    @Override
    public void onSessionUpdated(
            @NonNull BarcodeCapture barcodeCapture,
            @NonNull BarcodeCaptureSession session,
            @NonNull FrameData data
    ) {
        if (session.getNewlyRecognizedBarcodes().isEmpty()) return;

        final Barcode firstBarcode = session.getNewlyRecognizedBarcodes().get(0);

        // This method is invoked on a non-UI thread, so in order to perform UI work,
        // we have to switch to the main thread.
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                storeAndNotifyNewBarcode(firstBarcode);
            }
        });
    }

    private void storeAndNotifyNewBarcode(Barcode barcode) {
        results.add(barcode);
        scanTimeoutTimer.cancel();
        scanTimeoutTimer.start();
        notifyCodeAdded(barcode);
    }

    @Override
    public void onObservationStarted(@NonNull BarcodeCapture barcodeCapture) {}

    @Override
    public void onObservationStopped(@NonNull BarcodeCapture barcodeCapture) {}

    @Override
    public void onFrameSourceChanged(
            @NonNull DataCaptureContext dataCaptureContext, @NonNull FrameSource frameSource
    ) {}

    @Override
    public void onModeAdded(
            @NonNull DataCaptureContext dataCaptureContext, @NonNull DataCaptureMode dataCaptureMode
    ) {}

    @Override
    public void onStatusChanged(
            @NonNull DataCaptureContext dataCaptureContext, @NonNull ContextStatus contextStatus
    ) {
        scanTimeoutTimer.cancel();
        inTimeoutState = false;
        notifyClearTimeoutOverlay();

        // We want to start the countdown to show the tap-to-continue overlay only if the license
        // is valid.
        if (contextStatus.isValid()) {
            scanTimeoutTimer.start();
        }
    }

    @Override
    public void onModeRemoved(
            @NonNull DataCaptureContext dataCaptureContext, @NonNull DataCaptureMode dataCaptureMode
    ) {}

    @Override
    public void onObservationStarted(@NonNull DataCaptureContext dataCaptureContext) {}

    @Override
    public void onObservationStopped(@NonNull DataCaptureContext dataCaptureContext) {}

    public interface Listener {
        @MainThread
        void onCodeAdded(Barcode barcode);

        @MainThread
        void onResetCodes();

        @MainThread
        void onInactiveTimeout();

        @MainThread
        void clearTimeoutOverlay();
    }
}
