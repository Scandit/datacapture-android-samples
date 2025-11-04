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

package com.scandit.datacapture.usdlverificationsample.ui.scan;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.CapturedSides;
import com.scandit.datacapture.id.data.RejectionReason;
import com.scandit.datacapture.id.data.VizResult;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;
import com.scandit.datacapture.id.verification.VerificationResult;
import com.scandit.datacapture.usdlverificationsample.data.CameraRepository;
import com.scandit.datacapture.usdlverificationsample.data.IdCaptureRepository;
import com.scandit.datacapture.usdlverificationsample.di.Injector;
import com.scandit.datacapture.usdlverificationsample.ui.result.CaptureResult;
import com.scandit.datacapture.usdlverificationsample.ui.result.ResultMapper;

/**
 * The view model for the screen where the user may capture a Driver's License.
 */
public class ScanViewModel extends ViewModel {
    /**
     * The repository to interact with the device's camera. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private final CameraRepository cameraRepository =
            Injector.getInstance().getCameraRepository();

    /**
     * The repository to interact with the IdCapture mode. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private final IdCaptureRepository idCaptureRepository =
            Injector.getInstance().getIdCaptureRepository();


    /**
     * The observer of IdCaptureOverlays that come from the lower layer.
     * IdCaptureOverlay offers an UI to aid the user in the capture process, it needs to be
     * attached to DataCaptureView.
     */
    private final Observer<IdCaptureOverlay> idCaptureOverlaysObserver =
            this::onNewIdCaptureOverlay;

    /**
     * The observer of data captured from driver's licenses.
     */
    private final Observer<CapturedIdEvent> capturedIdsObserver = this::onIdCaptured;

    /**
     * The observer of rejected documents.
     */
    private final Observer<RejectedIdEvent> rejectedIdsObserver = this::onIdRejected;

    /**
     * The state representing the currently displayed UI.
     */
    private ScanUiState uiState = ScanUiState.builder().build();

    /**
     * An event to display the UI that informs the user that the document has been rejected.
     */
    private final MutableLiveData<GoToRejectedDocument> gotToRejectedDocument = new MutableLiveData<>();

    /**
     * The stream of UI states.
     */
    private final MutableLiveData<ScanUiState> uiStates = new MutableLiveData<>();

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
         * Post the initial UI state.
         */
        uiStates.postValue(uiState);
    }

    @Override
    protected void onCleared() {
        /*
         * Stop observing the streams of the lower layer to avoid memory leak.
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
     * Events to display the UI with the result of ID capture.
     */
    public LiveData<GoToResult> goToResult() {
        return goToResult;
    }

    /**
     * An event to display the UI that informs the user that the document has been rejected.
     */
    public LiveData<GoToRejectedDocument> gotToRejectedDocument() {
        return gotToRejectedDocument;
    }

    /**
     * Start or resume the ID capture process.
     */
    public void startIdCapture() {
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

    /**
     * Update the UI that aids the user in the ID capture process.
     */
    private void onNewIdCaptureOverlay(IdCaptureOverlay overlay) {
        uiState = uiState.toBuilder().overlay(overlay).build();
        uiStates.postValue(uiState);
    }

    /**
     * Whenever a US driver's license is captured, this method processes the captured document.
     * Documents with inconsistent data, forged AAMVA barcodes, or expiration issues are
     * automatically rejected by IdCapture settings and won't reach this callback.
     * This method also updates the UI that aids the user in the ID capture process.
     */
    private void onIdCaptured(CapturedIdEvent capturedIdEvent) {
        CapturedId capturedId = capturedIdEvent.getContentIfNotHandled();
        if (capturedId == null) return;

        VizResult viz = capturedId.getViz();
        if (viz == null || viz.getCapturedSides() != CapturedSides.FRONT_AND_BACK) {
            return;
        }

        stopIdCapture();

        // Process the captured document - verification results are available in capturedId.verificationResult
        processVerificationResults(capturedId);
    }

    /**
     * Whenever a document is rejected, this method handles the rejection appropriately.
     * For verification failures (inconsistent data, forged AAMVA barcode), show detailed
     * verification results. For other rejections, show rejection dialog.
     */
    private void onIdRejected(RejectedIdEvent rejectedIdEvent) {
        RejectedId rejectedId = rejectedIdEvent.getContentIfNotHandled();
        if (rejectedId == null) return;

        idCaptureRepository.resetIdCapture();
        stopIdCapture();

        CapturedId capturedId = rejectedId.getId();
        RejectionReason reason = rejectedId.getReason();

        // Handle verification failures by showing verification results instead of rejection dialog
        if (reason == RejectionReason.INCONSISTENT_DATA ||
            reason == RejectionReason.FORGED_AAMVA_BARCODE ||
            reason == RejectionReason.DOCUMENT_EXPIRED) {
            handleVerificationFailure(capturedId, reason);
        } else {
            // For other rejection reasons, show the standard rejection dialog
            gotToRejectedDocument.postValue(new GoToRejectedDocument(rejectedId));
        }
    }

    /**
     * Handle verification failures by showing detailed verification results.
     * Simply pass the rejection reason and front review image (if available) to the result screen.
     * The result screen will handle the progressive verification logic based on the specific reason.
     */
    private void handleVerificationFailure(CapturedId capturedId, RejectionReason reason) {
        // Extract front review image from verification result if available (only for data consistency failures)
        VerificationResult verificationResult = capturedId.getVerificationResult();
        Bitmap frontReviewImage = reason == RejectionReason.INCONSISTENT_DATA &&
            verificationResult != null && verificationResult.getDataConsistency() != null
            ? verificationResult.getDataConsistency().getFrontReviewImage()
            : null;

        navigateToResult(capturedId, reason, frontReviewImage);
    }

    /**
     * Process a captured driver's license that has passed all built-in verification checks.
     * Documents with inconsistent data, forged AAMVA barcodes, or expiration issues are
     * automatically rejected by IdCapture settings and won't reach this method.
     * Since all verification checks passed, show successful results.
     */
    private void processVerificationResults(CapturedId capturedId) {
        // Extract verification results from the captured document
        VerificationResult verificationResult = capturedId.getVerificationResult();

        // Since the document reached onIdCaptured, all verification checks passed
        // Extract front review image if available from data consistency result
        Bitmap frontReviewImage = verificationResult != null && verificationResult.getDataConsistency() != null
            ? verificationResult.getDataConsistency().getFrontReviewImage()
            : null;

        // All verification checks passed - no rejection reason
        navigateToResult(capturedId, null, frontReviewImage);
    }

    /**
     * Convert the data extracted from the document to a result entity and dispatch an event
     * to navigate to the result screen.
     */
    private void navigateToResult(
            CapturedId capturedId,
            @Nullable RejectionReason rejectionReason,
            @Nullable Bitmap frontReviewImage
        ) {
        final CaptureResult result = new ResultMapper(capturedId).mapResult(
                rejectionReason,
                frontReviewImage
        );
        goToResult.postValue(new GoToResult(result));
    }
}
