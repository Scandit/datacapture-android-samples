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

import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureListener;
import com.scandit.datacapture.id.capture.IdCaptureSession;
import com.scandit.datacapture.id.capture.IdCaptureSettings;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.IdImageType;
import com.scandit.datacapture.id.data.SupportedSides;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;
import com.scandit.datacapture.idcapturesettingssample.ui.scan.ScanResult;

import androidx.annotation.NonNull;

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
    private final MutableLiveData<ScanResult> scanResults = new MutableLiveData<>();

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
    public LiveData<ScanResult> scanResults() {
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

        /*
         * Enable the desired document types from settings.
         */
        idCaptureSettings.setSupportedDocuments(settingsRepository.getSupportedDocuments());

        /*
         * Enable showing the desired image types from the settings.
         */
        Arrays.stream(IdImageType.values()).forEach((type) -> {
            idCaptureSettings.setShouldPassImageTypeToResult(type, settingsRepository.getShouldPassImageForType(type));
        });

        /*
         * Enable the desired document sides from settings.
         */
        idCaptureSettings.setSupportedSides(settingsRepository.getSupportedSides());
        idCapture = IdCapture.forDataCaptureContext(dataCaptureContext, idCaptureSettings);
        idCapture.addListener(this);
    }

    public IdCaptureOverlay buildIdCaptureOverlay() {
        IdCaptureOverlay overlay = IdCaptureOverlay.newInstance(idCapture, null);
        overlay.setIdLayoutStyle(settingsRepository.getOverlayStyle());
        overlay.setIdLayoutLineStyle(settingsRepository.getOverlayLineStyle());
        overlay.setCapturedBrush(settingsRepository.getCapturedBrush());
        return overlay;
    }

    /**
     * The callback invoked by IdCapture every time that the document is captured.
     */
    @Override
    @WorkerThread
    public void onIdCaptured(
            @NonNull IdCapture mode,
            @NonNull IdCaptureSession session,
            @NonNull FrameData data
    ) {

        /*
         * Implement to handle data captured from personal identification documents or their
         * elements like a barcode or the Machine Readable Zone (MRZ).
         *
         * This callback is executed on the background thread. We post the value to the LiveData
         * in order to return to the UI thread.
         */
        CapturedId capturedId = session.getNewlyCapturedId();
        boolean needsBackScan = false;
        if (capturedId != null &&
                capturedId.getViz() != null &&
                capturedId.getViz().isBackSideCaptureSupported() &&
                capturedId.getViz().getCapturedSides() == SupportedSides.FRONT_ONLY
        ) {
            needsBackScan = true;
        }
        scanResults.postValue(
                new ScanResult(
                        capturedId,
                        settingsRepository.isContinuousScanEnabled(),
                        needsBackScan
                )
        );
    }

    @Override
    @WorkerThread
    public void onObservationStarted(@NonNull IdCapture mode) {
        // Not interested in this callback.
    }

    @Override
    @WorkerThread
    public void onObservationStopped(@NonNull IdCapture mode) {
        // Not interested in this callback.
    }

    @Override
    @WorkerThread
    public void onIdLocalized(
            @NonNull IdCapture mode,
            @NonNull IdCaptureSession session,
            @NonNull FrameData data
    ) {
        /*
         * Implement to handle a personal identification document or its part localized within
         * a frame. A document or its part is considered localized when it's detected in a frame,
         * but its data is not yet extracted.
         *
         * In this sample we are not interested in this callback.
         */
    }

    @Override
    @WorkerThread
    public void onIdRejected(
            @NonNull IdCapture mode,
            @NonNull IdCaptureSession session,
            @NonNull FrameData data
    ) {
        /*
         * Implement to handle documents recognized in a frame, but rejected.
         * A document or its part is considered rejected when:
         *   (a) it's a valid document, but its type is not enabled in the settings,
         *   (b) it's a barcode of a correct symbology, but the data is encoded in an unexpected format,
         *   (c) it's Machine Readable Zone (MRZ), but the data is encoded in an unexpected format.
         *
         * This callback is executed on the background thread. We post the value to the LiveData
         * in order to return to the UI thread.
         */
    }

    @Override
    @WorkerThread
    public void onErrorEncountered(
            @NonNull IdCapture mode,
            @NonNull Throwable error,
            @NonNull IdCaptureSession session,
            @NonNull FrameData data
    ) {
        // Not interested in this callback.
    }
}
