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

package com.scandit.datacapture.ageverifieddeliverysample.ui.scan;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.scandit.datacapture.ageverifieddeliverysample.ui.scan.DriverLicenseSide.BACK_BARCODE;
import static com.scandit.datacapture.ageverifieddeliverysample.ui.scan.DriverLicenseSide.FRONT_VIZ;
import static com.scandit.datacapture.ageverifieddeliverysample.ui.scan.ScanUiState.INITIAL_DRIVER_LICENSE_SIDE;
import static com.scandit.datacapture.ageverifieddeliverysample.ui.scan.ScanUiState.INITIAL_TARGET_DOCUMENT;
import static com.scandit.datacapture.ageverifieddeliverysample.ui.scan.TargetDocument.DRIVER_LICENSE;
import static com.scandit.datacapture.ageverifieddeliverysample.ui.scan.TargetDocument.MILITARY_ID;
import static com.scandit.datacapture.ageverifieddeliverysample.ui.scan.TargetDocument.PASSPORT;
import static com.scandit.datacapture.ageverifieddeliverysample.ui.scan.VerificationFailureReason.DOCUMENT_EXPIRED;
import static com.scandit.datacapture.ageverifieddeliverysample.ui.scan.VerificationFailureReason.HOLDER_UNDERAGE;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.ageverifieddeliverysample.data.CameraRepository;
import com.scandit.datacapture.ageverifieddeliverysample.di.Injector;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureListener;
import com.scandit.datacapture.id.capture.IdCaptureSession;
import com.scandit.datacapture.id.capture.IdCaptureSettings;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.DateResult;
import com.scandit.datacapture.id.data.IdDocumentType;
import com.scandit.datacapture.id.data.LocalizedOnlyId;
import com.scandit.datacapture.id.data.RejectedId;
import com.scandit.datacapture.id.ui.IdLayout;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The view model for the screen  where the user may capture the recipient's document.
 */
public class ScanViewModel extends ViewModel implements IdCaptureListener {
    /**
     * The delay in milliseconds after which the hint that informs the user that they may attempt
     * to manually enter recipient's document data should be displayed.
     */
    private static final long ENTER_MANUALLY_HINT_DELAY_MS = 20000;

    /**
     * The legal age in the US.
     */
    private static final long LEGAL_AGE = 21;

    /**
     * The repository to interact with the device's camera. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private final CameraRepository cameraRepository = Injector.getInstance().getCameraRepository();

    /**
     * Get the current IdCapture.
     */
    private final IdCapture idCapture = Injector.getInstance().getIdCapture();

    /*
     * IdCaptureOverlay displays the additional UI to guide the user through the capture process.
     */
    private final IdCaptureOverlay overlay = Injector.getInstance().getOverlay();

    /**
     * The observer of a single event when the hint that informs the user that they may attempt
     * to manually enter recipient's document data should be displayed.
     */
    private final Observer<Object> enterManuallyHintObserver = this::onHintEnterManually;

    /**
     * The observer of captured document data.
     */
    private final Observer<CapturedId> capturedIdsObserver = this::verifyCapturedId;

    /**
     * The observer of PDF417 barcodes that cannot be parsed.
     */
    private final Observer<RejectedId> rejectedBarcodesObserver = this::emitBarcodeRejected;

    /**
     * The timer to schedule displaying hints after a short delay. In this sample we use the timer
     * for this purpose, but you may for example replace it with the delay operator of a reactive
     * stream.
     */
    private final Timer timer = new Timer();

    private TimerTask showEnterManuallyHint = null;

    /**
     * The state representing the currently displayed UI.
     */
    private ScanUiState uiState = ScanUiState.builder().build();

    /**
     * The stream of UI states.
     */
    private final MutableLiveData<ScanUiState> uiStates = new MutableLiveData<>();

    /**
     * Events to display the UI to manually enter the recipient's document data.
     */
    private final MutableLiveData<GoToManualEntry> goToManualEntry = new MutableLiveData<>();

    /**
     * Events to display the UI that informs the user that a given PDF417 barcode
     * can be captured, but its data cannot be parsed.
     */
    private final MutableLiveData<GoToBarcodeNotSupported> goToBarcodeNotSupported = new MutableLiveData<>();

    /**
     * Events to display the UI that informs the user the a given ID or MRZ is detected, but cannot
     * be parsed.
     */
    private final MutableLiveData<GoToIdLocalizedButNotCaptured> goToIdLocalizedButNotCaptured = new MutableLiveData<>();

    /**
     * Events to display the UI that informs the user that the recipient's document data verification failed
     * and explains the reason. That UI allows either to retry the capture process or to refuse
     * the delivery entirely.
     */
    private final MutableLiveData<GoToVerificationFailure> goToVerificationFailure = new MutableLiveData<>();

    /**
     * Events to display the UI that informs the user that the recipient's document data verification was
     * successful and allows to proceed with the delivery.
     */
    private final MutableLiveData<GoToVerificationSuccess> goToVerificationSuccess = new MutableLiveData<>();

    /**
     * The observer that emits a single event when the hint that informs the user that they may attempt
     * to manually enter recipient's document data should be displayed.
     */
    private final MutableLiveData<Object> enterManuallyHints = new MutableLiveData<>();

    /**
     * The stream of captured document data.
     */
    private final MutableLiveData<CapturedId> capturedIds = new MutableLiveData<>();

    /**
     * The stream of localized ID documents.
     */
    private final MutableLiveData<LocalizedOnlyId> localizedIds = new MutableLiveData<>();

    /**
     * The stream of PDF417 barcodes that cannot be parsed.
     */
    private final MutableLiveData<RejectedId> rejectedBarcodes = new MutableLiveData<>();

    public ScanViewModel() {
        /*
         * Observe the stream of the lower layer and the timer events.
         */
        enterManuallyHints.observeForever(enterManuallyHintObserver);
        capturedIds.observeForever(capturedIdsObserver);
        rejectedBarcodes.observeForever(rejectedBarcodesObserver);

        /*
         * Set up the IdCapture with the initial document configuration.
         */
        IdDocumentType idDocumentType =
                getSupportedDocument(INITIAL_TARGET_DOCUMENT, INITIAL_DRIVER_LICENSE_SIDE);
        idCapture.addListener(this);
        updateIdCapture(idDocumentType);

        uiState = uiState.toBuilder().overlay(overlay).build();

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
        enterManuallyHints.removeObserver(enterManuallyHintObserver);
        capturedIds.removeObserver(capturedIdsObserver);
        rejectedBarcodes.removeObserver(rejectedBarcodesObserver);
        timer.cancel();
    }

    /**
     * The stream of UI states.
     */
    public LiveData<ScanUiState> uiStates() {
        return uiStates;
    }

    /**
     * Events to display the UI to manually enter the recipient's document data.
     */
    public LiveData<GoToManualEntry> goToManualEntry() {
        return goToManualEntry;
    }

    /**
     * Events to display the UI that informs the user that a given PDF417 barcode
     * can be captured, but its data cannot be parsed.
     */
    public LiveData<GoToBarcodeNotSupported> goToBarcodeNotSupported() {
        return goToBarcodeNotSupported;
    }

    /**
     * Events to display the UI that informs the user the a given ID or MRZ is detected, but cannot
     * be parsed.
     */
    public LiveData<GoToIdLocalizedButNotCaptured> goToIdLocalizedButNotCaptured() {
        return goToIdLocalizedButNotCaptured;
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
     * The user toggled the toggle to switch between capturing the barcode at the back side of the recipient's
     * driver's license and OCRing the front side of the document.
     *
     * Update the IdCapture with the new configuration. If the user selected to OCR the front side
     * of the recipient's driver's license, dismiss the hint to do so.
     *
     * Schedule the hint that informs the user that they may attempt to manually enter
     * the recipient's document data if necessary.
     */
    public void onFrontBackToggled(boolean isChecked) {
        DriverLicenseSide side = isChecked ? FRONT_VIZ : BACK_BARCODE;
        onDriverLicenseSideSelected(side);
    }

    /**
     * Either capturing the barcode at the back side of the recipient's
     * driver's license or OCRing the front side of the document is selected.
     *
     * Update the IdCapture with the new configuration. If the user selected to OCR the front side
     * of the recipient's driver's license, dismiss the hint to do so.
     *
     * Schedule the hint that informs the user that they may attempt to manually enter
     * the recipient's document data if necessary.
     */
    public void onDriverLicenseSideSelected(DriverLicenseSide side) {
        IdDocumentType documentType = getSupportedDocument(uiState.getTargetDocument(), side);
        updateIdCapture(documentType);

        ScanUiState.Builder uiStateBuilder = uiState.toBuilder()
                .driverLicenseSide(side);

        resetScheduledHints();

        if (side == FRONT_VIZ && uiState.getEnterManuallyHintVisibility() != VISIBLE) {
            scheduleEnterManuallyHint();
        }

        uiState = uiStateBuilder.build();

        uiStates.postValue(uiState);
    }

    /**
     * Dismiss the hint the hint that points the user towards the buttons to switch between
     * the available document types.
     */
    void onSelectTargetDocumentHintClicked() {
        uiState = uiState.toBuilder()
                .selectTargetDocumentHintVisibility(View.GONE)
                .build();

        uiStates.postValue(uiState);
    }

    /**
     * The user will attempt to capture the recipient's driver's license.
     *
     * Update the IdCapture with the new configuration.
     */
    public void onDriverLicenseSelected() {
        DriverLicenseSide side = uiState.getDriverLicenseSide();
        IdDocumentType documentType = getSupportedDocument(DRIVER_LICENSE, side);
        updateIdCapture(documentType);

        resetScheduledHints();

        if (uiState.getDriverLicenseSide() == FRONT_VIZ && uiState.getEnterManuallyHintVisibility() != VISIBLE) {
            scheduleEnterManuallyHint();
        }

        uiState = uiState.toBuilder()
                .targetDocument(DRIVER_LICENSE)
                .driverLicenseToggleVisibility(VISIBLE)
                .build();

        uiStates.postValue(uiState);
    }

    /**
     * The user will attempt to capture the MRZ of the recipient's passport.
     *
     * Update the IdCapture with the new configuration.
     *
     * Schedule the hint that informs the user that they may attempt to manually enter
     * the recipient's document data if not yet shown/scheduled.
     */
    public void onPassportSelected() {
        DriverLicenseSide side = uiState.getDriverLicenseSide();

        IdDocumentType documentType = getSupportedDocument(PASSPORT, side);
        updateIdCapture(documentType);

        ScanUiState.Builder uiStateBuilder = uiState.toBuilder()
                .targetDocument(PASSPORT)
                .driverLicenseToggleVisibility(INVISIBLE);

        resetScheduledHints();

        if (uiState.getEnterManuallyHintVisibility() != VISIBLE) {
            scheduleEnterManuallyHint();
        }

        uiState = uiStateBuilder.build();

        uiStates.postValue(uiState);
    }

    /**
     * The user will attempt to capture the barcode of the recipient's U.S. military ID (US
     * Uniformed Services document).
     *
     * Update the IdCapture with the new configuration.
     *
     * Schedule the hint that informs the user that they may attempt to manually enter
     * the recipient's document data if not yet shown/scheduled.
     */
    public void onMilitaryIdSelected() {
        DriverLicenseSide side = uiState.getDriverLicenseSide();

        IdDocumentType documentType = getSupportedDocument(MILITARY_ID, side);
        updateIdCapture(documentType);

        ScanUiState.Builder uiStateBuilder = uiState.toBuilder()
                .targetDocument(MILITARY_ID)
                .driverLicenseToggleVisibility(INVISIBLE);

        resetScheduledHints();

        if (uiState.getEnterManuallyHintVisibility() != VISIBLE) {
            scheduleEnterManuallyHint();
        }

        uiState = uiStateBuilder.build();

        uiStates.postValue(uiState);
    }

    public void resetIdCaptureState() {
        IdDocumentType idDocumentType =
                getSupportedDocument(INITIAL_TARGET_DOCUMENT, INITIAL_DRIVER_LICENSE_SIDE);
        updateIdCapture(idDocumentType);
        uiState = ScanUiState.builder().build().toBuilder().overlay(overlay).build();
        uiStates.postValue(uiState);
        resumeCapture();
    }

    /**
     * Update the IdCapture to extract information from a different document type.
     */
    private void updateIdCapture(IdDocumentType newDocumentType) {
        IdCaptureSettings settings = new IdCaptureSettings();
        settings.setSupportedDocuments(newDocumentType);

        idCapture.applySettings(settings);
    }

    /**
     * Schedule the hint that informs the user that they may attempt to manually enter
     * the recipient's document data. The TimerTask triggers in the background thread, so we post
     * it to the LiveData in order to return to the UI thread.
     *
     * In this sample we use the timer for this purpose, but you may for example replace
     * it with the delay operator of a reactive stream.
     */
    private void scheduleEnterManuallyHint() {
        showEnterManuallyHint = new TimerTask() {
            @Override
            public void run() {
                enterManuallyHints.postValue(new Object());
            }
        };
        timer.schedule(showEnterManuallyHint, ENTER_MANUALLY_HINT_DELAY_MS);
    }

    /**
     * The user will attempt the enter the recipient's document data manually. Display the proper UI.
     */
    void onManualEntrySelected() {
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
     * Some pop-up covering the screen got dismissed, resume the capture.
     */
    public void onPopUpDismissed() {
        cameraRepository.turnOnCamera();
        idCapture.setEnabled(true);
    }

    /**
     * The UI to enter the recipient's data manually has been dismissed, resume the capture.
     */
    public void onManualEntryDismissed() {
        onPopUpDismissed();

        uiState = uiState.toBuilder()
                .enterManuallyHintVisibility(VISIBLE)
                .build();

        uiStates.postValue(uiState);
    }

    private void resetScheduledHints() {
        if (showEnterManuallyHint != null) {
            showEnterManuallyHint.cancel();
            showEnterManuallyHint = null;
        }
    }

    /**
     * Resume ID capture.
     */
    void resumeCapture() {
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
     * After a short delay, display the hint the hint that informs the user that they may attempt
     * to manually enter recipient's document data.
     */
    private void onHintEnterManually(Object ignored) {
        uiState = uiState.toBuilder()
                .enterManuallyHintVisibility(VISIBLE)
                .build();

        uiStates.postValue(uiState);
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

        /*
         * The recipient's date of birth is necessary to verify if they are not underage.
         */
        if (isDateOfBirthCaptured(capturedId)) {
            pauseCapture();

            /*
             * The callback is executed in the background thread. We post the value to the LiveData
             * in order to return to the UI thread.
             */
            capturedIds.postValue(session.getNewlyCapturedId());
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
     * Verify the data of this CapturedId.
     */
    private void verifyCapturedId(CapturedId capturedId) {
        resetScheduledHints();
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
        if (isExpired(data)) {
            goToVerificationFailure.postValue(new GoToVerificationFailure(DOCUMENT_EXPIRED));
        } else if (isHolderUnderage(data)) {
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
    private boolean isHolderUnderage(DocumentData data) {
        long age = ChronoUnit.YEARS.between(data.getDateOfBirth(), LocalDate.now());

        return age < LEGAL_AGE;
    }

    @Override
    @WorkerThread
    public void onIdLocalized(
            @NotNull IdCapture mode,
            @NotNull IdCaptureSession session,
            @NotNull FrameData data
    ) {
        /*
         * Implement to handle a personal identification document or its part localized within
         * a frame. A document or its part is considered localized when it's detected in a frame,
         * but its data is not yet extracted.
         */

        LocalizedOnlyId localizedId = session.getNewlyLocalizedOnlyId();

        /*
         * The callback is executed in the background thread. We post the value to the LiveData
         * in order to return to the UI thread.
         */
        localizedIds.postValue(localizedId);
    }

    /**
     * We detected a document, but couldn't extract the data yet. Show a pop-up to inform
     * the user that they may attempt to try another method of entering the data:
     * * to capture the front of card if they are trying to capture the barcode
     * * to enter the data manually otherwise
     */
    private void goToTryAnotherMethod() {
        idCapture.setEnabled(false);

        if (isBackOfDriverLicenseExpected()) {
            goToBarcodeNotSupported.postValue(new GoToBarcodeNotSupported());
        } else {
            goToIdLocalizedButNotCaptured.postValue(new GoToIdLocalizedButNotCaptured());
        }
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
         *   (a) it's a valid personal identification document, but its not a driver's license.
         *   (b) it's a PDF417 barcode, but the data is encoded in an unexpected format.
         */

        RejectedId rejectedId = session.getNewlyRejectedId();

        if (isBarcodeExpected()) {
            /*
             * The callback is executed in the background thread. We post the value to the LiveData
             * in order to return to the UI thread.
             */
            rejectedBarcodes.postValue(rejectedId);
        }
    }

    /**
     * Check whether back of driver's license is expected to be scanned.
     */
    private boolean isBackOfDriverLicenseExpected() {
        return uiState.getTargetDocument() == DRIVER_LICENSE &&
                uiState.getDriverLicenseSide() == BACK_BARCODE;
    }

    /**
     * Check whether U.S. military ID (US Uniformed Services document) is expected to be scanned.
     */
    private boolean isMilitaryIdExpected() {
        return uiState.getTargetDocument() == MILITARY_ID;
    }

    /**
     * Check whether a barcode is expected to be scanned.
     */
    private boolean isBarcodeExpected() {
        return isBackOfDriverLicenseExpected() || isMilitaryIdExpected();
    }

    /**
     * Display the UI that informs the user that a given PDF417 barcode can be captured, but its
     * data cannot be parsed.
     */
    private void emitBarcodeRejected(RejectedId rejectedId) {
        if (isBarcodeExpected()) {
            goToBarcodeNotSupported.postValue(new GoToBarcodeNotSupported());
        }
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
        goToTryAnotherMethod();
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

    /**
     * Extract from the selected document kind and/or side the layout of the additional UI
     * to guide the user through the capture process. AUTO tries to display the best UI based on
     * the selected IdCapture configuration, however it may be overridden be selecting one of
     * the specific UIs.
     */
    private static IdLayout getOverlayLayout(TargetDocument document) {
        switch (document) {
            case DRIVER_LICENSE:
                return IdLayout.AUTO;
            case PASSPORT:
                return IdLayout.TD3;
            case MILITARY_ID:
                return IdLayout.PDF417;
            default:
                throw new AssertionError("Unknown target document " + document);
        }
    }

    /**
     * Extract from the selected document kind and/or side the configuration to setup IdCapture.
     */
    private static IdDocumentType getSupportedDocument(
            TargetDocument document,
            DriverLicenseSide side
    ) {
        switch (document) {
            case DRIVER_LICENSE:
                switch (side) {
                    case BACK_BARCODE:
                        return IdDocumentType.AAMVA_BARCODE;
                    case FRONT_VIZ:
                        return IdDocumentType.DL_VIZ;
                    default:
                        throw new AssertionError("Unknown driver license side " + side);
                }
            case PASSPORT:
                return IdDocumentType.PASSPORT_MRZ;
            case MILITARY_ID:
                return IdDocumentType.US_US_ID_BARCODE;
            default:
                throw new AssertionError("Unknown target document " + document);
        }
    }
}
