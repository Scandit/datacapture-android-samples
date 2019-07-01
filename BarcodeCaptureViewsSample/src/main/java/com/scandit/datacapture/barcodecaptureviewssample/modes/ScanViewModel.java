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

package com.scandit.datacapture.barcodecaptureviewssample.modes;

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
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.FrameSourceState;

public class ScanViewModel extends ViewModel implements BarcodeCaptureListener {

    private final DataCaptureManager dataCaptureManager = DataCaptureManager.CURRENT;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private ResultListener listener;

    public ScanViewModel() {
        dataCaptureManager.barcodeCapture.applySettings(getBarcodeCaptureSettings());

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

        return barcodeCaptureSettings;
    }

    public DataCaptureContext getDataCaptureContext() {
        return dataCaptureManager.dataCaptureContext;
    }

    public BarcodeCapture getBarcodeCapture() {
        return dataCaptureManager.barcodeCapture;
    }

    public void setListener(@Nullable ResultListener listener) {
        this.listener = listener;
    }

    public void resumeScanning() {
        dataCaptureManager.barcodeCapture.setEnabled(true);
    }

    public void pauseScanning() {
        dataCaptureManager.barcodeCapture.setEnabled(false);
    }

    public void startFrameSource() {
        if (dataCaptureManager.camera != null) {
            dataCaptureManager.camera.switchToDesiredState(FrameSourceState.ON);
        }
    }

    public void stopFrameSource() {
        if (dataCaptureManager.camera != null) {
            dataCaptureManager.camera.switchToDesiredState(FrameSourceState.OFF);
        }
    }

    @Override
    public void onBarcodeScanned(
            @NonNull BarcodeCapture barcodeCapture,
            @NonNull BarcodeCaptureSession session,
            @NonNull FrameData data
    ) {
        final Barcode firstBarcode = session.getNewlyRecognizedBarcodes().get(0);

        if (listener != null && firstBarcode != null) {
            // Stop recognizing barcodes for as long as we are displaying the result.
            // There won't be any new results until the capture mode is enabled again.
            // Note that disabling the capture mode does not stop the camera, the camera
            // continues to stream frames until it is turned off.
            pauseScanning();

            // This method is invoked on a non-UI thread, so in order to perform UI work,
            // we have to switch to the main thread.
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onCodeScanned(firstBarcode);
                }
            });
        }
    }

    @Override
    public void onSessionUpdated(
            @NonNull BarcodeCapture barcodeCapture,
            @NonNull BarcodeCaptureSession session,
            @NonNull FrameData data
    ) {}

    @Override
    public void onObservationStarted(@NonNull BarcodeCapture barcodeCapture) {}

    @Override
    public void onObservationStopped(@NonNull BarcodeCapture barcodeCapture) {}

    public interface ResultListener {
        @MainThread
        void onCodeScanned(Barcode barcodeResult);
    }
}
