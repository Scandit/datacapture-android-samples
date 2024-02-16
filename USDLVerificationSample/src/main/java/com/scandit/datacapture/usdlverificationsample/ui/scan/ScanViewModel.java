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
import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.CapturedResultType;
import com.scandit.datacapture.id.data.SupportedSides;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;
import com.scandit.datacapture.id.verification.aamvabarcode.AamvaBarcodeVerificationTask;
import com.scandit.datacapture.id.verification.aamvavizbarcode.AamvaVizBarcodeComparisonResult;
import com.scandit.datacapture.usdlverificationsample.R;
import com.scandit.datacapture.usdlverificationsample.data.CameraRepository;
import com.scandit.datacapture.usdlverificationsample.data.DriverLicenseVerificationRepository;
import com.scandit.datacapture.usdlverificationsample.data.IdCaptureRepository;
import com.scandit.datacapture.usdlverificationsample.di.Injector;
import com.scandit.datacapture.usdlverificationsample.ui.result.ResultMapper;
import com.scandit.datacapture.usdlverificationsample.ui.result.CaptureResult;

/**
 * The view model for the screen where the user may capture a Driver's License.
 */
public class ScanViewModel extends ViewModel {
    private static final String US_ISO = "USA";

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
     * The repository that allows to verify Driver's licenses. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private final DriverLicenseVerificationRepository driverLicenseVerificationRepository =
            Injector.getInstance().getDriverLicenseVerificationRepository();

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
     * The state representing the currently displayed UI.
     */
    private ScanUiState uiState = ScanUiState.builder().build();

    /**
     * An event to display the UI that informs the user that the captured driver's license is not
     * issued in the United States.
     */
    private final MutableLiveData<GoToUnsupportedDriverLicense> goToUnsupportedDriverLicense = new MutableLiveData<>();

    /**
     * An event to display the UI that informs the user that the verification failed
     */
    private final MutableLiveData<GoToBarcodeVerificationFailure> goToBarcodeVerificationFailure =
            new MutableLiveData<>();

    /**
     * The stream of UI states.
     */
    private final MutableLiveData<ScanUiState> uiStates = new MutableLiveData<>();

    /**
     * Events to display the UI with the result of ID capture.
     */
    private final MutableLiveData<GoToResult> goToResult = new MutableLiveData<>();

    /**
     * The task that runs the verification for a given captured driver's license.
     */
    @Nullable
    private AamvaBarcodeVerificationTask barcodeVerificationTask;

    /**
     * The captured driver's license.
     */
    @Nullable
    private CapturedId driverLicense;

    public ScanViewModel() {
        /*
         * Observe the streams of data from the lower layer.
         */
        idCaptureRepository.idCaptureOverlays().observeForever(idCaptureOverlaysObserver);
        idCaptureRepository.capturedIds().observeForever(capturedIdsObserver);

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
     * An event to display the UI that informs the user that the captured driver's license is
     * not issued in the United States.
     */
    public LiveData<GoToUnsupportedDriverLicense> goToUnsupportedDriverLicense() {
        return goToUnsupportedDriverLicense;
    }

    /**
     * An event to display the UI that informs the user that the verification failed
     */
    public LiveData<GoToBarcodeVerificationFailure> goToBarcodeVerificationFailure() {
        return goToBarcodeVerificationFailure;
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
     * Whenever a driver's license is captured, this method checks whether the scanning is complete
     * and if the license is issued in the United States. If so, the driver's license is verified.
     *
     * If the license is not issued in the United States, a dialog is shown to warn the user.
     *
     * This method also updates the UI that aids the user in the ID capture process.
     */
    private void onIdCaptured(CapturedIdEvent capturedIdEvent) {
        CapturedId capturedId = capturedIdEvent.getContentIfNotHandled();
        if (capturedId == null) return;

        boolean idBackScanNeeded =
                capturedId.getCapturedResultType() == CapturedResultType.VIZ_RESULT &&
                        capturedId.getViz().getCapturedSides() == SupportedSides.FRONT_ONLY &&
                        capturedId.getViz().isBackSideCaptureSupported();

        boolean isUSDriverLicense = capturedId.getIssuingCountryIso() != null &&
                capturedId.getIssuingCountryIso().equals(US_ISO);

        if (!isUSDriverLicense) {
            /*
             * Warn the user if the document the captured driver's license is not issued in
             * the United States.
             */
            idCaptureRepository.resetIdCapture();
            stopIdCapture();
            goToUnsupportedDriverLicense.postValue(new GoToUnsupportedDriverLicense());
        } else if (!idBackScanNeeded) {
            /*
             * If document scanning is complete, store and verify the driver's license.
             */
            driverLicense = capturedId;
            verifyDriverLicense(capturedId);
        }
    }

    /**
     * Verify a captured driver's license by running up to three checks:
     *
     * 1. First, a simple on-device verification where the data from the front of the card is
     * compared with the data encoded in the barcode from the back of the card.
     *
     * 2. Check if the driver's license is not expired.
     *
     * 3. If front & back match and the driver's license has not expired, run a
     * verification.
     */
    private void verifyDriverLicense(CapturedId capturedId) {
        stopIdCapture();

        AamvaVizBarcodeComparisonResult comparisonResult =
                driverLicenseVerificationRepository.compareFrontAndBack(capturedId);
        boolean isFrontBackComparisonSuccessful = comparisonResult.getChecksPassed();
        Bitmap frontMismatchImage = comparisonResult.getFrontMismatchImage();
        boolean isShownLicenseWarning = frontMismatchImage == null &&
            capturedId.getAamvaBarcode() != null &&
            !isFrontBackComparisonSuccessful;

        /*
         * If front and back match AND ID is not expired, run verification
         */
        if (isFrontBackComparisonSuccessful && !capturedId.isExpired()) {
            barcodeVerificationTask = driverLicenseVerificationRepository.verifyIdOnBarcode(capturedId);
            setIsBarcodeVerificationRunning(true);
            setUpBarcodeVerificationTaskListeners();
        } else {
            navigateToResult(capturedId, isFrontBackComparisonSuccessful, false, frontMismatchImage, isShownLicenseWarning);
        }
    }

    /**
     * Set up success and error listeners for the verification of the captured
     * driver's license.
     */
    public void setUpBarcodeVerificationTaskListeners() {
        if (barcodeVerificationTask == null) {
            return;
        }

        /*
         * Present the data to the user when the back-end returns its verification result.
         */
        barcodeVerificationTask.doOnVerificationResult(barcodeVerificationResult -> {
            if (driverLicense != null) {
                boolean checksPassed = barcodeVerificationResult.getAllChecksPassed();
                navigateToResult(driverLicense, true, checksPassed, null, false);
                setIsBarcodeVerificationRunning(false);
                barcodeVerificationTask = null;
                driverLicense = null;
            }
            return null;
        });

        /*
         * If the verification fails, show an error dialog to the user.
         */
        if (barcodeVerificationTask != null) {
            barcodeVerificationTask.doOnConnectionFailure(throwable -> {
                if (driverLicense != null) {
                    goToBarcodeVerificationFailure.postValue(new GoToBarcodeVerificationFailure());
                    setIsBarcodeVerificationRunning(false);
                    barcodeVerificationTask = null;
                    driverLicense = null;
                }
                return null;
            });
        }
    }

    /**
     * Indicate whether the verification is currently in progress.
     */
    private void setIsBarcodeVerificationRunning(boolean isBarcodeVerificationRunning) {
        uiState = uiState.toBuilder()
                .isBarcodeVerificationRunning(isBarcodeVerificationRunning)
                .build();
        uiStates.postValue(uiState);
    }

    /**
     * Convert the data extracted from the document to a result entity and dispatch an event
     * to navigate to the result screen.
     */
    private void navigateToResult(
            CapturedId capturedId,
            boolean isFrontBackComparisonSuccessful,
            boolean isBarcodeVerificationSuccessful,
            @Nullable Bitmap verificationImage,
            boolean isShownLicenseWarning
    ) {
        final CaptureResult result = new ResultMapper(capturedId).mapResult(
                capturedId.isExpired(),
                isFrontBackComparisonSuccessful,
                isBarcodeVerificationSuccessful,
                verificationImage,
                isShownLicenseWarning
        );
        goToResult.postValue(new GoToResult(result));
    }
}
