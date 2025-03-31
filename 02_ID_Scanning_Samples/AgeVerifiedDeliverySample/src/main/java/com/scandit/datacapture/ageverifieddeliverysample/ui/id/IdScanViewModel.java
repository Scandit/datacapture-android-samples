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

package com.scandit.datacapture.ageverifieddeliverysample.ui.id;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.ageverifieddeliverysample.data.CameraRepository;
import com.scandit.datacapture.ageverifieddeliverysample.di.Injector;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureFeedback;
import com.scandit.datacapture.id.capture.IdCaptureListener;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.RejectionReason;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;

/**
 * The view model for the screen  where the user may capture the recipient's document.
 */
public class IdScanViewModel extends ViewModel implements IdCaptureListener {

    /**
     * DataCaptureContext is necessary to create DataCaptureView.
     */
    private final DataCaptureContext dataCaptureContext =
            Injector.getInstance().getDataCaptureContext();

    /**
     * The repository to interact with the device's camera. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private final CameraRepository cameraRepository =
            Injector.getInstance().getIdCameraRepository();

    /**
     * Get the current IdCapture.
     */
    private final IdCapture idCapture = Injector.getInstance().getIdCapture();

    /**
     * IdCaptureOverlay displays the additional UI to guide the user through the ID capture process.
     */
    private final IdCaptureOverlay idCaptureOverlay = Injector.getInstance().getIdCaptureOverlay();

    /**
     * IdCaptureFeedback emits a sound and/or a vibration when a document is captured or rejected.
     */
    private final IdCaptureFeedback feedback = Injector.getInstance().getIdCaptureFeedback();

    /**
     * The state representing the currently displayed UI.
     */
    private IdScanUiState uiState = IdScanUiState.builder().build();

    /**
     * The stream of UI states.
     */
    private final MutableLiveData<IdScanUiState> uiStates = new MutableLiveData<>();

    /**
     * An event for the fragment to display the UI that informs the user that the given ID has been
     * rejected.
     */
    private final MutableLiveData<GoToRejectedDocumentDialog> goToRejectedDocumentDialog =
            new MutableLiveData<>();

    /**
     * Events to display the UI that informs the user that the recipient's document data
     * verification was
     * successful and allows to proceed with the delivery.
     */
    private final MutableLiveData<GoToVerificationSuccess> goToVerificationSuccess =
            new MutableLiveData<>();

    /**
     * Events to navigate to the barcode scanning fragment to capture the barcode of the package.
     */
    private final MutableLiveData<GoToBarcodeScanning> goToBarcodeScanningScreen =
            new MutableLiveData<>();

    /**
     * Set up ID capture and attach the mode to the data capture context.
     */
    public void setUpIdCaptureMode() {
        dataCaptureContext.removeCurrentMode();
        dataCaptureContext.setMode(idCapture);
        /*
         * Set up the IdCapture with the initial document configuration.
         */
        uiState = uiState.toBuilder().overlay(idCaptureOverlay).build();
        idCapture.addListener(this);

        /*
         * Post the initial UI state.
         */
        uiStates.postValue(uiState);
    }

    /**
     * The stream of UI states.
     */
    public LiveData<IdScanUiState> uiStates() {
        return uiStates;
    }

    /**
     * Events for the fragment to display the UI that informs the user that the given ID has been
     * rejected.
     */
    public LiveData<GoToRejectedDocumentDialog> goToRejectedDocumentDialog() {
        return goToRejectedDocumentDialog;
    }

    /**
     * Events to display the UI that informs the user that the recipient's document data
     * verification was
     * successful and allows to proceed with the delivery.
     */
    public LiveData<GoToVerificationSuccess> goToVerificationSuccess() {
        return goToVerificationSuccess;
    }

    /**
     * Events to navigate to the barcode scanning fragment to capture the barcode of the package.
     */
    public LiveData<GoToBarcodeScanning> goToBarcodeScanningScreen() {
        return goToBarcodeScanningScreen;
    }

    public void resetScanningFlow() {
        uiState = IdScanUiState.builder().overlay(null).build();
        uiStates.postValue(uiState);
        resumeCapture();
        goToBarcodeScanningScreen.postValue(new GoToBarcodeScanning());
    }

    /**
     * Resume ID capture.
     */
    public void resumeCapture() {
        cameraRepository.turnOnCamera();
        idCapture.setEnabled(true);
    }

    /**
     * Stop the camera preview.
     */
    void turnOffCamera() {
        cameraRepository.turnOffCamera();
    }

    /**
     * The callback invoked by IdCapture every time that the document is captured.
     */
    @Override
    @WorkerThread
    public void onIdCaptured(@NonNull IdCapture mode, @NonNull CapturedId id) {
        pauseCapture();

        /*
         * The recipient's date of birth is necessary to verify if they are not underage.
         */
        if (isDateOfBirthCaptured(id)) {
            /*
             * The callback is executed in the background thread. We post the value to the LiveData
             * in order to return to the UI thread.
             */
            goToVerificationSuccess.postValue(new GoToVerificationSuccess());
            feedback.getIdCaptured().emit();
        } else {
            // Reject documents that do not include a date of birth.
            goToRejectedDocumentDialog.postValue(new GoToRejectedDocumentDialog(RejectionReason.NOT_ACCEPTED_DOCUMENT_TYPE));
            feedback.getIdRejected().emit();
        }
    }

    private void pauseCapture() {
        idCapture.setEnabled(false);
        cameraRepository.turnOffCamera();
    }

    /**
     * Check whether this CapturedId contains the date of birth.
     */
    private boolean isDateOfBirthCaptured(CapturedId capturedId) {
        return capturedId.getDateOfBirth() != null && capturedId.getDateOfBirth().getDay() != null;
    }

    @Override
    @WorkerThread
    public void onIdRejected(
            @NonNull IdCapture mode,
            @Nullable CapturedId id,
            @NonNull RejectionReason reason
    ) {
        /*
         * Implement to handle documents recognized in a frame, but rejected.
         * A document or its part is considered rejected when:
         *   (a) it's a valid personal identification document, but not enabled in the settings,
         *   (b) it's a PDF417 barcode or a Machine Readable Zone (MRZ), but the data is encoded
         * in an unexpected format,
         *   (c) the document meets the conditions of a rejection rule enabled in the settings,
         *   (d) the document has been localized, but could not be captured within a period of time.
         */
        pauseCapture();
        goToRejectedDocumentDialog.postValue(new GoToRejectedDocumentDialog(reason));
        feedback.getIdRejected().emit();
    }
}
