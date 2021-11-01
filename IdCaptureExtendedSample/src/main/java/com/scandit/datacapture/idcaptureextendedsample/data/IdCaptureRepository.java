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

package com.scandit.datacapture.idcaptureextendedsample.data;

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
import com.scandit.datacapture.id.data.IdDocumentType;
import com.scandit.datacapture.id.data.IdImageType;
import com.scandit.datacapture.id.data.RejectedId;
import com.scandit.datacapture.id.data.SupportedSides;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;

import org.jetbrains.annotations.NotNull;

/**
 * The repository to interact with the IdCapture mode.
 */
public class IdCaptureRepository implements IdCaptureListener {
    /**
     * The stream of IdCaptureOverlays. IdCaptureOverlay provides UI to aid the user in the
     * capture process. It needs to attached to DataCaptureView..
     */
    private final MutableLiveData<IdCaptureOverlay> idCaptureOverlays = new MutableLiveData<>();

    /**
     * The stream of data captured from personal identification documents or their parts.
     */
    private final MutableLiveData<CapturedId> capturedIds = new MutableLiveData<>();

    /**
     * The stream of information about personal identification documents or their parts that
     * were detected in a frame, but rejected.
     *
     * A document or its part is considered rejected when:
     *   (a) it's a valid document, but its type is not enabled in the settings,
     *   (b) it's a barcode of a correct symbology, but the data is encoded in an unexpected format,
     *   (c) it's Machine Readable Zone (MRZ), but the data is encoded in an unexpected format.
     */
    private final MutableLiveData<RejectedId> rejectedIds = new MutableLiveData<>();

    /**
     * The DataCaptureContext that the current IdCapture is attached to.
     */
    public final DataCaptureContext dataCaptureContext;

    /**
     * The current IdCapture.
     */
    private IdCapture idCapture;

    public IdCaptureRepository(DataCaptureContext dataCaptureContext) {
        this.dataCaptureContext = dataCaptureContext;
    }

    /**
     * The stream of IdCaptureOverlays. IdCaptureOverlay provides UI to aid the user in the
     * capture process. It needs to attached to DataCaptureView..
     */
    public LiveData<IdCaptureOverlay> idCaptureOverlays() {
        return idCaptureOverlays;
    }

    /**
     * The stream of data captured from personal identification documents or their parts.
     */
    public LiveData<CapturedId> capturedIds() {
        return capturedIds;
    }

    /**
     * The stream of information about personal identification documents or their parts that
     * were detected in a frame, but rejected.
     *
     * A document or its part is considered rejected when:
     *   (a) it's a valid document, but its type is not enabled in the settings,
     *   (b) it's a barcode of a correct symbology, but the data is encoded in an unexpected format,
     *   (c) it's Machine Readable Zone (MRZ), but the data is encoded in an unexpected format.
     */
    public LiveData<RejectedId> rejectedIds() {
        return rejectedIds;
    }

    /**
     * Create IdCapture configured to extract data for the specified type of personal identification
     * documents or their parts.
     *
     * In this sample, the user may extract data from:
     * * barcodes - for example PDF417 present at the back side of US Driver's Licenses,
     * * Machine Readable Zones (MRZ) - like ones in passports or European IDs,
     * * human-readable text (VIZ) - like the holder's name or date of birth printed on an ID.
     */
    public void createIdCapture(CapturedDataType type) {
        if (idCapture != null) {
            idCapture.removeListener(this);
            dataCaptureContext.removeMode(idCapture);
        }

        idCapture = IdCapture.forDataCaptureContext(dataCaptureContext, getSettingsForMode(type));
        idCapture.addListener(this);

        idCaptureOverlays.postValue(IdCaptureOverlay.newInstance(idCapture, null));
    }

    private IdCaptureSettings getSettingsForMode(CapturedDataType type) {
        switch (type) {
            case BARCODE:
                return getBarcodeSettings();
            case MRZ:
                return getMrzSettings();
            case VIZ:
                return getVizSettings();
            default:
                throw new AssertionError("Unknown CapturedDataType: " + type);
        }
    }

    /*
     * Extract data from barcodes present on various personal identification document types.
     */
    private IdCaptureSettings getBarcodeSettings() {
        IdCaptureSettings settings = new IdCaptureSettings();
        settings.setShouldPassImageTypeToResult(IdImageType.FACE, true);
        settings.setSupportedDocuments(
                IdDocumentType.AAMVA_BARCODE,
                IdDocumentType.ARGENTINA_ID_BARCODE,
                IdDocumentType.COLOMBIA_ID_BARCODE,
                IdDocumentType.SOUTH_AFRICA_DL_BARCODE,
                IdDocumentType.SOUTH_AFRICA_ID_BARCODE,
                IdDocumentType.US_US_ID_BARCODE
        );
        return settings;
    }

    /*
     * Extract data from Machine Readable Zones (MRZ) present for example on IDs, passports or visas.
     */
    private IdCaptureSettings getMrzSettings() {
        IdCaptureSettings settings = new IdCaptureSettings();
        settings.setSupportedDocuments(
                IdDocumentType.ID_CARD_MRZ,
                IdDocumentType.PASSPORT_MRZ,
                IdDocumentType.VISA_MRZ,
                IdDocumentType.SWISS_DL_MRZ
        );
        return settings;
    }

    /*
     * Extract data from personal identification document's human-readable texts (like
     * the holder's name or date of birth printed on an ID).
     */
    private IdCaptureSettings getVizSettings() {
        IdCaptureSettings settings = new IdCaptureSettings();
        settings.setSupportedSides(SupportedSides.FRONT_AND_BACK);
        settings.setShouldPassImageTypeToResult(IdImageType.FACE, true);
        settings.setShouldPassImageTypeToResult(IdImageType.ID_FRONT, true);
        settings.setShouldPassImageTypeToResult(IdImageType.ID_BACK, true);
        settings.setSupportedDocuments(
                IdDocumentType.ID_CARD_VIZ,
                IdDocumentType.DL_VIZ
        );
        return settings;
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
     * Reset the state of IdCapture. This is necessary if you decide to skip the back side of
     * a document when the back side scan is supported. If you omit this call, IdCapture still
     * expects to capture the back side of the previous document instead of the new one.
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
         * Implement to handle data captured from personal identification documents or their
         * elements like a barcode or the Machine Readable Zone (MRZ).
         *
         * This callback is executed on the background thread. We post the value to the LiveData
         * in order to return to the UI thread.
         */
        capturedIds.postValue(session.getNewlyCapturedId());
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
         * A document or its part is considered rejected when:
         *   (a) it's a valid document, but its type is not enabled in the settings,
         *   (b) it's a barcode of a correct symbology, but the data is encoded in an unexpected format,
         *   (c) it's Machine Readable Zone (MRZ), but the data is encoded in an unexpected format.
        *
         * This callback is executed on the background thread. We post the value to the LiveData
         * in order to return to the UI thread.
         */
        rejectedIds.postValue(session.getNewlyRejectedId());
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
