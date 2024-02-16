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

package com.scandit.datacapture.searchandfindsample.search;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.barcode.capture.BarcodeCapture;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureListener;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSession;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.FrameSource;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.searchandfindsample.models.DataCaptureManager;
import com.scandit.datacapture.searchandfindsample.models.SearchDataCaptureManager;

import org.jetbrains.annotations.NotNull;

public final class SearchScanViewModel extends ViewModel implements BarcodeCaptureListener {

    private final SearchDataCaptureManager dataCaptureManager = new SearchDataCaptureManager();

    final DataCaptureContext dataCaptureContext = dataCaptureManager.dataCaptureContext;
    final BarcodeCapture barcodeCapture = dataCaptureManager.barcodeCapture;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Listener listener = null;

    private Barcode lastScannedBarcode;
    private final Object lock = new Object();

    public SearchScanViewModel() {
        // Register self as a listener to get informed whenever a new barcode got recognized.
        barcodeCapture.addListener(this);
    }

    @Override
    public void onBarcodeScanned(
            @NotNull BarcodeCapture barcodeCapture,
            @NotNull BarcodeCaptureSession session,
            @NotNull FrameData data
    ) {
        synchronized (lock) {
            if (!session.getNewlyRecognizedBarcodes().isEmpty()
                    && session.getNewlyRecognizedBarcodes().get(0) != null) {
                lastScannedBarcode = session.getNewlyRecognizedBarcodes().get(0);

                String code = lastScannedBarcode.getData() != null ? lastScannedBarcode.getData() : "";
                // This method is invoked on a non-UI thread, so in order to perform UI work,
                // we have to switch to the main thread.
                notifyListenerOnMainThread(code);
            }
        }
    }

    private void resetCode() {
        synchronized (lock) {
            lastScannedBarcode = null;
        }
    }

    private void notifyListenerOnMainThread(final String code) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onCodeScanned(code);
                }
            }
        });
    }

    void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    void resumeScanning() {
        dataCaptureManager.camera().applySettings(BarcodeCapture.createRecommendedCameraSettings());
        dataCaptureContext.addMode(barcodeCapture);
        FrameSource frameSource = dataCaptureContext.getFrameSource();
        if (frameSource != null) {
            frameSource.switchToDesiredState(FrameSourceState.ON, null);
        }
        barcodeCapture.setEnabled(true);
    }

    void pauseScanning() {
        dataCaptureContext.removeMode(barcodeCapture);
        FrameSource frameSource = dataCaptureContext.getFrameSource();
        if (frameSource != null) {
            frameSource.switchToDesiredState(FrameSourceState.OFF, null);
        }
        barcodeCapture.setEnabled(false);
    }

    void onSearchClicked() {
        synchronized (lock) {
            if (lastScannedBarcode != null && listener != null) {
                listener.goToFind(lastScannedBarcode);
                resetCode();
            }
        }
    }

    @Override
    public void onSessionUpdated(
            @NotNull BarcodeCapture barcodeCapture,
            @NotNull BarcodeCaptureSession session,
            @NotNull FrameData data
    ) {
    }

    @Override
    public void onObservationStarted(@NotNull BarcodeCapture barcodeCapture) {
    }

    @Override
    public void onObservationStopped(@NotNull BarcodeCapture barcodeCapture) {
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Unregister self as listener when clearing the ViewModel.
        barcodeCapture.removeListener(this);
    }

    interface Listener {
        void onCodeScanned(String code);

        void goToFind(Barcode barcodeToFind);
    }
}
