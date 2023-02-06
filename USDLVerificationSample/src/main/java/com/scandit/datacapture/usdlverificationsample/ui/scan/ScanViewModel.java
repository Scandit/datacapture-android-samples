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
import com.scandit.datacapture.id.verification.aamvacloud.AamvaCloudVerificationTask;
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
     * An event to display the UI that informs the user that the cloud-based verification failed
     * (network errors, etc.).
     */
    private final MutableLiveData<GoToCloudVerificationFailure> goToCloudVerificationFailure =
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
     * The task that runs the cloud-based verification for a given captured driver's license.
     */
    @Nullable
    private AamvaCloudVerificationTask cloudVerificationTask;

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
         * Trigger the create of IdCapture for the initial configuration.
         */
        idCaptureRepository.createIdCapture();

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
     * An event to display the UI that informs the user that the cloud-based verification failed
     * (network errors, etc.).
     */
    public LiveData<GoToCloudVerificationFailure> goToCloudVerificationFailure() {
        return goToCloudVerificationFailure;
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
        boolean shouldScanBackSide = isUSDriverLicense && idBackScanNeeded;
        int scanHint = getScanHint(!shouldScanBackSide);

        uiState = uiState.toBuilder()
                .scanHint(scanHint)
                .build();
        uiStates.postValue(uiState);

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
     * Returns the text of the additional hint to help the user with the capture process, depending
     * on the side of the document that the user currently attempts to capture.
     */
    @StringRes
    private int getScanHint(boolean frontSide) {
        if (frontSide) {
            return R.string.scanning_dl_front_side_helper_text;
        } else {
            return R.string.scanning_dl_back_side_helper_text;
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
     * 3. If front & back match and the driver's license has not expired, run a cloud-based
     * verification.
     */
    private void verifyDriverLicense(CapturedId capturedId) {
        stopIdCapture();

        AamvaVizBarcodeComparisonResult comparisonResult =
                driverLicenseVerificationRepository.compareFrontAndBack(capturedId);
        boolean isFrontBackComparisonSuccessful = comparisonResult.getChecksPassed();

        /*
         * If front and back match AND ID is not expired, run cloud-based verification
         */
        if (isFrontBackComparisonSuccessful && !capturedId.isExpired()) {
            cloudVerificationTask = driverLicenseVerificationRepository.verifyIdOnCloud(capturedId);
            setIsCloudVerificationRunning(true);
            setUpCloudVerificationTaskListeners();
        } else {
            navigateToResult(capturedId, isFrontBackComparisonSuccessful, false);
        }
    }

    /**
     * Set up success and error listeners for the cloud-based verification of the captured
     * driver's license.
     */
    public void setUpCloudVerificationTaskListeners() {
        if (cloudVerificationTask == null) {
            return;
        }

        /*
         * Present the data to the user when the back-end returns its verification result.
         */
        cloudVerificationTask.doOnVerificationResult(cloudVerificationResult -> {
            if (driverLicense != null) {
                boolean checksPassed = cloudVerificationResult.getAllChecksPassed();
                navigateToResult(driverLicense, true, checksPassed);
                setIsCloudVerificationRunning(false);
                cloudVerificationTask = null;
                driverLicense = null;
            }
            return null;
        });

        /*
         * If the cloud-based verification fails (network errors, etc.), show an error dialog
         * to the user.
         */
        cloudVerificationTask.doOnConnectionFailure(throwable -> {
            if (driverLicense != null) {
                goToCloudVerificationFailure.postValue(new GoToCloudVerificationFailure());
                setIsCloudVerificationRunning(false);
                cloudVerificationTask = null;
                driverLicense = null;
            }
            return null;
        });
    }

    /**
     * Indicate whether the cloud verification is currently in progress.
     */
    private void setIsCloudVerificationRunning(boolean isCloudVerificationRunning) {
        uiState = uiState.toBuilder()
                .isCloudVerificationRunning(isCloudVerificationRunning)
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
            boolean isCloudVerificationSuccessful
    ) {
        final CaptureResult result = new ResultMapper(capturedId).mapResult(
                capturedId.isExpired(),
                isFrontBackComparisonSuccessful,
                isCloudVerificationSuccessful
        );
        goToResult.postValue(new GoToResult(result));
    }
}
