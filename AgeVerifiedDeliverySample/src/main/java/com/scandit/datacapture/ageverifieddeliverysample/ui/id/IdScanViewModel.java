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

import static com.scandit.datacapture.ageverifieddeliverysample.ui.verificationresult.failure.VerificationFailureReason.DOCUMENT_EXPIRED;
import static com.scandit.datacapture.ageverifieddeliverysample.ui.verificationresult.failure.VerificationFailureReason.HOLDER_UNDERAGE;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.ageverifieddeliverysample.data.CameraRepository;
import com.scandit.datacapture.ageverifieddeliverysample.di.Injector;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureFeedback;
import com.scandit.datacapture.id.capture.IdCaptureListener;
import com.scandit.datacapture.id.capture.IdCaptureSession;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.DateResult;
import com.scandit.datacapture.id.data.RejectedId;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

/**
 * The view model for the screen  where the user may capture the recipient's document.
 */
public class IdScanViewModel extends ViewModel implements IdCaptureListener {

    /**
     * The legal age in the US.
     */
    private static final long LEGAL_AGE = 21;

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
     * The observer of captured document data.
     */
    private final Observer<CapturedId> capturedIdsObserver = this::processCapturedId;

    /**
     * The observer of rejected ID documents.
     */
    private final Observer<RejectedId> rejectedIdsObserver = this::emitRejectedDocument;

    /**
     * The state representing the currently displayed UI.
     */
    private IdScanUiState uiState = IdScanUiState.builder().build();

    /**
     * The stream of UI states.
     */
    private final MutableLiveData<IdScanUiState> uiStates = new MutableLiveData<>();

    /**
     * Events to display the UI to manually enter the recipient's document data.
     */
    private final MutableLiveData<GoToManualEntry> goToManualEntry = new MutableLiveData<>();

    /**
     * Events to display the UI that informs the user that the given ID or MRZ is detected, but cannot
     * be parsed.
     */
    private final MutableLiveData<GoToTimeoutDialog> goToFirstTimeoutDialog =
            new MutableLiveData<>();

    /**
     * Events to display the UI that informs the user another time that the given ID or MRZ is
     * detected, but cannot be parsed.
     */
    private final MutableLiveData<GoToSubsequentTimeoutDialog> goToSubsequentTimeoutDialog =
            new MutableLiveData<>();

    /**
     * Events to display the UI that informs the user that the recipient's document data verification failed
     * and explains the reason. That UI allows either to retry the capture process or to refuse
     * the delivery entirely.
     */
    private final MutableLiveData<GoToVerificationFailure> goToVerificationFailure =
            new MutableLiveData<>();

    /**
     * Events to display the UI that informs the user that the recipient's document data verification was
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
     * Events to display the UI that informs the user that the document can be captured, but it is
     * not supported.
     */
    private final MutableLiveData<GoToUnsupportedDocument> goToUnsupportedDocument =
            new MutableLiveData<>();

    /**
     * The stream of captured document data.
     */
    private final MutableLiveData<CapturedId> capturedIds = new MutableLiveData<>();

    /**
     * The stream of rejected ID documents.
     */
    private final MutableLiveData<RejectedId> rejectedIds = new MutableLiveData<>();

    /**
     * Set up ID capture and attach the mode to the data capture context.
     */
    public void setUpIdCaptureMode() {
        /*
         * Observe the stream of the lower layer and the timer events.
         */
        capturedIds.observeForever(capturedIdsObserver);
        rejectedIds.observeForever(rejectedIdsObserver);

        dataCaptureContext.removeAllModes();
        dataCaptureContext.addMode(idCapture);
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
     * Detach the mode from the data capture context.
     */
    public void removeIdCaptureMode() {
        dataCaptureContext.removeMode(idCapture);
    }

    @Override
    protected void onCleared() {
        /*
         * Stop observing the streams of the lower layer and the timer events to avoid memory leak.
         */
        capturedIds.removeObserver(capturedIdsObserver);
        rejectedIds.removeObserver(rejectedIdsObserver);
    }

    /**
     * The stream of UI states.
     */
    public LiveData<IdScanUiState> uiStates() {
        return uiStates;
    }

    /**
     * Events to display the UI to manually enter the recipient's document data.
     */
    public LiveData<GoToManualEntry> goToManualEntry() {
        return goToManualEntry;
    }

    /**
     * Events to display the UI that informs the user that the given ID or MRZ is detected, but cannot
     * be parsed.
     */
    public LiveData<GoToTimeoutDialog> goToFirstTimeoutDialog() {
        return goToFirstTimeoutDialog;
    }

    /**
     * Events to display the UI that informs the user another time that the given ID or MRZ is
     * detected, but cannot be parsed.
     */
    public LiveData<GoToSubsequentTimeoutDialog> goToSubsequentTimeoutDialog() {
        return goToSubsequentTimeoutDialog;
    }

    /**
     * Events to display the UI that informs the user that the recipient's document data verification failed
     * and explains the reason. That UI allows either to retry the capture process or to refuse
     * the delivery entirely.
     */
    public LiveData<GoToVerificationFailure> goToVerificationFailure() {
        return goToVerificationFailure;
    }

    /**
     * Events to display the UI that informs the user that the recipient's document data verification was
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

    /**
     * Events to display the UI that informs the user that the document can be captured, but it is
     * not supported.
     */
    public LiveData<GoToUnsupportedDocument> goToUnsupportedDocument() {
        return goToUnsupportedDocument;
    }

    public void resetScanningFlow() {
        uiState = IdScanUiState.builder().overlay(null).build();
        uiStates.postValue(uiState);
        resumeCapture();
        goToBarcodeScanningScreen.postValue(new GoToBarcodeScanning());
    }

    /**
     * The user will attempt the enter the recipient's document data manually. Display the proper UI.
     */
    public void onManualEntrySelected() {
        pauseCapture();
        goToManualEntry.postValue(new GoToManualEntry());
    }

    /**
     * The user manually entered the recipient's document data. Proceed with the verification.
     */
    public void onDataEnteredManually(DocumentData manualEntry) {
        verifyDocumentData(manualEntry);
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
    public void onIdCaptured(
            @NotNull IdCapture mode,
            @NotNull IdCaptureSession session,
            @NotNull FrameData data
    ) {
        CapturedId capturedId = session.getNewlyCapturedId();
        pauseCapture();

        /*
         * The recipient's date of birth is necessary to verify if they are not underage.
         */
        if (isDateOfBirthCaptured(capturedId)) {
            // Emit the feedback for a captured document.
            feedback.getIdCaptured().emit();
            /*
             * The callback is executed in the background thread. We post the value to the LiveData
             * in order to return to the UI thread.
             */
            capturedIds.postValue(session.getNewlyCapturedId());
        } else {
            // Emit the feedback for a rejected document.
            feedback.getIdRejected().emit();
            // Reject documents that do not include a date of birth.
            goToUnsupportedDocument.postValue(new GoToUnsupportedDocument());
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

    /**
     * Process this CapturedId.
     */
    private void processCapturedId(CapturedId capturedId) {
        verifyDocumentData(toDocumentData(capturedId));
    }

    /**
     * Extract from this CapturedId the information crucial for the verification process.
     */
    private DocumentData toDocumentData(CapturedId capturedId) {
        DateResult expiryResult = capturedId.getDateOfExpiry();
        LocalDate expiryDate = null;

        if (expiryResult != null) {
            expiryDate = toLocalDate(expiryResult);
        }

        return new DocumentData(
                toLocalDate(capturedId.getDateOfBirth()),
                capturedId.getFullName(),
                expiryDate
        );
    }

    /**
     * Display the UI that informs the user that the document can be captured, but it is
     * not supported.
     */
    private void emitRejectedDocument(RejectedId rejectedId) {
        goToUnsupportedDocument.postValue(new GoToUnsupportedDocument());
    }

    /**
     * Transform DateResult into LocalDate. It is possible that the DateResult does not contain
     * the day information in which case the LocalDate will point to the end of the month.
     */
    private LocalDate toLocalDate(DateResult result) {
        if (result.getDay() != null) {
            return LocalDate.of(result.getYear(), result.getMonth(), result.getDay());
        } else {
            YearMonth yearMonth = YearMonth.of(result.getYear(), result.getMonth());
            return yearMonth.atEndOfMonth();
        }
    }

    /**
     * Check whether the recipient's document is still valid and verify that they are not underage.
     */
    private void verifyDocumentData(DocumentData data) {
        pauseCapture();

        if (isExpired(data)) {
            goToVerificationFailure.postValue(new GoToVerificationFailure(DOCUMENT_EXPIRED));
        } else if (isHolderUnderage(data.getDateOfBirth())) {
            goToVerificationFailure.postValue(new GoToVerificationFailure(HOLDER_UNDERAGE));
        } else {
            goToVerificationSuccess.postValue(new GoToVerificationSuccess());
        }
    }

    /**
     * Check whether the recipient's document is expired.
     */
    private boolean isExpired(DocumentData data) {
        if (data.getDateOfExpiry() == null) {
            return false;
        }

        return LocalDate.now().isAfter(data.getDateOfExpiry());
    }

    /**
     * Check whether the recipient is underage.
     */
    private boolean isHolderUnderage(LocalDate dateOfBirth) {
        long age = ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());

        return age < LEGAL_AGE;
    }

    /**
     * We detected a document, but couldn't extract the data yet. Show a pop-up to inform
     * the user that they may attempt to try another method of entering the data:
     * * to capture the other side of the document
     * * to enter the data manually otherwise
     */
    private void handleIdCaptureTimeout() {
        // Emit the feedback for an ID capture timeout.
        feedback.getIdCaptureTimeout().emit();

        pauseCapture();
        int timeoutCount = uiState.getTimeoutCount() + 1;
        uiState = uiState.toBuilder()
                .timeoutCount(timeoutCount)
                .build();

        idCapture.setEnabled(false);

        if (timeoutCount > 1) {
            goToSubsequentTimeoutDialog.postValue(new GoToSubsequentTimeoutDialog());
        } else {
            goToFirstTimeoutDialog.postValue(new GoToTimeoutDialog());
        }
        uiStates.postValue(uiState);
    }

    /**
     * We detected a document, but couldn't extract the data yet. Show a pop-up to inform
     * the user that they may attempt to try another method of entering the data:
     * * to capture the front of card if they are trying to capture the barcode
     * * to enter the data manually otherwise
     */
    @Override
    @WorkerThread
    public void onIdCaptureTimedOut(
            @NonNull IdCapture mode,
            @NonNull IdCaptureSession session,
            @NonNull FrameData data
    ) {
        handleIdCaptureTimeout();
    }

    @Override
    @WorkerThread
    public void onIdRejected(
            @NotNull IdCapture mode,
            @NotNull IdCaptureSession session,
            @NotNull FrameData data
    ) {
        /*
         * Implement to handle documents recognized in a frame, but rejected.
         * A document or its part is considered rejected when:
         *   (a) it's a valid personal identification document, but not enabled in the settings.
         *   (b) it's a PDF417 barcode, but the data is encoded in an unexpected format.
         */

        RejectedId rejectedId = session.getNewlyRejectedId();

        // Emit the feedback for a rejected document.
        feedback.getIdRejected().emit();

        /*
         * The callback is executed in the background thread. We post the value to the LiveData
         * in order to return to the UI thread.
         */
        rejectedIds.postValue(rejectedId);
    }

    @Override
    @WorkerThread
    public void onIdLocalized(
            @NotNull IdCapture mode,
            @NotNull IdCaptureSession session,
            @NotNull FrameData data
    ) {
        // Not interested in this callback.
    }

    @Override
    @WorkerThread
    public void onErrorEncountered(
            @NotNull IdCapture mode,
            @NotNull Throwable error,
            @NotNull IdCaptureSession session,
            @NotNull FrameData data
    ) {
        // Not interested in this callback.
    }

    @Override
    @WorkerThread
    public void onObservationStarted(@NotNull IdCapture mode) {
        // Not interested in this callback.
    }

    @Override
    @WorkerThread
    public void onObservationStopped(@NotNull IdCapture mode) {
        // Not interested in this callback.
    }
}
