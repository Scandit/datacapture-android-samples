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
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.id.capture.DriverLicense;
import com.scandit.datacapture.id.capture.FullDocumentScanner;
import com.scandit.datacapture.id.capture.IdCapture;
import com.scandit.datacapture.id.capture.IdCaptureDocument;
import com.scandit.datacapture.id.capture.IdCaptureListener;
import com.scandit.datacapture.id.capture.IdCaptureSettings;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.IdCaptureRegion;
import com.scandit.datacapture.id.data.IdImageType;
import com.scandit.datacapture.id.data.RejectionReason;
import com.scandit.datacapture.id.ui.overlay.IdCaptureOverlay;
import com.scandit.datacapture.usdlverificationsample.ui.scan.CapturedIdEvent;
import com.scandit.datacapture.usdlverificationsample.ui.scan.RejectedId;
import com.scandit.datacapture.usdlverificationsample.ui.scan.RejectedIdEvent;

import java.util.Arrays;
import java.util.List;

/**
 * The repository to interact with the IdCapture mode.
 */
public class IdCaptureRepository implements IdCaptureListener {

    private static final List<IdCaptureDocument> ACCEPTED_DOCUMENTS = Arrays.asList(
            new DriverLicense(IdCaptureRegion.US)
    );

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
     * The stream rejected documents.
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
     * Create IdCapture configured to extract data from the front & the back of IDs & Driver's
     * Licenses.
     *
     * Additionally create an IdCaptureOverlay. IdCaptureOverlay provides UI to aid the user in the
     * capture process. It needs to attached to DataCaptureView.
     */
    public IdCaptureRepository(DataCaptureContext dataCaptureContext) {
        this.dataCaptureContext = dataCaptureContext;

        IdCaptureSettings settings = new IdCaptureSettings();
        settings.setAcceptedDocuments(ACCEPTED_DOCUMENTS);
        settings.setScannerType(new FullDocumentScanner());
        settings.setShouldPassImageTypeToResult(IdImageType.FACE, true);
        settings.setShouldPassImageTypeToResult(IdImageType.CROPPED_DOCUMENT, true);

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
     * The stream of rejected documents.
     */
    public LiveData<RejectedIdEvent> rejectedIds() {
        return rejectedIds;
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
    public void onIdCaptured(@NonNull IdCapture mode, @NonNull CapturedId id) {
        /*
         * Implement to handle data captured from driver's licenses.
         *
         * This callback is executed on the background thread. We post the value to the LiveData
         * in order to return to the UI thread.
         */
        capturedIds.postValue(new CapturedIdEvent(id));
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
         *   (c) the document meets the conditions of a rejection rule enabled in the settings,
         *   (d) the document has been localized, but could not be captured within a period of time.
         */
        rejectedIds.postValue(new RejectedIdEvent(new RejectedId(id, reason)));
    }
}
