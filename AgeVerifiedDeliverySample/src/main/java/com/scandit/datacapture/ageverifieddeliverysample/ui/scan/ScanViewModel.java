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

import androidx.annotation.StringRes;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.ageverifieddeliverysample.R;
import com.scandit.datacapture.ageverifieddeliverysample.data.CameraRepository;
import com.scandit.datacapture.ageverifieddeliverysample.data.IdCaptureRepository;
import com.scandit.datacapture.ageverifieddeliverysample.di.Injector;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureListener;
import com.scandit.datacapture.id.capture.IdCaptureSession;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.DateResult;
import com.scandit.datacapture.id.data.IdDocumentType;
import com.scandit.datacapture.id.ui.IdLayout;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.scandit.datacapture.ageverifieddeliverysample.ui.scan.DriverLicenseSide.BACK_BARCODE;
import static com.scandit.datacapture.ageverifieddeliverysample.ui.scan.DriverLicenseSide.FRONT_VIZ;
import static com.scandit.datacapture.ageverifieddeliverysample.ui.scan.TargetDocument.DRIVER_LICENSE;
import static com.scandit.datacapture.ageverifieddeliverysample.ui.scan.TargetDocument.PASSPORT;
import static com.scandit.datacapture.ageverifieddeliverysample.ui.scan.VerificationFailureReason.DOCUMENT_EXPIRED;
import static com.scandit.datacapture.ageverifieddeliverysample.ui.scan.VerificationFailureReason.HOLDER_UNDERAGE;

/**
 * The view model for the screen  where the user may capture the recipient's document.
 */
public class ScanViewModel extends ViewModel implements IdCaptureListener {
    /**
     * The delay in milliseconds after which the hint that informs the user that they may attempt
     * to OCR the front side of the recipient's driver's license should be displayed.
     */
    private static final long SCAN_DRIVER_LICENSE_VIZ_HINT_DELAY_MS = 4000;

    /**
     * The delay in milliseconds after which the hint that informs the user that they may attempt
     * to manually enter recipient's document data should be displayed.
     */
    private static final long ENTER_MANUALLY_HINT_DELAY_MS = 8000;

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
     * The repository to interact with the IdCapture mode. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private final IdCaptureRepository idCaptureRepository = Injector.getInstance().getIdCaptureRepository();

    /**
     * The observer of IdCapture objects from the lower layer. The IdCapture is recreated every
     * time a new type of document to capture is selected.
     */
    private final Observer<IdCapture> idCapturesObserver = this::onNewIdCapture;

    /**
     * The observer of a single event when the hint that informs the user that they may attempt
     * to OCR the front side of the recipient's driver's license should be displayed.
     */
    private final Observer<Object> driverLicenseVizScanHintObserver = this::onHintDriverLicenseVizScanDelay;

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
     * The timer to schedule displaying hints after a short delay. In this sample we use the timer
     * for this purpose, but you may for example replace it with the delay operator of a reactive
     * stream.
     */
    private final Timer timer = new Timer();

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
     * The stream that emits a single event when the hint that informs the user that they may attempt
     * to OCR the front side of the recipient's driver's license should be displayed.
     */
    private final MutableLiveData<Object> driverLicenseVizScanHints = new MutableLiveData<>();

    /**
     * The observer that emits a single event when the hint that informs the user that they may attempt
     * to manually enter recipient's document data should be displayed.
     */
    private final MutableLiveData<Object> enterManuallyHints = new MutableLiveData<>();

    /**
     * The stream of captured document data.
     */
    private final MutableLiveData<CapturedId> capturedIds = new MutableLiveData<>();

    public ScanViewModel() {
        /*
         * Observe the stream of the lower layer and the timer events.
         */
        idCaptureRepository.idCaptures().observeForever(idCapturesObserver);
        driverLicenseVizScanHints.observeForever(driverLicenseVizScanHintObserver);
        enterManuallyHints.observeForever(enterManuallyHintObserver);
        capturedIds.observeForever(capturedIdsObserver);

        /*
         * Trigger the create of IdCapture for the initial document configuration.
         */
        IdDocumentType idDocumentType =
                getSupportedDocument(uiState.getTargetDocument(), uiState.getDriverLicenseSide());
        idCaptureRepository.recreateIdCapture(idDocumentType);

        /*
         * Post the initial UI state.
         */
        uiStates.postValue(uiState);

        /*
         * Schedule the hint that informs the user that they may attempt to OCR the front side
         * of the recipient's driver's license. The TimerTask triggers in the background thread, so we post
         * it to the LiveData in order to return to the UI thread.
         *
         * In this sample we use the timer for this purpose, but you may for example replace
         * it with the delay operator of a reactive stream.
         */
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                driverLicenseVizScanHints.postValue(new Object());
            }
        }, SCAN_DRIVER_LICENSE_VIZ_HINT_DELAY_MS);
    }

    @Override
    protected void onCleared() {
        /*
         * Stop observing the streams of the lower layer and the timer events to avoid memory leak.
         */
        idCaptureRepository.idCaptures().removeObserver(idCapturesObserver);
        driverLicenseVizScanHints.removeObserver(driverLicenseVizScanHintObserver);
        enterManuallyHints.removeObserver(enterManuallyHintObserver);
        capturedIds.removeObserver(capturedIdsObserver);
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
     * Recreate the IdCapture with the new configuration. If the user selected to OCR the front side
     * of the recipient's driver's license, dismiss the hint to do so.
     *
     * Schedule the hint that informs the user that they may attempt to manually enter
     * the recipient's document data if necessary.
     */
    void onFrontBackToggled(boolean isChecked) {
        DriverLicenseSide side = isChecked ? FRONT_VIZ : BACK_BARCODE;
        @StringRes int scanHint = getScanHint(uiState.getTargetDocument(), side);

        IdDocumentType documentType = getSupportedDocument(uiState.getTargetDocument(), side);
        idCaptureRepository.recreateIdCapture(documentType);

        ScanUiState.Builder uiStateBuilder = uiState.toBuilder()
                .driverLicenseSide(side)
                .scanHint(scanHint);

        if (side == FRONT_VIZ) {
            uiStateBuilder.scanDriverLicenseVizHintStatus(ScanDriverLicenseVizHintStatus.DISMISSED);

            if (uiState.getEnterManuallyHintStatus() == EnterManuallyHintStatus.NOT_SCHEDULED) {
                scheduleEnterManuallyHint();
                uiStateBuilder.enterManuallyHintStatus(EnterManuallyHintStatus.SCHEDULED);
            }
        }

        uiState = uiStateBuilder.build();

        uiStates.postValue(uiState);
    }

    /**
     * The user will attempt to capture the recipient's driver's license.
     *
     * Recreate the IdCapture with the new configuration.
     */
    void onDriverLicenseSelected() {
        DriverLicenseSide side = uiState.getDriverLicenseSide();
        @StringRes int scanHint = getScanHint(DRIVER_LICENSE, side);

        IdDocumentType documentType = getSupportedDocument(DRIVER_LICENSE, side);
        idCaptureRepository.recreateIdCapture(documentType);

        uiState = uiState.toBuilder()
                .targetDocument(DRIVER_LICENSE)
                .scanHint(scanHint)
                .driverLicenseToggleVisibility(VISIBLE)
                .scanDriverLicenseVizHintVisibility(VISIBLE)
                .build();

        uiStates.postValue(uiState);
    }

    /**
     * The user will attempt to capture the MRZ of the recipient's passport.
     *
     * Recreate the IdCapture with the new configuration.
     *
     * Schedule the hint that informs the user that they may attempt to manually enter
     * the recipient's document data if not yet shown/scheduled.
     */
    void onPassportSelected() {
        DriverLicenseSide side = uiState.getDriverLicenseSide();
        @StringRes int scanHint = getScanHint(PASSPORT, side);

        IdDocumentType documentType = getSupportedDocument(PASSPORT, side);
        idCaptureRepository.recreateIdCapture(documentType);

        ScanUiState.Builder uiStateBuilder = uiState.toBuilder()
                .targetDocument(PASSPORT)
                .scanHint(scanHint)
                .driverLicenseToggleVisibility(INVISIBLE)
                .scanDriverLicenseVizHintVisibility(INVISIBLE);

        if (uiState.getEnterManuallyHintStatus() == EnterManuallyHintStatus.NOT_SCHEDULED) {
            scheduleEnterManuallyHint();
            uiStateBuilder.enterManuallyHintStatus(EnterManuallyHintStatus.SCHEDULED);
        }

        uiState = uiStateBuilder.build();

        uiStates.postValue(uiState);
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
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                enterManuallyHints.postValue(new Object());
            }
        }, ENTER_MANUALLY_HINT_DELAY_MS);
    }

    /**
     * The user will attempt the enter the recipient's document data manually. Display the proper UI.
     */
    void onManualEntrySelected() {
        goToManualEntry.postValue(new GoToManualEntry());
    }

    /**
     * The user manually entered the recipient's document data. Proceed with the verification.
     */
    public void onDataEnteredManually(DocumentData manualEntry) {
        verifyDocumentData(manualEntry);
    }

    /**
     * Start the camera preview.
     */
    void turnOnCamera() {
        cameraRepository.turnOnCamera();
    }

    /**
     * Stop the camera preview.
     */
    void turnOffCamera() {
        cameraRepository.turnOffCamera();
    }

    /**
     * IdCapture recreated with the current configuration.
     */
    private void onNewIdCapture(IdCapture idCapture) {
        idCapture.addListener(this);

        /*
         * IdCaptureOverlay displays the additional UI to guide the user through the capture process.
         */
        IdCaptureOverlay overlay = IdCaptureOverlay.newInstance(idCapture, null);
        overlay.setIdLayout(getOverlayLayout(uiState.getTargetDocument()));

        uiState = uiState.toBuilder().overlay(overlay).build();
        uiStates.postValue(uiState);
    }

    /**
     * After a short delay, display the hint that informs the user that they may attempt
     * to OCR the front side of the recipient's driver's license.
     */
    private void onHintDriverLicenseVizScanDelay(Object ignored) {
        if (uiState.getScanDriverLicenseVizHintStatus() == ScanDriverLicenseVizHintStatus.SCHEDULED) {
            uiState = uiState.toBuilder()
                    .scanDriverLicenseVizHintStatus(ScanDriverLicenseVizHintStatus.DISPLAYED)
                    .build();

            uiStates.postValue(uiState);
        }
    }

    /**
     * After a short delay, display the hint the hint that informs the user that they may attempt
     * to manually enter recipient's document data.
     */
    private void onHintEnterManually(Object ignored) {
        uiState = uiState.toBuilder()
                .enterManuallyHintStatus(EnterManuallyHintStatus.DISPLAYED)
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
            idCaptureRepository.disableIdCapture();

            /*
             * The callback is executed in the background thread. We post the value to the LiveData
             * in order to return to the UI thread.
             */
            capturedIds.postValue(session.getNewlyCapturedId());
        }
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
            default:
                throw new AssertionError("Unknown target document " + document);
        }
    }

    /**
     * Extract from the selected document kind and/or side the text of the additional hint
     * to aid the user with the capture process.
     */
    @StringRes
    private static int getScanHint(
            TargetDocument document,
            DriverLicenseSide side
    ) {
        switch (document) {
            case DRIVER_LICENSE:
                switch (side) {
                    case BACK_BARCODE:
                        return R.string.scan_hint_driver_license_barcode;
                    case FRONT_VIZ:
                        return R.string.scan_hint_driver_license_viz;
                    default:
                        throw new AssertionError("Unknown driver license side " + side);
                }
            case PASSPORT:
                return R.string.scan_hint_passport_mrz;
            default:
                throw new AssertionError("Unknown target document " + document);
        }
    }
}
