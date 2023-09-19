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

package com.scandit.datacapture.ageverifieddeliverysample.ui.barcode;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.ageverifieddeliverysample.data.CameraRepository;
import com.scandit.datacapture.ageverifieddeliverysample.di.Injector;
import com.scandit.datacapture.barcode.capture.BarcodeCapture;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureListener;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSession;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The view model for the screen where the user may capture the recipient's barcode.
 */
public class BarcodeScanViewModel extends ViewModel implements BarcodeCaptureListener {

    /**
     * DataCaptureContext is necessary to create DataCaptureView.
     */
    private final DataCaptureContext dataCaptureContext = Injector.getInstance().getDataCaptureContext();

    /**
     * The repository to interact with the device's camera. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private final CameraRepository cameraRepository = Injector.getInstance().getBarcodeCameraRepository();

    /**
     * Get the current BarcodeCapture.
     */
    private final BarcodeCapture barcodeCapture = Injector.getInstance().getBarcodeCapture();

    /**
     * BarcodeCaptureOverlay displays the additional UI to guide the user through the barcode capture process.
     */
    private final BarcodeCaptureOverlay barcodeCaptureOverlay = Injector.getInstance().getBarcodeCaptureOverlay();

    /**
     * The state representing the currently displayed UI.
     */
    private BarcodeScanUiState uiState = BarcodeScanUiState.builder().build();

    /**
     * The stream of UI states.
     */
    private final MutableLiveData<BarcodeScanUiState> uiStates = new MutableLiveData<>();

    /**
     * Events to display the UI that informs the user that the delivery requires an age verification
     * of the recipient.
     */
    private final MutableLiveData<GoToAgeVerificationRequired> goToAgeVerificationRequired =
            new MutableLiveData<>();

    /**
     * Events to navigate to the ID scanning fragment to capture the ID of the recipient.
     */
    private final MutableLiveData<GoToIdScanning> goToIdScanningScreen =
            new MutableLiveData<>();

    /**
     * Set up Barcode capture and attach the mode to the data capture context.
     */
    public void setUpBarcodeCaptureMode() {
        dataCaptureContext.removeAllModes();
        dataCaptureContext.addMode(barcodeCapture);
        /*
         * Set up the BarcodeCapture with the initial document configuration.
         */
        uiState = uiState.toBuilder().overlay(barcodeCaptureOverlay).build();
        barcodeCapture.addListener(this);

        /*
         * Post the initial UI state.
         */
        uiStates.postValue(uiState);
    }

    /**
     * Detach the mode from the data capture context.
     */
    public void removeBarcodeCaptureMode() {
        dataCaptureContext.removeMode(barcodeCapture);
    }

    /**
     * The stream of UI states.
     */
    public LiveData<BarcodeScanUiState> uiStates() {
        return uiStates;
    }

    /**
     * Events to display the UI that informs the user that the delivery requires an age verification
     * of the recipient.
     */
    public LiveData<GoToAgeVerificationRequired> goToAgeVerificationRequired() {
        return goToAgeVerificationRequired;
    }

    /**
     * Events to navigate to the ID scanning fragment to capture the ID of the recipient.
     */
    public LiveData<GoToIdScanning> goToIdScanningScreen() {
        return goToIdScanningScreen;
    }

    /**
     * Stop the camera preview.
     */
    void turnOffCamera() {
        cameraRepository.turnOffCamera();
    }

    /**
     * Propagate an event used to navigate to the ID scanning fragment to capture the ID of
     * the recipient.
     */
    public void onIdCaptureSelected() {
        goToIdScanningScreen.postValue(new GoToIdScanning());
    }

    /**
     * Resume barcode capture.
     */
    public void resumeCapture() {
        cameraRepository.turnOnCamera();
        barcodeCapture.setEnabled(true);
    }

    /**
     * Pause barcode capture.
     */
    private void pauseCapture() {
        barcodeCapture.setEnabled(false);
        cameraRepository.turnOffCamera();
    }

    /**
     * The callback invoked by BarcodeCapture every time that a barcode is captured.
     */
    @Override
    @WorkerThread
    public void onBarcodeScanned(
            @NonNull BarcodeCapture barcodeCapture, @NonNull BarcodeCaptureSession session,
            @NonNull FrameData data
    ) {
        List<Barcode> barcodes = session.getNewlyRecognizedBarcodes();

        if (!barcodes.isEmpty()) {
            pauseCapture();
            goToAgeVerificationRequired.postValue(new GoToAgeVerificationRequired());
        }
    }

    @Override
    @WorkerThread
    public void onSessionUpdated(
            @NonNull BarcodeCapture barcodeCapture, @NonNull BarcodeCaptureSession session,
            @NonNull FrameData data
    ) {
        // Not interested in this callback.
    }

    @Override
    @WorkerThread
    public void onObservationStarted(@NotNull BarcodeCapture mode) {
        // Not interested in this callback.
    }

    @Override
    @WorkerThread
    public void onObservationStopped(@NotNull BarcodeCapture mode) {
        // Not interested in this callback.
    }
}
