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

package com.scandit.datacapture.idcaptureextendedsample.ui.scan;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.CapturedResultType;
import com.scandit.datacapture.id.data.RejectedId;
import com.scandit.datacapture.id.data.SupportedSides;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;
import com.scandit.datacapture.idcaptureextendedsample.data.CameraRepository;
import com.scandit.datacapture.idcaptureextendedsample.data.CapturedDataType;
import com.scandit.datacapture.idcaptureextendedsample.data.IdCaptureRepository;
import com.scandit.datacapture.idcaptureextendedsample.di.Injector;
import com.scandit.datacapture.idcaptureextendedsample.ui.result.IdCaptureResultFactory;
import com.scandit.datacapture.idcaptureextendedsample.ui.result.CaptureResult;

/**
 * The view model for the screen where the user may capture an ID document.
 */
public class ScanViewModel extends ViewModel {
    /**
     * This sample offers the user to capture three kinds of ID document elements - barcodes
     * present on IDs, Machine Readable Zones (MRZ) or human-readable parts of documents (VIZ).
     * By default we capture barcodes.
     */
    private static final CapturedDataType DEFAULT_ID_CAPTURE_MODE = CapturedDataType.BARCODE;

    /**
     * The repository to interact with the device's camera. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private final CameraRepository cameraRepository = Injector.getInstance().getCameraRepository();

    /**
     * The repository to interact with the IdCapture mode. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private final IdCaptureRepository idCaptureRepository =
            Injector.getInstance().getIdCaptureRepository();

    /**
     * The observer of IdCaptureOverlays that come from the lower layer. The overlay is recreated
     * every time the user selects a different kind of documents or their parts to capture.
     * IdCaptureOverlay offers an UI to aid the user in the capture process, it needs to be
     * attached to DataCaptureView.
     */
    private final Observer<IdCaptureOverlay> idCaptureOverlaysObserver =
            this::onNewIdCaptureOverlay;

    /**
     * The observer of data captured from personal identification documents or their parts.
     */
    private final Observer<CapturedIdEvent> capturedIdsObserver = this::onIdCaptured;

    /**
     * The observer of information about personal identification documents or their parts that
     * were detected in a frame, but rejected.
     *
     * A document or its part is considered rejected when:
     *   (a) it's a valid document, but its type is not enabled in the settings,
     *   (b) it's a barcode of a correct symbology, but the data is encoded in an unexpected format,
     *   (c) it's Machine Readable Zone (MRZ), but the data is encoded in an unexpected format.
     */
    private final Observer<RejectedIdEvent> rejectedIdsObserver = this::onIdRejected;

    /**
     * The state representing the currently displayed UI.
     */
    private ScanUiState uiState = ScanUiState.builder().build();

    /**
     * The stream of UI states.
     */
    private final MutableLiveData<ScanUiState> uiStates = new MutableLiveData<>();

    /**
     * Events to display the UI that informs the user that the captured ID is not
     * supported by the selected settings.
     */
    private final MutableLiveData<GoToIdNotSupported> goToIdNotSupported = new MutableLiveData<>();

    /**
     * Events to display the UI with the result of ID capture.
     */
    private final MutableLiveData<GoToResult> goToResult = new MutableLiveData<>();

    public ScanViewModel() {
        /*
         * Observe the streams of data from the lower layer.
         */
        idCaptureRepository.idCaptureOverlays().observeForever(idCaptureOverlaysObserver);
        idCaptureRepository.capturedIds().observeForever(capturedIdsObserver);
        idCaptureRepository.rejectedIds().observeForever(rejectedIdsObserver);

        /*
         * Trigger the create of IdCapture for the initial configuration.
         */
        idCaptureRepository.updateIdCapture(DEFAULT_ID_CAPTURE_MODE);

        /*
         * Post the initial UI state.
         */
        uiStates.postValue(uiState);
    }

    @Override
    protected void onCleared() {
        /*
         * Stop observing the streams of the lower layer and the timer events to avoid memory leak.
         */
        idCaptureRepository.idCaptureOverlays().removeObserver(idCaptureOverlaysObserver);
        idCaptureRepository.capturedIds().removeObserver(capturedIdsObserver);
        idCaptureRepository.rejectedIds().removeObserver(rejectedIdsObserver);
    }

    /**
     * The stream of UI states.
     */
    public LiveData<ScanUiState> uiStates() {
        return uiStates;
    }

    /**
     * Events to display the UI that informs the user that the captured ID is not
     * supported by the selected settings.
     */
    public LiveData<GoToIdNotSupported> goToIdNotSupported() {
        return goToIdNotSupported;
    }

    /**
     * Events to display the UI with the result of ID capture.
     */
    public LiveData<GoToResult> goToResult() {
        return goToResult;
    }

    /**
     * Capture barcodes present on ID documents.
     */
    public void onCaptureBarcodesSelected() {
        onModeSelected(CapturedDataType.BARCODE);
    }

    /**
     * Capture Machine Readable Zones (MRZ).
     */
    public void onCaptureMrzSelected() {
        onModeSelected(CapturedDataType.MRZ);
    }

    /**
     * Capture human-readable parts of documents (VIZ).
     */
    public void onCaptureVizSelected() {
        onModeSelected(CapturedDataType.VIZ);
    }

    /**
     * Capture the specified element of personal identification documents.
     */
    private void onModeSelected(CapturedDataType mode) {
        idCaptureRepository.updateIdCapture(mode);

        uiState = uiState.toBuilder()
                .mode(mode)
                .build();

        uiStates.postValue(uiState);
    }

    /**
     * The user dismissed `Document not supported` dialog. Continue the capture process.
     */
    public void onIdNotSupportedDismissed() {
        idCaptureRepository.enableIdCapture();
    }

    /**
     * Start or resume the ID capture process.
     */
    void startIdCapture() {
        cameraRepository.turnOnCamera();
        idCaptureRepository.enableIdCapture();
    }

    /**
     * Pause the ID capture process.
     */
    void stopIdCapture() {
        idCaptureRepository.disableIdCapture();
        cameraRepository.turnOffCamera();
    }

    private void onNewIdCaptureOverlay(IdCaptureOverlay overlay) {
        /*
         * Update the UI that aids the user in the ID capture process.
         */
        uiState = uiState.toBuilder().overlay(overlay).build();
        uiStates.postValue(uiState);
    }

    private void onIdCaptured(CapturedIdEvent capturedIdEvent) {
        CapturedId capturedId = capturedIdEvent.getContentIfNotHandled();
        if (capturedId == null) return;
        /*
         * Viz documents support multiple sides scanning.
         * In case the back side is supported and not yet captured we don't display the result
         */
        if (capturedId.getCapturedResultType() != CapturedResultType.VIZ_RESULT ||
                capturedId.getViz().getCapturedSides() != SupportedSides.FRONT_ONLY ||
                !capturedId.getViz().isBackSideCaptureSupported()) {
            final CaptureResult result = IdCaptureResultFactory.extract(capturedId);
            goToResult.postValue(new GoToResult(result));
        }
    }

    private void onIdRejected(RejectedIdEvent rejectedIdEvent) {
        RejectedId rejectedId = rejectedIdEvent.getContentIfNotHandled();
        if (rejectedId == null) return;
        /*
         * Inform the user that data from a document that they try to
         * scan cannot be extracted.
         *
         * This may happen when:
         *   (a) it's a valid document, but its type is not enabled in the settings,
         *   (b) it's a barcode of a correct symbology, but the data is encoded in an unexpected format,
         *   (c) it's Machine Readable Zone (MRZ), but the data is encoded in an unexpected format.
         */
        goToIdNotSupported.postValue(new GoToIdNotSupported());
    }
}
