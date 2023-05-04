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

package com.scandit.datacapture.idcapturesettingssample.ui.scan;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;
import com.scandit.datacapture.idcapturesettingssample.data.CameraRepository;
import com.scandit.datacapture.idcapturesettingssample.data.DataCaptureViewRepository;
import com.scandit.datacapture.idcapturesettingssample.data.IdCaptureRepository;
import com.scandit.datacapture.idcapturesettingssample.di.Injector;
import com.scandit.datacapture.idcapturesettingssample.mappers.IdCaptureResultFactory;
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult;

public class ScanViewModel extends ViewModel {

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
     * The repository to interact with DataCaptureViews. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private final DataCaptureViewRepository dataCaptureViewRepository = Injector.getInstance().getDataCaptureViewRepository();

    /**
     * The observer of captured document data.
     */
    private final Observer<ScanResultEvent> scanResultsObserver = this::displayCapturedResult;

    /**
     * Events to display the UI with the details of a captured personal
     * identification document data.
     */
    private final MutableLiveData<ShowCaptureResultEvent> showCaptureResult = new MutableLiveData<>();

    /**
     * Events to display a non-blocking UI with some of the captured personal
     * identification document data.
     */
    private final MutableLiveData<ShowCaptureResultToastEvent> showToastResult = new MutableLiveData<>();

    /**
     * Events to display a blocking alert notifying that scanning of the document's back is available.
     */
    private final MutableLiveData<ShowBackScanAvailableEvent> showScanBack = new MutableLiveData<>();

    public ScanViewModel() {

        /*
         * Observe the results stream and forward it to the fragment.
         */
        idCaptureRepository.scanResults().observeForever(scanResultsObserver);
    }

    @Override
    protected void onCleared() {
        /*
         * Stop observing the streams of the lower layer and the timer events to avoid memory leak.
         */
        idCaptureRepository.scanResults().removeObserver(scanResultsObserver);
    }

    /**
     * Events to display the UI with the details of a captured personal
     * identification document data.
     */
    public LiveData<ShowCaptureResultEvent> showCapturedResult() {
        return showCaptureResult;
    }

    /**
     * Events to display the UI with a Toast containing some minimal details of a captured personal
     * identification document data.
     */
    public LiveData<ShowCaptureResultToastEvent> showCapturedResultToast() {
        return showToastResult;
    }

    /**
     * Events to display the UI with an alert notifying that the back scan is available.
     */
    public LiveData<ShowBackScanAvailableEvent> showScanBack() {
        return showScanBack;
    }

    /**
     * Reset the IdCapture. This allows to keep scanning after skipping scanning the back of a document.
     */
    public void resetIdCapture() {
        idCaptureRepository.resetIdCapture();
    }

    /**
     * Shortcut method to both disable idCapture and turn off the camera.
     */
    void disableIdCaptureAndTurnOffCamera() {
        disableIdCapture();
        turnOffCamera();
    }

    /**
     * Shortcut method to both enable idCapture and turn on the camera.
     */
    void enableIdCaptureAndTurnOnCamera() {
        enableIdCapture();
        turnOnCamera();
    }

    /**
     * Resume scanning by enabling the IdCapture mode.
     * Once IdCapture is enabled it will start processing the camera frames.
     */
    void enableIdCapture() {
        idCaptureRepository.enableIdCapture();
    }

    /**
     * Pause scanning by disabling the IdCapture mode.
     * Once IdCapture is disabled it will no longer process camera frames.
     */
    void disableIdCapture() {
        idCaptureRepository.disableIdCapture();
    }

    /**
     * Start the camera preview.
     */
    void turnOnCamera() {
        /*
         * Switch the camera on. The camera frames will be sent to TextCapture for processing.
         * Additionally the preview will appear on the screen. The camera is started asynchronously,
         * and you may notice a small delay before the preview appears.
         */
        cameraRepository.turnOnCamera();
    }

    /**
     * Stop the camera preview.
     */
    void turnOffCamera() {
        cameraRepository.turnOffCamera();
    }

    /**
     * Requests the idCapture instance to be refreshed. The refresh happens in the IdCaptureRepository class.
     */
    void refreshComponents() {
        idCaptureRepository.refreshIdCapture();
        cameraRepository.refreshCamera();
    }

    /**
     * Requests an IdCaptureOverlay to be built.
     */
    IdCaptureOverlay buildIdCaptureOverlay() {
        return idCaptureRepository.buildIdCaptureOverlay();
    }

    /**
     * Request a DataCaptureView to be built
     */
    DataCaptureView buildDataCaptureView(Context context) {
        return dataCaptureViewRepository.buildDataCaptureView(context);
    }

    /**
     * Display the UI with the details of a captured personal
     * identification document data.
     */
    private void displayCapturedResult(ScanResultEvent event) {
        ScanResult scanResult = event.getContentIfNotHandled();

        if (scanResult == null) return;

        final CaptureResult result = IdCaptureResultFactory.extract(scanResult.getCapturedId());

        if (scanResult.isNeedsBackScan()) {
            showScanBack.postValue(new ShowBackScanAvailableEvent(result));
        } else if (!scanResult.isContinuousMode()) {
            showCaptureResult.postValue(new ShowCaptureResultEvent(result));
        } else {
            /*
             * Display the result in a non-blocking way, so the mode doesn't have to stop scanning.
             */
            showToastResult.postValue(new ShowCaptureResultToastEvent(result));
        }
    }

    void showCapturedResultInSelectedMode(CaptureResult result) {
        if (!idCaptureRepository.isContinuousMode()) {
            showCaptureResult.postValue(new ShowCaptureResultEvent(result));
        } else {
            showToastResult.postValue(new ShowCaptureResultToastEvent(result));
        }
    }
}
