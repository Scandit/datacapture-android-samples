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

package com.scandit.datacapture.idcapturesettingssample.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.id.capture.FullDocumentScanner;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureListener;
import com.scandit.datacapture.id.capture.IdCaptureScanner;
import com.scandit.datacapture.id.capture.IdCaptureSettings;
import com.scandit.datacapture.id.capture.SingleSideScanner;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.IdImageType;
import com.scandit.datacapture.id.data.RejectionReason;
import com.scandit.datacapture.id.data.CapturedSides;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;
import com.scandit.datacapture.idcapturesettingssample.ui.scan.ScanResult;
import com.scandit.datacapture.idcapturesettingssample.ui.scan.ScanResultEvent;

import java.util.Arrays;

/**
 * The repository to interact with the IdCapture mode.
 */
public class IdCaptureRepository implements IdCaptureListener {
    /**
     * The IdCapture mode. We need to recreate the IdCapture every time the selected document kind
     * and/or side changes.
     */
    private volatile IdCapture idCapture;

    /**
     * Repository containing the desired settings for IdCapture.
     */
    private final SettingsRepository settingsRepository;

    /**
     * The stream of Captured IDs.
     */
    private final MutableLiveData<ScanResultEvent> scanResults = new MutableLiveData<>();

    /**
     * The DataCaptureContext that the current IdCapture is attached to.
     */
    private final DataCaptureContext dataCaptureContext;

    public IdCaptureRepository(
            DataCaptureContext dataCaptureContext,
            SettingsRepository settingsRepository
    ) {
        this.settingsRepository = settingsRepository;
        this.dataCaptureContext = dataCaptureContext;
    }

    /**
     * The stream of captured IDs.
     */
    public LiveData<ScanResultEvent> scanResults() {
        return scanResults;
    }


    /**
     * Resets the internal IdCapture state.
     */
    public void resetIdCapture() {
        idCapture.reset();
    }

    /**
     * Make IdCapture process incoming frames. This happens asynchronously.
     */
    public void enableIdCapture() {
        idCapture.setEnabled(true);
    }

    /**
     * Stop IdCapture from further processing of frames. This happens asynchronously, so some
     * results may still be delivered.
     */
    public void disableIdCapture() {
        idCapture.setEnabled(false);
    }

    /**
     * Request a new IdCapture to be built with the current settings.
     * We need to recreate the IdCapture every time the selected document kind and/or side changes.
     */
    public void refreshIdCapture() {
        if (idCapture != null) {
            /*
             * Before recreating a new IdCapture, the old one's listener needs to be removed to
             * avoid leaks or undesired callback invocations.
             */
            dataCaptureContext.removeMode(idCapture);
            idCapture.removeListener(this);
        }

        IdCaptureSettings idCaptureSettings = new IdCaptureSettings();

        // Set the accepted document types from settings
        idCaptureSettings.setAcceptedDocuments(settingsRepository.getAcceptedDocuments());
        // Set the rejected document types from settings
        idCaptureSettings.setRejectedDocuments(settingsRepository.getRejectedDocuments());

        // Enable showing the desired image types from the settings.
        Arrays.stream(IdImageType.values()).forEach((type) -> {
            idCaptureSettings.setShouldPassImageTypeToResult(type,
                    settingsRepository.getShouldPassImageForType(type));
        });

        // Set the desired scanner type from settings.
        idCaptureSettings.setScannerType(buildIdCaptureScanner());

        // Set the desired anonymization mode from settings.
        idCaptureSettings.setAnonymizationMode(settingsRepository.getAnonymizationMode());
        // Set whether voided IDs should be rejected
        idCaptureSettings.setRejectVoidedIds(settingsRepository.shouldRejectVoidedIds());
        // Set whether extra information should be extracted from the back side of European driver's licenses
        idCaptureSettings.setDecodeBackOfEuropeanDrivingLicense(settingsRepository.shouldDecodeBackOfEuropeanDrivingLicense());
        idCapture = IdCapture.forDataCaptureContext(dataCaptureContext, idCaptureSettings);
        idCapture.addListener(this);
        idCapture.setFeedback(settingsRepository.buildFeedbackFromSettings());
    }

    public IdCaptureOverlay buildIdCaptureOverlay() {
        IdCaptureOverlay overlay = IdCaptureOverlay.newInstance(idCapture, null);
        overlay.setIdLayoutStyle(settingsRepository.getOverlayStyle());
        overlay.setTextHintPosition(settingsRepository.getTextHintPosition());
        overlay.setShowTextHints(settingsRepository.getShowTextHints());
        overlay.setIdLayoutLineStyle(settingsRepository.getOverlayLineStyle());
        overlay.setCapturedBrush(settingsRepository.getCapturedBrush());
        if (!settingsRepository.getViewfinderFrontText().isEmpty()) {
            overlay.setFrontSideTextHint(settingsRepository.getViewfinderFrontText());
        }
        if (!settingsRepository.getViewfinderBackText().isEmpty()) {
            overlay.setBackSideTextHint(settingsRepository.getViewfinderBackText());
        }
        return overlay;
    }

    /**
     * The callback invoked by IdCapture every time that the document is captured.
     */
    @Override
    @WorkerThread
    public void onIdCaptured(@NonNull IdCapture mode, @NonNull CapturedId id) {

        /*
         * Implement to handle data captured from personal identification documents or their
         * elements like a barcode or the Machine Readable Zone (MRZ).
         *
         * This callback is executed on the background thread. We post the value to the LiveData
         * in order to return to the UI thread.
         */
        boolean needsBackScan =  id.getViz() != null &&
                id.getViz().isBackSideCaptureSupported() &&
                id.getViz().getCapturedSides() == CapturedSides.FRONT_ONLY;

        /*
         * Scan of the back of the document is enabled, the mode needs to be disabled while the
         * user decides if they want to continue scanning or just display the partial result.
         *
         * Don't capture unnecessarily when the result is displayed without continuous mode.
         */
        if (needsBackScan || !settingsRepository.isContinuousScanEnabled()) {
            disableIdCapture();
        }

        scanResults.postValue(
                new ScanResultEvent(
                        new ScanResult(
                                id,
                                settingsRepository.isContinuousScanEnabled(),
                                needsBackScan
                        )
                )
        );
    }

    public boolean isContinuousMode() {
        return settingsRepository.isContinuousScanEnabled();
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
         *   (b) it's a PDF417 barcode or a Machine Readable Zone (MRZ), but the data is encoded in an unexpected format,
         *   (c) it's a voided document and rejectVoidedIds is enabled in the settings,
         *   (d) the document has been localized, but could not be captured within a period of time.
         */
    }

    private IdCaptureScanner buildIdCaptureScanner() {
        if (settingsRepository.isFullScannerEnabled()) {
            return new FullDocumentScanner();
        } else {
            return new SingleSideScanner(
                    settingsRepository.isSingleSideScannerBarcodeEnabled(),
                    settingsRepository.isSingleSideScannerMrzEnabled(),
                    settingsRepository.isSingleSideScannerVizEnabled()
            );
        }
    }
}
