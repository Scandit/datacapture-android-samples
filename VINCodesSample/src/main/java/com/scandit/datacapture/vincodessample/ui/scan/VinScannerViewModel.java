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

package com.scandit.datacapture.vincodessample.ui.scan;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.core.common.feedback.Feedback;
import com.scandit.datacapture.core.ui.overlay.DataCaptureOverlay;
import com.scandit.datacapture.vincodessample.data.BarcodeCaptureRepository;
import com.scandit.datacapture.vincodessample.data.CameraRepository;
import com.scandit.datacapture.vincodessample.data.TextCaptureRepository;
import com.scandit.datacapture.vincodessample.data.VinParserRepository;
import com.scandit.datacapture.vincodessample.data.VinResult;
import com.scandit.datacapture.vincodessample.di.Injector;

/**
 * The view model for the screen where the user may capture a Vehicle Identification Number (VIN).
 */
public class VinScannerViewModel extends ViewModel {
    /**
     * The repository to interact with the device's camera. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private final CameraRepository cameraRepository = Injector.getInstance().getCameraRepository();

    /**
     * The repository that sends the data decoded from a Vehicle Identification Number (VIN).
     * We use our own dependency injection to obtain it, but you may use your favorite framework,
     * like Dagger or Hilt instead.
     */
    private final VinParserRepository vinParserRepository = Injector.getInstance().getVinParserRepository();

    /**
     * The repository to interact with the BarcodeCapture mode. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private final BarcodeCaptureRepository barcodeCaptureRepository = Injector.getInstance().getBarcodeCaptureRepository();

    /**
     * The repository to interact with the TextCapture mode. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private final TextCaptureRepository textCaptureRepository = Injector.getInstance().getTextCaptureRepository();

    /**
     * The observer of data decoded from VINs.
     */
    private final Observer<VinResult> vinsObserver = this::onNewVin;

    /**
     * The observer of errors encountered when parsing VINs.
     */
    private final Observer<String> vinParsingErrorsObserver = this::onNewVinParsingError;

    /**
     * The state representing the currently displayed UI.
     */
    private VinScannerUiState uiState = VinScannerUiState.builder().build();

    /**
     * The stream of UI states.
     */
    private final MutableLiveData<VinScannerUiState> uiStates = new MutableLiveData<>();

    /**
     * Events to display the UI with the result of ID capture.
     */
    private final MutableLiveData<GoToResult> goToResult = new MutableLiveData<>();

    /**
     * Events to display the error encountered when parsing VINs.
     */
    private final MutableLiveData<ShowVinParsingError> showVinParsingError = new MutableLiveData<>();

    /**
     * A sound/vibration to trigger once a VIN has been parsed.
     */
    private Feedback feedback = Feedback.defaultFeedback();

    /**
     * Whether the UI should handle any parser results.
     */
    private boolean shouldHandleResults = false;

    public VinScannerViewModel() {
        /*
         * Observe the streams of data from the lower layer.
         */
        vinParserRepository.getVins().observeForever(vinsObserver);
        vinParserRepository.getErrorMessages().observeForever(vinParsingErrorsObserver);

        /*
         * This sample offers to capture VINs in two ways: either to decode them from barcodes or
         * to OCR ones in plain text. Initially we capture barcodes, but the user can switch
         * the mode at any time.
         */
        switchTo(VinScannerMode.BARCODE);
    }

    @Override
    protected void onCleared() {
        /*
         * Stop observing the streams of the lower layer and the timer events to avoid memory leak.
         */
        vinParserRepository.getVins().removeObserver(vinsObserver);
        vinParserRepository.getErrorMessages().removeObserver(vinParsingErrorsObserver);
    }

    /**
     * The stream of UI states.
     */
    public LiveData<VinScannerUiState> uiStates() {
        return uiStates;
    }

    /**
     * Events to display the UI with the result of ID capture.
     */
    public LiveData<GoToResult> goToResult() {
        return goToResult;
    }

    /**
     * Events to display the error encountered when parsing VINs.
     */
    public LiveData<ShowVinParsingError> showVinParsingError() {
        return showVinParsingError;
    }

    /**
     * Capture the specified element of personal identification documents.
     */
    public void switchTo(VinScannerMode mode) {
        uiState = uiState.toBuilder()
                .mode(mode)
                .overlay(getCurrentOverlay())
                .build();

        enableMode();

        uiStates.postValue(uiState);
    }

    /**
     * Get the overlay appropriate for the currently selected way of capturing VINs.
     */
    private DataCaptureOverlay getCurrentOverlay() {
        if (uiState.getMode() == VinScannerMode.BARCODE) {
            return barcodeCaptureRepository.getBarcodeCaptureOverlay();
        } else {
            return textCaptureRepository.getTextCaptureOverlay();
        }
    }

    /**
     * Start or resume the capture process.
     */
    void startCapture() {
        cameraRepository.turnOnCamera();
        enableMode();
    }

    /**
     * Enable the currently selected mode.
     */
    void enableMode() {
        shouldHandleResults = true;

        if (uiState.getMode() == VinScannerMode.BARCODE) {
            barcodeCaptureRepository.enableBarcodeCapture();
            textCaptureRepository.disableTextCapture();
        } else {
            barcodeCaptureRepository.disableBarcodeCapture();
            textCaptureRepository.enableTextCapture();
        }
    }

    private void disableMode() {
        barcodeCaptureRepository.disableBarcodeCapture();
        textCaptureRepository.disableTextCapture();

        shouldHandleResults = false;
    }

    /**
     * Pause the capture process.
     */
    void stopCapture() {
        disableMode();
        cameraRepository.turnOffCamera();
    }

    /**
     * Display the data decoded from a VIN.
     */
    private void onNewVin(VinResult vin) {
        if (shouldHandleResults) {
            disableMode();

            feedback.emit();
            goToResult.postValue(new GoToResult());
        }
    }

    /**
     * Display an error encountered when parsing VINs.
     */
    private void onNewVinParsingError(String message) {
        if (shouldHandleResults) {
            disableMode();

            showVinParsingError.postValue(new ShowVinParsingError(message));
        }
    }
}
