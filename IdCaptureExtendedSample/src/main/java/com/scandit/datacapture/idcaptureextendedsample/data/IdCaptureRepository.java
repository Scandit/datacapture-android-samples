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
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.CapturedResultType;
import com.scandit.datacapture.id.data.IdDocumentType;
import com.scandit.datacapture.id.data.IdImageType;
import com.scandit.datacapture.id.data.SupportedSides;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;
import com.scandit.datacapture.idcaptureextendedsample.ui.scan.CapturedIdEvent;
import com.scandit.datacapture.idcaptureextendedsample.ui.scan.RejectedIdEvent;

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
    private final MutableLiveData<CapturedIdEvent> capturedIds = new MutableLiveData<>();

    /**
     * The stream of information about personal identification documents or their parts that
     * were detected in a frame, but rejected.
     *
     * A document or its part is considered rejected when:
     *   (a) it's a valid document, but its type is not enabled in the settings,
     *   (b) it's a barcode of a correct symbology, but the data is encoded in an unexpected format,
     *   (c) it's Machine Readable Zone (MRZ), but the data is encoded in an unexpected format.
     */
    private final MutableLiveData<RejectedIdEvent> rejectedIds = new MutableLiveData<>();

    /**
     * The DataCaptureContext that the current IdCapture is attached to.
     */
    public final DataCaptureContext dataCaptureContext;

    /**
     * The current IdCapture.
     */
    private final IdCapture idCapture;

    /**
     * Create IdCapture configured to extract data for the specified type of personal identification
     * documents or their parts.
     *
     * In this sample, the user may extract data from:
     * * barcodes - for example PDF417 present at the back side of US Driver's Licenses,
     * * Machine Readable Zones (MRZ) - like ones in passports or European IDs,
     * * human-readable text (VIZ) - like the holder's name or date of birth printed on an ID.
     *
     * Additionally create an IdCaptureOverlay. IdCaptureOverlay provides UI to aid the user in the
     * capture process. It needs to attached to DataCaptureView.
     */
    public IdCaptureRepository(DataCaptureContext dataCaptureContext) {
        this.dataCaptureContext = dataCaptureContext;

        idCapture = IdCapture.forDataCaptureContext(dataCaptureContext, new IdCaptureSettings());
        idCapture.addListener(this);

        IdCaptureOverlay overlay = IdCaptureOverlay.newInstance(idCapture, null);
        idCaptureOverlays.postValue(overlay);
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
    public LiveData<CapturedIdEvent> capturedIds() {
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
    public LiveData<RejectedIdEvent> rejectedIds() {
        return rejectedIds;
    }

    /**
     * Configure the IdCapture to extract data for the specified type of personal identification
     * documents or their parts.
     *
     * In this sample, the user may extract data from:
     * * barcodes - for example PDF417 present at the back side of US Driver's Licenses,
     * * Machine Readable Zones (MRZ) - like ones in passports or European IDs,
     * * human-readable text (VIZ) - like the holder's name or date of birth printed on an ID.
     */
    public void updateIdCapture(CapturedDataType type) {
        idCapture.applySettings(getSettingsForMode(type));
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
                IdDocumentType.COLOMBIA_DL_BARCODE,
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
                IdDocumentType.SWISS_DL_MRZ,
                IdDocumentType.CHINA_MAINLAND_TRAVEL_PERMIT_MRZ,
                IdDocumentType.CHINA_EXIT_ENTRY_PERMIT_MRZ,
                IdDocumentType.CHINA_ONE_WAY_PERMIT_BACK_MRZ,
                IdDocumentType.CHINA_ONE_WAY_PERMIT_FRONT_MRZ,
                IdDocumentType.APEC_BUSINESS_TRAVEL_CARD_MRZ
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

    @Override
    @WorkerThread
    public void onIdCaptured(
            @NotNull IdCapture mode,
            @NotNull IdCaptureSession session,
            @NotNull FrameData data
    ) {
        CapturedId capturedId = session.getNewlyCapturedId();
        boolean isCaptureComplete = capturedId != null &&
                capturedId.getCapturedResultType() != CapturedResultType.VIZ_RESULT ||
                        capturedId.getViz().getCapturedSides() != SupportedSides.FRONT_ONLY ||
                        !capturedId.getViz().isBackSideCaptureSupported();

        /*
         * Disable ID capture if the captured document is complete. It's important to disable ID
         * capture as soon as possible to avoid capturing other documents while the result is
         * displayed.
         */
        if (isCaptureComplete) {
            disableIdCapture();
        }
        /*
         * Implement to handle data captured from personal identification documents or their
         * elements like a barcode or the Machine Readable Zone (MRZ).
         *
         * This callback is executed on the background thread. We post the value to the LiveData
         * in order to return to the UI thread.
         */
        capturedIds.postValue(new CapturedIdEvent(capturedId));
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
        rejectedIds.postValue(new RejectedIdEvent(session.getNewlyRejectedId()));
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
