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

package com.scandit.datacapture.usdlverificationsample.data;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureListener;
import com.scandit.datacapture.id.capture.IdCaptureSession;
import com.scandit.datacapture.id.capture.IdCaptureSettings;
import com.scandit.datacapture.id.data.IdDocumentType;
import com.scandit.datacapture.id.data.IdImageType;
import com.scandit.datacapture.id.data.SupportedSides;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;
import com.scandit.datacapture.usdlverificationsample.ui.scan.CapturedIdEvent;

import org.jetbrains.annotations.NotNull;

/**
 * The repository to interact with the IdCapture mode.
 */
public class IdCaptureRepository implements IdCaptureListener {
    /**
     * The stream of IdCaptureOverlays. IdCaptureOverlay provides UI to aid the user in the
     * capture process. It needs to be attached to a DataCaptureView.
     */
    private final MutableLiveData<IdCaptureOverlay> idCaptureOverlays = new MutableLiveData<>();

    /**
     * The stream of data captured from driver's licenses.
     */
    private final MutableLiveData<CapturedIdEvent> capturedIds = new MutableLiveData<>();

    /**
     * The DataCaptureContext that the current IdCapture is attached to.
     */
    public final DataCaptureContext dataCaptureContext;

    /**
     * The current IdCapture.
     */
    private final IdCapture idCapture;

    /**
     * Create IdCapture configured to extract data from the front & the back of IDs & Driver's
     * Licenses.
     *
     * Additionally create an IdCaptureOverlay. IdCaptureOverlay provides UI to aid the user in the
     * capture process. It needs to attached to DataCaptureView.
     */
    public IdCaptureRepository(DataCaptureContext dataCaptureContext) {
        this.dataCaptureContext = dataCaptureContext;

        IdCaptureSettings settings = new IdCaptureSettings();
        settings.setSupportedDocuments(IdDocumentType.DL_VIZ, IdDocumentType.ID_CARD_VIZ);
        settings.setSupportedSides(SupportedSides.FRONT_AND_BACK);
        settings.setShouldPassImageTypeToResult(IdImageType.FACE, true);

        idCapture = IdCapture.forDataCaptureContext(dataCaptureContext, settings);
        idCapture.addListener(this);

        idCaptureOverlays.postValue(IdCaptureOverlay.newInstance(idCapture, null));
    }

    /**
     * The stream of IdCaptureOverlays. IdCaptureOverlay provides UI to aid the user in the
     * capture process. It needs to attached to DataCaptureView.
     */
    public LiveData<IdCaptureOverlay> idCaptureOverlays() {
        return idCaptureOverlays;
    }

    /**
     * The stream of data captured from driver's licenses.
     */
    public LiveData<CapturedIdEvent> capturedIds() {
        return capturedIds;
    }

    /**
     * Enable IdCapture so it may process the incoming frames.
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
     * Reset the state of IdCapture. If the captured document has not been issued in the United
     * States, we skip the back side of this document and this is necessary to reset the state of
     * IdCapture. If this call is omitted, IdCapture still expects to capture the back side of the
     * previous non-US document instead of the new one.
     */
    public void resetIdCapture() {
        idCapture.reset();
    }

    @Override
    @WorkerThread
    public void onIdCaptured(
            @NotNull IdCapture mode,
            @NotNull IdCaptureSession session,
            @NotNull FrameData data
    ) {
        /*
         * Implement to handle data captured from driver's licenses.
         *
         * This callback is executed on the background thread. We post the value to the LiveData
         * in order to return to the UI thread.
         */
        capturedIds.postValue(new CapturedIdEvent(session.getNewlyCapturedId()));
    }

    @Override
    @WorkerThread
    public void onIdLocalized(
            @NotNull IdCapture mode,
            @NotNull IdCaptureSession session,
            @NotNull FrameData data
    ) {
        /*
         * Implement to handle a driver's license localized within a frame. A driver's license is
         * considered localized when it's detected in a frame, but its data is not yet extracted.
         *
         * In this sample we are not interested in this callback.
         */
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
         * Here, the document is considered rejected when:
         *   (a) it's not a driver's license,
         *   (b) the barcode at the back side of a document is encoded in an unexpected format.
         *
         * In this sample we are not interested in this callback.
         */
    }

    @Override
    @WorkerThread
    public void onIdCaptureTimedOut(
            @NonNull IdCapture mode,
            @NonNull IdCaptureSession session,
            @NonNull FrameData data
    ) {
        /*
         * Implement to handle documents that were localized, but could not be captured within a period of time.
         *
         * In this sample we are not interested in this callback.
         */
    }

    @Override
    @WorkerThread
    public void onErrorEncountered(
            @NotNull IdCapture mode,
            @NotNull Throwable error,
            @NotNull IdCaptureSession session,
            @NotNull FrameData data
    ) {
        // In this sample, we are not interested in this callback.
    }

    @Override
    @WorkerThread
    public void onObservationStarted(@NotNull IdCapture mode) {
        // In this sample, we are not interested in this callback.
    }

    @Override
    @WorkerThread
    public void onObservationStopped(@NotNull IdCapture mode) {
        // In this sample, we are not interested in this callback.
    }
}
