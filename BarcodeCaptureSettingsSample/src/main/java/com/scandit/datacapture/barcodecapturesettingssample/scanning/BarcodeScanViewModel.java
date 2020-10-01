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

package com.scandit.datacapture.barcodecapturesettingssample.scanning;

import androidx.lifecycle.ViewModel;
import com.scandit.datacapture.barcode.capture.BarcodeCapture;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureListener;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSession;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.SymbologyDescription;
import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.FrameSourceState;
import org.jetbrains.annotations.NotNull;

public class BarcodeScanViewModel extends ViewModel implements BarcodeCaptureListener {

    private final SettingsManager settingsManager = SettingsManager.getCurrentSettings();
    private Listener listener = null;

    public BarcodeScanViewModel() {
        settingsManager.getBarcodeCapture().addListener(this);
    }

    @Override
    public void onBarcodeScanned(
            @NotNull BarcodeCapture barcodeCapture,
            @NotNull BarcodeCaptureSession session,
            @NotNull FrameData data
    ) {
        if (!session.getNewlyRecognizedBarcodes().isEmpty()) {
            if (!isContinuousScanningEnabled()) {
                pauseScanning();
            }
            if (listener != null) {
                Barcode barcode = session.getNewlyRecognizedBarcodes().get(0);
                String resultData = barcode.getData();
                if (barcode.getAddOnData() != null) {
                    resultData += " " + barcode.getAddOnData();
                }
                listener.showDialog(
                        SymbologyDescription.create(barcode.getSymbology()).getReadableName(),
                        resultData,
                        barcode.getSymbolCount()
                );
            }
        }
    }

    @Override
    public void onSessionUpdated(
            @NotNull BarcodeCapture barcodeCapture,
            @NotNull BarcodeCaptureSession session,
            @NotNull FrameData data
    ) {}

    @Override
    public void onObservationStarted(@NotNull BarcodeCapture barcodeCapture) {}

    @Override
    public void onObservationStopped(@NotNull BarcodeCapture barcodeCapture) {}

    void startFrameSource() {
        settingsManager.getCamera().switchToDesiredState(FrameSourceState.ON);
    }

    void stopFrameSource() {
        settingsManager.getCamera().switchToDesiredState(FrameSourceState.OFF, null);
    }

    void resumeScanning() {
        settingsManager.getBarcodeCapture().setEnabled(true);
    }

    void pauseScanning() {
        settingsManager.getBarcodeCapture().setEnabled(false);
    }

    SettingsManager getCurrentSettings() {
        return settingsManager;
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    boolean isContinuousScanningEnabled() {
        return settingsManager.isContinuousScanningEnabled();
    }

    interface Listener {
        void showDialog(String symbologyName, String data, int symbolCount);
    }
}
